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
import org.bukkit.permissions.PermissionDefault;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Invisible extends PlayerCommand {

	/**
	 *
	 */
	public Invisible() {
		permNode = "admincmd.player.invisible";
		cmdName = "bal_invisible";
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
		final Player target = Utils.getUser(sender, args, permNode);
		final boolean noPickUp = ConfigEnum.NPINVISIBLE.getBoolean();
		if (target != null) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", Utils.getPlayerName(target));
			final ACPlayer acp = ACPlayer.getPlayer(target);
			if (!InvisibleWorker.getInstance().hasInvisiblePowers(target)) {
				InvisibleWorker.getInstance().vanish(target, false);
				Utils.sI18n(target, "invisibleEnabled");
				if (noPickUp && !acp.hasPower(Type.NO_PICKUP)) {
					acp.setPower(Type.NO_PICKUP);
					Utils.sI18n(target, "npEnabled");
				}
				if (!target.equals(sender)) {
					Utils.sI18n(sender, "invisibleEnabledTarget", replace);
					if (noPickUp && acp.hasPower(Type.NO_PICKUP)) {
						Utils.sI18n(sender, "npEnabledTarget", replace);
					}
				}
				acp.setPower(Type.INVISIBLE);
			} else {
				InvisibleWorker.getInstance().reappear(target);
				Utils.sI18n(target, "invisibleDisabled");
				if (noPickUp && acp.hasPower(Type.NO_PICKUP)) {
					acp.removePower(Type.NO_PICKUP);
					Utils.sI18n(target, "npDisabled");
				}
				if (!target.equals(sender)) {
					Utils.sI18n(sender, "invisibleDisabledTarget", replace);
					if (noPickUp && !acp.hasPower(Type.NO_PICKUP)) {
						Utils.sI18n(sender, "npDisabledTarget", replace);
					}

				}
				acp.removePower(Type.INVISIBLE);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#registerBukkitPerm()
	 */
	@Override
	public void registerBukkitPerm() {
		super.registerBukkitPerm();
		plugin.getPermissionLinker().addPermChild(
				"admincmd.invisible.notatarget", PermissionDefault.OP);
		plugin.getPermissionLinker().addPermChild("admincmd.invisible.cansee",
				PermissionDefault.OP);
	}

}
