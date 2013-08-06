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
package be.Balor.Tools.CommandUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.PermissionException;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Player.EmptyPlayer;
import be.Balor.Player.PlayerManager;
import be.Balor.Tools.PlayerComparator;
import be.Balor.Tools.Type;
import be.Balor.Tools.Compatibility.ACMinecraftReflection;
import be.Balor.Tools.Compatibility.NMSBuilder;
import be.Balor.Tools.Compatibility.Reflect.MethodHandler;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import belgium.Balor.Workers.AFKWorker;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Antoine
 * 
 */
public final class Users {

	/**
	 * 
	 */
	private Users() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Get the ACPlayer, useful when working with only the AC user informations
	 * 
	 * @param sender
	 *            sender of the command
	 * @param args
	 *            args in the command
	 * @param permNode
	 *            permission node to execute the command
	 * @return null if the ACPlayer can't be get else the ACPlayer
	 * @throws PermissionException
	 * @throws PlayerNotFound
	 */
	public static ACPlayer getACPlayer(final CommandSender sender,
			final CommandArgs args, final String permNode)
			throws PlayerNotFound, ActionNotPermitedException {
		Player target;
		try {
			target = Users.getUser(sender, args, permNode, 0, false);
		} catch (final Exception ex) {
			target = null;
		}
		ACPlayer actarget;
		if (target == null) {
			if (args.length == 0) {
				sender.sendMessage("You must type the player name");
				return null;
			}
			final String playername = args.getString(0);
			actarget = Users.getACPlayer(sender, playername);
		} else {
			if (!Immunity.checkImmunity(sender, target)) {
				throw new PlayerNotFound(LocaleManager.I18n("insufficientLvl"), sender);
			}
			actarget = ACPlayer.getPlayer(target);
		}
		return actarget;
	}

	/**
	 * Get the ACPlayer, useful when working with only the AC user informations
	 * using the -P parameter.
	 * 
	 * @param sender
	 *            sender of the command
	 * @param args
	 *            args in the command
	 * @param permNode
	 *            permission node to execute the command
	 * @return null if the ACPlayer can't be get else the ACPlayer
	 * @throws PermissionException
	 * @throws PlayerNotFound
	 */
	public static ACPlayer getACPlayerParam(final CommandSender sender,
			final CommandArgs args, final String permNode)
			throws PlayerNotFound, ActionNotPermitedException {
		Player target;
		try {
			target = Users.getUserParam(sender, args, permNode);
		} catch (final Exception ex) {
			target = null;
		}
		ACPlayer actarget;
		if (target == null) {
			if (!args.hasFlag('P')) {
				throw new PlayerNotFound("You must type the player name!",
						sender);
			}
			final String playername = args.getValueFlag('P');
			actarget = Users.getACPlayer(sender, playername);
		} else {
			if (!Immunity.checkImmunity(sender, target)) {
				throw new PlayerNotFound(LocaleManager.I18n("insufficientLvl"), sender);
			}
			actarget = ACPlayer.getPlayer(target);
		}
		return actarget;
	}

