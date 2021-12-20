package pl.chillcode.chillcoderandomcode.award.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import pl.chillcode.chillcoderandomcode.award.IAward;
import pl.chillcode.chillcoderandomcode.hook.VaultHook;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class MoneyAward implements IAward {
    double awardMoney;

    @Override
    public void giveAward(final Player player) {
        VaultHook.getEconomy().depositPlayer(player, awardMoney);
    }
}
