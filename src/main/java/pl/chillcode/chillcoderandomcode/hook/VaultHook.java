package pl.chillcode.chillcoderandomcode.hook;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

@UtilityClass
@Getter
public class VaultHook {
    @Getter
    private Economy economy;
    @Getter
    private boolean enableVault;

    public void init() {
        try {
            final RegisteredServiceProvider<Economy> registration = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (registration == null) {
                enableVault = false;
                throw new ClassNotFoundException();
            }

            economy = registration.getProvider();
            enableVault = true;

            Bukkit.getLogger().info("[ChillCodeRandomCode] Vault został poprawnie załadowany");
        } catch (final NoClassDefFoundError | ClassNotFoundException exception) {
            Bukkit.getLogger().warning("[ChillCodeRandomCode] Nie odnaleziono plugin vault, dawanie nagród pieniędzmi jest niemożliwe!");
        }
    }
}