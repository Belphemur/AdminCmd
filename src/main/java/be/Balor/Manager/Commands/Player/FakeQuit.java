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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */

public class FakeQuit extends PlayerCommand {

	/**
	 *
	 */
	public FakeQuit() {
		permNode = "admincmd.player.fakequit";
		cmdName = "bal_fakequit";
		other = true;
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
		final Player player = Utils.getUser(sender, args, permNode);
		if (player != null) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", Utils.getPlayerName(player));
			final ACPlayer acp = ACPlayer.getPlayer(player.getName());
			if (acp.hasPower(Type.FAKEQUIT)) {
				acp.removePower(Type.FAKEQUIT);
				Utils.broadcastFakeJoin(player);
				Utils.addPlayerInOnlineList(player);
				ACHelper.getInstance().removeFakeQuit(player);
				Utils.sI18n(player, "fakeQuitDisabled");
				if (!player.equals(sender)) {
					Utils.sI18n(sender, "fakeQuitDisabledTarget", replace);
				}
			} else {
				acp.setPower(Type.FAKEQUIT);
				Utils.sI18n(player, "fakeQuitEnabled");
				Utils.broadcastFakeQuit(player);
				ACHelper.getInstance().addFakeQuit(player);
				Utils.removePlayerFromOnlineList(player);
				if (!player.equals(sender)) {
					Utils.sI18n(sender, "fakeQuitEnabledTarget", replace);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null;
	}
}