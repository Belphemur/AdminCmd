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

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Player.IBan;
import be.Balor.Player.ITempBan;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Help.String.ACMinecraftFontWidthCalculator;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import be.Balor.bukkit.AdminCmd.LocaleHelper;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Whois extends PlayerCommand {
	/**
	 *
	 */
	public Whois() {
		super("bal_whois", "admincmd.player.whois");
		other = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
	 * CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		if (args.hasFlag('w')) {
			ACWorld w;
			try {
				if (args.length >= 1) {
					w = ACWorld.getWorld(args.getString(0));
				} else if (Utils.isPlayer(sender)) {
					w = ACWorld
							.getWorld(((Player) sender).getWorld().getName());
				} else {
					return;
				}
			} catch (final WorldNotLoaded e) {
				Utils.sI18n(sender, "worldNotFound", "world", args.getString(0));
				return;
			}
			sender.sendMessage(ChatColor.GREEN
					+ ACMinecraftFontWidthCalculator.strPadCenterChat(
							ChatColor.AQUA + " " + w.getName() + " "
									+ ChatColor.GREEN, '='));
			for (final Entry<String, String> power : w.getInformationsList()
					.entrySet()) {
				String line = ChatColor.GOLD + power.getKey() + ChatColor.WHITE
						+ " : ";
				final int sizeRemaining = ACMinecraftFontWidthCalculator.chatwidth
						- ACMinecraftFontWidthCalculator.getStringWidth(line);
				line += ACMinecraftFontWidthCalculator.strPadLeftChat(
						ChatColor.GREEN + power.getValue(), sizeRemaining, ' ');
				sender.sendMessage(line);
			}
			return;
		}
		final ACPlayer actarget = Utils.getACPlayer(sender, args, permNode);
		if (actarget == null) {
			return;
		}
		sender.sendMessage(ChatColor.AQUA
				+ ACMinecraftFontWidthCalculator.strPadCenterChat(
						ChatColor.DARK_GREEN
								+ " "
								+ (actarget.isOnline()
										? Utils.getPlayerName(
												actarget.getHandler(), sender)
										: actarget.getName()) + " "
								+ ChatColor.AQUA, '='));
		// Login
		String loginDate = ChatColor.GOLD + "Last Login" + ChatColor.WHITE
				+ " : ";
		final int logSizeRemaining = ACMinecraftFontWidthCalculator.chatwidth
				- ACMinecraftFontWidthCalculator.getStringWidth(loginDate);
		loginDate += ACMinecraftFontWidthCalculator.strPadLeftChat(
				ChatColor.GREEN
						+ Utils.replaceDateAndTimeFormat(actarget,
								Type.Whois.LOGIN), logSizeRemaining, ' ');
		sender.sendMessage(loginDate);

		// Logout
		String logoutDate = ChatColor.GOLD + "Last Quit" + ChatColor.WHITE
				+ " : ";
		final int logoutSizeRemaining = ACMinecraftFontWidthCalculator.chatwidth
				- ACMinecraftFontWidthCalculator.getStringWidth(logoutDate);
		logoutDate += ACMinecraftFontWidthCalculator.strPadLeftChat(
				ChatColor.GREEN
						+ Utils.replaceDateAndTimeFormat(actarget,
								Type.Whois.LOGOUT), logoutSizeRemaining, ' ');
		sender.sendMessage(logoutDate);

		// Presentation
		String presentation = ChatColor.GOLD + "Presentation" + ChatColor.WHITE
				+ " : ";
		final int presSizeRemaining = ACMinecraftFontWidthCalculator.chatwidth
				- ACMinecraftFontWidthCalculator.getStringWidth(presentation);
		presentation += ACMinecraftFontWidthCalculator.strPadLeftChat(
				ChatColor.GREEN + actarget.getPresentation(),
				presSizeRemaining, ' ');
		sender.sendMessage(presentation);

		// Played
		final long total = actarget.getCurrentPlayedTime();
		final Map<String, String> replace = Utils.playedTime(
				actarget.getName(), total);
		String played = ChatColor.GOLD + "Time Played" + ChatColor.WHITE
				+ " : ";
		int strSizeRem = ACMinecraftFontWidthCalculator.chatwidth
				- ACMinecraftFontWidthCalculator.getStringWidth(played);
		played += ACMinecraftFontWidthCalculator.strPadLeftChat(ChatColor.GREEN
				+ Utils.I18n("elapsedTotalTime", replace), strSizeRem, ' ');
		sender.sendMessage(played);
		// Banned
		final IBan ban = ACHelper.getInstance().getBan(actarget.getName());
		if (ban != null) {
			String banned = ChatColor.GOLD + "Banned" + ChatColor.WHITE + " : ";
			final int banSizeRem = ACMinecraftFontWidthCalculator.chatwidth
					- ACMinecraftFontWidthCalculator.getStringWidth(banned);
			banned += ACMinecraftFontWidthCalculator.strPadLeftChat(
					ChatColor.GREEN
							+ (ban instanceof ITempBan ? Utils
									.replaceDateAndTimeFormat(((ITempBan) ban)
											.getEndBan()) : "Permanent"),
					banSizeRem, ' ');
			sender.sendMessage(banned);
		}
		// Powers
		for (final Entry<String, String> power : actarget.getPowersString()
				.entrySet()) {
			final Type powerType = Type.matchType(power.getKey());
			if (powerType == Type.INVISIBLE) {
				continue;
			}
			String line = ChatColor.GOLD + powerType.display()
					+ ChatColor.WHITE + " : ";
			final int sizeRemaining = ACMinecraftFontWidthCalculator.chatwidth
					- ACMinecraftFontWidthCalculator.getStringWidth(line);
			line += ACMinecraftFontWidthCalculator.strPadLeftChat(
					ChatColor.GREEN + power.getValue(), sizeRemaining, ' ');
			sender.sendMessage(line);
		}
		// Invisible
		String line = ChatColor.GOLD + "invisible" + ChatColor.WHITE + " : ";
		final int sizeRemaining = ACMinecraftFontWidthCalculator.chatwidth
				- ACMinecraftFontWidthCalculator.getStringWidth(line);
		line += ACMinecraftFontWidthCalculator
				.strPadLeftChat(
						ChatColor.GREEN
								+ String.valueOf((InvisibleWorker.getInstance()
										.hasInvisiblePowers(
												actarget.getHandler()) && PermissionManager
										.hasPerm(sender,
												"admincmd.invisible.cansee",
												false))), sizeRemaining, ' ');
		sender.sendMessage(line);

		// Immunity Level
		final int level = actarget.getInformation("immunityLvl").getInt(
				ConfigEnum.DIMMUNITY.getInt());
		String immuLvl = ChatColor.GOLD + "Immunity Level" + ChatColor.WHITE
				+ " : ";
		strSizeRem = ACMinecraftFontWidthCalculator.chatwidth
				- ACMinecraftFontWidthCalculator.getStringWidth(immuLvl);
		immuLvl += ACMinecraftFontWidthCalculator.strPadLeftChat(
				ChatColor.GREEN + String.valueOf(level), strSizeRem, ' ');
		sender.sendMessage(immuLvl);

		// GameMode
		String gameMode = ChatColor.GREEN + "Current GameMode: "
				+ ChatColor.GOLD;
		String currentMode = "";
		if (actarget.isOnline()) {
			if (actarget.getHandler().getGameMode() == GameMode.CREATIVE) {
				currentMode = "Creative";
			} else {
				currentMode = "Survival";
			}
		} else {
			currentMode = actarget.getInformation("gameMode").getString();
			if (currentMode == null) {
				currentMode = LocaleHelper.UNKNOWN.getLocale();
			}
		}

		strSizeRem = ACMinecraftFontWidthCalculator.chatwidth
				- ACMinecraftFontWidthCalculator.getStringWidth(gameMode);
		gameMode += ACMinecraftFontWidthCalculator.strPadLeftChat(
				ChatColor.GREEN + String.valueOf(currentMode), strSizeRem, ' ');
		sender.sendMessage(gameMode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null;
	}

}
