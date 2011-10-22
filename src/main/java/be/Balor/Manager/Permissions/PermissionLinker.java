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
package be.Balor.Manager.Permissions;

import java.util.LinkedList;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;


import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PermissionLinker {
	protected LinkedList<PermParent> permissions = new LinkedList<PermParent>();
	protected PermParent majorPerm;
	protected String name;

	/**
	 * 
	 */
	protected PermissionLinker(String name) {
		this.name = name;
	}

	/**
	 * Get the PermissionLinker 
	 * Note: The PermissionManager may only retain a weak
	 * reference to the newly created PermissionLinker. 
	 * It is important to understand that
	 * a previously created PermissionLinker with the given name may be garbage collected
	 * at any time if there is no strong reference to the PermissionLinker. 
	 * In particular,
	 * this means that two back-to-back calls like
	 * {@code PermissionLinker("MyPermissionLinker").addPermParent(...)} may use different PermissionLinker objects
	 * named "MyPermissionLinker" if there is no strong reference to the PermissionLinker named
	 * "MyPermissionLinker" elsewhere in the program.
	 * 
	 * @param name
	 * @return
	 */
	public static synchronized PermissionLinker getPermissionLinker(String name) {
		return PermissionManager.getInstance().demandPermissionLinker(name);
	}

	/**
	 * Set major permission, the root.
	 * 
	 * @param major
	 */
	public void setMajorPerm(PermParent major) {
		majorPerm = major;
		for (PermParent pp : permissions)
			majorPerm.addChild(pp.getPermName());
	}
	public void setMajorPerm(String major) {
		majorPerm = new PermParent(major);
		for (PermParent pp : permissions)
			majorPerm.addChild(pp.getPermName());
	}

	/**
	 * Add some important node (like myplygin.item)
	 * 
	 * @param toAdd
	 */
	public void addPermParent(PermParent toAdd) {
		permissions.add(toAdd);
	}
	public void addPermParent(String toAdd) {
		permissions.add(new PermParent(toAdd));
	}


	/**
	 * Add permission child (like myplugin.item.add)
	 * 
	 * @param permNode
	 * @param bukkitDefault
	 * @return
	 */
	public Permission addPermChild(String permNode,
			PermissionDefault bukkitDefault) {
		Permission bukkitPerm = null;
		if ((bukkitPerm = ACPluginManager.getServer().getPluginManager().getPermission(permNode)) == null) {
			bukkitPerm = new Permission(permNode, bukkitDefault);
			ACPluginManager.getServer().getPluginManager().addPermission(bukkitPerm);
			for (PermParent pp : permissions)
				if (permNode.contains(pp.getCompareName()))
					pp.addChild(permNode);
		}
		return bukkitPerm;
	}

	/**
	 * Add a perm on the fly
	 * 
	 * @param permNode
	 * @param parentNode
	 * @return
	 */
	public static Permission addOnTheFly(String permNode, String parentNode) {
		Permission child;
		if ((child = ACPluginManager.getServer().getPluginManager().getPermission(permNode)) == null) {
			Permission parent = ACPluginManager.getServer()
					.getPluginManager().getPermission(parentNode);
			child = new Permission(permNode, PermissionDefault.OP);
			ACPluginManager.getServer().getPluginManager().addPermission(child);
			parent.getChildren().put(permNode, true);
		}
		return child;

	}

	/**
	 * Add permission child (like myplugin.item.add)
	 * 
	 * @param permNode
	 * @return
	 */
	public Permission addPermChild(String permNode) {
		return addPermChild(permNode, PermissionDefault.OP);
	}

	/**
	 * Register all parent node.
	 */
	public void registerAllPermParent() {
		for (PermParent pp : permissions)
			pp.registerBukkitPerm();
		majorPerm.registerBukkitPerm();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
