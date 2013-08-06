/************************************************************************
 * This file is part of AdminCmd.
 *
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdminCmd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/
package be.Balor.bukkit.AdminCmd;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import be.Balor.Manager.LocaleManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public enum LocaleHelper {
	ITEMID("itemId", ChatColor.GOLD + "%player" + ChatColor.GRAY + " is holding " + ChatColor.GREEN + "%item" + ChatColor.WHITE + ":" + ChatColor.RED + "%data"),
	EGG_PARAM("eggCmdParam", ChatColor.RED + "The parameter " + ChatColor.GOLD + "-t" + ChatColor.RED + " is needed to choose the egg."),
	EGG_PARAM_BLOCK("eggBlockParam", ChatColor.RED + "The parameter " + ChatColor.GOLD + "-b" + ChatColor.RED
			+ " is needed to choose the block to use for replacement."),
	EGG_PARAM_ENTITY("eggEntityParam", ChatColor.RED + "The parameter " + ChatColor.GOLD + "-e" + ChatColor.RED
			+ " is needed to choose the entity id to spawn."),
	EGG_PARAM_MOB("eggMobParam", ChatColor.RED + "The parameter " + ChatColor.GOLD + "-m" + ChatColor.RED + " is needed to choose the mob to spawn."),
	DONT_EXISTS("dontExists", ChatColor.RED + "The %type " + ChatColor.GOLD + "%value " + ChatColor.RED + "don't exists !"),
	CANT_ENCHANT("cantEnchantItem", ChatColor.RED + "This item " + ChatColor.GOLD + "%item" + ChatColor.RED + " can't be enchanted with " + ChatColor.GREEN
			+ "%enchant"),
	SUCCESS_ENCHANT("echantmentsAdded", ChatColor.GREEN + "Your item " + ChatColor.GOLD + "%item" + ChatColor.GREEN + " is now enchanted !"),
	HIGH_EFFECT("highEggEffect", ChatColor.GREEN + "You are now " + ChatColor.GOLD + "HIGH" + ChatColor.GREEN + ", prepare to see ... the world differently !"),
	POTION_EFFECT("potionEffect", ChatColor.MAGIC + "magic" + ChatColor.AQUA + "%player" + ChatColor.LIGHT_PURPLE + " had drink the potion" + ChatColor.GOLD
			+ " %potion !" + ChatColor.MAGIC + "magic"),
	SPYMSG_TITLE("spymsgTitle", ChatColor.GREEN + "[SpyMsg]" + ChatColor.WHITE),
	SPYMSG_HEADER("spymsgMessageHeader", "#spymsgTitle# " + "%sender" + "-" + "%receiver" + ChatColor.WHITE + ": "),
	TYPE_ENCHANTMENT("dontExistsType.enchant", "enchantment"),
	TYPE_POTION("dontExistsType.potion", "potion"),
	TYPE_CMD("dontExistsType.cmd", "command"),
	TYPE_MAT("dontExistsType.mat", "material"),
	PLAYER_KICKED("playerKicked", ChatColor.GOLD + "%player " + ChatColor.YELLOW + "has been kicked from the Server, reason:" + ChatColor.RED + " %reason"),
	PLAYER_QUITCMD_MSG("quitCmdMsg", ChatColor.GOLD + "%player " + ChatColor.YELLOW + "left the game: %reason"),
	WORLD_NOT_LOADED("worldNotLoaded", ChatColor.RED + "This World is not loaded :" + ChatColor.GOLD + " %message"),
	MISSING_ARG("missingArg", ChatColor.RED + "You must provide a %arg as an argument to use the" + " command " + ChatColor.AQUA + "%cmdName"),
	TP_DIM("dimensionTeleport", ChatColor.GREEN + "You were successfully teleported to world %world!"),
	TP_DIM_LIST("dimTpList", ChatColor.GREEN + "Worlds which are currently available to teleport to: " + " //n " + ChatColor.GOLD + "%list"),
	TP_ALL("tpAll", ChatColor.GREEN + "You have been successfully teleported to " + ChatColor.GOLD + "%loc" + ChatColor.GREEN + " by " + ChatColor.GOLD
			+ "%sender"),
	BL_ITEM_ALREADY("itemBlAlready", ChatColor.RED + "This item " + ChatColor.GOLD + "%item " + ChatColor.RED + "is already blacklisted"),
	BL_ITEM_PROBLEM("blItemSaveProb", ChatColor.GOLD + "Can't save the blackList file"),
	BL_ITEM_NOT_BLISTED("blItemNotBl", ChatColor.RED + "This item " + ChatColor.GOLD + "%item" + ChatColor.RED + " is not black listed."),
	OLD_BANLIST("banList", ChatColor.GOLD + "%player, " + ChatColor.AQUA + "Banned for :" + ChatColor.GOLD + " %reason on %date"),
	BANLIST("banListUpdated", "#banList# by " + ChatColor.RED + "%banner"),
	INACC_IP("inaccurateIp", ChatColor.RED + "This IP " + ChatColor.GOLD + "%ip " + ChatColor.RED + "is incorrect !"),
	NO_BAN_FOUND("notBanned", ChatColor.GOLD + "No ban found for" + ChatColor.RED + " %ban"),
	P_CLEARED_SENDER("powersClearedSender", ChatColor.GREEN + "Successfully removed all powers from %target" + "!"),
	P_CLEARED_TARGET("powersClearedTarget", ChatColor.AQUA + "All of your active powers have been removed by" + ChatColor.GOLD + " %sender" + ChatColor.AQUA
			+ "!"),
	P_CLEARED("powersCleared", ChatColor.GREEN + "You have successfully removed all your powers!"),
	MUTELIST("muteList", ChatColor.GOLD + "%player, " + ChatColor.AQUA + "%msg"),
	NO_MUTED("noMutedPlayers", ChatColor.RED + "There isn't any muted players."),
	NO_BANNED("noBannedPlayers", ChatColor.RED + "There isn't any banned players."),
	UNMUTED_PLAYERS("unMutedPlayers", ChatColor.AQUA + "%nb " + ChatColor.GREEN + "players have been unmuted."),
	TXT_RELOADED("allTxtReloaded", ChatColor.GREEN + "All Text File have been successfully reloaded."),
	KILLED("kill", ChatColor.RED + "killed"),
	FEEDED("feed", ChatColor.DARK_PURPLE + "feeded"),
	HEALED("heal", ChatColor.MAGIC + "aaaa" + ChatColor.GREEN + "healed" + ChatColor.MAGIC + "aaaa"),
	NEW_STATE("nState", ChatColor.GOLD + "You have been "),
	NEW_STATE_PLAYER("pNewState", ChatColor.GOLD + "%player has been "),
	SUICIDE("suicide", ChatColor.RED + "You commited suicide."),
	BCAST("broadcast", ChatColor.YELLOW + "[BROADCAST]" + ChatColor.GOLD + "%message"),
	UNKNOWN("unknown", "unknown"),
	IP_BROADCAST("ipBroadcast", ChatColor.GOLD + "%player " + ChatColor.RED + "joined from the same IP" + ChatColor.GOLD + " as %player2" + ChatColor.RED
			+ ". The IP is: " + ChatColor.AQUA + "%ip"),
	GROUP_SPAWN_SET("groupSpawnSet", ChatColor.GREEN + "Spawn for group" + ChatColor.GOLD + "%groupName" + ChatColor.GREEN + " set."),
	GROUP_SPAWN("groupSpawn", ChatColor.GREEN + "You haven been successfully teleported to the" + " spawn of group: " + ChatColor.GOLD + "%groupName"),
	NO_GROUP_SPAWN("noGroupSpawn", ChatColor.RED + "The group " + ChatColor.AQUA + "%groupName" + ChatColor.RED + " does not have a group spawn"),
	NO_SUCH_GROUP("noSuchGroup", ChatColor.RED + "A group with the name " + ChatColor.AQUA + "%groupName" + ChatColor.RED + " does not exist."),
	DEATH_MSG("deathMessage", ChatColor.AQUA + "%player " + ChatColor.YELLOW + "%msg"),
	TELEPORT_SOON("tpSoonMsg", ChatColor.AQUA + "You'll be teleported to your location in " + ChatColor.GREEN + "%sec seconds"),
	AFK_KICK_BCAST("afkKickBroadcast", ChatColor.AQUA + "%player" + ChatColor.GOLD + " has been kicked out of the game due to being AFK too long."),
	NOT_A_BLOCK("notABlock", ChatColor.RED + "You did not enter a valid block id!"),
	WARP_NO_PERM("noWarpPerm", ChatColor.RED + "You do not have the permission to " + "teleport to the following warp point: " + ChatColor.GOLD + "%point"),
	MUTE_BROADCAST("MuteBroadcast", ChatColor.RED + "%player has been muted" + " by %muter %time, reason: %reason"),
	PLAYER_NOT_MUTED("notMuted", ChatColor.GOLD + "%player " + ChatColor.RED + "is not muted."),
	TIME_LEFT("tempBanTimeLeft", "Time left: %d d %h h %m m %s s"),
	DEFAULT_BAN_MESSAGE("defaultBanMessage", "You have been banned from the server !"),
	NEW_VERSION("newVersionFound", "There is a new version of AdminCmd on www.admincmd.com : %version"),
	PTIME_SET("playerTime", ChatColor.GREEN + "The time for the player " + ChatColor.GOLD + "%player" + ChatColor.GREEN + " is now set to " + ChatColor.AQUA
			+ "%time"),
	CANT_COLOR("itemCantColor", ChatColor.RED + "This items " + ChatColor.GOLD + "(%item)" + ChatColor.RED + " can't be colored ! Possible Items: "
			+ ChatColor.WHITE + "%items"),
	COLOR_D_EXISTS("colorDontExists", ChatColor.RED + "This color " + ChatColor.GOLD + "(%color)" + ChatColor.RED + " don't exists. Possible Colors : "
			+ ChatColor.WHITE + "%colors"),
	ENCHANT_EXIST("enchantExists", ChatColor.GOLD + "%enchant" + ChatColor.RED + " does't exist or is already presents on the item."),
	ENCHANT_CONFLICT("enchantConflict", ChatColor.RED + "Conflict between 2 enchantments : " + ChatColor.WHITE + "%e1 and " + ChatColor.GOLD + "%e2"),
	WALK_SPEED_SET("walkspeed.success", ChatColor.GREEN + "The walkspeed of " + ChatColor.GOLD + "%player" + ChatColor.GREEN + " is now set to "
			+ ChatColor.WHITE + "%value"),
	WALK_SPEED_ERROR("walkspeed.error", ChatColor.RED + "The Walkspeed must be between -1 and 1 and could be decimal (0.8545)."),
	WALK_SPEED_GET("walkspeed.get", ChatColor.YELLOW + "The WalkSpeed of " + ChatColor.GOLD + "%player " + ChatColor.YELLOW + "is " + ChatColor.GOLD + "%value"),
	REPLACE_KIT_NOT_PERM("kit.notPermReplace", ChatColor.RED + "You don't have the permission to replace an existing kit."),
	ARMORED_KIT("kit.armored", ChatColor.GOLD + "Armored Kit"),
	NORMAL_KIT("kit.normal", ChatColor.GOLD + "Normal Kit"),
	REMOVE_SP("power.remove", ChatColor.GREEN + "Removed all super powers of " + ChatColor.DARK_PURPLE + "%player"),
	REMOVED_SP("power.removed", ChatColor.YELLOW + "Removed powers : " + ChatColor.GOLD + "%powers"),
	SUCCESS_DYN_KIT("kit.successDynKit", ChatColor.GREEN + "The %type" + ChatColor.GREEN + " named " + ChatColor.GOLD + "%kit" + ChatColor.GREEN
			+ " has been successfully created."),
	ERROR_DOT_HOME("home.dotError", ChatColor.RED + "You can't use the " + ChatColor.GOLD + "dot (.)" + ChatColor.RED + " in a home name."),
	ERROR_LIMIT_REACHED("home.limitReached", ChatColor.RED + "You have reached the limit of home, to continue use this feature, delete " + ChatColor.GOLD
			+ "%number home(s)."),
	ERROR_EXTERNAL_WARP("warp.nonPlayer", ChatColor.RED + "You have to set the world name in the command. " + ChatColor.GOLD + "Eg : /twp world:test"),
	ERROR_HOLD_ITEM("item.holdError", ChatColor.RED + "You have to hold an item in hand to do that."),
	ERROR_NOT_ONLINE("power.notOnline", ChatColor.DARK_PURPLE + "%player" + ChatColor.RED
			+ " is not online. It's needed to check his permissions to remove his powers. Use -n to remove ALL powers of " + ChatColor.DARK_PURPLE + "%player");

	private final String key;
	private final String locale;

	private LocaleHelper(final String key, final String locale) {
		this.key = key;
		this.locale = locale;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	public static void addAllLocales() {
		for (final LocaleHelper lh : values()) {
			LocaleManager.getInstance().addLocale(lh.key, lh.locale);
		}
	}

	/**
	 * Send the locale the the CommandSender
	 * 
	 * @param sender
	 *            CommandSender that will get the message
	 * @param replace
	 *            Replacement variables
	 */
	public void sendLocale(final CommandSender sender, final Map<String, String> replace) {
		LocaleManager.sI18n(sender, this, replace);
	}

	/**
	 * Send the locale the the CommandSender
	 * 
	 * @param sender
	 *            CommandSender that will get the message
	 * @param key
	 *            key to replace
	 * @param value
	 *            value to replace the key
	 */
	public void sendLocale(final CommandSender sender, final String key, final String value) {
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put(key, value);
		LocaleManager.sI18n(sender, this, replace);
	}

	/**
	 * Send the locale the the CommandSender
	 * 
	 * @param sender
	 *            CommandSender that will get the messages
	 */
	public void sendLocale(final CommandSender sender) {
		LocaleManager.sI18n(sender, this);
	}

	/**
	 * Get the locale with replacement variables
	 * 
	 * @param replace
	 *            Replacement variables
	 * @return the locale with the variables replaced in.
	 */
	public String getLocale(final Map<String, String> replace) {
		return LocaleManager.I18n(this, replace);
	}

	/**
	 * Get the locale without replacement variables
	 * 
	 * @return the locale
	 */
	public String getLocale() {
		return LocaleManager.I18n(this);
	}

}
