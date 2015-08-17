package mod.timersofdeath;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import darklib.PlayerData;
import darklib.SavedData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.Arrays;
import java.util.List;

public class Events {

    public static final int POTION_DURATION = 40;

    public static int SEVERITY_DEFAULT = 1;
    public static int SEVERITY_1       = 2;
    public static int SEVERITY_2       = 3;
    public static int SEVERITY_3       = 4;

    public static String[] sources1;
    public static String[] sources2;
    public static String[] sources3;

    public static Events INSTANCE;

    static {
        sources1 = new String[]{};
        sources2 = new String[]{};
        sources3 = new String[]{"drown", "starve"};
    }

    public Events() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (event.entity.worldObj.isRemote)
            return;

        if (event.entity instanceof EntityPlayer) {
            PlayerData data = TimersOfDeath.INSTANCE.playerData.getData((EntityPlayer) event.entity);
            if (Arrays.asList(sources3).contains(event.source.damageType))
                data.addDeath(event.entity.worldObj.getTotalWorldTime(), SEVERITY_3);
            else if (Arrays.asList(sources2).contains(event.source.damageType))
                data.addDeath(event.entity.worldObj.getTotalWorldTime(), SEVERITY_2);
            else if (Arrays.asList(sources1).contains(event.source.damageType))
                data.addDeath(event.entity.worldObj.getTotalWorldTime(), SEVERITY_1);
            else
                data.addDeath(event.entity.worldObj.getTotalWorldTime(), SEVERITY_DEFAULT);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.worldObj.getTotalWorldTime() % POTION_DURATION != 0) // once every 2 seconds
            return;

        if (event.phase != TickEvent.Phase.START || event.player.worldObj.isRemote)
            return;

        PlayerData data = TimersOfDeath.INSTANCE.playerData.getData(event.player);
        data.cleanDeathTimes(event.player.worldObj.getTotalWorldTime());

        int weightedDeaths = Logic.getAmountOfDeaths(data);

        // try applying debuffs
        List<Config.Debuff> debuffs = Config.debuffs;
        for (Config.Debuff debuff : debuffs) {
            if (debuff.enable && weightedDeaths >= debuff.minimumSeverity) {
                double amountTired = weightedDeaths / debuff.minimumSeverity;
                int scale = (int) Math.max(amountTired, debuff.maxScale);
                event.player.addPotionEffect(
                        new PotionEffect(debuff.potion.getId(), POTION_DURATION * 2, scale));
            }
        }

    }

    @SubscribeEvent
    public void onPlayerJoinServer(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player.worldObj.isRemote)
            return;

        PlayerData data = SavedData.instance().getData(event.player);
        data.onPlayerJoined(event.player.worldObj.getTotalWorldTime());
    }

    @SubscribeEvent
    public void onPlayerLeaveServer(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player.worldObj.isRemote)
            return;

        PlayerData data = SavedData.instance().getData(event.player);
        data.onPlayerLeft(event.player.worldObj.getTotalWorldTime());
    }
}
