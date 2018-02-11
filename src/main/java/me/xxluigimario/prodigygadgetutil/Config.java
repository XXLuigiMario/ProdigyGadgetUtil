package me.xxluigimario.prodigygadgetutil;

import com.hazebyte.crate.api.crate.AnimationType;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Joel
 */
public enum Config {

    MESSAGES_UNLOCK_ENABLE("Messages.OnUnlock.Enable", true),
    MESSAGES_UNLOCK("Messages.OnUnlock.Message", "&7You've unlocked: %s &8&l[%s&8&l]"),
    MESSAGES_EVERYTHING("Messages.Everything", "&6&lYou've already got everything!"),
    MESSAGES_RELOAD("Messages.Reload", "&7%s &8&l> &aReload complete!"),
    CRATE_NAME("Crate.Name", "GadgetBox"),
    CRATE_DISPLAYNAME("Crate.DisplayName", "&3GadgetBox"),
    CRATE_TYPE("Crate.Type", AnimationType.CSGO.name()),
    CRATE_KEY_ID("Crate.Key.ID", 131),
    CRATE_KEY_NAME("Crate.Key.Name", "&3GadgetBox Key"),
    CRATE_KEY_LORE("Crate.Key.Lore", Arrays.asList("&7Right click a GadgetBox", "&7to win a prize!")),
    CRATE_EFFECT_OPEN("Crate.Effect.Open.1", new HashMap<String, String>()
    {{
        put("class", "SphereEffect");
    }}),
    CRATE_EFFECT_DORMANT("Crate.Effect.Dormant.1", new HashMap<String, String>()
    {{
        put("class", "StarEffect");
        put("particle", "CRIT");
    }});

    private final String path;
    private final Object defaultValue;

    private Config(String path, Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public String getPath() {
        return path;
    }

    public static void copyDefaults(FileConfiguration config) {
        for (Config value : values()) {
            if (!config.isSet(value.path)) {
                config.set(value.path, value.defaultValue);
            }
        }
    }
}
