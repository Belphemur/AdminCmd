package com.Balor.bukkit.AdminCmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

import belgium.Balor.Workers.Worker;

import com.Balor.files.utils.FilesManager;
import com.Balor.files.utils.Utils;

/**
 * Handle commands
 * 
 * @authors Plague, Balor
 */
public class AdminCmdWorker extends Worker {

	private Player player;
	private HashMap<Material, String[]> materialsColors;
	private List<Integer> listOfPossibleRepair;
	private FilesManager fManager;
	private List<Integer> blacklist;
	private AdminCmd pluginInstance;
	private ArrayList<String> thunderGods = new ArrayList<String>();
	private HashMap<String, Material> alias = new HashMap<String, Material>();

	public AdminCmdWorker(String path) {
		materialsColors = new HashMap<Material, String[]>();
		materialsColors.put(Material.WOOL, new String[] { "White", "Orange", "Magenta",
				"LightBlue", "Yellow", "LimeGreen", "Pink", "Gray", "LightGray", "Cyan", "Purple",
				"Blue", "Brown", "Green", "Red", "Black" });
		materialsColors.put(Material.INK_SACK, new String[] { "Black", "Red", "Green", "Brown",
				"Blue", "Purple", "Cyan", "LightGray", "Gray", "Pink", "LimeGreen", "Yellow",
				"LightBlue", "Magenta", "Orange", "White" });
		materialsColors.put(Material.LOG, new String[] { "Oak", "Pine", "Birch" });
		materialsColors.put(Material.STEP, new String[] { "Stone", "Sandstone", "Wooden",
				"Cobblestone" });
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
		fManager = new FilesManager(path);
		blacklist = getBlackListedItems();
		alias = fManager.getAlias();
	}

	/**
	 * @param pluginInstance
	 *            the pluginInstance to set
	 */
	public void setPluginInstance(AdminCmd pluginInstance) {
		this.pluginInstance = pluginInstance;
	}

