package pl.crystalek.crcrandomcode.award.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.crystalek.crcrandomcode.award.IAward;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class ItemAward implements IAward {
    ItemStack[] awardItemList;

    @Override
    public void giveAward(final Player player) {
        player.getInventory().addItem(awardItemList);
    }
}
