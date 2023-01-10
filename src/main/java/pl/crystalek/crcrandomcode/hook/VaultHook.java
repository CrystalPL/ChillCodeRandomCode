package pl.crystalek.crcrandomcode.hook;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class VaultHook {
    final JavaPlugin plugin;
    Economy economy;
    boolean enableVault;

    public VaultHook init() {
        try {
            final RegisteredServiceProvider<Economy> registration = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (registration == null) {
                enableVault = false;
                throw new ClassNotFoundException();
            }

            economy = registration.getProvider();
            enableVault = true;

            plugin.getLogger().info("[ChillCodeRandomCode] Vault został poprawnie załadowany");
        } catch (final NoClassDefFoundError | ClassNotFoundException exception) {
            plugin.getLogger().warning("[ChillCodeRandomCode] Nie odnaleziono plugin vault, dawanie nagród pieniędzmi jest niemożliwe!");
        }

        return this;
    }
}