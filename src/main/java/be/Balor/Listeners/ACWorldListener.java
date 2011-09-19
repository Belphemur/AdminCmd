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

import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;

import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACWorldListener extends WorldListener {
	@Override
	public void onWorldLoad(WorldLoadEvent event) {
		ACWorld world = ACWorld.getWorld(event.getWorld().getName());
		int task = world.getInformation(Type.TIME_FREEZED.toString()).getInt(-1);
		if (task != -1) {
			task = ACPluginManager.getScheduler().scheduleAsyncRepeatingTask(
					ACHelper.getInstance().getCoreInstance(), new Utils.SetTime(event.getWorld()),
					0, 10);
			world.setInformation(Type.TIME_FREEZED.toString(), task);
		}

	}
}
