package modreq.Inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;


public class ModreqList {
	public static Inventory inv;

	public ModreqList(String status, String page) {
		inv = Bukkit.createInventory(null, 54, "Modreq list - " + status);
		if(!page.contains("1") && !page.contains("0")) { //
			int prevpage = Integer.parseInt(page) - 1;
			inv.setItem(52, createGuiItem(ChatColor.translateAlternateColorCodes('&', "&cPrev page &r#" + prevpage), new ArrayList<String>(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&cPrev page &r" + prevpage))), Material.BOOK));
		}
		int nextpage = Integer.parseInt(page) + 1;
		inv.setItem(53, createGuiItem(ChatColor.translateAlternateColorCodes('&', "&cNext page &r#" + nextpage), new ArrayList<String>(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&cNext page &r" + nextpage))), Material.BOOK));

	}

	public static void addItem(String id, String text, String created, String claim) {
		if(claim != null) {
			inv.addItem(createGuiItem("Modreq #" + id, new ArrayList<String>(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&b" + text), ChatColor.translateAlternateColorCodes('&', "Created by &c" + created), "", ChatColor.translateAlternateColorCodes('&', "Claim by &a" + claim))), Material.PAPER));

		} else {
			inv.addItem(createGuiItem("Modreq #" + id, new ArrayList<String>(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&b" + text), ChatColor.translateAlternateColorCodes('&', "Created by &c" + created))), Material.PAPER));

		}
	}

	public static ItemStack createGuiItem(String name, ArrayList<String> desc, Material mat) {
		ItemStack i = new ItemStack(mat, 1);
		ItemMeta iMeta = i.getItemMeta();
		iMeta.setDisplayName(name);
		iMeta.setLore(desc);
		i.setItemMeta(iMeta);
		return i;
	}

}