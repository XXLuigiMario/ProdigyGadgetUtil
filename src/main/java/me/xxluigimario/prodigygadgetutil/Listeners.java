package me.xxluigimario.prodigygadgetutil;

import com.hazebyte.crate.api.CrateAPI;
import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.api.crate.reward.Tag;
import com.hazebyte.crate.api.effect.Category;
import com.hazebyte.crate.api.event.CrateInteractEvent;
import fr.cocoraid.prodigygadget.configs.configfiles.CoreConfig;
import fr.onecraft.clientstats.ClientStats;
import fr.onecraft.clientstats.ClientStatsAPI;
import me.xxluigimario.prodigygadgetutil.cosmetics.Cosmetic;
import me.xxluigimario.prodigygadgetutil.cosmetics.CosmeticManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Joel
 */
public class Listeners implements Listener {

    private final FileConfiguration config = GadgetUtil.getPluginConfig();
    private final CoreConfig core = GadgetUtil.getCoreConfig();

    /*
     * If the player interacts with our custom crate, we personalize it's
     * rewards.
     */
    @EventHandler
    public void onCrateOpen(CrateInteractEvent e) {
        Crate crate = e.getCrate();
        for (Category category : Category.values()) {
            Bukkit.getLogger().info(String.format("Crate '%s', %s:", crate.getCrateName(), category.name()));
            int i = 0;
            for (ConfigurationSection section : crate.getEffect(category)) {
                i++;
                for (String key : section.getKeys(false)) {
                    Bukkit.getLogger().info(String.format("Effect #%d, key %s: %s", i, key, section.getString(key)));
                }
            }
        }
        if (!updateRewards(e.getCrate(), e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    /*
     * We check to see if we have to update the rewards of the crate.
     * Only returns false if the crate we're dealing with is our custom crate
     * and we the player has already got all the available cosmetics.
     */
    public boolean updateRewards(Crate crate, Player player) {
        if (crate.getUUID().equals(GadgetUtil.getCustomCrate().getUUID())) {
            ArrayList<Reward> rewards = new ArrayList<>();
            List<Cosmetic> cosmetics = CosmeticManager.getCosmetics(player, false);
            if (cosmetics.isEmpty()) {
                player.sendMessage(GadgetUtil.getString(Config.MESSAGES_EVERYTHING));
                return false;
            }
            for (Cosmetic cosmetic : cosmetics) {
                if (!cosmetic.isEnabled()) {
                    continue;
                }
                Reward reward = CrateAPI.getCrateRegistrar().createReward();
                reward.setDisplayItem(cosmetic.getItemStack());
                reward.getCommands().add("/pex user " + player.getName() + " add " + cosmetic.getPerm().getPerm());
                if (config.getBoolean(Config.MESSAGES_UNLOCK_ENABLE.getPath())) {
                    String message = String.format(GadgetUtil.getString(Config.MESSAGES_UNLOCK), cosmetic.getFormattedName(), cosmetic.getType().getFormattedName());
                    reward.setMessages(Tag.MESSAGE, Arrays.asList(message));
                }
                reward.setChance(1);
                rewards.add(reward);
            }
            crate.setRewards(rewards);
        }
        return true;
    }

    /*
     * If the player is using a version older than 1.9, we gracefully downgrade
     * the selector menu.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        ClientStatsAPI cstats = ClientStats.getApi();
        boolean selectorDisabled = false;
        if (cstats != null && cstats.isVersionDetectionEnabled()
                && cstats.getProtocol(e.getPlayer().getUniqueId()) <= 47) {
            selectorDisabled = true;
            core.Selector3DEnable = false;
        }
        GadgetUtil.getPGListener().interactWithItem(e);
        if (selectorDisabled) {
            core.Selector3DEnable = true;
        }
    }
}
