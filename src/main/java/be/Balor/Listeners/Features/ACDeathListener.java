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
 *
 **************************************************************************/

package be.Balor.Listeners.Features;

import java.util.HashMap;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class ACDeathListener implements Listener {

	private final boolean noMessage;

	public ACDeathListener(final boolean noMessage) {
		this.noMessage = noMessage;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDeath(final PlayerDeathEvent e) {
		if (noMessage) {
			e.setDeathMessage(null);
			return;
		}
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", Utils.getPlayerName(e.getEntity()));
		String message = null;
		EntityDamageEvent cause = e.getEntity().getLastDamageCause();
		if (cause instanceof EntityDamageByEntityEvent) {
			message = getMessage(cause);
		} else {
			switch(cause.getCause()) {
				case CONTACT:
					message = ACHelper.getInstance().getDeathMessage("cactus");
					break;
				case DROWNING:
					message = ACHelper.getInstance().getDeathMessage("drowning");
					break;
				case FALL:
					message = ACHelper.getInstance().getDeathMessage("falling");
					break;
				case FIRE_TICK:
				case FIRE:
					message = ACHelper.getInstance().getDeathMessage("fire");
					break;
				case STARVATION:
					message = ACHelper.getInstance().getDeathMessage("starving");
					break;
				case SUFFOCATION:
					message = ACHelper.getInstance().getDeathMessage("suffocation");
					break;
				case VOID:
					message = ACHelper.getInstance().getDeathMessage("void");
					break;
				case LAVA:
					message = ACHelper.getInstance().getDeathMessage("lava");
					break;
				case POISON:
					message = ACHelper.getInstance().getDeathMessage("poisen");
					break;
				case LIGHTNING:
					message = ACHelper.getInstance().getDeathMessage("lightning");
					break;
				case MAGIC:
					message = ACHelper.getInstance().getDeathMessage("magic");
					break;
				case SUICIDE:
					message = ACHelper.getInstance().getDeathMessage("suicide");
					break;
				case ENTITY_EXPLOSION:
					message = ACHelper.getInstance().getDeathMessage("creeper");
					break;
				case BLOCK_EXPLOSION:
					message = ACHelper.getInstance().getDeathMessage("tnt");
					break;
				default:
					message = ACHelper.getInstance().getDeathMessage("default");
					break;
			}
		}
		if (message == null) {
			message = "just died.";
		}
		replace.put("msg", message);
		e.setDeathMessage(LocaleHelper.DEATH_MSG.getLocale(replace));
		//Utils.broadcastMessage(LocaleHelper.DEATH_MSG.getLocale(replace));
	}

	private String getMessage(final EntityDamageEvent e) {
		Entity damager = ((EntityDamageByEntityEvent) e).getDamager();
		return "";
	}
}