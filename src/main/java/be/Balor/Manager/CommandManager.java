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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class CommandManager implements CommandExecutor {
	private HashMap<Command, ACCommands> commands = new HashMap<Command, ACCommands>();
	private final int MAX_THREADS = 5;
	private ArrayList<ExecutorThread> threads = new ArrayList<CommandManager.ExecutorThread>(
			MAX_THREADS);
	private int cmdCount = 0;
	private static CommandManager instance = null;
	private JavaPlugin plugin;

	/**
	 * @return the instance
	 */
	public static CommandManager getInstance() {
		if (instance == null)
			instance = new CommandManager();
		return instance;
	}

	/**
	 * @param plugin
	 *            the plugin to set
	 */
	public void setPlugin(JavaPlugin plugin) {
		this.plugin = plugin;
		for (int i = 0; i < MAX_THREADS; i++)
			threads.add(new ExecutorThread());
	}

	/**
	 * Register command
	 * 
	 * @param clazz
	 */
	public void registerCommand(Class<?> clazz) {
		try {
			ACCommands command = (ACCommands) clazz.newInstance();
			command.initializeCommand(plugin);
			command.registerBukkitPerm();
			command.getPluginCommand().setExecutor(this);
			commands.put(command.getPluginCommand(), command);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (CommandException e) {
			Logger.getLogger("Minecraft").info("[AdminCmd] " + e.getMessage());
		}
	}

	public void checkAlias() {
		for (Command cmd : PluginCommandYamlParser.parse(plugin)) {
			cmd.getAliases().removeAll(plugin.getCommand(cmd.getName()).getAliases());
			String aliases = "";
			for (String alias : cmd.getAliases())
				aliases += alias + ", ";
			if (!aliases.isEmpty())
				Logger.getLogger("Minecraft").info(
						"[" + plugin.getDescription().getName() + "] Disabled Alias(es) for "
								+ cmd.getName() + " : " + aliases);
		}
	}

	public void stopAllExecutorThreads() {
		for (ExecutorThread t : threads) {
			t.stopThread();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			ACCommands cmd = null;
			if (commands.containsKey(command)
					&& (cmd = commands.get(command)).permissionCheck(sender) && cmd.argsCheck(args)) {
				threads.get(cmdCount).addCommand(cmd, sender, args);
				cmdCount++;
				if (cmdCount == MAX_THREADS)
					cmdCount = 0;
				return true;
			} else
				return false;
		} catch (Throwable t) {
			Logger.getLogger("Minecraft")
					.severe("[AdminCmd] The command "
							+ command.getName()
							+ " throw an Exception please report the log to this thread : http://forums.bukkit.org/threads/admincmd.10770");
			sender.sendMessage("[AdminCmd]"
					+ ChatColor.RED
					+ " The command "
					+ command.getName()
					+ " throw an Exception please report the server.log to this thread : http://forums.bukkit.org/threads/admincmd.10770");
			t.printStackTrace();
			return false;
		}
	}

	/**
	 * @author Balor (aka Antoine Aflalo)
	 * 
	 */
	private class ExecutorThread extends Thread {
		protected LinkedBlockingQueue<ACCommands> commands;
		protected LinkedBlockingQueue<CommandSender> sendersQueue;
		protected LinkedBlockingQueue<String[]> argsQueue;
		protected final int MAX_REQUEST = 5;
		boolean stop = false;
		Semaphore sema;
		Object threadSync = new Object();

		/**
		 * 
		 */
		public ExecutorThread() {
			commands = new LinkedBlockingQueue<ACCommands>(MAX_REQUEST);
			sendersQueue = new LinkedBlockingQueue<CommandSender>(MAX_REQUEST);
			argsQueue = new LinkedBlockingQueue<String[]>(MAX_REQUEST);
			sema = new Semaphore(0);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			while (true) {
				try {
					sema.acquire();
					synchronized (threadSync) {
						if (this.stop)
							break;
					}
					commands.poll().execute(sendersQueue.poll(), argsQueue.poll());
				} catch (InterruptedException e) {
				}

			}
		}

		public synchronized void stopThread() {
			stop = true;
			sema.release();
		}

		public synchronized void addCommand(final ACCommands cmd, final CommandSender sender,
				final String[] args) throws InterruptedException {
			commands.put(cmd);
			sendersQueue.put(sender);
			argsQueue.put(args);
			sema.release();
			if (!isAlive())
				this.start();
		}

	}
}
