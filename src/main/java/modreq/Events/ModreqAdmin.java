package modreq.Events;

import modreq.Database.Database;
import modreq.Inventory.ModreqList;
import modreq.Messages.Discord;
import modreq.Modreq;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ModreqAdmin {
    private static Inventory inv;

    public static void list(Player player, String[] args) {
        try {
            String claim = null;
            String page = "1", status = "open";
            String limit = "51", offset = "0";
            if (args.length > 1 && (args[1].contains("open") || args[1].contains("close") || args[1].contains("all"))) {
                if (args.length > 2) {
                    page = args[2];
                }
                status = args[1];
            } else if (args.length > 1) {
                page = args[1];
            }
            ModreqList list = new ModreqList(status, page);

            if (args.length > 1) {
                if (Integer.valueOf(page) > 1) {
                    offset = String.valueOf(Integer.valueOf(page) + 49);
                }
            }

            ResultSet rs = Database.getTickets(limit, offset, status);
            if (rs.next() == false) {
                player.sendMessage("Tato stránka neexistuje");
            } else {
                do {
                    claim = null;
                    if(rs.getString("claim_uuid") != null){
                        claim = Bukkit.getServer().getOfflinePlayer(UUID.fromString(rs.getString("claim_uuid"))).getName();
                    }
                    list.addItem(rs.getString("PK_idm"), rs.getString("text"), Bukkit.getServer().getOfflinePlayer(UUID.fromString(rs.getString("uuid"))).getName(), claim);
                } while (rs.next());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        player.openInventory(ModreqList.inv);
    }

    public static void claim(Player player, String[] args) {

        if (args.length > 1) {
            try {
                Player clamedticket = null;
                boolean allow = true;
                ResultSet result = null;

                result = Modreq.statement.executeQuery("SELECT * FROM modreqs WHERE PK_idm = " + args[1] + ";");

                while (result.next()) {
                    if (result.getString("claim_uuid") == null) {
                        allow = true;
                        clamedticket = Bukkit.getServer().getOfflinePlayer(UUID.fromString(result.getString("uuid"))).getPlayer();
                    } else {
                        allow = false;
                    }
                }
                if (allow) {
                    Database.UpdateNotSend(args[1]);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.prefix +
                            Modreq.messages.getString("ticket_claim")
                                    .replace("%id%", args[1])

                    ));
                    if(clamedticket  != null) {
                        clamedticket.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.prefix +
                                Modreq.messages.getString("ticket_to_user_claim")
                                        .replace("%id%", args[1])
                                        .replace("%by%", player.getDisplayName())
                        ));
                        Database.UpdateSend(args[1]);
                    }
                    Discord.Claimsend("discord_claim_ticket", args[1], player.getUniqueId().toString());

                    Database.ClaimTicket(args[1], player.getUniqueId().toString());
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.prefix + Modreq.messages.getString("ticket_is_claimed")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.prefix + "Error: /modreq claim <id>"));
        }
    }

    public static void unclaim(Player player, String[] args) {
        if (args.length > 1) {
            try {
                Player clamedticket = null;
                boolean allow = true;
                ResultSet result = null;

                result = Modreq.statement.executeQuery("SELECT * FROM modreqs WHERE PK_idm = " + args[1] + ";");

                while (result.next()) {
                    if(result.getString("claim_uuid") == null) {
                        allow = false;
                    }else {
                        if (result.getString("claim_uuid").contains(player.getUniqueId().toString())) {
                            allow = true;
                            clamedticket = Bukkit.getServer().getOfflinePlayer(result.getString("uuid")).getPlayer();
                        } else {
                            allow = false;
                        }
                    }
                }
                if (allow) {
                    Database.UpdateNotSend(args[1]);
                    Database.UnClaimTicket(args[1]);

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.prefix +
                            Modreq.messages.getString("ticket_unclaim")
                                    .replace("%id%", args[1])

                    ));


                    if(clamedticket != null) {
                        clamedticket.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.prefix +
                                Modreq.messages.getString("ticket_to_user_unclaim")
                                        .replace("%id%", args[1])
                                        .replace("%by%", player.getDisplayName())
                        ));
                        Database.UpdateSend(args[1]);
                    }
                    Discord.unClaimSend("discord_unclaim_ticket", args[1], player.getUniqueId().toString());


                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.prefix + Modreq.messages.getString("ticket_user_unclaim_problem")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.prefix + "Error: /modreq unclaim <id>"));
        }
    }

    public static void close(Player player, String[] args) {
        if (args.length > 2) { // modreq close 2 aaa  aaa / 0 1 2 3
            try {
                Player clamedticket = null;
                boolean allow = true,claim = false;
                String answer = "";
                ResultSet result = null;

                result = Modreq.statement.executeQuery("SELECT * FROM modreqs WHERE PK_idm = " + args[1] + ";");

                while (result.next()) {
                    if (result.getString("claim_uuid") == null) {
                        allow = true;
                        clamedticket = Bukkit.getServer().getOfflinePlayer(UUID.fromString(result.getString("uuid"))).getPlayer();
                    }else {
                        if (result.getString("claim_uuid").contains(player.getUniqueId().toString()) && result.getString("status").contains("open")) {
                            allow = true;
                            clamedticket = Bukkit.getServer().getOfflinePlayer(UUID.fromString(result.getString("uuid"))).getPlayer();
                        } else {
                            allow = false;
                        }
                    }
                }
                if (allow) {
                    Database.UpdateNotSend(args[1]);
                    if(claim){
                        ModreqAdmin.claim(player, args);
                    }
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.prefix +  Modreq.messages.getString("ticket_close").replace("%id%", args[1])));

                    for(int i = 2; i < args.length; i++) {
                        answer += args[i] + " ";
                    }
                    if(clamedticket != null) {
                        clamedticket.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.prefix +
                                Modreq.messages.getString("ticket_to_user_close")
                                        .replace("%id%", args[1])
                                        .replace("%by%", player.getDisplayName().toString())
                                        .replace("%answer%", answer)
                        ));
                        Database.UpdateSend(args[1]);
                    }
                    Discord.closeSend("discord_close_ticket", args[1], answer);

                    Database.CloseTicket(args[1], answer);

                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.prefix +
                            Modreq.messages.getString("ticket_cant_close")
                                    .replace("%id%", args[1])
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.prefix + "Error: /modreq close <id> <answer>"));
        }
    }

    public static void tp(Player player, String[] args) {
        try {
            if (args.length > 1) {
                ResultSet rs = Database.getTicketId(args[1]);
                while (rs.next()) {
                    WorldCreator wc = new WorldCreator(rs.getString("world"));
                    World world = Bukkit.createWorld(wc);
                    player.teleport(new Location(world, rs.getInt("x"), rs.getInt("y"), rs.getInt("z")), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Modreq.prefix + "&r" + "/modreq tp <id>"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
