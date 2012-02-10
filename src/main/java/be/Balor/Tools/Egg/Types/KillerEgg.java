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
package be.Balor.Tools.Egg.Types;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityLiving;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEggThrowEvent;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Egg.EggType;
import be.Balor.Tools.Egg.Exceptions.ProcessingArgsException;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class KillerEgg extends EggType<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7763897329319981939L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Egg.EggType#onEvent(org.bukkit.event.player.
	 * PlayerEggThrowEvent)
	 */
	@Override
	public void onEvent(PlayerEggThrowEvent event) {
		event.setHatching(false);
		final Location loc = event.getEgg().getLocation();
		event.getEgg().remove();
		final List<EntityLiving> entities = new ArrayList<EntityLiving>();
		final CraftPlayer p = (CraftPlayer) event.getPlayer();
		final World w = p.getWorld();
		final int radius = value * value;
		for (Object entity : ((CraftWorld) w).getHandle().entityList)
			if (entity instanceof EntityLiving)
				entities.add((EntityLiving) entity);
		ACPluginManager.getScheduler().scheduleAsyncDelayedTask(ACPluginManager.getCorePlugin(),
				new Runnable() {

					@Override
					public void run() {
						int count = 0;
						w.playEffect(loc, Effect.SMOKE, 1, value);
						w.playEffect(loc, Effect.BLAZE_SHOOT, 0, value);
						for (EntityLiving entity : entities) {
							if (entity.equals(p.getHandle())) {
								continue;
							}
							Location entityLoc = new Location(w, entity.locX, entity.locY,
									entity.locZ, entity.yaw, entity.pitch);
							if (entityLoc.distanceSquared(loc) > radius)
								continue;
							entity.die(DamageSource.playerAttack(p.getHandle()));
							entity.die();
							count++;
						}
						p.sendMessage(String.valueOf(count) + " killed.");
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Tools.Egg.EggType#processArguments(org.bukkit.entity.Player,
	 * be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	protected void processArguments(Player sender, CommandArgs args) throws ProcessingArgsException {
		int radius = ConfigEnum.DEGG_KILL_RADIUS.getInt();
		String valFlag = args.getValueFlag('r');
		if (valFlag != null)
			try {
				radius = Integer.parseInt(valFlag);
			} catch (NumberFormatException e) {
				Utils.sI18n(sender, "NaN", "number", valFlag);
				return;
			}
		value = radius;

	}

}
