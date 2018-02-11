package me.xxluigimario.prodigygadgetutil.cosmetics;

import me.xxluigimario.prodigygadgetutil.ReflectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Joel
 */
public class CosmeticManager {

    private static final HashMap<CosmeticType, List<Cosmetic>> BY_TYPE = new HashMap<>();
    private static final Random RAND = new Random();

    public static List<Cosmetic> getList(CosmeticType type) {
        if (BY_TYPE.containsKey(type)) {
            return BY_TYPE.get(type);
        }
        List<Cosmetic> cosmetics = new ArrayList<>();
        for (Object obj : (List<?>) ReflectionUtils.invokeMethod("getList", type.getTypeClass(), null)) {
            Class<?> wrapper = obj.getClass();
            Class<?> cosmClass = (Class<?>) ReflectionUtils.invokeMethod("get" + type.getName() + "Class", wrapper, obj);
            String name = cosmClass.getSimpleName().replaceAll("(?i)" + type.getName(), "").toLowerCase();
            ItemStack is = (ItemStack) ReflectionUtils.invokeMethod("getItem", wrapper, obj);
            boolean enabled = (boolean) ReflectionUtils.invokeMethod("isEnable", wrapper, obj);
            cosmetics.add(new Cosmetic(name, type, is, enabled));
        }
        BY_TYPE.put(type, cosmetics);
        return cosmetics;
    }

    public static List<Cosmetic> getList() {
        List<Cosmetic> all = new ArrayList<>();
        for (CosmeticType type : CosmeticType.values()) {
            all.addAll(getList(type));
        }
        return all;
    }

    public static List<Cosmetic> getCosmeticsOfType(CosmeticType type, Player player, boolean owned) {
        return getCosmeticsFrom(getList(type), player, owned);
    }

    public static List<Cosmetic> getCosmetics(Player player, boolean owned) {
        return getCosmeticsFrom(getList(), player, owned);
    }

    private static List<Cosmetic> getCosmeticsFrom(List<Cosmetic> cosmetics, Player player, boolean owned) {
        List<Cosmetic> meetConditions = new ArrayList<>();
        PermissionUser user = PermissionsEx.getUser(player);
        for (Cosmetic cosmetic : getList()) {
            boolean hasPerm = user.has(cosmetic.getPerm().getPerm());
            if (hasPerm && owned || !hasPerm && !owned) {
                meetConditions.add(cosmetic);
            }
        }
        return meetConditions;
    }

    public static Cosmetic getRandomCosmeticOfType(CosmeticType type, Player player, boolean owned) {
        return randomFrom(getCosmeticsOfType(type, player, owned));
    }

    public static Cosmetic getRandomCosmetic(Player player, boolean owned) {
        return randomFrom(getCosmetics(player, owned));
    }

    private static <T> T randomFrom(List<T> list) {
        return list.get(RAND.nextInt(list.size()));
    }
}
