package mod.timersofdeath;

import darklib.PlayerData;

public class Logic {
    public static int getAmountOfDeaths(PlayerData data) {
        int deathAmount = 0;
        for (PlayerData.Death death : data.deathTimes) {
            deathAmount += death.severity;
        }

        return deathAmount;
    }
}
