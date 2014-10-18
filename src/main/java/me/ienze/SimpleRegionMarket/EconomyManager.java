package me.ienze.SimpleRegionMarket;

import java.util.logging.Level;
import me.ienze.SimpleRegionMarket.handlers.LangHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {

    private final SimpleRegionMarket plugin;
    private int enableEconomy;
    private Economy economy;

    public EconomyManager(SimpleRegionMarket plugin) {
        this.plugin = plugin;
    }

    public void setupEconomy() {
        final Server server = plugin.getServer();
        enableEconomy = SimpleRegionMarket.configurationHandler.getBoolean("Enable_Economy") ? 1 : 0;
        if (enableEconomy > 0) {
            if (server.getPluginManager().getPlugin("Register") == null && server.getPluginManager().getPlugin("Vault") == null) {
                LangHandler.directOut(Level.WARNING, "MAIN.WARN.NO_ECO_API");
                enableEconomy = 0;
            } else {
                enableEconomy = 2;
                if (!setupVaultEconomy()) {
                    LangHandler.directOut(Level.WARNING, "MAIN.WARN.VAULT_NO_ECO");
                    enableEconomy = 0;
                }
            }
        }
    }

    private Boolean setupVaultEconomy() {
        final RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return economy != null;
    }


    public boolean isEconomy() {
        return enableEconomy > 1 || (enableEconomy == 1);
    }

    public boolean econGiveMoney(OfflinePlayer account, double money) {
        if (money == 0) {
            LangHandler.directOut(Level.FINEST, "[EconomyManager] Money is zero");
            return true;
        }
        try {
            if (enableEconomy == 2) {
                if (money > 0) {
                    LangHandler.directOut(Level.FINEST, "[EconomyManager - Register] Adding " + String.valueOf(money) + " to Account " + account);
                    economy.depositPlayer(account, money);
                } else {
                    LangHandler.directOut(Level.FINEST, "[EconomyManager] Money is zero");
                    economy.withdrawPlayer(account, -money);
                }
            }
        } catch (final Exception e) {
            return false;
        }
        return true;
    }

    public boolean econHasEnough(OfflinePlayer account, double money) {
        boolean ret = false;
        if (money == 0) {
            return true;
        }
        if (enableEconomy == 2) {
            ret = economy.has(account, money);
        }
        return ret;
    }

    public String econFormat(double price) {
        String ret = null;
        if (enableEconomy == 2) {
            ret = economy.format(price);
        }
        if (ret == null) {
            ret = String.valueOf(price);
        }
        return ret;
    }

    public boolean moneyTransaction(OfflinePlayer from, OfflinePlayer to, double money) {
        try {
            if (to == null) {
                if (econHasEnough(from, money)) {
                    econGiveMoney(from, -money);
                    return true;
                } else {
                    LangHandler.ErrorOut(from.getPlayer(), "PLAYER.ERROR.NO_MONEY", null);
                    return false;
                }
            } else if (from == null) {
                econGiveMoney(to, money);
                return true;
            } else {
                if (econHasEnough(from, money)) {
                    econGiveMoney(from, -money);
                    econGiveMoney(to, money);
                    return true;
                } else {
                    LangHandler.ErrorOut(from.getPlayer(), "PLAYER.ERROR.NO_MONEY", null);
                    return false;
                }
            }
        } catch (final Exception e) {
        }
        if (from.getPlayer() != null) {
            LangHandler.ErrorOut(from.getPlayer(), "PLAYER.ERROR.ECO_PROBLEM", null);
        }
        return false;
    }
}
