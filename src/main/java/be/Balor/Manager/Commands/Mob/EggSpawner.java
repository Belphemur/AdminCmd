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
package be.Balor.Manager.Commands.Mob;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Egg.EggPermissionManager;
import be.Balor.Tools.Egg.EggType;
import be.Balor.Tools.Egg.Exceptions.DontHaveThePermissionException;
import be.Balor.Tools.Egg.Exceptions.ExceptionType;
import be.Balor.Tools.Egg.Exceptions.ParameterMissingException;
import be.Balor.Tools.Egg.Exceptions.ProcessingArgsException;
import be.Balor.Tools.Egg.Types.NormalEgg;

import com.google.common.base.Joiner;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class EggSpawner extends MobCommand {

	/**
	 *
	 */
	public EggSpawner() {
		this.cmdName = "bal_egg";
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
		if (!Utils.isPlayer(sender)) {
			return;
		}
		final Player player = (Player) sender;
		final ACPlayer acp = ACPlayer.getPlayer(player);
		EggType<?> egg = null;
		try {
			egg = EggType.createEggType(player, args);
		} catch (final ParameterMissingException e) {
			if (e.getParam() == 'e') {
				final String list = Joiner
						.on(", ")
						.skipNulls()
						.join(EggPermissionManager.INSTANCE
								.getEggTypeNames(player));
				sender.sendMessage(e.getMessage());
				sender.sendMessage(ChatColor.GOLD + "Egg List : ");
				sender.sendMessage(ChatColor.YELLOW + list);
			} else {
				Utils.sI18n(sender, "paramMissing", "param",
						String.valueOf(e.getParam()));
				sender.sendMessage(e.getMessage());
			}
			return;
		} catch (final ProcessingArgsException e) {
			if (e.getType().equals(ExceptionType.NO_CLASS)) {
				Utils.sI18n(sender, "eggDontExists", "egg", e.getMessage());
			} else if (e.getType().equals(ExceptionType.DONT_EXISTS)) {
				Utils.sI18n(sender, "entityDontExists", "entity",
						e.getMessage());
			} else if (e.getType().equals(ExceptionType.CUSTOM)) {
				final Map<String, String> replace = new HashMap<String, String>();
				replace.put("egg", args.getValueFlag('t'));
				replace.put("error", e.getMessage());
				Utils.sI18n(sender, "eggCustomError", replace);
			} else {
				ACLogger.severe("Problem with an Egg Type : " + e.getMessage(),
						e);
			}
			return;
		} catch (final DontHaveThePermissionException e) {
			sender.sendMessage(e.getMessage());
			return;
		} catch (final NullPointerException e) {
			if (args.hasFlag('e')) {
				final String list = Joiner
						.on(", ")
						.skipNulls()
						.join(EggPermissionManager.INSTANCE
								.getEggTypeNames(player));
				sender.sendMessage(ChatColor.GOLD + "Egg List : ");
				sender.sendMessage(ChatColor.YELLOW + list);
			} else {
				Utils.sI18n(sender, "eggNoParamGiven");
				sender.sendMessage(e.getMessage());
			}
			return;
		}
		if (egg instanceof NormalEgg) {
			acp.removePower(Type.EGG);
			Utils.sI18n(sender, "eggNormal");
		} else {
			acp.setPower(Type.EGG, egg);
			Utils.sI18n(sender, "eggEnabled", "egg", egg.toString());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Commands.CoreCommand#permissionCheck(org.bukkit.command
	 * .CommandSender)
	 */
	@Override
	public boolean permissionCheck(final CommandSender sender) {
		return true;
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
