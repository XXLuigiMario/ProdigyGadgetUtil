package me.xxluigimario.prodigygadgetutil.cosmetics;

import fr.cocoraid.prodigygadget.utils.Perm;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Joel
 */
public class Cosmetic {

    private final String name;
    private final CosmeticType type;
    private final ItemStack is;
    private final boolean isEnabled;
    private final Perm perm;

    public Cosmetic(String name, CosmeticType type, ItemStack is, boolean isEnabled) {
        this.name = name;
        this.type = type;
        this.is = is;
        this.isEnabled = isEnabled;
        this.perm = new Perm(type.name().toLowerCase(), name.toLowerCase());
    }

    public String getName() {
        return name;
    }
    
    public String getFormattedName() {
        return is.getItemMeta().getDisplayName();
    }

    public CosmeticType getType() {
        return type;
    }
    
    public ItemStack getItemStack() {
        return is;
    }
    
    public boolean isEnabled() {
        return isEnabled;
    }

    public Perm getPerm() {
        return perm;
    }
}
