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
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.AdminCmd;

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
	private boolean threadsStarted = false;

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
		startThreads();
	}

	public void startThreads() {
		if (!threadsStarted) {
			threads.clear();
			for (int i = 0; i < MAX_THREADS; i++) {
				threads.add(new ExecutorThread());
				threads.get(i).start();
			}
		}
		threadsStarted = true;
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
		threadsStarted = false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			ACCommands cmd = null;
			if (commands.containsKey(command)
					&& (cmd = commands.get(command)).permissionCheck(sender) && cmd.argsCheck(args)) {
				if (cmd.getCmdName().equals("bal_replace") || cmd.getCmdName().equals("bal_undo"))
					AdminCmd.getBukkitServer()
							.getScheduler()
							.scheduleSyncDelayedTask(ACHelper.getInstance().getPluginInstance(),
									new SyncTask(cmd, sender, args));
				else
					threads.get(cmdCount).addCommand(cmd, sender, args);
				if (!cmd.getCmdName().equals("bal_repeat")) {
					if (Utils.isPlayer(sender, false))
						ACHelper.getInstance().addValue(Type.REPEAT_CMD, (Player) sender,
								new ACCommandContainer(sender, cmd, args));
					else
						ACHelper.getInstance().addValue(Type.REPEAT_CMD, "serverConsole",
								new ACCommandContainer(sender, cmd, args));
				}
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
			if (cmdCount == 0)
				threads.get(4).start();
			else
				threads.get(cmdCount - 1).start();
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
			ACCommands command = null;
			while (true) {
				try {
					sema.acquire();
					synchronized (threadSync) {
						if (this.stop)
							break;
					}
					command = commands.peek();
					commands.poll().execute(sendersQueue.poll(), argsQueue.poll());
				} catch (InterruptedException e) {
				} catch (Throwable t) {
					Logger.getLogger("Minecraft")
							.severe("[AdminCmd] The command "
									+ command.getCmdName()
									+ " throw an Exception please report the log to this thread : http://forums.bukkit.org/threads/admincmd.10770");
					AdminCmd.getBukkitServer()
							.broadcastMessage(
									"[AdminCmd] The command "
											+ command.getCmdName()
											+ " throw an Exception please report the log to this thread : http://forums.bukkit.org/threads/admincmd.10770");
					t.printStackTrace();
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
		}

	}

	private class SyncTask implements Runnable {
		private ACCommands cmd;
		private CommandSender sender;
		private String[] args;

		/**
		 * 
		 */
		public SyncTask(ACCommands cmd, CommandSender sender, String[] args) {
			this.cmd = cmd;
			this.sender = sender;
			this.args = args;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			cmd.execute(sender, args);

		}

	}
}