	/**
	 * @return the pluginInstance
	 */
	public AdminCmd getPluginInstance() {
		return pluginInstance;
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
	@SuppressWarnings("deprecation")
	public boolean playerList() {
		Player[] online = player.getServer().getOnlinePlayers();
		player.sendMessage(ChatColor.RED + "Online players: " + ChatColor.WHITE + online.length);
		String buffer = "";
		if (AdminCmdWorker.getPermission() == null) {
			for (int i = 0; i < online.length; ++i) {
				Player p = online[i];
				String name = p.getDisplayName();
				if (buffer.length() + name.length() + 2 >= 256) {
					player.sendMessage(buffer);
					buffer = "";
				}
				buffer += name + ", ";
			}
		} else {
			// changed the playerlist, now support prefixes from groups!!! @foxy
			for (int i = 0; i < online.length; ++i) {
				Player p = online[i];
				String name = p.getName();
				String prefixstring;
				String world = player.getWorld().getName();
				try {
					prefixstring = AdminCmdWorker.getPermission()
							.safeGetUser(world, player.getName()).getPrefix();
				} catch (Exception e) {
					String group = AdminCmdWorker.getPermission().getGroup(world, player.getName());
					prefixstring = AdminCmdWorker.getPermission().getGroupPrefix(world, group);
				}

				if (prefixstring.length() > 1) {
					String result = Utils.colorParser(prefixstring);
					if (result == null)
						buffer += prefixstring + name + ChatColor.WHITE + ", ";
					else
						buffer += result + name + ChatColor.WHITE + ", ";

				} else {
					buffer += name + ", ";
				}
			}

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
			player.sendMessage("Successfully teleported " + ChatColor.BLUE + pFrom.getName()
					+ ChatColor.WHITE + " to " + ChatColor.GREEN + pTo.getName());
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
			String msg = "[" + ChatColor.RED + "private" + ChatColor.WHITE + "] "
					+ player.getDisplayName() + " - ";
			for (int i = 1; i < args.length; ++i)
				msg += args[i] + " ";
			msg.trim();
			String parsed = Utils.colorParser(msg);
			if (parsed == null)
				parsed = msg;
			buddy.sendMessage(parsed);
			player.sendMessage(parsed);
		} else
			player.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + args[0]
					+ ChatColor.RED + " not found!");
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
				player.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + name[0]
						+ ChatColor.RED + " not found!");
				return true;
			}
		player.sendMessage(loc.getBlockX() + " N, " + loc.getBlockZ() + " E, " + loc.getBlockY()
				+ " H");
		String facing[] = { "W", "NW", "N", "NE", "E", "SE", "S", "SW" };
		double yaw = ((loc.getYaw() + 22.5) % 360);
		if (yaw < 0)
			yaw += 360;
		player.sendMessage(msg + " facing " + ChatColor.RED + facing[(int) (yaw / 45)]);
		return true;
	}

	/**
	 * Clear the inventory of the user
	 * 
	 * @param name
	 *            the player who will have his inventory cleared
	 * @return
	 */
	public boolean clearInventory(String name) {
		player.getServer().getPlayer(name).getInventory().clear();
		player.getServer().getPlayer(name).getInventory().setHelmet(null);
		player.getServer().getPlayer(name).getInventory().setChestplate(null);
		player.getServer().getPlayer(name).getInventory().setLeggings(null);
		player.getServer().getPlayer(name).getInventory().setBoots(null);
		player.sendMessage(ChatColor.RED + "Inventory of " + ChatColor.WHITE + name + ChatColor.RED
				+ " cleared");
		return true;
	}

	/**
	 * Add an item to the BlackList
	 * 
	 * @param name
	 * @return
	 */
	public boolean setBlackListedItem(String name) {
		Material m = checkMaterial(name);
		if (m != null) {
			Configuration config = fManager.getFile("blacklist.yml");
			List<Integer> list = config.getIntList("BlackListed", null);
			if (list == null)
				list = new ArrayList<Integer>();
			list.add(m.getId());
			config.setProperty("BlackListed", list);
			config.save();
			if (blacklist == null)
				blacklist = new ArrayList<Integer>();
			blacklist.add(m.getId());
			player.sendMessage(ChatColor.GREEN + "Item (" + ChatColor.WHITE + m.name()
					+ ChatColor.GREEN + ") added to the Black List.");
			return true;
		}
		return false;
	}

	/**
	 * remove a black listed item
	 * 
	 * @param name
	 * @return
	 */
	public boolean removeBlackListedItem(String name) {
		Material m = checkMaterial(name);
		if (m != null) {
			Configuration config = fManager.getFile("blacklist.yml");
			List<Integer> list = config.getIntList("BlackListed", null);
			if (list == null)
				list = new ArrayList<Integer>();
			if (!list.isEmpty() && list.contains(m.getId())) {
				list.remove((Integer) m.getId());
				config.setProperty("BlackListed", list);
				config.save();
			}
			if (blacklist != null && !blacklist.isEmpty() && blacklist.contains(m.getId()))
				blacklist.remove((Integer) m.getId());
			player.sendMessage(ChatColor.GREEN + "Item (" + ChatColor.WHITE + m.name()
					+ ChatColor.GREEN + ") removed from the Black List.");
			return true;
		}
		return false;
	}

	/**
	 * Get the blacklisted items
	 * 
	 * @return
	 */
	private List<Integer> getBlackListedItems() {
		return fManager.getFile("blacklist.yml")
				.getIntList("BlackListed", new ArrayList<Integer>());
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
			player.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + name[0]
					+ ChatColor.RED + " not found!");
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
		if (!hasPerm(player, "admincmd.item.noblacklist") && blacklist.contains(hand.getTypeId())) {
			player.sendMessage(ChatColor.DARK_RED + "This item (" + ChatColor.WHITE
					+ hand.getType().name() + ChatColor.DARK_RED + ") is black listed.");
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
			if ((hand.getAmount() + toAdd) > hand.getMaxStackSize()) {
				int inInventory = (hand.getAmount() + toAdd) - hand.getMaxStackSize();
				hand.setAmount(hand.getMaxStackSize());
				player.getInventory().addItem(new ItemStack(hand.getType(), inInventory));
				player.sendMessage("Excedent(s) item(s) (" + ChatColor.BLUE + inInventory
						+ ChatColor.WHITE + ") have been stored in your inventory");

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
		Material m = Utils.checkMaterial(mat);
		if (m == null)
			player.sendMessage(ChatColor.RED + "Unknown material: " + ChatColor.WHITE + mat);
		return m;

	}

	public boolean tpTo(String[] args) {
		double x;
		double y;
		double z;
		try {
			x = Double.parseDouble(args[0]);
			y = Double.parseDouble(args[1]);
			z = Double.parseDouble(args[2]);
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED + "Location has to be formed by numbers");
			return true;
		}
		player.teleport(new Location(player.getWorld(), x, y, z));
		return true;
	}

	public boolean repair() {

		ItemStack item = player.getItemInHand();
		if (item != null && listOfPossibleRepair.contains(item.getTypeId())) {
			item.setDurability((short) 0);
			player.sendMessage("Your item " + ChatColor.RED + item.getType() + ChatColor.WHITE
					+ " have been successfully repaired.");
		} else
			player.sendMessage("You can't repair this item : " + ChatColor.RED + item.getType());

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
		Material m = null;
		m = alias.get(args[0]);
		if (m == null)
			m = checkMaterial(args[0]);
		if (m == null)
			return true;
		if (!hasPerm(player, "admincmd.item.noblacklist") && blacklist.contains(m.getId())) {
			player.sendMessage(ChatColor.DARK_RED + "This item (" + ChatColor.WHITE + m.name()
					+ ChatColor.DARK_RED + ") is black listed.");
			return true;
		}
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
					player.sendMessage(ChatColor.RED + "No such player: " + ChatColor.WHITE
							+ args[2]);
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
			target.sendMessage(ChatColor.RED + "[" + player.getName() + "]" + ChatColor.WHITE
					+ " send you " + ChatColor.GOLD + cnt + " " + m.name());
			player.sendMessage(ChatColor.RED + "Added " + ChatColor.GOLD + cnt + " " + m.name()
					+ " to " + ChatColor.WHITE + target.getName() + "'s inventory");
		} else
			player.sendMessage(ChatColor.RED + "Added " + ChatColor.GOLD + cnt + " " + m.name()
					+ " to " + ChatColor.WHITE + "your inventory");
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

	public boolean weather(String type, String[] duration) {
		if (type == "clear") {
			player.getWorld().setThundering(false);
			player.getWorld().setStorm(false);
			player.sendMessage(ChatColor.GOLD + "Sky cleared");
		} else {
			if (duration == null || duration.length < 1) {
				player.getWorld().setStorm(true);
				player.getWorld().setWeatherDuration(12000);
				player.sendMessage(ChatColor.GOLD + "Storm set for 10 mins");
			} else {
				try {
					player.getWorld().setStorm(true);
					int time = Integer.parseInt(duration[0]);
					player.getWorld().setWeatherDuration(time * 1200);
					player.sendMessage(ChatColor.GOLD + "Storm set for " + time + " mins");
				} catch (NumberFormatException e) {
					player.sendMessage(ChatColor.BLUE + "Sorry, that (" + duration[0]
							+ ") isn't a number!");
					player.getWorld().setStorm(true);
					player.getWorld().setWeatherDuration(12000);
					player.sendMessage(ChatColor.GOLD + "Storm set for 10 mins");
				}
			}
		}
		return true;
	}

	public boolean strikePlayer(String playerName) {
		if (player.getServer().getPlayer(playerName) != null) {
			player.getWorld().strikeLightning(
					player.getServer().getPlayer(playerName).getLocation());
			player.sendMessage(ChatColor.GOLD + playerName + " was striked by Thor");
		} else
			player.sendMessage(ChatColor.RED + "No such player: " + ChatColor.WHITE + playerName);
		return true;
	}

	public boolean thor() {
		String player = this.player.getName();
		if (thunderGods.contains(player)) {
			thunderGods.remove(player);
			this.player.sendMessage(ChatColor.DARK_PURPLE + "You have lost the power of Thor");
		} else {
			thunderGods.add(player);
			this.player.sendMessage(ChatColor.DARK_PURPLE + "You have now the power of Thor");
		}
		return true;
	}

	public boolean alias(String[] args) {
		Material m = checkMaterial(args[1]);
		if (m == null)
			return true;
		String alias = args[0];
		this.alias.put(alias, m);
		this.fManager.addAlias(alias, m.getId());
		player.sendMessage(ChatColor.BLUE + "You can now use " + ChatColor.GOLD + alias
				+ ChatColor.BLUE + " for the item " + ChatColor.GOLD + m.name());
		return true;
	}

	public boolean rmAlias(String alias) {
		this.fManager.removeAlias(alias);
		this.alias.remove(alias);
		player.sendMessage(ChatColor.GOLD + alias + ChatColor.RED + " removed");
		return true;
	}

	public boolean spawnMob(String args[]) {
		final String name = args[0];
		int nbTaped;
		try {
			nbTaped = Integer.parseInt(args[1]);
		} catch (Exception e) {
			nbTaped = 1;
		}
		final int nb = nbTaped;
		final CreatureType ct = CreatureType.fromName(name);
		if (ct == null) {
			player.sendMessage(ChatColor.RED + "No such creature: " + ChatColor.WHITE + name);
			return true;
		}
		AdminCmd.getBukkitServer().getScheduler()
				.scheduleAsyncDelayedTask(pluginInstance, new Runnable() {

					@Override
					public void run() {
						for (int i = 0; i < nb; i++) {
							player.getWorld().spawnCreature(player.getLocation(), ct);
							try {
								Thread.sleep(110);
							} catch (InterruptedException e) {
								// e.printStackTrace();
							}
						}
						player.sendMessage(ChatColor.BLUE + "Spawned " + ChatColor.WHITE + nb + " "
								+ name);
					}
				});
		return true;
	}

	public boolean hasThorPowers(String player) {
		return thunderGods.contains(player);
	}

	// changes the color of a colorable item in hand
	public boolean itemColor(String color) {
		// help?
		if (color.equalsIgnoreCase("help")) {
			player.sendMessage(ChatColor.RED + "Wool: " + ChatColor.WHITE
					+ printColors(Material.WOOL));
			player.sendMessage(ChatColor.RED + "Dyes: " + ChatColor.WHITE
					+ printColors(Material.INK_SACK));
			player.sendMessage(ChatColor.RED + "Logs: " + ChatColor.WHITE
					+ printColors(Material.LOG));
			player.sendMessage(ChatColor.RED + "Slab: " + ChatColor.WHITE
					+ printColors(Material.STEP));
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
