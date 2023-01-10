package pl.crystalek.crcrandomcode.command;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.impl.SingleCommand;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.message.api.MessageAPI;
import pl.crystalek.crcrandomcode.code.Code;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class CodeCommand extends SingleCommand {
    Code code;

    public CodeCommand(final MessageAPI messageAPI, final Map<Class<? extends Command>, CommandData> commandDataMap, final Code code) {
        super(messageAPI, commandDataMap);

        this.code = code;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (code.isCodeExpired()) {
            messageAPI.sendMessage("codeExpired", sender);
            return;
        }

        if (code.getRandomCode() == null) {
            messageAPI.sendMessage("codeNoExist", sender);
            return;
        }

        code.testCode((Player) sender, args[0]);
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public boolean isUseConsole() {
        return false;
    }

    @Override
    public String getCommandUsagePath() {
        return "commandUsage";
    }

    @Override
    public int maxArgumentLength() {
        return 1;
    }

    @Override
    public int minArgumentLength() {
        return 1;
    }
}
