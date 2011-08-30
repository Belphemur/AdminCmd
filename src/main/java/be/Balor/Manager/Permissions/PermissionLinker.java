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

import org.bukkit.permissions.PermissionDefault;

import be.Balor.bukkit.AdminCmd.AdminCmd;

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

	/**
	 * Add some important node (like myplygin.item)
	 * 
	 * @param toAdd
	 */
	public void addPermParent(PermParent toAdd) {
		permissions.add(toAdd);
	}

	/**
	 * Add permission child (like myplugin.item.add)
	 * 
	 * @param permNode
	 * @param bukkitDefault
	 * @return
	 */
	public org.bukkit.permissions.Permission addPermChild(String permNode,
			PermissionDefault bukkitDefault) {
		org.bukkit.permissions.Permission bukkitPerm = null;
		if ((bukkitPerm = AdminCmd.getBukkitServer().getPluginManager().getPermission(permNode)) == null) {
			bukkitPerm = new org.bukkit.permissions.Permission(permNode, bukkitDefault);
			AdminCmd.getBukkitServer().getPluginManager().addPermission(bukkitPerm);
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
	public org.bukkit.permissions.Permission addOnTheFly(String permNode, String parentNode) {
		org.bukkit.permissions.Permission child;
		if ((child = AdminCmd.getBukkitServer().getPluginManager().getPermission(permNode)) == null) {
			org.bukkit.permissions.Permission parent = AdminCmd.getBukkitServer()
					.getPluginManager().getPermission(parentNode);
			child = new org.bukkit.permissions.Permission(permNode, PermissionDefault.OP);
			AdminCmd.getBukkitServer().getPluginManager().addPermission(child);
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
	public org.bukkit.permissions.Permission addPermChild(String permNode) {
		return addPermChild(permNode, PermissionDefault.OP);
	}

	/**
	 * Register all parent node.
	 */
	public void registerAllPermParent() {
		for (PermParent pp : permissions)
			pp.registerBukkitPerm();
		majorPerm.registerBukkitPerm();
		permissions = null;
		majorPerm = null;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
