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
package be.Balor.Listeners.Commands;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import be.Balor.Tools.MobCheck;
import be.Balor.Tools.Type;
import be.Balor.World.ACWorld;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACCreatureSpawnListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(final CreatureSpawnEvent event) {
		if (event.isCancelled())
			return;
		final Entity e = event.getEntity();
		if (!MobCheck.isMonster(e) && !MobCheck.isAnimal(e))
			return;
		final World world = e.getWorld();
		final ACWorld acWorld = ACWorld.getWorld(world.getName());
		final List<LivingEntity> livEntities = world.getLivingEntities();
		Integer limit = acWorld.getInformation(Type.MOB_LIMIT.toString()).getInt(-1);
		if (limit != -1) {
			if ((livEntities.size() - world.getPlayers().size()) >= limit)
				event.setCancelled(true);
		}
		if (!event.isCancelled()) {
			final Class<? extends Entity> entityClass = e.getClass();
			final String entityName = entityClass.getSimpleName();
			limit = acWorld.getMobLimit(entityName);
			if (limit == -1)
				return;
			final int count = world.getEntitiesByClass(entityClass).size();
			if (count >= limit)
				event.setCancelled(true);

		}
	}
}
