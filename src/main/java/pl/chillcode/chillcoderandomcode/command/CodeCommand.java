package pl.chillcode.chillcoderandomcode.command;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.chillcode.chillcoderandomcode.code.Code;
import pl.chillcode.chillcoderandomcode.config.Config;
import pl.crystalek.crcapi.message.MessageAPI;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class CodeCommand extends Command {
    Code code;
    MessageAPI messageAPI;

    public CodeCommand(final Config config, final Code code, final MessageAPI messageAPI) {
        super(config.getCodeCommandName());
        setAliases(config.getCodeCommandAliases());

        this.code = code;
        this.messageAPI = messageAPI;
    }

    @Override
    public boolean execute(final CommandSender sender, final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            messageAPI.sendMessage("noConsole", sender);
            return true;
        }

        if (args.length != 1) {
            messageAPI.sendMessage("commandUsage", sender);
            return true;
        }

        if (code.isCodeExpired()) {
            messageAPI.sendMessage("codeExpired", sender);
            return true;
        }

        if (code.getRandomCode() == null) {
            messageAPI.sendMessage("codeNoExist", sender);
            return true;
        }

        code.testCode((Player) sender, args[0]);
        return true;
    }
}
