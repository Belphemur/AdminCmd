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

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import be.Balor.Manager.Exceptions.CommandAlreadyExist;
import be.Balor.Manager.Exceptions.CommandNotFound;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class ACCommands {
	protected String permNode = null;
	protected String cmdName = null;
	protected Permission bukkitPerm = null;
	protected PermissionDefault bukkitDefault = PermissionDefault.OP;
	protected boolean other = false;
	protected PluginCommand pluginCommand;
	protected ExecutorThread executorThread = new ExecutorThread(this);

	/**
	 * 
	 */
	public ACCommands() {
		permNode = "";
		cmdName = "";
	}

	/**
	 * Add the argument to the command, to execute it.
	 * 
	 * @param sender
	 * @param args
	 * @throws InterruptedException
	 */
	public void addArgs(CommandSender sender, String[] args) throws InterruptedException {
		executorThread.addArgs(sender, args);
		if (!executorThread.isAlive())
			executorThread.start();
	}

	/**
	 * Stop the command executor thread
	 */
	public void stopThread() {
		executorThread.stopThread();
	}

	/**
	 * Execute the command
	 * 
	 * @param args
	 */
	public abstract void execute(CommandSender sender, String... args);

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
		return PermissionManager.hasPerm(sender, bukkitPerm);
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
		bukkitPerm = PermissionManager.getInstance().addPermChild(permNode, bukkitDefault);
		if (other)
			PermissionManager.getInstance().addPermChild(permNode + ".other", bukkitDefault);
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
	public void initializeCommand(JavaPlugin plugin) throws CommandNotFound, CommandAlreadyExist {
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
	 * @author Balor (aka Antoine Aflalo)
	 * 
	 */
	private class ExecutorThread extends Thread {
		protected ACCommands command;
		protected LinkedBlockingQueue<CommandSender> sendersQueue;
		protected LinkedBlockingQueue<String[]> argsQueue;
		boolean stop = false;
		Semaphore sema;
		Object threadSync = new Object();

		/**
		 * 
		 */
		public ExecutorThread(ACCommands cmd) {
			command = cmd;
			sendersQueue = new LinkedBlockingQueue<CommandSender>(5);
			argsQueue = new LinkedBlockingQueue<String[]>(5);
			sema = new Semaphore(0);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			boolean stop = false;
			while (!stop) {
				synchronized (threadSync) {
					stop = this.stop;
				}
				CommandSender sender = sendersQueue.poll();
				try {
					sema.acquire();
					command.execute(sender, argsQueue.poll());
				} catch (InterruptedException e) {
				} catch (Throwable t) {
					Logger.getLogger("Minecraft")
							.severe("[AdminCmd] The command "
									+ command.getCmdName()
									+ " throw an Exception please report the log to this thread : http://forums.bukkit.org/threads/admincmd.10770");
					sender.sendMessage("[AdminCmd]"
							+ ChatColor.RED
							+ " The command "
							+ command.getCmdName()
							+ " throw an Exception please report the server.log to this thread : http://forums.bukkit.org/threads/admincmd.10770");
					Logger.getLogger("Minecraft").severe(Arrays.toString(t.getStackTrace()));
				}

			}
		}

		public synchronized void stopThread() {
			stop = true;
		}

		public synchronized void addArgs(CommandSender sender, String[] args)
				throws InterruptedException {
			sendersQueue.put(sender);
			argsQueue.put(args);
			sema.release();
		}

	}

}
