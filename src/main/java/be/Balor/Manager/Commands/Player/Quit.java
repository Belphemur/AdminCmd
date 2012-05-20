/*************************************************************************
 * This file is part of AdminCmd.
 *
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdminCmd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 **************************************************************************/

package be.Balor.Manager.Commands.Player;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class Quit extends PlayerCommand {

	public Quit() {
		cmdName = "bal_quit";
		permNode = "admincmd.player.quit";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
	 * CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args) {
		if (Utils.isPlayer(sender, true)) {
			final Player quitting = (Player) sender;
			final HashMap<String, String> replace = new HashMap<String, String>();
			String reason = "";
			if (args == null || args.length == 0) {
				reason = "disconnect:quitting";
			} else {
				for (int i = 0; i < args.length; i++) {
					reason += args.getString(i) + " ";
				}
			}
			replace.put("reason", reason);
			replace.put("player", Utils.getPlayerName(quitting));
			ACPlayer.getPlayer(quitting).setPower(Type.KICKED);
			quitting.kickPlayer("Disconnected");
			Utils.broadcastMessage(LocaleHelper.PLAYER_QUITCMD_MSG.getLocale(replace));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return true;
	}

}
