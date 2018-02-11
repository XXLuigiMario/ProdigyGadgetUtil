package me.xxluigimario.prodigygadgetutil;

import org.bukkit.Bukkit;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 *
 * @author Joel
 */
public class ReflectionUtils {

    public static Object get(String name, Class<?> clazz, Object instance) {
        Field field = getField(name, clazz);
        field.setAccessible(true);
        try {
            return field.get(instance);
        } catch (Exception e) {
            handleException(e, "getting value of declared field '" + name + "' from " + clazz.getSimpleName());
            return null;
        }
    }

    public static void set(String name, Class<?> clazz, Object instance, Object value) {
        Field field = getField(name, clazz);
        field.setAccessible(true);
        try {
            field.set(instance, value);
        } catch (Exception e) {
            handleException(e, "setting value of declared field '" + name + "' from " + clazz.getSimpleName());
        }
    }

    public static Object invokeMethod(String name, Class<?> clazz, Object instance, Object... args) {
        try {
            Class<?>[] types = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                types[i] = args[i].getClass();
            }
            Method method = clazz.getDeclaredMethod(name, types);
            method.setAccessible(true);
            return method.invoke(instance, args);
        } catch (Exception e) {
            handleException(e, "invoking declared method '" + name + "' from " + clazz.getSimpleName());
            return null;
        }
    }

    public static Field getField(String name, Class<?> clazz) {
        try {
            return clazz.getDeclaredField(name);
        } catch (Exception e) {
            handleException(e, "getting declared field '" + name + "' from " + clazz.getSimpleName());
            return null;
        }
    }

    public static <T> T allocateInstance(Class<T> clazz) {
        try {
            if (hasParameterlessConstructor(clazz)) {
                return clazz.newInstance();
            }
            return (T) getUnsafe().allocateInstance(clazz);
        } catch (Exception e) {
            handleException(e, "allocating new instance of class '" + clazz.getSimpleName() + "'.");
            return null;
        }
    }

    private static boolean hasParameterlessConstructor(Class<?> clazz) {
        return Stream.of(clazz.getConstructors())
                .anyMatch((c) -> c.getParameterCount() == 0);
    }

    private static void handleException(Exception e, String reason) {
        Bukkit.getLogger().log(Level.SEVERE, "An unexpected error has occurred while " + reason, e);
    }

    @SuppressWarnings("restriction")
    private static Unsafe getUnsafe() throws Exception {
        Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
        singleoneInstanceField.setAccessible(true);
        return (Unsafe) singleoneInstanceField.get(null);
    }
}
