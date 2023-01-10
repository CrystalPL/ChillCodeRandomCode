package pl.crystalek.crcrandomcode.code;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.crystalek.crcapi.core.time.TimeUtil;
import pl.crystalek.crcapi.message.api.MessageAPI;
import pl.crystalek.crcrandomcode.config.Config;
import pl.crystalek.crcrandomcode.task.CodeExpiredTask;
import pl.crystalek.crcrandomcode.task.CodeNotifyTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Code {
    final Map<UUID, Integer> useCodeMap = new HashMap<>();
    final Config config;
    final JavaPlugin plugin;
    final MessageAPI messageAPI;

    @Getter
    String randomCode;
    @Getter
    long codeStartTime;
    @Getter
    @Setter
    boolean codeExpired;
    CodeExpiredTask codeExpiredTask;
    CodeNotifyTask codeNotifyTask;

    public void testCode(final Player player, final String code) throws IllegalStateException {
        if (!code.equals(randomCode)) {
            messageAPI.sendMessage("incorrectCode", player);
            return;
        }

        final Integer codeInRow = useCodeMap.get(player.getUniqueId());
        if (codeInRow != null && codeInRow + 1 > config.getCodeInRow()) {
            messageAPI.sendMessage("codeInRow", player);
            return;
        }

        randomCode = null;
        config.getAward().giveAward(player);
        useCodeMap.clear();
        useCodeMap.put(player.getUniqueId(), codeInRow == null ? 1 : codeInRow + 1);

        final Map<String, Object> replacements = ImmutableMap.of(
                "{REWRITE_TIME}", TimeUtil.getDateInString(System.currentTimeMillis() - codeStartTime, config.getTimeDelimiter(), config.isShortFormTime()),
                "{PLAYER_NAME}", player.getName(),
                "{CODE_TIME}", TimeUtil.getDateInString(config.getCodeDelayTime() * 1000L, config.getTimeDelimiter(), config.isShortFormTime())
        );

        messageAPI.sendMessage("correctCode", player, replacements);
        messageAPI.broadcast("correctCodeBroadcast", replacements);

        codeExpiredTask.cancel();
        randomCode();
    }

    public void randomCode() {
        codeNotifyTask = new CodeNotifyTask(config, messageAPI, this, System.currentTimeMillis() + config.getCodeDelayTime() * 1000L);
        codeNotifyTask.runTaskTimerAsynchronously(plugin, 0, 20);
    }

    public void generateCode() {
        List<Character> characterList = new ArrayList<>(config.getCharacterList());
        Collections.shuffle(characterList);
        characterList = characterList.subList(0, config.getCodeLength());

        randomCode = characterList.stream().map(String::valueOf).collect(Collectors.joining());
        codeStartTime = System.currentTimeMillis();

        messageAPI.broadcast("sendCode", ImmutableMap.of("{CODE}", randomCode));
        codeNotifyTask.cancel();
        codeExpired = false;

        codeExpiredTask = new CodeExpiredTask(messageAPI, this);
        codeExpiredTask.runTaskLaterAsynchronously(plugin, config.getCodeExpiredTime() * 20L);
    }
}
