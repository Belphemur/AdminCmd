package com.Balor.bukkit.AdminCmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
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

	private CommandSender sender;
	private HashMap<Material, String[]> materialsColors;
	private List<Integer> listOfPossibleRepair;
	private FilesManager fManager;
	private List<Integer> blacklist;
	private AdminCmd pluginInstance;
	private TreeSet<String> thunderGods = new TreeSet<String>();
	private TreeSet<String> gods = new TreeSet<String>();
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

	public void setSender(CommandSender player) {
		this.sender = player;
	}

	private boolean isPlayer() {
		return isPlayer(true);
	}

	private boolean isPlayer(boolean msg) {
		if (sender instanceof Player)
			return true;
		else {
			if (msg)
				sender.sendMessage("You must be a player to use this command.");
			return false;
		}
	}

	private void setTime(World w, String arg) {
		long curtime = w.getTime();
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
			}
		w.setTime(newtime);
	}

	// all functions return if they handled the command
	// false then means to show the default handle
	// ! make sure the player variable IS a player!
	// set world time to a new value
	public boolean timeSet(String arg) {
		if (isPlayer(false)) {
			Player p = (Player) sender;
			setTime(p.getWorld(), arg);
		} else {
			for (World w : sender.getServer().getWorlds())
				setTime(w, arg);
		}
		return true;

	}

	// just an alias to /time day
	public boolean timeDay() {
		timeSet("day");
		return true;
	}

	// lists all online players
	@SuppressWarnings("deprecation")
	public boolean playerList() {
		Player[] online = sender.getServer().getOnlinePlayers();
		sender.sendMessage(ChatColor.RED + "Online players: " + ChatColor.WHITE + online.length);
		String buffer = "";
		if (AdminCmdWorker.getPermission() == null) {
			for (int i = 0; i < online.length; ++i) {
				Player p = online[i];
				String name = p.getDisplayName();
				if (buffer.length() + name.length() + 2 >= 256) {
					sender.sendMessage(buffer);
					buffer = "";
				}
				buffer += name + ", ";
			}
		} else {
			// changed the playerlist, now support prefixes from groups!!! @foxy
			for (int i = 0; i < online.length; ++i) {
				String name = online[i].getName();
				String prefixstring;
				String world = "";
				world = online[i].getWorld().getName();

				try {
					prefixstring = AdminCmdWorker.getPermission().safeGetUser(world, name)
							.getPrefix();
				} catch (Exception e) {
					String group = AdminCmdWorker.getPermission().getGroup(world, name);
					prefixstring = AdminCmdWorker.getPermission().getGroupPrefix(world, group);
				} catch (NoSuchMethodError e) {
					String group = AdminCmdWorker.getPermission().getGroup(world, name);
					prefixstring = AdminCmdWorker.getPermission().getGroupPrefix(world, group);
				}

				if (prefixstring != null && prefixstring.length() > 1) {
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
		sender.sendMessage(buffer);
		return true;
	}

	// teleports the player to another player
	public boolean playerTpTo(String name) {
		return isPlayer() ? this.tpP2P(((Player) sender).getName(), name) : true;
	}

	// teleports another player to the player
	public boolean playerTpHere(String name) {
		return isPlayer() ? this.tpP2P(name, ((Player) sender).getName()) : true;
	}

	// teleports chosen player to another player

	private boolean tpP2P(String nFrom, String nTo) {
		boolean found = true;
		Player pFrom = sender.getServer().getPlayer(nFrom);
		Player pTo = sender.getServer().getPlayer(nTo);
		if (pFrom == null) {
			sender.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + nFrom + ChatColor.RED
					+ " not found!");
			found = false;
		}
		if (pTo == null) {
			sender.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + nTo + ChatColor.RED
					+ " not found!");
			found = false;
		}
		if (found) {
			pFrom.teleport(pTo);
			sender.sendMessage("Successfully teleported " + ChatColor.BLUE + pFrom.getName()
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
		Player buddy = sender.getServer().getPlayer(args[0]);
		if (buddy != null) {

			String msg = "[" + ChatColor.RED + "private" + ChatColor.WHITE + "] ";
			if (isPlayer(false))
				msg += ((Player) sender).getDisplayName() + " - ";
			else
				msg += "Server Admin" + " - ";

			for (int i = 1; i < args.length; ++i)
				msg += args[i] + " ";
			msg.trim();
			String parsed = Utils.colorParser(msg);
			if (parsed == null)
				parsed = msg;
			buddy.sendMessage(parsed);
			sender.sendMessage(parsed);
		} else
			sender.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + args[0]
					+ ChatColor.RED + " not found!");
		return true;
	}

	// displays player's current location
	public boolean playerLocation(String[] name) {
		Location loc;
		String msg;
		if (name.length == 0) {
			if (isPlayer()) {
				loc = ((Player) sender).getLocation();
				msg = "You are";
			} else
				return true;
		} else
			try {
				loc = sender.getServer().getPlayer(name[0]).getLocation();
				msg = sender.getServer().getPlayer(name[0]).getName() + " is";
			} catch (Exception ex) {
				sender.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + name[0]
						+ ChatColor.RED + " not found!");
				return true;
			}
		sender.sendMessage(loc.getBlockX() + " N, " + loc.getBlockZ() + " E, " + loc.getBlockY()
				+ " H");
		String facing[] = { "W", "NW", "N", "NE", "E", "SE", "S", "SW" };
		double yaw = ((loc.getYaw() + 22.5) % 360);
		if (yaw < 0)
			yaw += 360;
		sender.sendMessage(msg + " facing " + ChatColor.RED + facing[(int) (yaw / 45)]);
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
		sender.getServer().getPlayer(name).getInventory().clear();
		sender.getServer().getPlayer(name).getInventory().setHelmet(null);
		sender.getServer().getPlayer(name).getInventory().setChestplate(null);
		sender.getServer().getPlayer(name).getInventory().setLeggings(null);
		sender.getServer().getPlayer(name).getInventory().setBoots(null);
		sender.sendMessage(ChatColor.RED + "Inventory of " + ChatColor.WHITE + name + ChatColor.RED
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
			sender.sendMessage(ChatColor.GREEN + "Item (" + ChatColor.WHITE + m.name()
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
			sender.sendMessage(ChatColor.GREEN + "Item (" + ChatColor.WHITE + m.name()
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
		Player target = null;
		if (name.length != 0)
			target = sender.getServer().getPlayer(name[0]);
		else if (isPlayer(false))
			target = ((Player) sender);
		else {
			sender.sendMessage("You must type the player name");
			return true;
		}
		if (target == null) {
			sender.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + name[0]
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
		if (isPlayer()) {
			ItemStack hand = ((Player) sender).getItemInHand();
			if (hand == null || hand.getType() == Material.AIR) {
				sender.sendMessage(ChatColor.RED + "You have to be holding something!");
				return true;
			}
			if (!hasPerm(((Player) sender), "admincmd.item.noblacklist")
					&& blacklist.contains(hand.getTypeId())) {
				sender.sendMessage(ChatColor.DARK_RED + "This item (" + ChatColor.WHITE
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
					((Player) sender).getInventory().addItem(
							new ItemStack(hand.getType(), inInventory));
					sender.sendMessage("Excedent(s) item(s) (" + ChatColor.BLUE + inInventory
							+ ChatColor.WHITE + ") have been stored in your inventory");

				} else
					hand.setAmount(hand.getAmount() + toAdd);
			}
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
			sender.sendMessage(ChatColor.RED + "Unknown material: " + ChatColor.WHITE + mat);
		return m;

	}

	public boolean tpTo(String[] args) {
		if (isPlayer()) {
			double x;
			double y;
			double z;
			try {
				x = Double.parseDouble(args[0]);
				y = Double.parseDouble(args[1]);
				z = Double.parseDouble(args[2]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "Location has to be formed by numbers");
				return true;
			}
			((Player) sender).teleport(new Location(((Player) sender).getWorld(), x, y, z));
		}
		return true;

	}

	public boolean repair() {
		if (isPlayer()) {
			ItemStack item = ((Player) sender).getItemInHand();
			if (item != null && listOfPossibleRepair.contains(item.getTypeId())) {
				item.setDurability((short) 0);
				sender.sendMessage("Your item " + ChatColor.RED + item.getType() + ChatColor.WHITE
						+ " have been successfully repaired.");
			} else
				sender.sendMessage("You can't repair this item : " + ChatColor.RED + item.getType());
		}
		return true;
	}

	public boolean repairAll(String[] args) {
		Player player = null;
		if (isPlayer()) {
			player = ((Player) sender);
			if (args != null && args.length >= 1)
				player = sender.getServer().getPlayer(args[0]);
		} else if (args != null && args.length >= 1)
			player = sender.getServer().getPlayer(args[0]);
		else
			sender.sendMessage("You must set the player name !");
		if (player != null) {
			for (ItemStack item : player.getInventory().getContents())
				if (item != null && listOfPossibleRepair.contains(item.getTypeId()))
					item.setDurability((short) 0);
			for (ItemStack item : player.getInventory().getArmorContents())
				if (item != null)
					item.setDurability((short) 0);

			sender.sendMessage("All " + player.getName() + "'s items have been repaired.");
		} else
			sender.sendMessage(ChatColor.RED + "No such player: " + ChatColor.WHITE + args[0]);

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
		if (!hasPerm(sender, "admincmd.item.noblacklist") && blacklist.contains(m.getId())) {
			sender.sendMessage(ChatColor.DARK_RED + "This item (" + ChatColor.WHITE + m.name()
					+ ChatColor.DARK_RED + ") is black listed.");
			return true;
		}
		// amount, damage and target player
		int cnt = 1;
		byte dam = 0;
		Player target = null;
		if (args.length >= 2) {
			try {
				cnt = Integer.parseInt(args[1]);
			} catch (Exception e) {
				return false;
			}
			if (args.length >= 3) {
				target = sender.getServer().getPlayer(args[2]);
				if (target == null) {
					sender.sendMessage(ChatColor.RED + "No such player: " + ChatColor.WHITE
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
		if (target == null) {
			if (isPlayer())
				target = ((Player) sender);
			else
				return true;
		}
		ItemStack stack = new ItemStack(m, cnt, dam);
		if (isPlayer(false)) {
			if (!target.getName().equals(((Player) sender).getName())) {
				target.sendMessage(ChatColor.RED + "[" + ((Player) sender).getName() + "]"
						+ ChatColor.WHITE + " send you " + ChatColor.GOLD + cnt + " " + m.name());

				sender.sendMessage(ChatColor.RED + "Added " + ChatColor.GOLD + cnt + " " + m.name()
						+ " to " + ChatColor.WHITE + target.getName() + "'s inventory");
			} else
				sender.sendMessage(ChatColor.RED + "Added " + ChatColor.GOLD + cnt + " " + m.name()
						+ " to " + ChatColor.WHITE + "your inventory");
		} else {
			target.sendMessage(ChatColor.RED + "[Server Admin]" + ChatColor.WHITE + " send you "
					+ ChatColor.GOLD + cnt + " " + m.name());
			sender.sendMessage(ChatColor.RED + "Added " + ChatColor.GOLD + cnt + " " + m.name()
					+ " to " + ChatColor.WHITE + target.getName() + "'s inventory");
		}
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

	private void weatherChange(World w, String type, String[] duration) {
		if (type == "clear") {
			w.setThundering(false);
			w.setStorm(false);
			sender.sendMessage(ChatColor.GOLD + "Sky cleared in world : " + w.getName());
		} else {
			if (duration == null || duration.length < 1) {
				w.setStorm(true);
				w.setWeatherDuration(12000);
				sender.sendMessage(ChatColor.GOLD + "Storm set for 10 mins in world : "
						+ w.getName());
			} else {
				try {
					w.setStorm(true);
					int time = Integer.parseInt(duration[0]);
					w.setWeatherDuration(time * 1200);
					sender.sendMessage(ChatColor.GOLD + "Storm set for " + time
							+ " mins in world : " + w.getName());
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.BLUE + "Sorry, that (" + duration[0]
							+ ") isn't a number!");
					w.setStorm(true);
					w.setWeatherDuration(12000);
					sender.sendMessage(ChatColor.GOLD + "Storm set for 10 mins in world : "
							+ w.getName());
				}
			}
		}
	}

	public boolean weather(String type, String[] duration) {
		if (isPlayer()) {
			weatherChange(((Player) sender).getWorld(), type, duration);
		} else
			for (World w : sender.getServer().getWorlds())
				weatherChange(w, type, duration);

		return true;
	}

	public boolean strikePlayer(String playerName) {
		Player p = null;
		if ((p = sender.getServer().getPlayer(playerName)) != null) {
			p.getWorld().strikeLightning(p.getLocation());
			sender.sendMessage(ChatColor.GOLD + p.getName() + " was striked by Thor");
		} else
			sender.sendMessage(ChatColor.RED + "No such player: " + ChatColor.WHITE + playerName);
		return true;
	}

	public boolean thor() {
		if (isPlayer()) {
			String player = ((Player) sender).getName();
			if (thunderGods.contains(player)) {
				thunderGods.remove(player);
				this.sender.sendMessage(ChatColor.DARK_PURPLE + "You have lost the power of Thor");
			} else {
				thunderGods.add(player);
				this.sender.sendMessage(ChatColor.DARK_PURPLE + "You have now the power of Thor");
			}
		}
		return true;
	}
	public boolean god() {
		if (isPlayer()) {
			String player = ((Player) sender).getName();
			if (gods.contains(player)) {
				gods.remove(player);
				this.sender.sendMessage(ChatColor.DARK_AQUA + "GOD mode disabled.");
			} else {
				gods.add(player);
				this.sender.sendMessage(ChatColor.DARK_AQUA + "GOD mode enabled.");
			}
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
		sender.sendMessage(ChatColor.BLUE + "You can now use " + ChatColor.GOLD + alias
				+ ChatColor.BLUE + " for the item " + ChatColor.GOLD + m.name());
		return true;
	}

	public boolean rmAlias(String alias) {
		this.fManager.removeAlias(alias);
		this.alias.remove(alias);
		sender.sendMessage(ChatColor.GOLD + alias + ChatColor.RED + " removed");
		return true;
	}

	public boolean spawnMob(String args[]) {
		if (isPlayer()) {

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
				sender.sendMessage(ChatColor.RED + "No such creature: " + ChatColor.WHITE + name);
				return true;
			}
			AdminCmd.getBukkitServer().getScheduler()
					.scheduleAsyncDelayedTask(pluginInstance, new Runnable() {

						@Override
						public void run() {
							for (int i = 0; i < nb; i++) {
								((Player) sender).getWorld().spawnCreature(
										((Player) sender).getLocation(), ct);
								try {
									Thread.sleep(110);
								} catch (InterruptedException e) {
									// e.printStackTrace();
								}
							}
							sender.sendMessage(ChatColor.BLUE + "Spawned " + ChatColor.WHITE + nb
									+ " " + name);
						}
					});
		}
		return true;
	}

	public boolean hasThorPowers(String player) {
		return thunderGods.contains(player);
	}
	
	public boolean hasGodPowers(String player) {
		return gods.contains(player);
	}

	// changes the color of a colorable item in hand
	public boolean itemColor(String color) {
		if (isPlayer()) {
			// help?
			if (color.equalsIgnoreCase("help")) {
				sender.sendMessage(ChatColor.RED + "Wool: " + ChatColor.WHITE
						+ printColors(Material.WOOL));
				sender.sendMessage(ChatColor.RED + "Dyes: " + ChatColor.WHITE
						+ printColors(Material.INK_SACK));
				sender.sendMessage(ChatColor.RED + "Logs: " + ChatColor.WHITE
						+ printColors(Material.LOG));
				sender.sendMessage(ChatColor.RED + "Slab: " + ChatColor.WHITE
						+ printColors(Material.STEP));
				return true;
			}
			// determine the value based on what you're holding
			short value = -1;
			Material m = ((Player) sender).getItemInHand().getType();

			if (materialsColors.containsKey(m))
				value = getColor(color, m);
			else {
				sender.sendMessage(ChatColor.RED + "You must hold a colorable material!");
				return true;
			}
			// error?
			if (value < 0) {
				sender.sendMessage(ChatColor.RED + "Color " + ChatColor.WHITE + color
						+ ChatColor.RED + " is not usable for what you're holding!");
				return true;
			}

			((Player) sender).getItemInHand().setDurability(value);
		}
		return true;
	}
	// ----- / item coloring section -----
}
