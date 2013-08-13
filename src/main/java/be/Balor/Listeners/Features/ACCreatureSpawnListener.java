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
package be.Balor.Listeners.Features;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Tools.Type;
import be.Balor.World.ACWorld;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACCreatureSpawnListener implements Listener {

	private final Map<World, Integer> generalLimit = new HashMap<World, Integer>();
	private final Map<World, Map<Class<? extends Entity>, Integer>> specifiedLimit = new HashMap<World, Map<Class<? extends Entity>, Integer>>();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCreatureSpawn(final CreatureSpawnEvent event) {
		final Entity e = event.getEntity();
		if (e instanceof HumanEntity) {
			return;
		}
		final World world = e.getWorld();
		final ACWorld acWorld;
		try {
			acWorld = ACWorld.getWorld(world);
		} catch (final WorldNotLoaded e2) {
			return;
		}

		Integer limit = acWorld.getInformation(Type.MOB_LIMIT.toString()).getInt(-1);
		if (limit != -1) {
			int count = getGeneralCount(world);
			if (count >= limit) {
				event.setCancelled(true);
				return;
			}
			generalLimit.put(world, ++count);

		}

		final Class<? extends Entity> entityClass = e.getClass();
		final String entityName = entityClass.getSimpleName();
		limit = acWorld.getMobLimit(entityName);
		if (limit == -1) {
			return;
		}
		event.setCancelled(checkSpecifiedLimit(world, limit, entityClass));

	}

	/**
	 * @param world
	 * @param limit
	 * @param entityClass
	 * @return true if the event need to be cancelled
	 */
	private boolean checkSpecifiedLimit(final World world, final Integer limit, final Class<? extends Entity> entityClass) {
		int count = 0;
		Map<Class<? extends Entity>, Integer> tmp = specifiedLimit.get(world);

		if (tmp == null) {
			tmp = new HashMap<Class<? extends Entity>, Integer>();
			count = world.getEntitiesByClass(entityClass).size();
			tmp.put(entityClass, count);
			specifiedLimit.put(world, tmp);
		} else {
			count = tmp.get(entityClass);
		}
		if (count >= limit) {
			return true;
		}
		tmp.put(entityClass, ++count);
		return false;
	}

	/**
	 * @param world
	 * @return
	 */
	private Integer getGeneralCount(final World world) {
		Integer count = generalLimit.get(world);
		if (count == null) {
			final List<LivingEntity> livEntities = world.getLivingEntities();
			count = livEntities.size() - world.getPlayers().size();
			generalLimit.put(world, count);
		}
		return count;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onMobKilled(final EntityDeathEvent event) {
		final Entity e = event.getEntity();
		if (!(e instanceof LivingEntity)) {
			return;
		}
		if (e instanceof HumanEntity) {
			return;
		}
		final World world = e.getWorld();
		Integer count = generalLimit.get(world);
		if (count == null) {
			return;
		}
		generalLimit.put(world, --count);

		final Map<Class<? extends Entity>, Integer> tmp = specifiedLimit.get(world);
		if (tmp == null) {
			return;
		}
		count = tmp.get(e.getClass());
		if (count == null) {
			return;
		}
		tmp.put(e.getClass(), --count);
	}
}
