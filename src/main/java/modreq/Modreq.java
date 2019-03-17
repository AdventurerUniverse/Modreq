package modreq;

import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInvsPlugin;
import modreq.Listeners.Inventory;
import modreq.Listeners.ModreqCommands;
import modreq.Listeners.ModreqListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Modreq extends JavaPlugin implements Listener{
    public static InventoryManager manager;
    private Connection connection;
    public static Statement statement;
    private File ConfigFile;
    public static FileConfiguration config = null;
    private File MessagesFile;
    public static FileConfiguration messages = null;
    public static String prefix = "[" + ChatColor.DARK_GREEN + "Modreq" + ChatColor.WHITE + "] ";

    @Override
    public void onEnable(){
        Bukkit.getPluginManager().registerEvents(new Inventory(this), this);
        this.getCommand("modreq").setExecutor(new ModreqCommands(this));
        this.getCommand("modreqs").setExecutor(new ModreqCommands(this));

        //new ModreqListener(this);
        this.loadConfig();
        this.loadMessages();
        // Asyn.. loading database
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    openConnection();
                    statement = connection.createStatement();
                    checkDatabase();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        r.runTaskAsynchronously(this);
        manager = new InventoryManager(this);
        manager.init();
        getLogger().info(prefix + ChatColor.AQUA + "Modreq has load");
    }
    public void onDisable() {
        getLogger().info(prefix + "Modreq has been disable !!");
    }

    public FileConfiguration getConfig() {
        return Modreq.config;
    }
    private void loadConfig() {
        ConfigFile = new File(getDataFolder(), "config.yml");
        if (!ConfigFile.exists()) {
            this.saveDefaultConfig();

            saveResource("config.yml", true);
        }
        config = new YamlConfiguration();

        try {
            config.load(ConfigFile);

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static FileConfiguration getMessage() { return Modreq.config;
    }
    private void loadMessages() {
        MessagesFile = new File(getDataFolder(), "messages.yml");
        if (!ConfigFile.exists()) {
            this.saveDefaultConfig();

            saveResource("messages.yml", true);
        }
        messages = new YamlConfiguration();

        try {
            messages.load(MessagesFile);

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + getConfig().getString("mysql_host") + ":" + getConfig().getString("mysql_port") + "/" + getConfig().getString("mysql_db"), getConfig().getString("mysql_user"), getConfig().getString("mysql_pass"));
            getLogger().info(prefix + ChatColor.AQUA + "Database has load");
        }
    }
    public void checkDatabase() {
        try {
            statement.execute("CREATE TABLE IF NOT EXISTS `modreqs` ( `PK_idm` int(11) NOT NULL AUTO_INCREMENT,  `uuid` varchar(255) NOT NULL, `nick` varchar(255) NOT NULL, `claim_uuid` VARCHAR(255) NOT NULL DEFAULT \'\'  ,`text` varchar(255) NOT NULL,  `close` int(11) NOT NULL DEFAULT \'0\', `answer` VARCHAR(255) NULL , PRIMARY KEY (`PK_idm`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
