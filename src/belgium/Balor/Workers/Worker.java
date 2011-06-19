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

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.nijiko.permissions.PermissionHandler;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class Worker {
	protected static PermissionHandler permission = null;
	public static final Logger log = Logger.getLogger("Minecraft");

	/**
	 * Check the permissions
	 * 
	 * @param player
	 * @param perm
	 * @return boolean
	 */
	public boolean hasPerm(CommandSender player, String perm) {
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
	public boolean hasPerm(CommandSender player, String perm, boolean errorMsg) {
		if(!(player instanceof Player))
			return true;
		if (permission == null) {
			if (perm.contains("admin") || perm.contains("free"))
				return player.isOp();
			return true;
		} else if (permission.has((Player)player, perm)) {
			return true;
		} else {
			if (errorMsg)
				player.sendMessage(ChatColor.RED + "You don't have the Permissions to do that "
						+ ChatColor.BLUE + "(" + perm + ")");
			return false;
		}

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