package pl.chillcode.chillcoderandomcode.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillcoderandomcode.award.AwardType;
import pl.chillcode.chillcoderandomcode.award.IAward;
import pl.chillcode.chillcoderandomcode.award.impl.ItemAward;
import pl.chillcode.chillcoderandomcode.award.impl.MoneyAward;
import pl.chillcode.chillcoderandomcode.hook.VaultHook;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.loader.CommandLoader;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.core.config.ConfigHelper;
import pl.crystalek.crcapi.core.config.ConfigParserUtil;
import pl.crystalek.crcapi.core.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.core.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Config extends ConfigHelper {
    Map<Class<? extends Command>, CommandData> commandDataMap;
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

    public Config(final JavaPlugin plugin, final String fileName) {
        super(plugin, fileName);
    }

    public void loadConfig() throws ConfigLoadException {
        this.commandDataMap = CommandLoader.loadCommands(configuration.getConfigurationSection("command"), plugin.getClass().getClassLoader());
        this.timeDelimiter = ConfigParserUtil.getString(configuration, "timeDelimiter");
        this.shortFormTime = ConfigParserUtil.getBoolean(configuration, "shortFormTime");
        this.codeInRow = ConfigParserUtil.getInt(configuration, "codeInRow");
        this.codeDelayTime = ConfigParserUtil.getInt(configuration, "codeDelayTime");
        this.codeExpiredTime = ConfigParserUtil.getInt(configuration, "codeExpiredTime");
        this.characterList = ConfigParserUtil.getString(configuration, "charList").chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        this.codeLength = ConfigParserUtil.getInt(configuration, "codeLength");
        this.codeNotifySecondList = loadCodeNotifySecondList();
        this.awardType = loadAwardType();
        this.award = loadAward();
    }

    private List<Integer> loadCodeNotifySecondList() throws ConfigLoadException {
        final List<Integer> codeNotifySecondList = new ArrayList<>();

        for (final String codeNotifySecond : configuration.getStringList("codeNotifySeconds")) {
            final Optional<Integer> numberOptional = NumberUtil.getInt(codeNotifySecond);
            if (!numberOptional.isPresent()) {
                throw new ConfigLoadException("Wartości pola codeNotifySeconds nie są liczbami!");
            }

            codeNotifySecondList.add(numberOptional.get());
        }

        return codeNotifySecondList;
    }

    private AwardType loadAwardType() throws ConfigLoadException {
        final String awardType = ConfigParserUtil.getString(configuration, "awardType");
        try {
            return AwardType.valueOf(awardType.toUpperCase());
        } catch (final IllegalArgumentException exception) {
            throw new ConfigLoadException("Nie odnaleziono nagrody typu: " + awardType);
        }
    }

    private IAward loadAward() throws ConfigLoadException {
        switch (awardType) {
            case MONEY:
                if (!VaultHook.isEnableVault()) {
                    throw new IllegalStateException("vault not found");
                }

                return new MoneyAward(ConfigParserUtil.getDouble(configuration, "awardMoney"));
            case ITEM:
                final ConfigurationSection awardItemConfigurationSection = configuration.getConfigurationSection("giveItem");
                final List<String> itemSectionNumberList = new ArrayList<>(awardItemConfigurationSection.getKeys(false));
                final ItemStack[] awardItemList = new ItemStack[itemSectionNumberList.size()];

                for (int i = 0; i < itemSectionNumberList.size(); i++) {
                    awardItemList[i] = ConfigParserUtil.getItem(awardItemConfigurationSection.getConfigurationSection(itemSectionNumberList.get(i)));
                }

                return new ItemAward(awardItemList);
            default:
                throw new IllegalStateException("Unexpected value: " + awardType);
        }
    }
}
