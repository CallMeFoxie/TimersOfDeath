package mod.timersofdeath;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import darklib.PlayerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.List;

public class Events {

    public static final int POTION_DURATION  = 45;
    // TODO configure this
    public static       int SEVERITY_DEFAULT = 1;
    // TODO configure this
    public static       int SEVERITY_DROWN   = 4;
    // TODO configure this
    public static       int SEVERITY_HUNGER  = 4;
    public static Events INSTANCE;

    public Events() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (event.entity.worldObj.isRemote)
            return;

        if (event.entity instanceof EntityPlayer) {
            PlayerData data = TimersOfDeath.INSTANCE.playerData.getData((EntityPlayer) event.entity);
            data.addDeath(event.entity.worldObj.getTotalWorldTime(), SEVERITY_DEFAULT);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.worldObj.getTotalWorldTime() % 40 != 0) // once every 2 seconds
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
                        new PotionEffect(debuff.potion.getId(), POTION_DURATION, scale));
            }
        }

    }
}
