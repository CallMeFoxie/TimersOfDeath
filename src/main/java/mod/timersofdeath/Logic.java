package mod.timersofdeath;

import darklib.PlayerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class Logic {
   public static int getAmountOfDeaths(PlayerData data) {
      int deathAmount = 0;
      for (PlayerData.Death death : data.deathTimes) {
         deathAmount += death.severity;
      }

      return deathAmount;
   }

   public static EntityPlayer getPlayer(String name) {
      List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
      for (EntityPlayerMP player : players) {
         if (player.getCommandSenderName().equals(name))
            return player;
      }

      return null;
   }
}
