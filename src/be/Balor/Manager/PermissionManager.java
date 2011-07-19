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
package be.Balor.Manager;

import java.util.LinkedList;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.Balor.bukkit.AdminCmd.AdminCmd;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class PermissionManager {
	private LinkedList<PermParent> permissions = new LinkedList<PermParent>();
	private PermParent majorPerm;
	private static PermissionManager instance=null;
	/**
	 * @return the instance
	 */
	public static PermissionManager getInstance() {
		if(instance == null)
			instance = new PermissionManager();
		return instance;
	}
	/**
	 * Set major permission, the root.
	 * @param major
	 */
	public void setMajorPerm(PermParent major)
	{
		majorPerm = major;
		for (PermParent pp : permissions)
			majorPerm.addChild(pp.getPermName());
	}
	/**
	 * Add some important node (like myplygin.item)
	 * @param toAdd
	 */
	public void addPermParent(PermParent toAdd) {
		permissions.add(toAdd);
	}
	/**
	 * Add permission child (like myplugin.item.add)
	 * @param permNode
	 * @param bukkitDefault
	 * @return
	 */
	public Permission addPermChild(String permNode, PermissionDefault bukkitDefault)
	{
		Permission bukkitPerm = null;
		if (AdminCmd.getBukkitServer().getPluginManager().getPermission(permNode) == null) {
			bukkitPerm = new Permission(permNode, bukkitDefault);
			AdminCmd.getBukkitServer().getPluginManager().addPermission(bukkitPerm);
			for (PermParent pp : permissions)
				if (permNode.contains(pp.getCompareName()))
					pp.addChild(permNode);
		}
		return bukkitPerm;
	}
	/**
	 * Add permission child (like myplugin.item.add)
	 * @param permNode
	 * @return
	 */
	public Permission addPermChild(String permNode)
	{
		 return addPermChild(permNode, PermissionDefault.OP);
	}
	/**
	 * Register all parent node.
	 */
	public void registerAllPermParent()
	{
		for (PermParent pp : permissions)
			pp.registerBukkitPerm();
		majorPerm.registerBukkitPerm();
	}


}
