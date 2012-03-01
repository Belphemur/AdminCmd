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

import be.Balor.Manager.Exceptions.CommandAlreadyExist;
import be.Balor.Manager.Exceptions.CommandNotFound;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.PermChild;
import be.Balor.Manager.Permissions.PermParent;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.AbstractAdminCmdPlugin;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class CoreCommand {
	protected String permNode = "";
	protected String cmdName = "";
	protected Permission bukkitPerm = null;
	protected PermissionDefault bukkitDefault = PermissionDefault.OP;
	protected boolean other = false;
	protected PluginCommand pluginCommand;
	protected final AbstractAdminCmdPlugin plugin;
	protected PermParent permParent;

	/**
	 * Constructor of CoreCommand
	 * 
	 * @param name
	 *            name of the command (in the plugin.yml)
	 * @param perm
	 *            permission needed by the player to execute the command
	 */
	public CoreCommand(String name, String perm) {
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
	public CoreCommand(String name, String perm, String plugin) {
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
	public CoreCommand(String name, String perm, String plugin, PermParent parent) {
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
	 * 
	 * @throws PlayerNotFound
	 *             the target player of the command is not found
	 */
	public abstract void execute(CommandSender sender, CommandArgs args) throws PlayerNotFound;

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
	public boolean permissionCheck(CommandSender sender) {
		if (permNode != null && !permNode.isEmpty())
			return PermissionManager.hasPerm(sender, bukkitPerm);
		else
			return true;
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
	 * Register the bukkit Permission
	 */
	public void registerBukkitPerm() {
		if (permNode != null && !permNode.isEmpty()) {
			if (permParent != null) {
				PermChild child = new PermChild(permNode, bukkitDefault);
				permParent.addChild(child);
				bukkitPerm = child.getBukkitPerm();
				if (other)
					permParent.addChild(new PermChild(permNode + ".other", bukkitDefault));

				return;
			}
			bukkitPerm = plugin.getPermissionLinker().addPermChild(permNode, bukkitDefault);
			if (other)
				plugin.getPermissionLinker().addPermChild(permNode + ".other", bukkitDefault);
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
		if ((pluginCommand = plugin.getCommand(cmdName)) == null)
			throw new CommandNotFound(cmdName + " is not loaded in bukkit. Command deactivated");

		if (pluginCommand.getAliases().isEmpty())
			throw new CommandAlreadyExist(cmdName
					+ " has all his alias already registered. Command deactivated");
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
		return "CoreCommand [permNode=" + permNode + ", cmdName=" + cmdName + ", plugin=" + plugin
				+ ", permParent=" + permParent + "]";
	}

}
