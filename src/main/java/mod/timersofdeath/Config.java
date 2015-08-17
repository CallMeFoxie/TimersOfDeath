package mod.timersofdeath;

import darklib.PlayerData;
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

        PlayerData.TIME_TO_REMEMBER = cfg.get("config", "timeToRemember", PlayerData.TIME_TO_REMEMBER).getInt();
        Events.sources1 = cfg.getStringList("sources1", "config", Events.sources1, "Type 1 severity damage sources");
        Events.sources2 = cfg.getStringList("sources2", "config", Events.sources2, "Type 2 severity damage sources");
        Events.sources3 = cfg.getStringList("sources3", "config", Events.sources3, "Type 3 severity damage sources");

        Events.SEVERITY_1 = cfg.getInt("severity1", "config", Events.SEVERITY_1, 0, 100, "Type 1 severity");
        Events.SEVERITY_2 = cfg.getInt("severity2", "config", Events.SEVERITY_2, 0, 100, "Type 2 severity");
        Events.SEVERITY_3 = cfg.getInt("severity3", "config", Events.SEVERITY_3, 0, 100, "Type 3 severity");

        Events.SEVERITY_DEFAULT = cfg.getInt("severity_default", "config", Events.SEVERITY_DEFAULT, 0, 100, "Severity for unknown sources");

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

    public static class Debuff {
        public boolean enable;
        public int     minimumSeverity;
        public int     maxScale;
        public Potion  potion;
    }
}
