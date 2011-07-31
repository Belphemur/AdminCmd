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
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;

import be.Balor.Manager.PermissionManager;
import be.Balor.Tools.MobCheck;
import be.Balor.Tools.Type;
import be.Balor.bukkit.AdminCmd.ACHelper;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACEntityListener extends EntityListener {
	private ACHelper worker;

	/**
	 * 
	 */
	public ACEntityListener(ACHelper admin) {
		worker = admin;
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled())
			return;
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		if (worker.isValueSet(Type.FLY, player)
				&& event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
			event.setCancelled(true);
			event.setDamage(0);
			return;
		} else if (worker.hasGodPowers(player.getName())) {
			if (event.getCause().equals(DamageCause.FIRE)
					|| event.getCause().equals(DamageCause.FIRE_TICK))
				player.setFireTicks(0);
			event.setCancelled(true);
			event.setDamage(0);
		}

	}

	@Override
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.isCancelled())
			return;
		if (!(event.getTarget() instanceof Player))
			return;
		Player p = (Player) event.getTarget();
		if (InvisibleWorker.getInstance().hasInvisiblePowers(p.getName())
				&& PermissionManager.hasPerm(p, "admincmd.invisible.notatarget"))
			event.setCancelled(true);
	}

	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		Entity e = event.getEntity();
		if (!MobCheck.isMonster(e) && !MobCheck.isAnimal(e))
			return;
		World world = e.getWorld();		
		Integer limit = (Integer) ACHelper.getInstance().getValue(Type.MOB_LIMIT, world.getName());
		if (limit != null) {
			Integer count = world.getLivingEntities().size() - world.getPlayers().size();
			if (count + 1 >= limit) {
				event.setCancelled(true);
				System.out.print("Spawn blocked");
			}
		}
	}
}
