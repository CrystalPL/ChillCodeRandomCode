package pl.chillcode.chillcoderandomcode;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillcoderandomcode.code.Code;
import pl.chillcode.chillcoderandomcode.command.CodeCommand;
import pl.chillcode.chillcoderandomcode.config.Config;
import pl.chillcode.chillcoderandomcode.hook.VaultHook;
import pl.crystalek.crcapi.command.CommandRegistry;
import pl.crystalek.crcapi.config.ConfigHelper;
import pl.crystalek.crcapi.message.MessageAPI;
import pl.crystalek.crcapi.singlemessage.SingleMessageAPI;

import java.io.IOException;

public final class ChillCodeRandomCode extends JavaPlugin {

    @Override
    public void onEnable() {
        final ConfigHelper configHelper = new ConfigHelper("config.yml", this);
        try {
            configHelper.checkExist();
            configHelper.load();
        } catch (final IOException exception) {
            getLogger().severe("Nie udało się utworzyć pliku konfiguracyjnego..");
            getLogger().severe("Wyłączanie pluginu..");
            Bukkit.getPluginManager().disablePlugin(this);
            exception.printStackTrace();
            return;
        }

        VaultHook.init();
        final Config config = new Config(configHelper.getConfiguration(), this);
        if (!config.load()) {
            getLogger().severe("Wyłączanie pluginu..");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final MessageAPI messageAPI = new SingleMessageAPI(this);
        if (!messageAPI.init()) {
            return;
        }

        final Code code = new Code(config, this, messageAPI);
        CommandRegistry.register(new CodeCommand(config, code, messageAPI));
        code.randomCode();
    }
}
