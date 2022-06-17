package pl.chillcode.chillcoderandomcode;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillcoderandomcode.code.Code;
import pl.chillcode.chillcoderandomcode.command.CodeCommand;
import pl.chillcode.chillcoderandomcode.config.Config;
import pl.chillcode.chillcoderandomcode.hook.VaultHook;
import pl.crystalek.crcapi.command.CommandRegistry;
import pl.crystalek.crcapi.core.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.message.api.MessageAPI;
import pl.crystalek.crcapi.message.api.MessageAPIProvider;

import java.io.IOException;

public final class ChillCodeRandomCode extends JavaPlugin {

    @Override
    public void onEnable() {
        final MessageAPI messageAPI = Bukkit.getServicesManager().getRegistration(MessageAPIProvider.class).getProvider().getSingleMessage(this);
        if (!messageAPI.init()) {
            return;
        }

        final Config config = new Config(this, "config.yml");
        try {
            config.checkExist();
            config.load();
        } catch (final IOException exception) {
            getLogger().severe("Nie udało się utworzyć pliku konfiguracyjnego..");
            getLogger().severe("Wyłączanie pluginu..");
            Bukkit.getPluginManager().disablePlugin(this);
            exception.printStackTrace();
            return;
        }

        try {
            config.loadConfig();
        } catch (final ConfigLoadException exception) {
            getLogger().severe(exception.getMessage());
            getLogger().severe("Wyłączanie pluginu..");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        VaultHook.init();

        final Code code = new Code(config, this, messageAPI);
        CommandRegistry.register(new CodeCommand(messageAPI, config.getCommandDataMap(), code));
        code.randomCode();
    }
}
