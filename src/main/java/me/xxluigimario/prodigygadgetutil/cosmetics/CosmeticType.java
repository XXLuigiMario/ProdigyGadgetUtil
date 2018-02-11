package me.xxluigimario.prodigygadgetutil.cosmetics;

import fr.cocoraid.prodigygadget.configs.configfiles.CoreConfig;
import fr.cocoraid.prodigygadget.cosmetics.gadget.IGadget;
import fr.cocoraid.prodigygadget.cosmetics.mood.ProdigyMood;
import fr.cocoraid.prodigygadget.cosmetics.morph.ProdigyMorph;
import fr.cocoraid.prodigygadget.cosmetics.particle.ProdigyParticle;
import fr.cocoraid.prodigygadget.cosmetics.pet.ProdigyPet;
import me.xxluigimario.prodigygadgetutil.GadgetUtil;
import org.apache.commons.lang.WordUtils;

/**
 *
 * @author Joel
 */
public enum CosmeticType {

    GADGET(IGadget.class),
    MOOD(ProdigyMood.class),
    MORPH(ProdigyMorph.class),
    PARTICLE(ProdigyParticle.class),
    PET(ProdigyPet.class);

    private static final CoreConfig CORE = GadgetUtil.getCoreConfig();

    private final Class<?> clazz;
    private String formattedName;

    static {
        GADGET.formattedName = CORE.itemgadgetName;
        MOOD.formattedName = CORE.itemmoodName;
        MORPH.formattedName = CORE.itemmorphName;
        PARTICLE.formattedName = CORE.itemparticleName;
        PET.formattedName = CORE.itempetName;
    }

    private CosmeticType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getTypeClass() {
        return clazz;
    }

    public String getName() {
        return WordUtils.capitalizeFully(name());
    }

    public String getFormattedName() {
        return formattedName;
    }
}
