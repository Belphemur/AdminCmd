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
package be.Balor.Tools.Egg;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import be.Balor.Manager.Permissions.PermissionManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class EggPermissionManager {

	public final static EggPermissionManager INSTANCE = new EggPermissionManager();
	private final Map<Class<? extends EggType<?>>, Permission> permissions = new HashMap<Class<? extends EggType<?>>, Permission>();

	/**
	 * 
	 */
	private EggPermissionManager() {
	}

	void addPermission(final Class<? extends EggType<?>> clazz,
			final Permission perm) {
		permissions.put(clazz, perm);
	}

	/**
	 * Get the list of every EggType that the player have access to.
	 * 
	 * @param player
	 *            used to check the permissions
	 * @return
	 */
	public SortedSet<String> getEggTypeNames(final Player player) {
		final SortedSet<String> result = new TreeSet<String>();
		for (final Entry<Class<? extends EggType<?>>, Permission> entry : permissions
				.entrySet()) {
			if (entry.getValue() == null) {
				result.add(entry.getKey().getSimpleName());
				continue;
			}
			if (!PermissionManager.hasPerm(player, entry.getValue(), false)) {
				continue;
			}
			result.add(entry.getKey().getSimpleName());
		}
		return result;
	}

	Permission getPermission(final EggType<?> eggInstance) {
		return permissions.get(eggInstance.getClass());
	}
}
