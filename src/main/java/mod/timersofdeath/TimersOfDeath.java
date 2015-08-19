package mod.timersofdeath;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.relauncher.Side;
import darklib.PlayerData;
import darklib.SavedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = TimersOfDeath.MODID, name = TimersOfDeath.MODNAME, acceptableRemoteVersions = "*", version = TimersOfDeath.VERSION)
public class TimersOfDeath {
    public static final String MODNAME = "Timers Of Death";
    public static final String MODID   = "timersofdeath";
    public static final String VERSION = "@VERSION@";

    @Mod.Instance(MODID)
    public static TimersOfDeath INSTANCE;

    SavedData playerData;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        new Config(event.getSuggestedConfigurationFile().getAbsolutePath());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        new Events();
        MinecraftForge.EVENT_BUS.register(Events.INSTANCE);
        FMLCommonHandler.instance().bus().register(Events.INSTANCE);

    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            return;

        World world = MinecraftServer.getServer().worldServers[0];

        playerData = (SavedData) world.loadItemData(SavedData.class, MODID);
        if (playerData == null) {
            playerData = new SavedData(MODID);
            world.setItemData(MODID, playerData);
            PlayerData.closeExisting(world.getTotalWorldTime());
        }
    }
}
