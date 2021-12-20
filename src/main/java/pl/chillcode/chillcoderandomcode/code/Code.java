package pl.chillcode.chillcoderandomcode.code;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillcoderandomcode.config.Config;
import pl.chillcode.chillcoderandomcode.task.EventNotifyTask;
import pl.crystalek.crcapi.message.MessageAPI;
import pl.crystalek.crcapi.time.TimeUtil;

import java.util.*;
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
    boolean codeExpired;
    int expiredTaskId;
    int notifyTaskId;

    public void testCode(final Player player, final String code) throws IllegalStateException {
        if (code.equals(randomCode)) {
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

            Bukkit.getScheduler().cancelTask(expiredTaskId);
            randomCode();
            return;
        }

        messageAPI.sendMessage("incorrectCode", player);
    }

    public void randomCode() {
        notifyTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new EventNotifyTask(config, messageAPI, this, System.currentTimeMillis() + config.getCodeDelayTime() * 1000L), 0, 20).getTaskId();
    }

    public void generateCode() {
        List<Character> characterList = new ArrayList<>(config.getCharacterList());
        Collections.shuffle(characterList);
        characterList = characterList.subList(0, config.getCodeLength());

        randomCode = characterList.stream().map(String::valueOf).collect(Collectors.joining());
        codeStartTime = System.currentTimeMillis();

        messageAPI.broadcast("sendCode", ImmutableMap.of("{CODE}", randomCode));
        Bukkit.getScheduler().cancelTask(notifyTaskId);
        codeExpired = false;

        expiredTaskId = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            codeExpired = true;
            messageAPI.broadcast("codeExpiredBroadcast");
            randomCode();
        }, config.getCodeExpiredTime() * 20L).getTaskId();
    }
}
