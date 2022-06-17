package pl.chillcode.chillcoderandomcode.task;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.chillcode.chillcoderandomcode.code.Code;
import pl.chillcode.chillcoderandomcode.config.Config;
import pl.crystalek.crcapi.core.time.TimeUtil;
import pl.crystalek.crcapi.message.api.MessageAPI;

import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class EventNotifyTask implements Runnable {
    final Config config;
    final MessageAPI messageAPI;
    final Code code;
    final long eventEndTime;

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
