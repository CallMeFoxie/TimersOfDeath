package mod.timersofdeath;

import net.minecraft.potion.Potion;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class Config {

    public static ArrayList<Debuff> debuffs;

    public Config(String filename) {
        Configuration cfg = new Configuration(new File(filename), true);
        cfg.load();

        String[] debuffNames = {"moveSlowdown", "digSlowdown", "harm", "confusion", "blindness", "hunger", "weakness", "poison", "wither"};
        boolean[] defaultEnable = {true, true, false, false, true, false, true, false, false};
        int[] minimumSeverity = {10, 10, 10, 10, 10, 10, 10, 10, 10};
        int[] defaultMaxScale = {3, 3, 1, 1, 2, 1, 3, 1, 1};
        int[] potionEffect = {Potion.moveSlowdown.getId(), Potion.digSlowdown.getId(), Potion.harm.getId(), Potion.confusion.getId(),
                Potion.blindness.getId(), Potion.hunger.getId(), Potion.weakness.getId(), Potion.poison.getId(), Potion.wither.getId()};

        Debuff[] debuffs = new Debuff[debuffNames.length];

        for (int i = 0; i < debuffNames.length; i++) {
            String baseName = debuffNames[i];
            Debuff debuff = new Debuff();
            debuff.potion = Potion.potionTypes[potionEffect[i]];
            debuff.enable = cfg.getBoolean(baseName + "_enable", "debuffs", defaultEnable[i], "Enable this debuff");
            debuff.maxScale = cfg.getInt(baseName + "_maxScale", "debuffs", defaultMaxScale[i], 0, 5, "Maximum scaling of this debuff");
            debuff.minimumSeverity = cfg.getInt(baseName + "_min", "debuffs", minimumSeverity[i], 1, 23999, "At which level is this debuff " +
                    "applied");
            debuffs[i] = debuff;
        }

        Config.debuffs = new ArrayList<Debuff>();
        Collections.addAll(Config.debuffs, debuffs);

        if (cfg.hasChanged())
            cfg.save();
    }

    // TODO configure this
    public static class Debuff {
        public boolean enable;
        public int     minimumSeverity;
        public int     maxScale;
        public Potion  potion;
    }
}
