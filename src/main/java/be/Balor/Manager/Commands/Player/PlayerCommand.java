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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.plugin.PluginManager;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class PlayerCommand extends CoreCommand {
	/**
 *
 */
	public PlayerCommand() {
		super();
		this.permParent = plugin.getPermissionLinker().getPermParent(
				"admincmd.player.*");
	}

	/**
	 * @param string
	 * @param string2
	 */
	public PlayerCommand(final String cmd, final String permNode) {
		super(cmd, permNode);
		this.permParent = plugin.getPermissionLinker().getPermParent(
				"admincmd.player.*");
	}

	/**
	 * Heal or refill the FoodBar of the selected player.
	 * 
	 * @param name
	 * @return
	 * @throws ActionNotPermitedException
	 * @throws PlayerNotFound
	 */
	public static boolean setPlayerHealth(final CommandSender sender,
			final CommandArgs name, final Type.Health toDo)
			throws PlayerNotFound, ActionNotPermitedException {
		final Player target = Utils.getUser(sender, name, "admincmd.player."
				+ toDo);
		if (target == null) {
			return false;
		}
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", Utils.getPlayerName(target));
		final PluginManager pluginManager = ACPluginManager.getServer()
				.getPluginManager();
		final String newStateLocale = LocaleHelper.NEW_STATE.getLocale();
		final String newStatePlayerLocale = LocaleHelper.NEW_STATE_PLAYER
				.getLocale(replace);
		switch (toDo) {
		case HEAL:
			final EntityRegainHealthEvent heal = new EntityRegainHealthEvent(
					target, target.getMaxHealth(), RegainReason.CUSTOM);
			pluginManager.callEvent(heal);
			if (!heal.isCancelled()) {
				target.setHealth(heal.getAmount());
				target.setFireTicks(0);
				final String msg = newStateLocale
						+ LocaleHelper.HEALED.getLocale();
				target.sendMessage(msg);
				if (!target.equals(sender)) {
					final String newStateMsg = newStatePlayerLocale
							+ LocaleHelper.HEALED.getLocale();
					sender.sendMessage(newStateMsg);
				}
			}
			break;
		case FEED:
			final FoodLevelChangeEvent foodEvent = new FoodLevelChangeEvent(
					target, 20);
			pluginManager.callEvent(foodEvent);
			if (!foodEvent.isCancelled()) {
				target.setFoodLevel(foodEvent.getFoodLevel());
				final String msg = newStateLocale
						+ LocaleHelper.FEEDED.getLocale();
				target.sendMessage(msg);
				if (!target.equals(sender)) {
					final String newStateMsg = newStatePlayerLocale
							+ LocaleHelper.FEEDED.getLocale();
					sender.sendMessage(newStateMsg);
				}
			}
			break;
		case KILL:
			if (target.equals(sender)) {
				final EntityDamageEvent dmgEvent = new EntityDamageEvent(
						target, EntityDamageEvent.DamageCause.SUICIDE,
						Double.MAX_VALUE);
				pluginManager.callEvent(dmgEvent);
				if (!dmgEvent.isCancelled()) {
					target.damage(dmgEvent.getDamage());
					LocaleHelper.SUICIDE.sendLocale(target);
				}
			} else {
				final EntityDamageEvent dmgEvent = new EntityDamageEvent(
						target, EntityDamageEvent.DamageCause.CUSTOM,
						Double.MAX_VALUE);
				pluginManager.callEvent(dmgEvent);
				if (!dmgEvent.isCancelled()) {
					if (Utils.isPlayer(sender, false)) {
						target.damage(dmgEvent.getDamage(), (Player) sender);
					} else {
						target.damage(dmgEvent.getDamage());
					}
					final String msg = newStateLocale
							+ LocaleHelper.KILLED.getLocale();
					target.sendMessage(msg);
					final String newStateMsg = newStatePlayerLocale
							+ LocaleHelper.KILLED.getLocale();
					sender.sendMessage(newStateMsg);
				}
			}
			if (Utils.logBlock != null) {
				Utils.logBlock.queueKill(
						Utils.isPlayer(sender, false) ? (Player) sender : null,
						target);
			}
			break;
		default:
			return false;
		}
		return true;
	}
}