package pl.crystalek.crcrandomcode;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.crystalek.crcapi.command.CommandRegistry;
import pl.crystalek.crcapi.core.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.message.api.MessageAPI;
import pl.crystalek.crcapi.message.api.MessageAPIProvider;
import pl.crystalek.crcrandomcode.code.Code;
import pl.crystalek.crcrandomcode.command.CodeCommand;
import pl.crystalek.crcrandomcode.config.Config;
import pl.crystalek.crcrandomcode.hook.VaultHook;

import java.io.IOException;

public final class CrCRandomCode extends JavaPlugin {

    @Override
    public void onEnable() {
        final MessageAPI messageAPI = Bukkit.getServicesManager().getRegistration(MessageAPIProvider.class).getProvider().getSingleMessage(this);
        if (!messageAPI.init()) {
            return;
        }

        final Config config = new Config(this, "config.yml", new VaultHook(this).init());
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

        final Code code = new Code(config, this, messageAPI);
        CommandRegistry.register(new CodeCommand(messageAPI, config.getCommandDataMap(), code));
        code.randomCode();
    }
}
