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
package be.Balor.Tools.CommandUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Antoine
 * 
 */
public final class Immunity {

	/**
	 * 
	 */
	private Immunity() {
	}

	public static boolean checkImmunity(final CommandSender sender,
			final ACPlayer target) {
		if (Immunity.preImmunityCheck(sender, target)) {
			return true;
		}
		final Player player = (Player) sender;
		final int pLvl = ACHelper.getInstance().getLimit(player,
				Type.Limit.IMMUNITY, "defaultImmunityLvl");
		int tLvl = 0;
		if (target.isOnline()) {
			tLvl = ACHelper.getInstance().getLimit(target.getHandler(),
					Type.Limit.IMMUNITY, "defaultImmunityLvl");
		} else {
			tLvl = target.getInformation("immunityLvl").getInt(0);
		}
		return Immunity.checkLvl(player, pLvl, tLvl);

	}

	/**
	 * Check the if the player have the right to execute the command on the
	 * other player
	 * 
	 * @param sender
	 *            the one who want to do the command
	 * @param target
	 *            the target of the command
	 * @return true if the sender have the right to execute the command, else
	 *         false.
	 */
	public static boolean checkImmunity(final CommandSender sender,
			final Player target) {
		if (Immunity.preImmunityCheck(sender, target)) {
			return true;
		}
		final Player player = (Player) sender;
		final int pLvl = ACHelper.getInstance().getLimit(player,
				Type.Limit.IMMUNITY, "defaultImmunityLvl");
		final int tLvl = ACHelper.getInstance().getLimit(target,
				Type.Limit.IMMUNITY, "defaultImmunityLvl");

		return Immunity.checkLvl(player, pLvl, tLvl);
	}

	/**
	 * Check the if the player have the right to execute the command on the
	 * other player
	 * 
	 * @param sender
	 *            the one who want to do the command
	 * @param args
	 *            containing the name of the target
	 * @param index
	 *            index of the target's name in the argument
	 * @return true if the sender have the right to execute the command, else
	 *         false with displaying an error message to the sender.
	 */
	public static boolean checkImmunity(final CommandSender sender,
			final CommandArgs args, final int index) {
		final Player target = sender.getServer().getPlayer(
				args.getString(index));
		if (target != null) {
			if (checkImmunity(sender, target)) {
				return true;
			} else {
				LocaleManager.sI18n(sender, "insufficientLvl");
				return false;
			}
		} else {
			if (Immunity.preImmunityCheck(sender, target)) {
				return true;
			}
			final Player player = (Player) sender;
			final int pLvl = ACHelper.getInstance().getLimit(player,
					Type.Limit.IMMUNITY, "defaultImmunityLvl");
			final int tLvl = ACPlayer.getPlayer(args.getString(index))
					.getInformation("immunityLvl").getInt(0);
			return Immunity.checkLvl(player, pLvl, tLvl);

		}
	}

	private static boolean checkLvl(final Player player, final int pLvl,
			final int tLvl) {
		if (PermissionManager.hasPerm(player, "admincmd.immunityLvl.samelvl",
				false) && pLvl != tLvl) {
			return false;
		}
		if (pLvl >= tLvl) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean preImmunityCheck(final CommandSender sender,
			final Object target) {
		if (!ConfigEnum.IMMUNITY.getBoolean()) {
			return true;
		}
		if (!Users.isPlayer(sender, false)) {
			return true;
		}
		if (target == null) {
			return true;
		}
		return false;
	}

}