	/**
	 * @param sender
	 * @param playername
	 * @return
	 * @throws PlayerNotFound
	 */
	public static ACPlayer getACPlayer(final CommandSender sender,
			final String playername) throws PlayerNotFound {
		ACPlayer actarget;
		actarget = ACPlayer.getPlayer(playername);
		if (actarget instanceof EmptyPlayer) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", playername);
			throw new PlayerNotFound(LocaleManager.I18n("playerNotFound", replace),
					sender);
		}
		if (!Immunity.checkImmunity(sender, actarget)) {
			throw new PlayerNotFound(LocaleManager.I18n("insufficientLvl"), sender);
		}
		return actarget;
	}

	/**
	 * Shortcut to online players.
	 * 
	 * @return
	 */
	public static List<Player> getOnlinePlayers() {
		return PlayerManager.getInstance().getOnlinePlayers();
	}

	public static Player getPlayer(final String name) {
		final Player[] players = ACPluginManager.getServer().getOnlinePlayers();
	
		Player found = null;
		final String lowerName = name.toLowerCase();
		int delta = Integer.MAX_VALUE;
		for (final Player player : players) {
			if (player.getName().toLowerCase().startsWith(lowerName)) {
				int curDelta = player.getName().length() - lowerName.length();
				if (curDelta < delta) {
					found = player;
					delta = curDelta;
				} else {
					curDelta = player.getDisplayName().length()
							- lowerName.length();
					if (curDelta < delta) {
						found = player;
						delta = curDelta;
					}
				}
				if (curDelta == 0) {
					break;
				}
			}
		}
		return found;
	
	}

	public static String getPlayerName(final Player player) {
		return Users.getPlayerName(player, null);
	}

	/**
	 * For compatibility
	 * 
	 * @param player
	 * @param sender
	 * @param withPrefix
	 * @return
	 */
	public static String getPlayerName(final Player player,
			final CommandSender sender, final boolean withPrefix) {
		return Users.getPlayerName(player, sender);
	}

	/**
	 * Get the complete player name with all prefix
	 * 
	 * @param player
	 *            player to get the name
	 * @param sender
	 *            sender that want the name
	 * @return the complete player name with prefix
	 */
	public static String getPlayerName(final Player player,
			final CommandSender sender) {
		assert (player != null);
		if (ConfigEnum.USE_PREFIX.getBoolean()) {
			final String prefix = Materials.colorParser(Users.getPrefix(player,
					sender));
			final String suffix = Materials.colorParser(PermissionManager
					.getSuffix(player));
			if (ConfigEnum.DNAME.getBoolean()) {
				return prefix + player.getDisplayName() + suffix
						+ ChatColor.YELLOW;
			}
	
			return prefix + player.getName() + suffix + ChatColor.YELLOW;
		}
	
		if (ConfigEnum.DNAME.getBoolean()) {
			return player.getDisplayName();
		}
	
		return player.getName();
	}

	/**
	 * Get the prefix of the player, by checking the right the sender have
	 * 
	 * @param player
	 * @return
	 */
	public static String getPrefix(final Player player,
			final CommandSender sender) {
		boolean isInv = false;
		String prefixstring = "";
		String statusPrefix = "";
		if (sender != null) {
			isInv = InvisibleWorker.getInstance().hasInvisiblePowers(player)
					&& PermissionManager.hasPerm(sender,
							"admincmd.invisible.cansee", false);
		}
		if (isInv) {
			statusPrefix = LocaleManager.I18n("invTitle");
		}
		if (AFKWorker.getInstance().isAfk(player)) {
			statusPrefix = LocaleManager.I18n("afkTitle") + statusPrefix;
		}
		prefixstring = PermissionManager.getPrefix(player);
		String result = statusPrefix;
		if (prefixstring != null && prefixstring.length() > 1) {
			result += prefixstring;
		}
		return Materials.colorParser(result);
	
	}

	public static Player getUser(final CommandSender sender,
			final CommandArgs args, final String permNode)
			throws PlayerNotFound, ActionNotPermitedException {
		return Users.getUser(sender, args, permNode, 0, true);
	}

	/**
	 * Get the user and check who launched the command.
	 * 
	 * @param sender
	 * @param args
	 * @param permNode
	 * @param index
	 * @param errorMsg
	 * @return
	 * @throws PlayerNotFound
	 * @throws PermissionException
	 */
	public static Player getUser(final CommandSender sender,
			final CommandArgs args, final String permNode, final int index,
			final boolean errorMsg) throws PlayerNotFound,
			ActionNotPermitedException {
		Player target = null;
		if (args.length >= index + 1) {
			target = getPlayer(args.getString(index));
			if (target != null) {
				if (target.equals(sender)) {
					return target;
				} else if (PermissionManager.hasPerm(sender, permNode
						+ ".other", false)) {
					if (Immunity.checkImmunity(sender, target)) {
						return target;
					} else {
						throw new PlayerNotFound(LocaleManager.I18n("insufficientLvl"),
								sender);
					}
				} else {
					throw new PermissionException(sender, permNode
							+ ".other");
				}
			}
		} else if (Users.isPlayer(sender, false)) {
			target = ((Player) sender);
		} else if (errorMsg) {
			sender.sendMessage("You must type the player name");
			return target;
		}
		if (target == null && errorMsg) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", args.getString(index));
			throw new PlayerNotFound(LocaleManager.I18n("playerNotFound", replace),
					sender);
		}
		return target;
	
	}

	/**
	 * Get the user using the -P param as indicating the userName and check who
	 * launched the command.
	 * 
	 * @param sender
	 *            sender of the command
	 * @param args
	 *            argument of the command
	 * @param permNode
	 *            permission to run the command
	 * @return target player if found
	 * @throws PlayerNotFound
	 *             if the target player is not found
	 * @throws PermissionException
	 *             if the player don't have the permission
	 */
	public static Player getUserParam(final CommandSender sender,
			final CommandArgs args, final String permNode)
			throws PlayerNotFound, ActionNotPermitedException {
		return Users.getUserParam(sender, args, permNode, true);
	}

	/**
	 * Get the user using the -P param as indicating the userName and check who
	 * launched the command.
	 * 
	 * @param sender
	 *            sender of the command
	 * @param args
	 *            argument of the command
	 * @param permNode
	 *            permission to run the command
	 * @param errorMsg
	 *            send or not the exception about an unfound player
	 * @return target player if found
	 * @throws PlayerNotFound
	 *             if the target player is not found
	 * @throws PermissionException
	 *             if the player don't have the permission
	 */
	public static Player getUserParam(final CommandSender sender,
			final CommandArgs args, final String permNode,
			final boolean errorMsg) throws PlayerNotFound,
			ActionNotPermitedException {
		Player target = null;
		final String playerName = args.getValueFlag('P');
		if (playerName != null) {
			target = getPlayer(playerName);
			if (target != null) {
				if (target.equals(sender)) {
					return target;
				} else if (PermissionManager.hasPerm(sender, permNode
						+ ".other", false)) {
					if (Immunity.checkImmunity(sender, target)) {
						return target;
					} else {
						throw new PlayerNotFound(LocaleManager.I18n("insufficientLvl"),
								sender);
					}
				} else {
					throw new PermissionException(sender, permNode
							+ ".other");
				}
			}
		} else if (Users.isPlayer(sender, false)) {
			target = ((Player) sender);
		} else if (errorMsg) {
			sender.sendMessage("You must type the player name");
			return target;
		}
		if (target == null && errorMsg) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", playerName);
			throw new PlayerNotFound(LocaleManager.I18n("playerNotFound", replace),
					sender);
		}
		return target;
	}

	/**
	 * Check if the command sender is a Player
	 * 
	 * @return
	 */
	public static boolean isPlayer(final CommandSender sender) {
		return Users.isPlayer(sender, true);
	}

	/**
	 * Checks if the command sender is a Player. Sends the sender an error
	 * message if he is not a player.
	 * 
	 * @param sender
	 * @param msg
	 *            - If {@code true} an error message will be sent.
	 * @return
	 */
	public static boolean isPlayer(final CommandSender sender, final boolean msg) {
		if (sender instanceof Player) {
			return true;
		} else {
			if (msg) {
				LocaleManager.sI18n(sender, "mustBePlayer");
			}
			return false;
		}
	}

	/**
	 * Remove the player from the online list (TAB key)
	 * 
	 * @param player
	 *            player to remove
	 */
	public static void removePlayerFromOnlineList(final Player player) {
		final Object server = ACMinecraftReflection.getHandle(player
				.getServer());
		final MethodHandler sendAll = new MethodHandler(server.getClass(),
				"sendAll", ACMinecraftReflection.getPacketClass());
		sendAll.invoke(server,
				NMSBuilder.buildPacket201PlayerInfo(player, false, 9999));
	}

	/**
	 * Broadcast message to every user since the bukkit one is bugged
	 * 
	 * @param message
	 */
	public static void broadcastMessage(final String message) {
		if (message == null) {
			return;
		}
		for (final Player p : getOnlinePlayers()) {
			p.sendMessage(message);
			// new ColouredConsoleSender((CraftServer)
			// ACPluginManager.getServer()).sendMessage(message);
		}
	}

	public static void sendMessage(final CommandSender sender,
			final CommandSender player, final String key) {
		Users.sendMessage(sender, player, key, null);
	}

	public static void sendMessage(final CommandSender sender,
			final CommandSender player, final String key,
			final Map<String, String> replace) {
		final String msg = LocaleManager.I18n(key, replace);
		if (msg != null && !msg.isEmpty()) {
			if (!sender.equals(player)) {
				player.sendMessage(msg);
			}
			sender.sendMessage(msg);
		}
	
	}

	/**
	 * Get the player list ordered by group and alphabetically for the sender
	 * 
	 * @param sender
	 *            sender of the command
	 * @return a Collection containing what to display
	 */
	public static Collection<String> getPlayerList(final CommandSender sender) {
		final List<Player> online = getOnlinePlayers();
		final Map<Player, String> players = new TreeMap<Player, String>(
				new PlayerComparator());
		for (final Player p : online) {
			if ((InvisibleWorker.getInstance().hasInvisiblePowers(p) || ACPlayer
					.getPlayer(p).hasPower(Type.FAKEQUIT))
					&& !PermissionManager.hasPerm(sender,
							"admincmd.invisible.cansee", false)) {
				continue;
			}
			players.put(p, getPlayerName(p, sender));
		}
		return Collections.unmodifiableCollection(players.values());
	}

}
