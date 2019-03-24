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
        if(args.length < 1){
            player.sendMessage(this.help());
            return true;
        }
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {

                if(player.hasPermission("modreq.admin")) {
                    if(cmd.getName().contains("report") || cmd.getName().contains("ticket")){
                            ModreqUser.create(player, args);

                    }else {
                        switch (args[0]) {
                            case "create":
                                args[0] = "";
                                ModreqUser.create(player, args);
                                break;
                            case "own":
                                ModreqUser.own(player, args);
                                break;
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
                            default:
                                player.sendMessage("/modreq create <text>");
                                break;
                        }
                    }
                }else{
                    switch (args[0]) {
                        case "create":
                            args[0] = "";
                            ModreqUser.create(player, args);
                            break;
                        case "own":
                            ModreqUser.own(player, args);
                            break;
                        default:
                            player.sendMessage("/modreq create <text>");
                            break;
                    }
                }
            }
        };
        r.runTaskAsynchronously(plugin);
        return true;
    }
    public String help(){
        return "Wrong !!" + "\n" +
                "Plese write: /modreq create <text>";
    }
}
