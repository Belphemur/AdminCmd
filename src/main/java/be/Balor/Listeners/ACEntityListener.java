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
package be.Balor.Listeners;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.MobCheck;
import be.Balor.Tools.Type;
import be.Balor.World.ACWorld;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACEntityListener extends EntityListener {

	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.isCancelled())
			return;
		final Entity e = event.getEntity();
		if (!MobCheck.isMonster(e) && !MobCheck.isAnimal(e))
			return;
		final World world = e.getWorld();
		final ACWorld acWorld = ACWorld.getWorld(world.getName());
		Integer limit = acWorld.getInformation(Type.MOB_LIMIT.toString()).getInt(-1);
		if (limit != -1) {
			if ((world.getLivingEntities().size() - world.getPlayers().size()) >= limit)
				event.setCancelled(true);
		}
		if (!event.isCancelled()) {
			final Class<?> entityClass = e.getClass();
			final String entityName = entityClass.getSimpleName();
			limit = acWorld.getMobLimit(entityName);
			if (limit == -1)
				return;
			int count = 0;
			for (final Entity entity : world.getLivingEntities())
				if (entity.getClass().equals(entityClass))
					count++;
			if (count >= limit)
				event.setCancelled(true);

		}
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled())
			return;
		if (!(event.getEntity() instanceof Player))
			return;
		final Player player = (Player) event.getEntity();
		if (ACPlayer.getPlayer(player.getName()).hasPower(Type.FLY)
				&& event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
			event.setCancelled(true);
			event.setDamage(0);
			return;
		} else if (ACPlayer.getPlayer(player.getName()).hasPower(Type.GOD)) {
			if (event.getCause().equals(DamageCause.FIRE)
					|| event.getCause().equals(DamageCause.FIRE_TICK))
				player.setFireTicks(0);
			event.setCancelled(true);
			event.setDamage(0);
		}

	}

	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		final Player player = (Player) event.getEntity();
		ACPlayer.getPlayer(player.getName()).setLastLocation(player.getLocation());
	}

	@Override
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.isCancelled())
			return;
		if (!(event.getTarget() instanceof Player))
			return;
		final Player p = (Player) event.getTarget();
		if (InvisibleWorker.getInstance().hasInvisiblePowers(p.getName())
				&& PermissionManager.hasPerm(p, "admincmd.invisible.notatarget", false))
			event.setCancelled(true);
	}

	@Override
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.isCancelled())
			return;
		if (!(event.getEntity() instanceof Player))
			return;
		final Player player = (Player) event.getEntity();
		if (ACPlayer.getPlayer(player.getName()).hasPower(Type.ETERNAL))
			event.setCancelled(true);
	}
}
