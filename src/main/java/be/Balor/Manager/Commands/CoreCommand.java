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
package be.Balor.Manager.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.CommandAlreadyExist;
import be.Balor.Manager.Exceptions.CommandNotFound;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.PermChild;
import be.Balor.Manager.Permissions.PermParent;
import be.Balor.Manager.Permissions.PermissionException;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.AbstractAdminCmdPlugin;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class CoreCommand {
	protected String permNode = "";
	protected String cmdName = "";
	protected PermChild permChild = null;
	protected PermissionDefault bukkitDefault = PermissionDefault.OP;
	protected boolean other = false;
	protected PluginCommand pluginCommand;
	protected final AbstractAdminCmdPlugin plugin;
	protected PermParent permParent;
	private PermParent addedPermParent = null;
	@Deprecated
	/**
	 * Remove the use of Permission in the command, better use the PermChild.
	 */
	protected Permission bukkitPerm = null;

	/**
	 * Constructor of CoreCommand
	 * 
	 * @param name
	 *            name of the command (in the plugin.yml)
	 * @param perm
	 *            permission needed by the player to execute the command
	 */
	public CoreCommand(final String name, final String perm) {
		this.permNode = perm;
		this.cmdName = name;
		this.plugin = ACPluginManager.getCorePlugin();
	}

	public CoreCommand() {
		this.plugin = ACPluginManager.getCorePlugin();
	}

	/**
	 * Constructor of CoreCommand
	 * 
	 * @param name
	 *            name of the command (in the plugin.yml)
	 * @param perm
	 *            permission needed by the player to execute the command
	 * @param plugin
	 *            name of the AdminCmd plugin that the command belong to.
	 */
	public CoreCommand(final String name, final String perm, final String plugin) {
		this.permNode = perm;
		this.cmdName = name;
		this.plugin = ACPluginManager.getPluginInstance(plugin);
	}

	/**
	 * Constructor of CoreCommand
	 * 
	 * @param name
	 *            name of the command (in the plugin.yml)
	 * @param perm
	 *            permission needed by the player to execute the command
	 * @param plugin
	 *            name of the AdminCmd plugin that the command belong to.
	 * @param parent
	 *            PermParent used to register the permission of the command
	 */
	public CoreCommand(final String name, final String perm, final String plugin, final PermParent parent) {
		this.permNode = perm;
		this.cmdName = name;
		this.plugin = ACPluginManager.getPluginInstance(plugin);
		this.permParent = parent;
	}

	/**
	 * Execute the command
	 * 
	 * @param sender
	 *            sender of the command
	 * @param args
	 *            arguments to be processed by the command
	 * @throws PermissionException
	 *             if the player don't have the permission to do that.
	 * @throws PlayerNotFound
	 *             the target player of the command is not found
	 */
	public abstract void execute(CommandSender sender, CommandArgs args) throws ActionNotPermitedException, PlayerNotFound;

	/**
	 * Check if the command can be executed
	 * 
	 * @param args
	 * @return
	 */
	public abstract boolean argsCheck(String... args);

	/**
	 * Check if the sender have the permission for the command.
	 * 
	 * @param sender
	 * @return
	 */
	public boolean permissionCheck(final CommandSender sender) {
		if (permNode != null && !permNode.isEmpty()) {
			return PermissionManager.hasPerm(sender, permChild.getPermName());
		} else {
			return true;
		}
	}

	/**
	 * @return the cmdName
	 */
	public String getCmdName() {
		return cmdName;
	}

	/**
	 * @return the permNode
	 */
	public String getPermNode() {
		return permNode;
	}

	/**
	 * Create and register the permission <b>permNode.*</b> and add it the
	 * wanted subpermission.
	 * 
	 * @param perm
	 *            the suffixe of the permission to add. The command concat the
	 *            permNode of the command with the new perm you register.
	 */
	public void addPermChild(final String perm) {
		this.addPermChild(new PermChild(permNode + "." + perm, bukkitDefault));
	}

	/**
	 * Create and register the permission <b>permNode.*</b> and add it the
	 * wanted subpermission.
	 * 
	 * @param perm
	 *            the permission to add.
	 */
	public void addPermChild(final PermChild perm) {
		if (addedPermParent == null) {
			addedPermParent = new PermParent(permNode + ".*");
			if (permParent != null) {
				permParent.addChild(addedPermParent);
			}
		}
		addedPermParent.addChild(perm);

	}

	/**
	 * Register the bukkit Permission
	 */
	public void registerBukkitPerm() {
		DebugLog.beginInfo("Registering permission");
		try {
			if (permNode == null || (permNode != null && permNode.isEmpty())) {
				return;
			}
			if (permParent != null) {
				DebugLog.beginInfo("Register_def permission in the permParent");
				permChild = new PermChild(permNode, bukkitDefault);
				permChild.setPluginCommand(this);
				permParent.addChild(permChild);
				if (other) {
					permParent.addChild(new PermChild(permNode + ".other", bukkitDefault));
				}
				DebugLog.endInfo();
				return;
			}
			DebugLog.beginInfo("Register permission without a PermParent");
			permChild = plugin.getPermissionLinker().addPermChild(permNode, bukkitDefault);
			permChild.setPluginCommand(this);
			if (addedPermParent != null) {
				plugin.getPermissionLinker().addPermParent(addedPermParent);
			}
			if (other) {
				plugin.getPermissionLinker().addPermChild(permNode + ".other", bukkitDefault);
			}
			DebugLog.endInfo();
		} finally {
			DebugLog.endInfo();
		}
	}

	/**
	 * Check if the command is usable on other people than the CommandSender
	 * 
	 * @return
	 */
	public boolean toOther() {
		return other;
	}

	/**
	 * Initialize the bukkit plugin command
	 * 
	 * @param plugin
	 * @throws CommandNotFound
	 * @throws CommandAlreadyExist
	 */
	public void initializeCommand() throws CommandNotFound, CommandAlreadyExist {
		DebugLog.beginInfo("Check for bukkit command and status of the command");
		try {
			if ((pluginCommand = plugin.getCommand(cmdName)) == null) {
				throw new CommandNotFound(cmdName + " is not loaded in bukkit. Command deactivated", this);
			}
			DebugLog.beginInfo("Check Alias of the commands");
			if (pluginCommand.getAliases().isEmpty()) {
				throw new CommandAlreadyExist(cmdName + " has all his alias already registered. Command deactivated", this);
			}
		} finally {
			DebugLog.endInfo();
			DebugLog.endInfo();
		}
	}

	/**
	 * @return the pluginCommand
	 */
	public PluginCommand getPluginCommand() {
		return pluginCommand;
	}

	/**
	 * @return the plugin
	 */
	public AbstractAdminCmdPlugin getPlugin() {
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CoreCommand [permNode=" + permNode + ", cmdName=" + cmdName + ", plugin=" + plugin + ", permParent=" + permParent + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bukkitDefault == null) ? 0 : bukkitDefault.hashCode());
		result = prime * result + ((cmdName == null) ? 0 : cmdName.hashCode());
		result = prime * result + (other ? 1231 : 1237);
		result = prime * result + ((permNode == null) ? 0 : permNode.hashCode());
		result = prime * result + ((permParent == null) ? 0 : permParent.hashCode());
		result = prime * result + ((plugin == null) ? 0 : plugin.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CoreCommand)) {
			return false;
		}
		final CoreCommand other = (CoreCommand) obj;
		if (bukkitDefault != other.bukkitDefault) {
			return false;
		}
		if (cmdName == null) {
			if (other.cmdName != null) {
				return false;
			}
		} else if (!cmdName.equals(other.cmdName)) {
			return false;
		}
		if (this.other != other.other) {
			return false;
		}
		if (permNode == null) {
			if (other.permNode != null) {
				return false;
			}
		} else if (!permNode.equals(other.permNode)) {
			return false;
		}
		if (permParent == null) {
			if (other.permParent != null) {
				return false;
			}
		} else if (!permParent.equals(other.permParent)) {
			return false;
		}
		if (plugin == null) {
			if (other.plugin != null) {
				return false;
			}
		} else if (!plugin.equals(other.plugin)) {
			return false;
		}
		return true;
	}

}
