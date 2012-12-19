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

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
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
	public void onPlayerDeath(final EntityDeathEvent event) {
		if (!(event instanceof PlayerDeathEvent)) {
			return;
		}
		final PlayerDeathEvent e = (PlayerDeathEvent) event;
		if (noMessage) {
			e.setDeathMessage(null);
			return;
		}
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", Utils.getPlayerName(e.getEntity()));
		String message = null;
		final EntityDamageEvent cause = e.getEntity().getLastDamageCause();
		try {
			if (cause instanceof EntityDamageByEntityEvent) {
				message = getMessage(cause);
			} else {
				switch (cause.getCause()) {
				case CONTACT:
					message = ACHelper.getInstance().getDeathMessage("contact");
					break;
				case DROWNING:
					message = ACHelper.getInstance()
							.getDeathMessage("drowning");
					break;
				case FALL:
					message = ACHelper.getInstance().getDeathMessage("falling");
					break;
				case FIRE_TICK:
				case FIRE:
					message = ACHelper.getInstance().getDeathMessage("fire");
					break;
				case STARVATION:
					message = ACHelper.getInstance()
							.getDeathMessage("starving");
					break;
				case SUFFOCATION:
					message = ACHelper.getInstance().getDeathMessage(
							"suffocation");
					break;
				case VOID:
					message = ACHelper.getInstance().getDeathMessage("void");
					break;
				case LAVA:
					message = ACHelper.getInstance().getDeathMessage("lava");
					break;
				case POISON:
					message = ACHelper.getInstance().getDeathMessage("poison");
					break;
				case LIGHTNING:
					message = ACHelper.getInstance().getDeathMessage(
							"lightning");
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
				case CUSTOM:
					message = ACHelper.getInstance().getDeathMessage("custom");
					break;
				default:
					message = ACHelper.getInstance().getDeathMessage("default");
					break;
				}
			}
		} catch (final NullPointerException ex) {
			message = null;
		}
		if (message == null) {
			return;
		}
		replace.put("msg", message);
		e.setDeathMessage(LocaleHelper.DEATH_MSG.getLocale(replace));
		// Utils.broadcastMessage(LocaleHelper.DEATH_MSG.getLocale(replace));
	}

	private String getMessage(final EntityDamageEvent e) {
		final Entity damager = ((EntityDamageByEntityEvent) e).getDamager();
		if (damager instanceof Wolf) {
			return ACHelper.getInstance().getDeathMessage("wolf");
		} else if (damager instanceof Player) {
			return ACHelper.getInstance().getDeathMessage("player");
		} else if (damager instanceof Skeleton) {
			return ACHelper.getInstance().getDeathMessage("skeleton");
		} else if (damager instanceof TNTPrimed) {
			return ACHelper.getInstance().getDeathMessage("TNTPrimed");
		} else if (damager instanceof Zombie) {
			if (damager instanceof PigZombie) {
				return ACHelper.getInstance().getDeathMessage("zombiepig");
			}
			return ACHelper.getInstance().getDeathMessage("zombie");
		} else if (damager instanceof Projectile) {
			if (damager instanceof Arrow) {
				if (((Arrow) damager).getShooter() == null) {
					return ACHelper.getInstance().getDeathMessage("dispenser");
				} else if (((Arrow) damager).getShooter() instanceof Player) {
					return ACHelper.getInstance().getDeathMessage("playerBow")
							+ Utils.getPlayerName(((Player) ((Arrow) damager)
									.getShooter()));
				} else if (((Projectile) damager).getShooter() instanceof Skeleton) {
					return ACHelper.getInstance().getDeathMessage("skeleton");
				}
			} else if (damager instanceof Fireball) {
				if (((Fireball) damager).getShooter() instanceof Blaze) {
					return ACHelper.getInstance().getDeathMessage("blaze");
				} else if (((Fireball) damager).getShooter() instanceof Ghast) {
					return ACHelper.getInstance().getDeathMessage("ghast");
				}
			} else if (damager instanceof ThrownPotion) {
				if (((ThrownPotion) damager).getShooter() instanceof Player) {
					return ACHelper.getInstance().getDeathMessage("potion");
				}
			}
		} else if (damager instanceof LivingEntity) {
			return (ACHelper.getInstance().getDeathMessage("mob")
					+ damager.getType().getName());
		}
		return ACHelper.getInstance().getDeathMessage("default");
	}
}