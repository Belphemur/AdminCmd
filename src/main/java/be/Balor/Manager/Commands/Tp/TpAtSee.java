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
package be.Balor.Manager.Commands.Tp;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class TpAtSee extends CoreCommand {

	/**
	 *
	 */
	public TpAtSee() {
		permNode = "admincmd.tp.see";
		cmdName = "bal_tpsee";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see be.Balor.Manager.ACCommand#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(CommandSender sender, CommandArgs args) {
		if (Utils.isPlayer(sender)) {
			Player player = (Player) sender;
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", Utils.getPlayerName(player));
			ACPlayer acp = ACPlayer.getPlayer(player.getName());
			if (acp.hasPower(Type.TP_AT_SEE)) {
				acp.removePower(Type.TP_AT_SEE);
				Utils.sI18n(player, "tpSeeDisabled");
			} else {
				acp.setPower(Type.TP_AT_SEE);
				Utils.sI18n(player, "tpSeeEnabled");
			}

		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see be.Balor.Manager.ACCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return true;
	}

}
