package mod.timersofdeath;

import darklib.PlayerData;
import darklib.SavedData;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class CommandClearDeaths implements ICommand {
   @Override
   public String getCommandName() {
      return "cleardeaths";
   }

   @Override
   public String getCommandUsage(ICommandSender p_71518_1_) {
      return "cleardeaths <player>";
   }

   @Override
   public List getCommandAliases() {
      return null;
   }

   @Override
   public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {
      if (p_71515_2_.length < 1)
         return;
      String name = p_71515_2_[0];

      EntityPlayer player = Logic.getPlayer(name);
      if (player == null)
         return;

      PlayerData data = SavedData.instance().getData(player);
      data.deathTimes = new ArrayList<PlayerData.Death>();
      data.onlines = new ArrayList<PlayerData.RecordedOnline>(); // should clear it ;o
      SavedData.instance().markDirty();
   }

   @Override
   public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
      return p_71519_1_ instanceof CommandBlockLogic || (p_71519_1_ instanceof EntityPlayer && ((EntityPlayer) p_71519_1_).capabilities.isCreativeMode);
   }

   @Override
   public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
      return null;
   }

   @Override
   public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
      return false;
   }

   @Override
   public int compareTo(Object o) {
      return 0;
   }
}
