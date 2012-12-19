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

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Help.String.ACMinecraftFontWidthCalculator;

import com.google.common.base.Joiner;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PlayerList extends PlayerCommand {

	/**
	 *
	 */
	public PlayerList() {
		permNode = "admincmd.player.list";
		cmdName = "bal_playerlist";
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

		final Collection<String> list = Utils.getPlayerList(sender);
		sender.sendMessage(Utils.I18n("onlinePlayers") + " " + ChatColor.WHITE
				+ list.size());
		final String toDisplay = Joiner.on(", ").join(list);
		if (toDisplay.length() >= ACMinecraftFontWidthCalculator.chatwidth) {
			sender.sendMessage(toDisplay.substring(0,
					ACMinecraftFontWidthCalculator.chatwidth));
			sender.sendMessage(toDisplay.substring(
					ACMinecraftFontWidthCalculator.chatwidth,
					toDisplay.length()));
			return;
		}
		sender.sendMessage(toDisplay);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return true;
	}

}
