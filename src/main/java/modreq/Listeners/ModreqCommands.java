package modreq.Listeners;

import modreq.Events.ModreqAdmin;
import modreq.Events.ModreqUser;
import modreq.Modreq;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;



public class ModreqCommands implements CommandExecutor {
    Modreq plugin;

    public ModreqCommands (Modreq plugin) {
        this.plugin = plugin;
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This cannot be run from the console!");
            return true;
        }
        Player player = (Player) sender;
        if(args[0].isEmpty()){
            player.sendMessage(this.help());
        }

        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                switch (args[0]) {
                    case "create":
                        args[0] = "";
                        ModreqUser.create(player, args);
                        break;
                    case "status":
                        ModreqUser.list(player, args);
                        break;
                }
                if(player.hasPermission("modreq.claim")) {
                    switch (args[0]) {
                        case "list":
                            ModreqAdmin.list(player, args);
                            break;
                        case "close":
                            ModreqAdmin.close(player, args);
                            break;
                        case "unclaim":
                            ModreqAdmin.unclaim(player, args);
                            break;
                        case "claim":
                            ModreqAdmin.claim(player, args);
                            break;
                        case "tp":
                            ModreqAdmin.tp(player, args);
                            break;
                    }
                }else{
                    player.sendMessage("You havent got a permissions");
                }
            }
        };
        r.runTaskAsynchronously(plugin);
        return true;
    }
    public String help(){
        return "/modreq create <text>";
    }
}
