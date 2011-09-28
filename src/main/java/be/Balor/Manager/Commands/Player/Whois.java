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
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Player.ACPlayer;
import be.Balor.Player.EmptyPlayer;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Help.String.ACMinecraftFontWidthCalculator;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Whois extends CoreCommand {
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
	public void execute(CommandSender sender, CommandArgs args) {
		if (args.hasFlag('w')) {
			ACWorld w;
			try {
				if (args.length >= 1)
					w = ACWorld.getWorld(args.getString(0));
				else if (Utils.isPlayer(sender))
					w = ACWorld.getWorld(((Player) sender).getWorld().getName());
				else
					return;
			} catch (WorldNotLoaded e) {
				Utils.sI18n(sender, "worldNotFound", "world", args.getString(0));
				return;
			}
			sender.sendMessage(ChatColor.GREEN
					+ ACMinecraftFontWidthCalculator.strPadCenterChat(
							ChatColor.AQUA + " " + w.getName() + " " + ChatColor.GREEN, '='));
			for (Entry<String, String> power : w.getInformations().entrySet()) {
				String line = ChatColor.GOLD + power.getKey() + ChatColor.WHITE + " : ";
				int sizeRemaining = ACMinecraftFontWidthCalculator.chatwidth
						- ACMinecraftFontWidthCalculator.getStringWidth(line);
				line += ACMinecraftFontWidthCalculator.strPadLeftChat(
						ChatColor.GREEN + power.getValue(), sizeRemaining, ' ');
				sender.sendMessage(line);
			}
			return;
		}
		Player target = Utils.getUser(sender, args, permNode, 0, !Utils.isPlayer(sender, false));
		ACPlayer actarget;
		if (target == null) {
			if (args.length == 0) {
				sender.sendMessage("You must type the player name");
				return;
			}
			actarget = ACPlayer.getPlayer(args.getString(0));
			if (actarget instanceof EmptyPlayer) {
				Utils.sI18n(sender, "playerNotFound", "player", actarget.getName());
				return;
			}
			if (!Utils.checkImmunity(sender, args, 0))
				return;
		} else
			actarget = ACPlayer.getPlayer(target.getName());
		sender.sendMessage(ChatColor.AQUA
				+ ACMinecraftFontWidthCalculator.strPadCenterChat(ChatColor.DARK_GREEN + " "
						+ actarget.getName() + " " + ChatColor.AQUA, '='));
		// Login
		String loginDate = ChatColor.GOLD + "Last Login" + ChatColor.WHITE + " : ";
		int logSizeRemaining = ACMinecraftFontWidthCalculator.chatwidth
				- ACMinecraftFontWidthCalculator.getStringWidth(loginDate);
		loginDate += ACMinecraftFontWidthCalculator.strPadLeftChat(
				ChatColor.GREEN + Utils.replaceDateAndTimeFormat(actarget.getName()),
				logSizeRemaining, ' ');
		sender.sendMessage(loginDate);

		// Played
		long total = actarget.getCurrentPlayedTime();
		Long[] time = Utils.transformToElapsedTime(total);
		HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("d", time[0].toString());
		replace.put("h", time[1].toString());
		replace.put("m", time[2].toString());
		replace.put("s", time[3].toString());
		String played = ChatColor.GOLD + "Time Played" + ChatColor.WHITE + " : ";
		int strSizeRem = ACMinecraftFontWidthCalculator.chatwidth
				- ACMinecraftFontWidthCalculator.getStringWidth(played);
		played += ACMinecraftFontWidthCalculator.strPadLeftChat(
				ChatColor.GREEN + Utils.I18n("elapsedTotalTime", replace), strSizeRem, ' ');
		sender.sendMessage(played);

		// Powers
		for (Entry<String, String> power : actarget.getPowers().entrySet()) {
			String line = ChatColor.GOLD + power.getKey() + ChatColor.WHITE + " : ";
			int sizeRemaining = ACMinecraftFontWidthCalculator.chatwidth
					- ACMinecraftFontWidthCalculator.getStringWidth(line);
			line += ACMinecraftFontWidthCalculator.strPadLeftChat(
					ChatColor.GREEN + power.getValue(), sizeRemaining, ' ');
			sender.sendMessage(line);
		}

		// Immunity Level
		int level;
		if (target != null)
			level = ACHelper.getInstance().getLimit(target, "immunityLvl", "defaultImmunityLvl");
		else
			level = actarget.getInformation("immunityLvl").getInt(
					ACHelper.getInstance().getConfInt("defaultImmunityLvl"));
		String immuLvl = ChatColor.GOLD + "Immunity Level" + ChatColor.WHITE + " : ";
		strSizeRem = ACMinecraftFontWidthCalculator.chatwidth
				- ACMinecraftFontWidthCalculator.getStringWidth(immuLvl);
		immuLvl += ACMinecraftFontWidthCalculator.strPadLeftChat(
				ChatColor.GREEN + String.valueOf(level), strSizeRem, ' ');
		sender.sendMessage(immuLvl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null;
	}

}
