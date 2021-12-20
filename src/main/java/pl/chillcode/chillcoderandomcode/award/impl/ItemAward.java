package pl.chillcode.chillcoderandomcode.award.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.chillcode.chillcoderandomcode.award.IAward;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class ItemAward implements IAward {
    ItemStack[] awardItemList;

    @Override
    public void giveAward(final Player player) {
        player.getInventory().addItem(awardItemList);
    }
}
