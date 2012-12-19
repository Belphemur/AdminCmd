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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Manager.Permissions.PermParent;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.TpRequest;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class TpToggle extends TeleportCommand {

	/**
	 *
	 */
	public TpToggle() {
		cmdName = "bal_tptoggle";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommand#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		if (Utils.isPlayer(sender)) {
			final Player player = (Player) sender;
			final ACPlayer acp = ACPlayer.getPlayer(player.getName());
			if (args.length >= 1 && acp.hasPower(Type.TP_REQUEST)
					&& args.getString(0).equalsIgnoreCase("yes")) {
				if (!PermissionManager.hasPerm(player,
						"admincmd.tp.toggle.allow")) {
					return;
				}
				final TpRequest request = acp.getTpRequest();
				if (request != null) {
					request.teleport(player);
					acp.removeTpRequest();
				} else {
					Utils.sI18n(sender, "noTpRequest");
				}
			} else {
				if (!PermissionManager
						.hasPerm(player, "admincmd.tp.toggle.use")) {
					return;
				}
				if (acp.hasPower(Type.TP_REQUEST)) {
					acp.removePower(Type.TP_REQUEST);
					Utils.sI18n(player, "tpRequestOff");
				} else {
					acp.setPower(Type.TP_REQUEST);
					Utils.sI18n(player, "tpRequestOn");
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#registerBukkitPerm()
	 */
	@Override
	public void registerBukkitPerm() {
		final PermParent parent = plugin.getPermissionLinker().getPermParent(
				"admincmd.tp.toggle.*");
		parent.addChild("admincmd.tp.toggle.allow").addChild(
				"admincmd.tp.toggle.use");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null;
	}

}
