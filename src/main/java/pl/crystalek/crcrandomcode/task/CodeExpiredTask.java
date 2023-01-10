package pl.crystalek.crcrandomcode.task;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.scheduler.BukkitRunnable;
import pl.crystalek.crcapi.message.api.MessageAPI;
import pl.crystalek.crcrandomcode.code.Code;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class CodeExpiredTask extends BukkitRunnable {
    MessageAPI messageAPI;
    Code code;

    @Override
    public void run() {
        code.setCodeExpired(true);
        messageAPI.broadcast("codeExpiredBroadcast");
        code.randomCode();
    }
}
