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
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import be.Balor.Tools.CommandUtils.Users;
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
                replace.put("player", Users.getPlayerName(e.getEntity()));
                String message = null;
                final EntityDamageEvent cause = e.getEntity().getLastDamageCause();
                try {
                        if (cause instanceof EntityDamageByEntityEvent) {
                                message = getMessage(cause);
                        } else {
                                switch (cause.getCause()) {
                                        case BLOCK_EXPLOSION:
                                                message = ACHelper.getInstance().getDeathMessage("explosion");
                                                break;
                                        case CONTACT:
                                                message = ACHelper.getInstance().getDeathMessage("contact");
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
                                        case LAVA:
                                                message = ACHelper.getInstance().getDeathMessage("lava");
                                                break;
                                        case STARVATION:
                                                message = ACHelper.getInstance().getDeathMessage("starvation");
                                                break;
                                        case SUFFOCATION:
                                                message = ACHelper.getInstance().getDeathMessage("suffocation");
                                                break;
                                        case THORNS:
                                                message = ACHelper.getInstance().getDeathMessage("thorns");
                                                break;
                                        case VOID:
                                                message = ACHelper.getInstance().getDeathMessage("void");
                                                break;
                                        case WITHER:
                                                message = ACHelper.getInstance().getDeathMessage("withereffect");
                                                break;
                                        case MAGIC:
                                                message = ACHelper.getInstance().getDeathMessage("magic");
                                                break;
                                        case SUICIDE:
                                                message = ACHelper.getInstance().getDeathMessage("suicide");
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
                        return;
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
                if (damager instanceof Player) {
                        return ACHelper.getInstance().getDeathMessage("player") + ((Player)damager).getDisplayName();
                } else if (damager instanceof TNTPrimed) {
                        return ACHelper.getInstance().getDeathMessage("tnt");
                } else if (damager instanceof FallingBlock) {
                        return ACHelper.getInstance().getDeathMessage("crushed");
                } else if (damager instanceof Zombie) {
                        if (damager instanceof PigZombie) {
                                return ACHelper.getInstance().getDeathMessage("pigzombie");
                        }
                        return ACHelper.getInstance().getDeathMessage("zombie");
                } else if (damager instanceof Skeleton) {
                        if (((Skeleton) damager).getSkeletonType() == SkeletonType.WITHER) {
                                return ACHelper.getInstance().getDeathMessage("witherskeleton");
                        }
                        return ACHelper.getInstance().getDeathMessage("skeleton");
                } else if (damager instanceof Spider) {
                        return ACHelper.getInstance().getDeathMessage("spider");
                } else if (damager instanceof CaveSpider) {
                        return ACHelper.getInstance().getDeathMessage("cavespider");
                } else if (damager instanceof Creeper) {
                        return ACHelper.getInstance().getDeathMessage("creeper");
                } else if (damager instanceof MagmaCube) {
                        return ACHelper.getInstance().getDeathMessage("magmacube");
                } else if (damager instanceof Blaze) {
                        return ACHelper.getInstance().getDeathMessage("blaze");
                } else if (damager instanceof Ghast) {
                        return ACHelper.getInstance().getDeathMessage("ghast");
                } else if (damager instanceof Slime) {
                        return ACHelper.getInstance().getDeathMessage("slime");
                } else if (damager instanceof Silverfish) {
                        return ACHelper.getInstance().getDeathMessage("silverfish");
                } else if (damager instanceof Giant) {
                        return ACHelper.getInstance().getDeathMessage("giant");
                } else if (damager instanceof EnderDragon) {
                        return ACHelper.getInstance().getDeathMessage("enderdragon");
                } else if (damager instanceof Wither) {
                        return ACHelper.getInstance().getDeathMessage("wither");
                } else if (damager instanceof IronGolem) {
                        return ACHelper.getInstance().getDeathMessage("irongolem");
                } else if (damager instanceof Enderman) {
                        return ACHelper.getInstance().getDeathMessage("enderman");
                } else if (damager instanceof Wolf) {
                        return ACHelper.getInstance().getDeathMessage("wolf");
                } else if (damager instanceof Player) {
                        return ACHelper.getInstance().getDeathMessage("melee");
                } else if (damager instanceof Projectile) {
                        if (damager instanceof Arrow) {
                                if (((Arrow) damager).getShooter() == null) {
                                        return ACHelper.getInstance().getDeathMessage("arrow");
                                } else if (((Arrow) damager).getShooter() instanceof Skeleton) {
                                        return ACHelper.getInstance().getDeathMessage("skeleton");
                                } else if (((Arrow) damager).getShooter() instanceof Player) {
                                        return ACHelper.getInstance().getDeathMessage("ranged") + Users.getPlayerName((Player) ((Arrow) damager).getShooter());
                                }
                        } else if (damager instanceof Fireball) {
                                if (((Fireball) damager).getShooter() == null) {
                                        return ACHelper.getInstance().getDeathMessage("fireball");
                                } else if (((Fireball) damager).getShooter() instanceof Ghast) {
                                        return ACHelper.getInstance().getDeathMessage("ghast");
                                } else if (((Fireball) damager).getShooter() instanceof Blaze) {
                                        return ACHelper.getInstance().getDeathMessage("blaze");
                                }
                        } else if (damager instanceof ThrownPotion) {
                                if (((ThrownPotion) damager).getShooter() instanceof Player) {
                                        return ACHelper.getInstance().getDeathMessage("potion");
                                } else if (((ThrownPotion) damager).getShooter() instanceof Witch) {
                                        return ACHelper.getInstance().getDeathMessage("witch");
                                }
                        }
                } else if (damager instanceof LivingEntity) {
                        return ACHelper.getInstance().getDeathMessage("mob") + damager.getType().toString().toLowerCase();
                }
                return ACHelper.getInstance().getDeathMessage("default");
        }
}
