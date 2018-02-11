package me.xxluigimario.prodigygadgetutil;

import com.hazebyte.crate.api.CrateAPI;
import com.hazebyte.crate.api.crate.AnimationType;
import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.crate.CrateType;
import com.hazebyte.crate.api.effect.Category;
import fr.cocoraid.prodigygadget.ProdigyGadget;
import fr.cocoraid.prodigygadget.configs.configfiles.CoreConfig;
import fr.cocoraid.prodigygadget.listener.events.JoinQuitListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Joel
 */
public class GadgetUtil extends JavaPlugin {

    private static JoinQuitListener pgListener;
    private static GadgetUtil instance;
    private static PluginDescriptionFile pdfFile;
    private static Crate customCrate;

    @Override
    public void onEnable() {
        // Standard plugin initialization procedure
        instance = this;
        pdfFile = getDescription();
        loadConfig(false);

        /*
         * We unregister ProdigyGadget's PlayerInteractEvent, as we want to
         * change its config before and after the event is handled,
         * so we handle it ourselves and pass it directly. (See Listeners.java)
         */
        ProdigyGadget pg = ProdigyGadget.getInstance();
        for (RegisteredListener rl : HandlerList.getRegisteredListeners(pg)) {
            Listener listener = rl.getListener();
            if (listener instanceof JoinQuitListener) {
                pgListener = (JoinQuitListener) listener;
                PlayerInteractEvent.getHandlerList().unregister(listener);
            }
        }

        // We add our custom crate to CrateReloaded.
        CrateAPI.getCrateRegistrar().add(customCrate);

        // And finally... we register our event handlers.
        getServer().getPluginManager().registerEvents(new Listeners(), this);
    }

    @Override
    public void onDisable() {
        // Force save crates
        CrateAPI.getInstance().getBlockCrateRegistrar().save();
    }

    public Crate loadCrate() {
        String name = getString(Config.CRATE_NAME);
        String display = getString(Config.CRATE_DISPLAYNAME);
        String sType = getString(Config.CRATE_TYPE);
        Crate crate = CrateAPI.getCrateRegistrar().createCrate(name, CrateType.KEY);
        AnimationType type;
        try {
            type = AnimationType.valueOf(sType);
        } catch (IllegalArgumentException e) {
            type = AnimationType.CSGO;
            getLogger().warning("Invalid crate type '" + sType + "', using " + type.name() + ".");
        }
        crate.setAnimationType(type);
        crate.setDisplayName(display);
        Material keyMat = Material.getMaterial(getConfig().getInt(Config.CRATE_KEY_ID.getPath()));
        ItemStack key = new ItemStack(keyMat, 1);
        ItemMeta im = key.getItemMeta();
        im.setDisplayName(getString(Config.CRATE_KEY_NAME));
        List<String> lore = new ArrayList<>();
        for (String str : getConfig().getStringList(Config.CRATE_KEY_LORE.getPath())) {
            lore.add(colorize(str));
        }
        im.setLore(lore);
        key.setItemMeta(im);
        crate.setItem(key);
        crate.setRequiresKey(true);
        crate.setAnimationType(AnimationType.CSGO);
        crate.addEffect(Category.OPEN, getConfig().getConfigurationSection(Config.CRATE_EFFECT_OPEN.getPath()));
        crate.addEffect(Category.PERSISTENT, getConfig().getConfigurationSection(Config.CRATE_EFFECT_DORMANT.getPath()));
        return crate;
    }

    public void loadConfig(boolean reload) {
        if (reload) {
            reloadConfig();
        }
        Config.copyDefaults(getConfig());
        saveConfig();
        customCrate = loadCrate();
    }

    public static String getString(Config value) {
        return colorize(instance.getConfig().getString(value.getPath()));
    }

    public static String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    protected static JoinQuitListener getPGListener() {
        return pgListener;
    }

    public static FileConfiguration getPluginConfig() {
        return instance.getConfig();
    }

    public static CoreConfig getCoreConfig() {
        return (CoreConfig) ProdigyGadget.ConfigType.CORE.getConfig();
    }

    public static Crate getCustomCrate() {
        return customCrate;
    }
}
