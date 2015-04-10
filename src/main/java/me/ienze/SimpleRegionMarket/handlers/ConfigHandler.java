package me.ienze.SimpleRegionMarket.handlers;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ienze.SimpleRegionMarket.SimpleRegionMarket;
import me.ienze.SimpleRegionMarket.TokenManager;
import me.ienze.SimpleRegionMarket.Utils;
import me.ienze.SimpleRegionMarket.signs.TemplateHotel;
import me.ienze.SimpleRegionMarket.signs.TemplateMain;
import me.ienze.SimpleRegionMarket.signs.TemplateSell;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigHandler {

    private FileConfiguration config;
    private final SimpleRegionMarket plugin;

    /**
     * Instantiates a new config handler.
     *
     * @param plugin the plugin
     */
    public ConfigHandler(SimpleRegionMarket plugin) {

        this.plugin = plugin;
        config = plugin.getConfig();

        //if not exist copy
        if (!new File(SimpleRegionMarket.getPluginDir() + "config.yml").exists()) {
            plugin.saveResource("config.yml", false);
        } else {

            Boolean changed = false;
            Boolean rewrite = false;

            //remove unnecessary lines
            FileConfiguration resourcesConfig = new YamlConfiguration();
            try {
                resourcesConfig.load(plugin.getResource("config.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }

            Set<String> rk = resourcesConfig.getKeys(false);

            for (String ck : config.getKeys(false)) {
                if (!rk.contains(ck) && !ck.equals("UpdaterTokenNames")) {
                    config.set(ck, null);
                    changed = true;
                }
            }

            if (changed) {
                plugin.saveConfig();
            }

            Set<String> ckey = config.getKeys(false);

            //add lost lines
            for (String rkey : rk) {
                if (!ckey.contains(rkey)) {
                    rewrite = true;
                }
            }

            if (rewrite) {
                plugin.saveResource("config.yml", true);
            }

            //Convert Files ot UUID format
            if (config.getString("version").equals("4.0.0")){

                //Convert Statistics

                if (new File(SimpleRegionMarket.getPluginDir() + "statistics.yml").exists()) {
                    FileConfiguration statistics = new YamlConfiguration();
                    try {
                        statistics.load(SimpleRegionMarket.getPluginDir() + "statistics.yml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidConfigurationException e) {
                        e.printStackTrace();
                    }

                    Bukkit.getLogger().log(Level.INFO, "[SimpleRegionMarket] Converting statistics.yml");
                    for(String world : statistics.getKeys(false)){
                        for(String user : statistics.getConfigurationSection(world + ".users").getKeys(false)){
                            String uuid = Bukkit.getOfflinePlayer(user).getUniqueId().toString();
                            for(String token : statistics.getConfigurationSection(world + ".users." + user).getKeys(false)){
                                statistics.createSection(world + ".users." + uuid + "." + token);
                                for(String value : statistics.getConfigurationSection(world + ".users." + user + "." + token).getKeys(false)){
                                    statistics.set(world + ".users." + uuid + "." + token + "." + value, statistics.getInt(world + ".users." + user + "." + token + "." + value));
                                }
                            }
                            statistics.set(world + ".users." + user, null);
                            Bukkit.getLogger().log(Level.INFO,  user + " ==> " + uuid);
                        }
                    }
                    try {
                        statistics.save(SimpleRegionMarket.getPluginDir() + "statistics.yml");
                    } catch (IOException ex) {
                        Logger.getLogger(ConfigHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                //Convert sell

                if (new File(SimpleRegionMarket.getPluginDir() + "signs/sell.yml").exists()) {
                    FileConfiguration sell = new YamlConfiguration();
                    try {
                        sell.load(SimpleRegionMarket.getPluginDir() + "signs/sell.yml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidConfigurationException e) {
                        e.printStackTrace();
                    }

                    Bukkit.getLogger().log(Level.INFO, "[SimpleRegionMarket] Converting sell.yml");
                    for(String world : sell.getKeys(false)){
                        for(String sign : sell.getConfigurationSection(world).getKeys(false)){
                            String owner = sell.getString(world + "." + sign + ".owner");
                            String account = sell.getString(world + "." + sign + ".account");
                            if(owner!=null) {
                                sell.set(world + "." + sign + ".owner", Bukkit.getOfflinePlayer(owner).getUniqueId().toString());
                            }
                            sell.set(world + "." + sign + ".account", Bukkit.getOfflinePlayer(account).getUniqueId().toString());
                        }
                    }
                    try {
                        sell.save(SimpleRegionMarket.getPluginDir() + "signs/sell.yml");
                    } catch (IOException ex) {
                        Logger.getLogger(ConfigHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                //Convert rent

                if (new File(SimpleRegionMarket.getPluginDir() + "signs/rent.yml").exists()) {
                    FileConfiguration rent = new YamlConfiguration();
                    try {
                        rent.load(SimpleRegionMarket.getPluginDir() + "signs/rent.yml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidConfigurationException e) {
                        e.printStackTrace();
                    }

                    Bukkit.getLogger().log(Level.INFO, "[SimpleRegionMarket] Converting rent.yml");
                    for(String world : rent.getKeys(false)){
                        for(String sign : rent.getConfigurationSection(world).getKeys(false)){
                            String owner = rent.getString(world + "." + sign + ".owner");
                            String account = rent.getString(world + "." + sign + ".account");
                            if(owner != null) {
                                rent.set(world + "." + sign + ".owner", Bukkit.getOfflinePlayer(owner).getUniqueId().toString());
                            }
                            rent.set(world + "." + sign + ".account", Bukkit.getOfflinePlayer(account).getUniqueId().toString());
                        }
                    }
                    try {
                        rent.save(SimpleRegionMarket.getPluginDir() + "signs/rent.yml");
                    } catch (IOException ex) {
                        Logger.getLogger(ConfigHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                //Convert let

                if (new File(SimpleRegionMarket.getPluginDir() + "signs/let.yml").exists()) {
                    FileConfiguration let = new YamlConfiguration();
                    try {
                        let.load(SimpleRegionMarket.getPluginDir() + "signs/let.yml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidConfigurationException e) {
                        e.printStackTrace();
                    }

                    Bukkit.getLogger().log(Level.INFO, "[SimpleRegionMarket] Converting let.yml");
                    for(String world : let.getKeys(false)){
                        for(String sign : let.getConfigurationSection(world).getKeys(false)){
                            String owner = let.getString(world + "." + sign + ".owner");
                            String account = let.getString(world + "." + sign + ".account");
                            if(owner != null){
                                let.set(world + "." + sign + ".owner", Bukkit.getOfflinePlayer(owner).getUniqueId().toString());
                            }
                            let.set(world + "." + sign + ".account", Bukkit.getOfflinePlayer(account).getUniqueId().toString());
                        }
                    }
                    try {
                        let.save(SimpleRegionMarket.getPluginDir() + "signs/let.yml");
                    } catch (IOException ex) {
                        Logger.getLogger(ConfigHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                //Convert bid

                if (new File(SimpleRegionMarket.getPluginDir() + "signs/bid.yml").exists()) {
                    FileConfiguration bid = new YamlConfiguration();
                    try {
                        bid.load(SimpleRegionMarket.getPluginDir() + "signs/bid.yml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidConfigurationException e) {
                        e.printStackTrace();
                    }

                    Bukkit.getLogger().log(Level.INFO, "[SimpleRegionMarket] Converting bid.yml");
                    for(String world : bid.getKeys(false)){
                        for(String sign : bid.getConfigurationSection(world).getKeys(false)){
                            String owner = bid.getString(world + "." + sign + ".owner");
                            String account = bid.getString(world + "." + sign + ".account");
                            if (owner != null) {
                                bid.set(world + "." + sign + ".owner", Bukkit.getOfflinePlayer(owner).getUniqueId().toString());
                            }
                            bid.set(world + "." + sign + ".account", Bukkit.getOfflinePlayer(account).getUniqueId().toString());
                            for(String user : bid.getConfigurationSection(world + "." + sign + ".user").getKeys(false)){
                                bid.set(world + "." + sign + ".user." + Bukkit.getOfflinePlayer(user).getUniqueId().toString(), bid.getInt(world + "." + sign + ".user." + user));
                                bid.set(world + "." + sign + ".user." + user, null);
                            }
                        }
                    }
                    try {
                        bid.save(SimpleRegionMarket.getPluginDir() + "signs/bid.yml");
                    } catch (IOException ex) {
                        Logger.getLogger(ConfigHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                config.set("version", "4.0.0");
                try {
                    config.save(SimpleRegionMarket.getPluginDir() + "config.yml");
                } catch (IOException ex) {
                    Logger.getLogger(ConfigHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void ReloadConfigHandler() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    /**
     * Gets the config.
     *
     * @return the config
     */
    public FileConfiguration getConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        return config;
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public Boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public boolean loadOld(File file) {

        final YamlConfiguration confighandle = YamlConfiguration.loadConfiguration(file);

        TemplateHotel tokenHotel = null;
        TemplateSell tokenAgent = null;

        String sellTokenString = "sell";
        String rentTokenString = "rent";

        System.out.println("[DEBUG] -------------------------------------");
        System.out.println("[DEBUG] --- Starting importing agents.yml ---");
        System.out.println("[DEBUG] -------------------------------------");

        if (getString("UpdaterTokenNames.sell") != null) {
            sellTokenString = getString("UpdaterTokenNames.sell");
        }

        if (getString("UpdaterTokenNames.rent") != null) {
            rentTokenString = getString("UpdaterTokenNames.rent");
        }

        System.out.println("[DEBUG] using tokens: 0=" + sellTokenString + " 1=" + rentTokenString);

        for (final TemplateMain token : TokenManager.tokenList) {

            if (token.id.equalsIgnoreCase(sellTokenString)) {
                tokenAgent = (TemplateSell) token;
            }
            if (token.id.equalsIgnoreCase(rentTokenString)) {
                tokenHotel = (TemplateHotel) token;
            }
        }
        if (tokenHotel == null || tokenAgent == null) {
            System.out.println("[DEBUG] Cant find tokens!");
            return false;
        }

        ConfigurationSection path;
        for (final String world : confighandle.getKeys(false)) {
            final World worldWorld = Bukkit.getWorld(world);
            if (worldWorld == null) {
                System.out.println("[DEBUG] World " + world + " is null!");
                continue;
            }
            System.out.println("[DEBUG] World " + world + ":");
            path = confighandle.getConfigurationSection(world);
            for (final String region : path.getKeys(false)) {
                final ProtectedRegion protectedRegion = SimpleRegionMarket.wgManager.getProtectedRegion(worldWorld, region);
                if (protectedRegion == null) {
                    System.out.println("[DEBUG] Cant find region " + region + "!");
                    continue;
                }
                path = confighandle.getConfigurationSection(world).getConfigurationSection(region);
                for (final String signnr : path.getKeys(false)) {
                    path = confighandle.getConfigurationSection(world).getConfigurationSection(region).getConfigurationSection(signnr);
                    if (path == null) {
                        continue;
                    }

                    if (path.getInt("Mode") == 1) { // HOTEL
                        if (!tokenHotel.entries.containsKey(world)) {
                            tokenHotel.entries.put(world, new HashMap<String, HashMap<String, Object>>());
                        }
                        if (!tokenHotel.entries.get(world).containsKey(region)) {
                            tokenHotel.entries.get(world).put(region, new HashMap<String, Object>());
                            Utils.setEntry(tokenHotel, world, region, "price", path.getInt("Price"));
                            Utils.setEntry(tokenHotel, world, region, "account", path.getInt("Account"));
                            Utils.setEntry(tokenHotel, world, region, "renttime", path.getLong("RentTime"));
                            if (path.isSet("ExpireDate")) {
                                Utils.setEntry(tokenHotel, world, region, "taken", true);
                                Utils.setEntry(tokenHotel, world, region, "hidden", true);
                                Utils.setEntry(tokenHotel, world, region, "owner", path.getString("RentBy"));
                                Utils.setEntry(tokenHotel, world, region, "expiredate", path.getLong("ExpireDate"));
                            } else {
                                Utils.setEntry(tokenHotel, world, region, "taken", false);
                                Utils.setEntry(tokenHotel, world, region, "hidden", false);
                            }
                        }

                        final ArrayList<Location> signLocations = Utils.getSignLocations(tokenHotel, world, region);
                        signLocations.add(new Location(worldWorld, path.getDouble("X"), path.getDouble("Y"), path.getDouble("Z")));
                        if (signLocations.size() == 1) {
                            Utils.setEntry(tokenHotel, world, region, "signs", signLocations);
                        }
                    } else { // SELL
                        if (!tokenAgent.entries.containsKey(world)) {
                            tokenAgent.entries.put(world, new HashMap<String, HashMap<String, Object>>());
                        }
                        if (!tokenAgent.entries.get(world).containsKey(region)) {
                            tokenAgent.entries.get(world).put(region, new HashMap<String, Object>());
                            Utils.setEntry(tokenAgent, world, region, "price", path.getInt("Price"));
                            Utils.setEntry(tokenAgent, world, region, "account", path.getInt("Account"));
                            Utils.setEntry(tokenAgent, world, region, "renttime", path.getLong("RentTime"));
                            Utils.setEntry(tokenAgent, world, region, "taken", false);
                        }

                        final ArrayList<Location> signLocations = Utils.getSignLocations(tokenAgent, world, region);
                        signLocations.add(new Location(worldWorld, path.getDouble("X"), path.getDouble("Y"), path.getDouble("Z")));
                        if (signLocations.size() == 1) {
                            Utils.setEntry(tokenAgent, world, region, "signs", signLocations);
                        }
                    }
                }
            }
        }
        System.out.println("[DEBUG] -------------------------------------");
        System.out.println("[DEBUG] ---       End of importing.       ---");
        System.out.println("[DEBUG] -------------------------------------");
        return true;
    }
}