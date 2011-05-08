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
package belgium.Balor.Workers;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.iConomy.iConomy;
import com.nijiko.permissions.PermissionHandler;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class Worker {
	protected static PermissionHandler permission = null;
	public static final Logger log = Logger.getLogger("Minecraft");
	protected static iConomy iConomy = null;
	protected HashMap<String, HashMap<String, Boolean>> permissions = new HashMap<String, HashMap<String, Boolean>>();

	/**
	 * Check the permissions
	 * 
	 * @param player
	 * @param perm
	 * @return boolean
	 */
	public boolean hasPerm(Player player, String perm) {
		return hasPerm(player, perm, true);
	}

	/**
	 * Check the permission with the possibility to disable the error msg
	 * 
	 * @param player
	 * @param perm
	 * @param errorMsg
	 * @return
	 */
	public boolean hasPerm(Player player, String perm, boolean errorMsg) {
		if (permission == null) {
			if (perm.contains("admin") || perm.contains("free"))
				return player.isOp();
			return true;
		}
		String playerName = player.getName();
		if (permissions.containsKey(playerName)) {
			if (permissions.get(playerName).containsKey(perm))
				if (permissions.get(playerName).get(perm))
					return true;
				else {
					if (errorMsg)
						player.sendMessage(ChatColor.RED
								+ "You don't have the Permissions to do that " + ChatColor.BLUE
								+ "(" + perm + ")");
					return false;
				}

			if (permission.has(player, perm)) {
				permissions.get(playerName).put(perm, true);
				return true;
			} else {
				permissions.get(playerName).put(perm, false);
				if (errorMsg)
					player.sendMessage(ChatColor.RED + "You don't have the Permissions to do that "
							+ ChatColor.BLUE + "(" + perm + ")");
			}
		} else {
			permissions.put(playerName, new HashMap<String, Boolean>());
			if (permission.has(player, perm)) {
				permissions.get(playerName).put(perm, true);
				return true;
			} else {
				permissions.get(playerName).put(perm, false);
				if (errorMsg)
					player.sendMessage(ChatColor.RED + "You don't have the Permissions to do that "
							+ ChatColor.BLUE + "(" + perm + ")");
			}

		}

		return false;

	}

	/**
	 * iConomy plugin
	 * 
	 * @return
	 */
	public static iConomy getiConomy() {
		return iConomy;
	}

	/**
	 * Set iConomy Plugin
	 * 
	 * @param plugin
	 * @return
	 */
	public static boolean setiConomy(iConomy plugin) {
		if (iConomy == null) {
			iConomy = plugin;
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Permission plugin
	 * 
	 * @return
	 */
	public static PermissionHandler getPermission() {
		return permission;
	}

	/**
	 * Set iConomy Plugin
	 * 
	 * @param plugin
	 * @return
	 */
	public static boolean setPermission(PermissionHandler plugin) {
		if (permission == null) {
			permission = plugin;
		} else {
			return false;
		}
		return true;
	}
}