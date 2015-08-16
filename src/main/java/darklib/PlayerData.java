package darklib;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.Iterator;

public class PlayerData {

    public static int TIME_TO_REMEMBER = 24000 * 7; // a week in MC

    public ArrayList<Death> deathTimes;

    public PlayerData() {
        deathTimes = new ArrayList<Death>();
    }

    public PlayerData(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    public void addDeath(long time, int severity) {
        deathTimes.add(new Death(time, severity));
    }

    // should be called periodically. Every few seconds? Prior to checking?
    public void cleanDeathTimes(long time) {
        Iterator<Death> iterator = deathTimes.iterator();
        while (iterator.hasNext()) {
            Death death = iterator.next();
            if (time - death.time > TIME_TO_REMEMBER) {
                iterator.remove();
            }
        }
    }

    public void readFromNBT(NBTTagCompound tag) {
        NBTTagList deaths = tag.getTagList("Deaths", 10);
        deathTimes = new ArrayList<Death>();
        for (int i = 0; i < deaths.tagCount(); i++) {
            NBTTagCompound compound = deaths.getCompoundTagAt(i);
            addDeath(compound.getLong("time"), compound.getInteger("severity"));
        }
    }

    public void writeToNBT(NBTTagCompound tag) {

    }

    public static class Death {
        public long time;
        public int  severity;

        public Death(long time, int severity) {
            this.time = time;
            this.severity = severity;
        }
    }
}
