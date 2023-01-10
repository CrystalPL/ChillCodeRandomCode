package pl.crystalek.crcrandomcode.task;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.scheduler.BukkitRunnable;
import pl.crystalek.crcapi.core.time.TimeUtil;
import pl.crystalek.crcapi.message.api.MessageAPI;
import pl.crystalek.crcrandomcode.code.Code;
import pl.crystalek.crcrandomcode.config.Config;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class CodeNotifyTask extends BukkitRunnable {
    Config config;
    MessageAPI messageAPI;
    Code code;
    long eventEndTime;

    @Override
    public void run() {
        final List<Integer> codeNotifySecondList = config.getCodeNotifySecondList();

        for (final Integer timeToNextNotify : codeNotifySecondList) {
            final long time = (eventEndTime - timeToNextNotify * 1000L) - System.currentTimeMillis();
            if (time >= 0 && time <= 1000) {
                messageAPI.broadcast("codeNotify", ImmutableMap.of("{TIME}", TimeUtil.getDateInString(timeToNextNotify * 1000L, config.getTimeDelimiter(), config.isShortFormTime())));
                break;
            }
        }

        final long time = eventEndTime - System.currentTimeMillis();
        if (time >= 0 && time <= 1000) {
            code.generateCode();
        }
    }
}
