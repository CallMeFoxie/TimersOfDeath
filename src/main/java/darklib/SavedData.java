package darklib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SavedData extends WorldSavedData {

    public static  SavedData                 instance;
    private static HashMap<UUID, PlayerData> savedData;

    public SavedData(String filename) {
        super(filename);
        savedData = new HashMap<UUID, PlayerData>();
        instance = this;
    }

    public static UUID getUUID(EntityPlayer player) {
        return player.getUniqueID();
    }

    public static SavedData instance() {
        return instance;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        NBTTagList list = tag.getTagList("PlayerData", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound playerData = list.getCompoundTagAt(i);
            UUID uuid = new UUID(playerData.getLong("UUIDMost"), playerData.getLong("UUIDLeast"));
            PlayerData data = new PlayerData(playerData);
            savedData.put(uuid, data);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<UUID, PlayerData> entry : savedData.entrySet()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setLong("UUIDMost", entry.getKey().getMostSignificantBits());
            compound.setLong("UUIDLeast", entry.getKey().getLeastSignificantBits());
            entry.getValue().writeToNBT(compound);
            list.appendTag(compound);
        }

        tag.setTag("PlayerData", list);
    }

    public PlayerData getPlayerData(UUID uuid) {
        PlayerData data = savedData.get(uuid);
        if (data == null) {
            data = new PlayerData();
            savedData.put(uuid, data);
        }

        return data;
    }

    public HashMap<UUID, PlayerData> getAllData() {
        return savedData;
    }

    public PlayerData getData(EntityPlayer player) {
        return getPlayerData(getUUID(player));
    }
}