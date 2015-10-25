package darklib;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class PlayerData {

   public static int TIME_TO_REMEMBER = 24000 * 7; // a week in MC

   public ArrayList<Death>          deathTimes;
   public ArrayList<RecordedOnline> onlines;

   public PlayerData() {
      deathTimes = new ArrayList<Death>();
   }

   public PlayerData(NBTTagCompound tag) {
      readFromNBT(tag);
   }

   public static void closeExisting(long serverTime) {
      HashMap<UUID, PlayerData> map = SavedData.instance().getAllData();
      for (PlayerData data : map.values()) {
         for (RecordedOnline online : data.onlines)
            if (online.end == -1)
               online.end = serverTime;
      }
   }

   public void addDeath(long time, int severity) {
      deathTimes.add(new Death(time, severity));
      SavedData.instance().markDirty();
   }

   public void addDeath(Death death) {
      deathTimes.add(death);
      SavedData.instance().markDirty();
   }

   public void onPlayerJoined(long time) {
      RecordedOnline online = new RecordedOnline(time);
      if (onlines == null)
         onlines = new ArrayList<RecordedOnline>();

      onlines.add(online);
   }

   public void onPlayerLeft(long time) {
      if (onlines == null || onlines.size() == 0) {
         return;
      }

      RecordedOnline online = onlines.get(onlines.size() - 1);
      if (online.end != -1) {
         return;
      }

      online.end = time;
   }

   // should be called periodically. Every few seconds? Prior to checking?
   public void cleanDeathTimes(long time) {
      // 1st step - remove post-TIME_TO_REMEMBER items

      if (onlines == null || onlines.size() == 0)
         return; // wat

      onlines.get(onlines.size() - 1).end = time; // update the current time

      int timeSum = 0;
      int i = onlines.size() - 1; // kinky, but messing with reverse iterators is bleh
      for (; i >= 0; i--) {
         timeSum += onlines.get(i).end - onlines.get(i).begin;
         if (timeSum > TIME_TO_REMEMBER) {
            onlines.get(i).begin += (timeSum - TIME_TO_REMEMBER); // pad the last online time
            break;
         }
      }
      // remove the previous unused times
      for (--i; i >= 0; i--) {
         onlines.remove(i);
      }

      // iterate through death times to find which ones are not in the time anymore
      Iterator<Death> iterator = deathTimes.iterator();
      while (iterator.hasNext()) {
         Death death = iterator.next();
         boolean found = false;
         for (RecordedOnline online : onlines) {
            if (online.isTimeInRange(death.time))
               found = true;
         }
         if (!found)
            iterator.remove();
      }

      SavedData.instance().markDirty();
   }

   public void readFromNBT(NBTTagCompound tag) {
      NBTTagList deaths = tag.getTagList("Deaths", 10);
      NBTTagList seenOnline = tag.getTagList("onlines", 10);

      deathTimes = new ArrayList<Death>();
      onlines = new ArrayList<RecordedOnline>();

      for (int i = 0; i < deaths.tagCount(); i++) {
         deathTimes.add(new Death(deaths.getCompoundTagAt(i)));
      }

      for (int i = 0; i < seenOnline.tagCount(); i++) {
         onlines.add(new RecordedOnline(seenOnline.getCompoundTagAt(i)));
      }

   }

   public void writeToNBT(NBTTagCompound tag) {
      NBTTagList deaths = new NBTTagList(), seenOnline = new NBTTagList();
      for (Death death : deathTimes)
         deaths.appendTag(death.writeToNBT(new NBTTagCompound()));
      for (RecordedOnline online : onlines)
         seenOnline.appendTag(online.writeToNBT(new NBTTagCompound()));

      tag.setTag("Deaths", deaths);
      tag.setTag("onlines", seenOnline);
   }

   public static class Death {
      public long time;
      public int  severity;

      public Death(long time, int severity) {
         this.time = time;
         this.severity = severity;
      }

      public Death(NBTTagCompound compound) {
         time = compound.getLong("time");
         severity = compound.getInteger("severity");
      }

      public NBTTagCompound writeToNBT(NBTTagCompound compound) {
         compound.setLong("time", time);
         compound.setInteger("severity", severity);

         return compound;
      }
   }

   public static class RecordedOnline {
      public long begin;
      public long end; // -1 for "now"

      public RecordedOnline(long b) {
         begin = b;
         end = -1;
      }

      public RecordedOnline(long b, long e) {
         begin = b;
         end = e;
      }

      public RecordedOnline(NBTTagCompound compound) {
         begin = compound.getLong("begin");
         end = compound.getLong("end");
      }

      public boolean isTimeInRange(long time) {
         if (end != -1)
            return time >= begin && time <= end;

         return time >= begin;
      }

      public NBTTagCompound writeToNBT(NBTTagCompound compound) {
         compound.setLong("begin", begin);
         compound.setLong("end", end);
         return compound;
      }
   }
}
