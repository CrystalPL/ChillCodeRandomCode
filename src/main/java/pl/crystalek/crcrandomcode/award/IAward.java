package pl.crystalek.crcrandomcode.award;

import org.bukkit.entity.Player;

public interface IAward {

    /**
     * Gives an award to a player
     *
     * @param player the player who will receive the award
     */
    void giveAward(final Player player);
}
