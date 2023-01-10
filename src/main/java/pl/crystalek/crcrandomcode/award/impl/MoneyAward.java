package pl.crystalek.crcrandomcode.award.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import pl.crystalek.crcrandomcode.award.IAward;
import pl.crystalek.crcrandomcode.hook.VaultHook;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class MoneyAward implements IAward {
    VaultHook vaultHook;
    double awardMoney;

    @Override
    public void giveAward(final Player player) {
        vaultHook.getEconomy().depositPlayer(player, awardMoney);
    }
}
