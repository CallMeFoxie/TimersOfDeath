package darklib;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.Iterator;

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
        onlines.add(online);
    }

    public void onPlayerLeft(long time) {
        if (onlines.size() == 0) {
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
        Iterator<Death> iterator = deathTimes.iterator();
        while (iterator.hasNext()) {
            Death death = iterator.next();
            if (time - death.time > TIME_TO_REMEMBER) {
                iterator.remove();
                SavedData.instance().markDirty();
            }
        }

        // now check and clear old times that are not used anymore
        Iterator<RecordedOnline> iterator1 = onlines.iterator();
        while (iterator1.hasNext()) {
            RecordedOnline online = iterator1.next();
            boolean found = false;
            for (Death death : deathTimes) {
                if (online.isTimeInRange(death.time)) {
                    found = true;
                    break;
                }
            }

            if (!found)
                iterator1.remove();
        }
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
        NBTTagList deaths = new NBTTagList();
        for (Death death : deathTimes) {
            deaths.appendTag(death.writeToNBT(new NBTTagCompound()));
        }
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
