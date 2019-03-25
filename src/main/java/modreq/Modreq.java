package modreq;

import fr.minuskube.inv.InventoryManager;
import modreq.Listeners.Listeners;
import modreq.Listeners.ModreqCommands;
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
        try {
            Bukkit.getServer().getPluginManager().registerEvents(new Listeners(this), this);
            this.getCommand("modreq").setExecutor(new ModreqCommands(this));
            this.getCommand("report").setExecutor(new ModreqCommands(this));
            this.getCommand("ticket").setExecutor(new ModreqCommands(this));
            this.getCommand("modreqs").setExecutor(new ModreqCommands(this));

            this.loadConfig();
            this.loadMessages();
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
        }catch (Exception e){
            e.printStackTrace();
        }
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
            statement.execute("CREATE TABLE `modreqs` (`PK_idm` int(11) NOT NULL, uuid varchar(255) NOT NULL,`claim_uuid` varchar(255) DEFAULT NULL,`text` varchar(255) NOT NULL,`status` varchar(15) NOT NULL DEFAULT 'open',`answer` varchar(255) DEFAULT NULL,`world` varchar(255) NOT NULL,`x` int(11) NOT NULL,`y` int(11) NOT NULL,`z` int(11) NOT NULL,  `send` varchar(10) NOT NULL DEFAULT '0',`date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
            statement.execute("ALTER TABLE `modreqs` ADD PRIMARY KEY (`PK_idm`);");
            statement.execute("ALTER TABLE `modreqs` MODIFY `PK_idm` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1; ");
        } catch (SQLException e) {
        }
    }
}
