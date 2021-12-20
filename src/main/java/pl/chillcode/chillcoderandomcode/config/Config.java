package pl.chillcode.chillcoderandomcode.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillcoderandomcode.award.AwardType;
import pl.chillcode.chillcoderandomcode.award.IAward;
import pl.chillcode.chillcoderandomcode.award.impl.ItemAward;
import pl.chillcode.chillcoderandomcode.award.impl.MoneyAward;
import pl.chillcode.chillcoderandomcode.hook.VaultHook;
import pl.crystalek.crcapi.config.ConfigParserUtil;
import pl.crystalek.crcapi.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.util.NumberUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Config {
    final FileConfiguration config;
    final JavaPlugin plugin;
    String codeCommandName;
    List<String> codeCommandAliases;
    String timeDelimiter;
    boolean shortFormTime;
    int codeInRow;
    int codeDelayTime;
    int codeExpiredTime;
    List<Character> characterList;
    int codeLength;
    List<Integer> codeNotifySecondList;
    AwardType awardType;
    IAward award;

    public boolean load() {
        this.codeCommandName = config.getString("command.code.name");
        this.codeCommandAliases = Arrays.asList(config.getString("command.code.aliases").split(", "));
        this.timeDelimiter = config.getString("timeDelimiter");
        this.shortFormTime = config.getBoolean("shortFormTime");

        final Optional<Integer> codeInRowOptional = NumberUtil.getInt(config.get("codeInRow"));
        if (!codeInRowOptional.isPresent()) {
            plugin.getLogger().severe("Ilość powtórzeń przepisania kodu z rzędu musi być liczbą całkowitą!");
            return false;
        }
        this.codeInRow = codeInRowOptional.get();

        final Optional<Integer> codeDelayTimeOptional = NumberUtil.getInt(config.get("codeDelayTime"));
        if (!codeDelayTimeOptional.isPresent()) {
            plugin.getLogger().severe("Czas następnych kodów musi być liczbą całkowitą!");
            return false;
        }
        this.codeDelayTime = codeDelayTimeOptional.get();

        final Optional<Integer> codeExpiredTimeOptional = NumberUtil.getInt(config.get("codeExpiredTime"));
        if (!codeExpiredTimeOptional.isPresent()) {
            plugin.getLogger().severe("Czas wygasania kodu musi być liczbą całkowitą!");
            return false;
        }
        this.codeExpiredTime = codeExpiredTimeOptional.get();

        this.characterList = config.getString("charList").chars().mapToObj(c -> (char) c).collect(Collectors.toList());

        final Optional<Integer> codeLengthOptional = NumberUtil.getInt(config.get("codeLength"));
        if (!codeLengthOptional.isPresent()) {
            plugin.getLogger().severe("Długość kodu musi być liczbą całkowitą!");
            return false;
        }
        this.codeLength = codeLengthOptional.get();

        this.codeNotifySecondList = config.getIntegerList("codeNotifySeconds");

        try {
            this.awardType = AwardType.valueOf(config.getString("giveType").toUpperCase());
        } catch (final IllegalArgumentException exception) {
            plugin.getLogger().severe("Nie odnaleziono nagrody typu: " + config.getString("giveType"));
            return false;
        }

        switch (awardType) {
            case MONEY:
                if (!VaultHook.isEnableVault()) {
                    throw new IllegalStateException("vault not found");
                }

                final Optional<Double> moneyAwardOptional = NumberUtil.getDouble(config.get("giveMoney"));
                if (!moneyAwardOptional.isPresent()) {
                    plugin.getLogger().severe("Ilość pieniędzy musi być liczbą!");
                    return false;
                }

                this.award = new MoneyAward(moneyAwardOptional.get());
                break;
            case ITEM:
                final ConfigurationSection awardItemConfigurationSection = config.getConfigurationSection("giveItem");
                final List<String> itemSectionNumberList = new ArrayList<>(awardItemConfigurationSection.getKeys(false));
                final ItemStack[] awardItemList = new ItemStack[itemSectionNumberList.size()];

                for (int i = 0; i < itemSectionNumberList.size(); i++) {
                    try {
                        awardItemList[i] = ConfigParserUtil.getItem(awardItemConfigurationSection.getConfigurationSection(itemSectionNumberList.get(i)));
                    } catch (final ConfigLoadException exception) {
                        plugin.getLogger().severe("Wystąpił błąd podczas ładowanie itemy w sekcji: " + itemSectionNumberList.get(i));
                        plugin.getLogger().severe(exception.getMessage());
                        return false;
                    }
                }

                this.award = new ItemAward(awardItemList);
                break;
        }

        return true;
    }
}
