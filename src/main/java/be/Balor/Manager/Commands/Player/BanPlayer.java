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
package be.Balor.Manager.Commands.Player;

import java.util.HashMap;
import java.util.regex.Matcher;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.NotANumberException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Player.ACPlayer;
import be.Balor.Player.Ban;
import be.Balor.Player.BannedIP;
import be.Balor.Player.BannedPlayer;
import be.Balor.Player.IBan;
import be.Balor.Player.ITempBan;
import be.Balor.Player.TempBannedIP;
import be.Balor.Player.TempBannedPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.Tools.CommandUtils.Immunity;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Threads.KickTask;
import be.Balor.Tools.Threads.UnBanTask;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class BanPlayer extends PlayerCommand {

	/**
	 *
	 */
	public BanPlayer() {
		permNode = "admincmd.player.ban";
		cmdName = "bal_ban";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		DebugLog.beginInfo("Banning : " + args.getString(0));
		try {
			final Player toBan = Users.getPlayer(args.getString(0));
			final HashMap<String, String> replace = new HashMap<String, String>();
			String message = null;
			String banPlayerString;
			if (toBan != null) {
				banPlayerString = toBan.getName();
				if (!Immunity.checkImmunity(sender, toBan)) {
					LocaleManager.sI18n(sender, "insufficientLvl");
					return;
				}
			} else {
				banPlayerString = args.getString(0);
			}
			Integer tmpBan = null;
			if (args.length >= 2) {
				if (args.hasFlag('m')) {
					message = LocaleManager.getInstance().get("kickMessages",
							args.getValueFlag('m'), "player", banPlayerString);
				}
				tmpBan = checkTempBan(args, banPlayerString, sender);
				if (tmpBan != null && tmpBan == -1) {
					return;
				}
			}
			message = parseMessage(args, message, tmpBan == null, sender);
			replace.put("player", banPlayerString);
			replace.put("reason", message);
			final IBan ban = getBanType(banPlayerString, tmpBan, message,
					sender, replace);

			if (ban == null) {
				return;
			}
			ACHelper.getInstance().banPlayer(ban);
			if (ban instanceof ITempBan) {
				ACPluginManager.getScheduler().runTaskLaterAsynchronously(
						getPlugin(), new UnBanTask((ITempBan) ban, true),
						Utils.secInTick * 60 * tmpBan);
				message += " " + ((ITempBan) ban).getReadableTimeLeft();
			}
			if (ConfigEnum.ADD_BANNER_IN_BAN.getBoolean()) {
				message += " by " + ban.getBanner();
			}
			if (toBan != null) {
				ACPlayer.getPlayer(toBan).setPower(Type.KICKED);
				new KickTask(toBan, message).scheduleSync();
			}
			Users.broadcastMessage(LocaleManager.I18n("ban", replace));
		} finally {
			DebugLog.endInfo();
		}

	}

	/**
	 * @param banPlayerString
	 * @param tmpBan
	 * @param message
	 * @param sender
	 * @param replace
	 * @return
	 */
	private IBan getBanType(final String banPlayerString, final Integer tmpBan,
			final String message, final CommandSender sender,
			final HashMap<String, String> replace) {
		DebugLog.beginInfo("Get the ban type");
		try {
			final Matcher ipv4 = Utils.REGEX_IP_V4.matcher(banPlayerString);
			final Matcher inaccurateIp = Utils.REGEX_INACCURATE_IP_V4
					.matcher(banPlayerString);
			Ban toDo;
			if (tmpBan != null) {
				DebugLog.addInfo("Temp ban for: " + tmpBan);
				replace.put("reason", message);
				if (inaccurateIp.find()) {
					if (!ipv4.find()) {
						replace.clear();
						replace.put("ip", banPlayerString);
						LocaleHelper.INACC_IP.sendLocale(sender, replace);
						return null;
					}
					toDo = new TempBannedIP(banPlayerString, message,
							tmpBan * 60);
				} else {
					toDo = new TempBannedPlayer(banPlayerString, message,
							tmpBan * 60);
				}
				DebugLog.addInfo("Banned for : "
						+ ((ITempBan) toDo).getReadableTimeLeft());

			} else {
				DebugLog.addInfo("Permanent ban");
				if (inaccurateIp.find()) {
					if (!ipv4.find()) {
						replace.clear();
						replace.put("ip", banPlayerString);
						LocaleHelper.INACC_IP.sendLocale(sender, replace);
						return null;
					}
					toDo = new BannedIP(banPlayerString, message);
				} else {
					toDo = new BannedPlayer(banPlayerString, message);
				}
			}
			if (!Users.isPlayer(sender, false)) {
				toDo.setBanner("Server Admin");
			} else {
				toDo.setBanner(ChatColor.stripColor(Users
						.getPlayerName((Player) sender)));
			}
			return toDo;
		} finally {
			DebugLog.endInfo();
		}
	}

	/**
	 * @param args
	 * @param message
	 * @param b
	 * @return
	 */
	private String parseMessage(final CommandArgs args, String message,
			final boolean tempBan, final CommandSender sender) {
		if (message == null || (message != null && message.isEmpty())) {
			message = "";
			if (tempBan) {
				for (int i = 1; i < args.length; i++) {
					message += args.getString(i) + " ";
				}
			} else {
				for (int i = 1; i < args.length - 1; i++) {
					message += args.getString(i) + " ";
				}
			}
		}
		message = message.trim();
		if (message.isEmpty()) {
			message = LocaleHelper.DEFAULT_BAN_MESSAGE.getLocale();
		}
		return message;
	}

	/**
	 * @param args
	 * @param banPlayerString
	 * @param sender
	 * @return
	 */
	private Integer checkTempBan(final CommandArgs args,
			final String banPlayerString, final CommandSender sender) {
		DebugLog.beginInfo("Check for tempBan");
		try {
			final int tmpIntTime = Utils.timeParser(args
					.getString(args.length - 1));
			if (tmpIntTime != -1) {
				DebugLog.addInfo("Time found in minute : " + tmpIntTime);
				return tmpIntTime;
			}
		} catch (final NotANumberException e) {
			LocaleManager.sI18n(sender, "NaN", "number",
					args.getString(args.length - 1));
			return -1;
		} catch (final Exception ex) {
		} finally {
			DebugLog.endInfo();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null && args.length >= 1;
	}

}
