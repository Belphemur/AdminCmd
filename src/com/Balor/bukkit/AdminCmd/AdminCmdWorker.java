package com.Balor.bukkit.AdminCmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Handle commands
 * 
 * @authors Plague, Balor
 */
public class AdminCmdWorker {

	private Player player;
	private HashMap<Material, String[]> materialsColors;
	private List<Integer> listOfPossibleRepair;

	public AdminCmdWorker() {
		materialsColors = new HashMap<Material, String[]>();
		materialsColors.put(Material.WOOL, new String[] { "White", "Orange", "Magenta", "LightBlue",
				"Yellow", "LimeGreen", "Pink", "Gray", "LightGray", "Cyan", "Purple", "Blue", "Brown",
				"Green", "Red", "Black" });
		materialsColors.put(Material.INK_SACK, new String[] { "Black", "Red", "Green", "Brown", "Blue",
				"Purple", "Cyan", "LightGray", "Gray", "Pink", "LimeGreen", "Yellow", "LightBlue", "Magenta",
				"Orange", "White" });
		materialsColors.put(Material.LOG, new String[] { "Oak", "Pine", "Birch" });
		materialsColors.put(Material.STEP, new String[] { "Stone", "Sandstone", "Wooden", "Cobblestone" });
		materialsColors.put(Material.DOUBLE_STEP, materialsColors.get(Material.STEP));
		listOfPossibleRepair = new ArrayList<Integer>();
		for (int i = 256; i <= 259; i++)
			listOfPossibleRepair.add(i);
		for (int i = 267; i <= 279; i++)
			listOfPossibleRepair.add(i);
		for (int i = 283; i <= 286; i++)
			listOfPossibleRepair.add(i);
		for (int i = 290; i <= 294; i++)
			listOfPossibleRepair.add(i);
		for (int i = 298; i <= 317; i++)
			listOfPossibleRepair.add(i);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	// all functions return if they handled the command
	// false then means to show the default handle
	// ! make sure the player variable IS a player!
	// set world time to a new value
	public boolean timeSet(String arg) {
		long curtime = player.getWorld().getTime();
		long newtime = curtime - (curtime % 24000);
		if (arg.equalsIgnoreCase("day"))
			newtime += 0;
		else if (arg.equalsIgnoreCase("night"))
			newtime += 14000;
		else if (arg.equalsIgnoreCase("dusk"))
			newtime += 12500;
		else if (arg.equalsIgnoreCase("dawn"))
			newtime += 23000;
		else
			// if not a constant, use raw time
			try {
				newtime += Integer.parseInt(arg);
			} catch (Exception e) {
				return false; // just print the usage...
			}
		player.getWorld().setTime(newtime);
		return true;
	}

	// just an alias to /time day
	public boolean timeDay() {
		player.getWorld().setTime(0);
		return true;
	}

	// lists all online players
	public boolean playerList() {
		Player[] online = player.getServer().getOnlinePlayers();
		player.sendMessage(ChatColor.RED + "Online players: " + ChatColor.WHITE + online.length);
		String buffer = "";
		for (int i = 0; i < online.length; ++i) {
			Player p = online[i];
			String name = p.getDisplayName();
			if (buffer.length() + name.length() + 2 >= 256) {
				player.sendMessage(buffer);
				buffer = "";
			}
			buffer += name + ", ";
		}
		player.sendMessage(buffer);
		return true;
	}

	// teleports the player to another player
	public boolean playerTpTo(String name) {
		return this.tpP2P(player.getName(), name);
	}

	// teleports another player to the player
	public boolean playerTpHere(String name) {
		return this.tpP2P(name, player.getName());
	}

	// teleports chosen player to another player

	private boolean tpP2P(String nFrom, String nTo) {
		boolean found = true;
		Player pFrom = player.getServer().getPlayer(nFrom);
		Player pTo = player.getServer().getPlayer(nTo);
		if (pFrom == null) {
			player.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + nFrom + ChatColor.RED
					+ " not found!");
			found = false;
		}
		if (pTo == null) {
			player.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + nTo + ChatColor.RED
					+ " not found!");
			found = false;
		}
		if (found) {
			pFrom.teleport(pTo);
			player.sendMessage("Succefully teleported " + ChatColor.BLUE + pFrom.getName() + ChatColor.WHITE
					+ " to " + ChatColor.GREEN + pTo.getName());
		}
		return true;
	}

	public boolean playerTpPlayer(String args[]) {
		return this.tpP2P(args[0], args[1]);

	}

	// sends a private message to another player
	// ! at least player argument has to be tested for existence beforehand!

	public boolean playerMessage(String args[]) {
		Player buddy = player.getServer().getPlayer(args[0]);
		if (buddy != null) {
			String msg = "[" + ChatColor.RED + "private" + ChatColor.WHITE + "] " + player.getDisplayName()
					+ " - ";
			for (int i = 1; i < args.length; ++i)
				msg += args[i] + " ";
			msg.trim();
			buddy.sendMessage(msg);
			player.sendMessage(msg);
		} else
			player.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + args[0] + ChatColor.RED
					+ " not found!");
		return true;
	}

	// displays player's current location
	public boolean playerLocation(String[] name) {
		Location loc;
		String msg;
		if (name.length == 0) {
			loc = player.getLocation();
			msg = "You are";
		} else
			try {
				loc = player.getServer().getPlayer(name[0]).getLocation();
				msg = player.getServer().getPlayer(name[0]).getName() + " is";
			} catch (Exception ex) {
				player.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + name[0] + ChatColor.RED
						+ " not found!");
				return true;
			}
		player.sendMessage(loc.getBlockX() + " N, " + loc.getBlockZ() + " E, " + loc.getBlockY() + " H");
		String facing[] = { "W", "NW", "N", "NE", "E", "SE", "S", "SW" };
		double yaw = ((loc.getYaw() + 22.5) % 360);
		if (yaw < 0)
			yaw += 360;
		player.sendMessage(msg + " facing " + ChatColor.RED + facing[(int) (yaw / 45)]);
		return true;
	}

	/**
	 * Heal the selected player.
	 * 
	 * @param name
	 * @return
	 */
	public boolean playerSetHealth(String[] name, int health) {
		Player target = player;
		if (name.length != 0)
			target = player.getServer().getPlayer(name[0]);
		if (target == null) {
			player.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + name[0] + ChatColor.RED
					+ " not found!");
			return true;
		}
		target.setHealth(health);

		return true;
	}

	/**
	 * Set the item in hand to the chosen number 64 if no number set. And add
	 * the overflow item to inventory if it's exceeds 64.
	 * 
	 * @param amount
	 * @return
	 */
	public boolean itemMore(String[] amount) {
		ItemStack hand = player.getItemInHand();
		if (hand == null || hand.getType() == Material.AIR) {
			player.sendMessage(ChatColor.RED + "You have to be holding something!");
			return true;
		}
		if (amount.length == 0)
			hand.setAmount(64);
		else {
			int toAdd;
			try {
				toAdd = Integer.parseInt(amount[0]);
			} catch (Exception e) {
				return false;
			}
			if ((hand.getAmount() + toAdd) > 64) {
				hand.setAmount(64);
				int inInventory = (hand.getAmount() + toAdd) - 64;
				ItemStack iss = new ItemStack(hand.getType(), inInventory);
				player.getInventory().addItem(iss);
				player.sendMessage("Excedent(s) item(s) (" + ChatColor.BLUE + inInventory + ChatColor.WHITE
						+ ") have been stored in your inventory");

			} else
				hand.setAmount(hand.getAmount() + toAdd);
		}

		return true;
	}

	/**
	 * Translate the id or name to a material
	 * 
	 * @param mat
	 * @return Material
	 */
	private Material checkMaterial(String mat) {
		Material m = null;
		try {
			int id = Integer.parseInt(mat);
			m = Material.getMaterial(id);
		} catch (NumberFormatException e) {
			m = Material.matchMaterial(mat);
		}
		if (m == null)
			player.sendMessage(ChatColor.RED + "Unknown material: " + ChatColor.WHITE + mat);
		return m;

	}

	public boolean repair() {

		ItemStack item = player.getItemInHand();
		if (!listOfPossibleRepair.contains(item.getTypeId()))
			player.sendMessage("You can't repair this item : " + ChatColor.RED + item.getType());
		else {
			item.setDurability((short) 0);
			player.sendMessage("Your item " + ChatColor.RED + item.getType() + ChatColor.WHITE
					+ " have been succefully repaired.");
		}

		return true;
	}

	public boolean repairAll() {
		for (ItemStack item : player.getInventory().getContents())
				if (item != null && listOfPossibleRepair.contains(item.getTypeId()))
					item.setDurability((short) 0);

		player.sendMessage("All your items have been repaired.");
		return true;
	}

	// gives the player item of his choice

	public boolean itemGive(String[] args) {
		// which material?
		Material m = checkMaterial(args[0]);
		if (m == null)
			return true;
		// amount, damage and target player
		int cnt = 1;
		byte dam = 0;
		Player target = player;
		if (args.length >= 2) {
			try {
				cnt = Integer.parseInt(args[1]);
			} catch (Exception e) {
				return false;
			}
			if (args.length >= 3) {
				target = player.getServer().getPlayer(args[2]);
				if (target == null) {
					player.sendMessage(ChatColor.RED + "No such player: " + ChatColor.WHITE + args[2]);
					return true;
				}
				if (args.length >= 4)
					try {
						dam = Byte.parseByte(args[3]);
					} catch (Exception e) {
						return true;
					}
			}
		}

		ItemStack stack = new ItemStack(m, cnt, dam);
		if (!target.getName().equals(player.getName())) {
			target.sendMessage(ChatColor.RED + "[" + player.getName() + "]" + ChatColor.WHITE + " send you "
					+ ChatColor.GOLD + cnt + " " + m.name());
			player.sendMessage(ChatColor.RED + "Added " + ChatColor.GOLD + cnt + " " + m.name() + " to "
					+ ChatColor.WHITE + target.getName() + "'s inventory");
		} else
			player.sendMessage(ChatColor.RED + "Added " + ChatColor.GOLD + cnt + " " + m.name() + " to "
					+ ChatColor.WHITE + "your inventory");
		target.getInventory().addItem(stack);
		return true;
	}

	// ----- / item coloring section -----

	// translates a given color value/name into a real color value
	// also does some checking (error = -1)
	private short getColor(String name, Material mat) {
		short value = -1;
		// first try numbered colors
		try {
			value = Short.parseShort(name);
		} catch (Exception e) {
			// try to find the name then
			for (short i = 0; i < materialsColors.get(mat).length; ++i)
				if (materialsColors.get(mat)[i].equalsIgnoreCase(name)) {
					value = i;
					break;
				}
		}
		// is the value OK?
		if (value < 0 || value >= materialsColors.get(mat).length)
			return -1;
		return value;
	}

	// returns all members of the color name array concatenated with commas
	private String printColors(Material mat) {
		String output = "";
		for (int i = 0; i < materialsColors.get(mat).length; ++i)
			output += materialsColors.get(mat)[i] + ", ";
		return output;
	}

	// changes the color of a colorable item in hand
	public boolean itemColor(String color) {
		// help?
		if (color.equalsIgnoreCase("help")) {
			player.sendMessage(ChatColor.RED + "Wool: " + ChatColor.WHITE + printColors(Material.WOOL));
			player.sendMessage(ChatColor.RED + "Dyes: " + ChatColor.WHITE + printColors(Material.INK_SACK));
			player.sendMessage(ChatColor.RED + "Logs: " + ChatColor.WHITE + printColors(Material.LOG));
			player.sendMessage(ChatColor.RED + "Slab: " + ChatColor.WHITE + printColors(Material.STEP));
			return true;
		}
		// determine the value based on what you're holding
		short value = -1;
		Material m = player.getItemInHand().getType();

		if (materialsColors.containsKey(m))
			value = getColor(color, m);
		else {
			player.sendMessage(ChatColor.RED + "You must hold a colorable material!");
			return true;
		}
		// error?
		if (value < 0) {
			player.sendMessage(ChatColor.RED + "Color " + ChatColor.WHITE + color + ChatColor.RED
					+ " is not usable for what you're holding!");
			return true;
		}

		player.getItemInHand().setDurability(value);
		return true;
	}
	// ----- / item coloring section -----
}
