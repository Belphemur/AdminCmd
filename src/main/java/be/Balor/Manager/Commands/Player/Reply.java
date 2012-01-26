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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import belgium.Balor.Workers.AFKWorker;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class Reply extends CoreCommand {

	/**
	 *
	 */
	public Reply() {
		permNode = "admincmd.player.reply";
		cmdName = "bal_reply";
	}

	@Override
	public void execute(CommandSender sender, CommandArgs args) {
		if (!Utils.isPlayer(sender, true))
			return;

		if (Utils.isPlayer(sender, false)
				&& ACPlayer.getPlayer(((Player) sender)).hasPower(Type.MUTED)
				&& ConfigEnum.MUTEDPM.getBoolean()) {
			Utils.sI18n(sender, "muteEnabled");
			return;
		}
		Player pSender = (Player) sender;
		Player buddy = ACHelper.getInstance().getReplyPlayer(pSender);
		if (buddy != null) {
			if (!buddy.isOnline()) {
				Utils.sI18n(sender, "offline");
				ACHelper.getInstance().removeReplyPlayer(pSender);
				return;
			}
			if (InvisibleWorker.getInstance().hasInvisiblePowers(buddy.getName())
					&& !PermissionManager.hasPerm(sender, "admincmd.invisible.cansee", false)) {
				Utils.sI18n(sender, "playerNotFound", "player", args.getString(0));
				return;
			}
			String senderPm = "";
			String msgPrefix = Utils.I18n("privateTitle");
			String msg = "";
			String senderName = "";
			senderName = pSender.getName();
			senderPm = Utils.getPlayerName(pSender, buddy) + ChatColor.WHITE + " - ";

			for (String arg : args)
				msg += arg + " ";
			msg = msg.trim();
			String parsed = Utils.colorParser(msg);
			if (parsed == null)
				parsed = msg;
			buddy.sendMessage(msgPrefix + senderPm + parsed);
			ACHelper.getInstance().setReplyPlayer(buddy, pSender);
			if (AFKWorker.getInstance().isAfk(buddy)) {
				AFKWorker.getInstance().sendAfkMessage((Player) sender, buddy);
			} else
				sender.sendMessage(msgPrefix + senderPm + parsed);
			String spyMsg = "[" + ChatColor.GREEN + "SpyMsg" + ChatColor.WHITE + "] " + senderName
					+ "-" + buddy.getName() + ": " + parsed;
			for (Player p : ACHelper.getInstance().getSpyPlayers())
				if (p != null && !p.getName().equals(senderName)
						&& !p.getName().equals(buddy.getName()))
					p.sendMessage(spyMsg);
			if (ConfigEnum.LOG_PM.getBoolean())
				ACLogger.info(spyMsg);
		} else
			Utils.sI18n(sender, "noPlayerToReply");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null && args.length >= 1;
	}

}
