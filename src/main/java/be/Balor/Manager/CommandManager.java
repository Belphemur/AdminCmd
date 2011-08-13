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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import be.Balor.Manager.Exceptions.CommandAlreadyExist;
import be.Balor.Manager.Exceptions.CommandDisabled;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.AdminCmd;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class CommandManager implements CommandExecutor {
	private HashMap<Command, ACCommand> commands = new HashMap<Command, ACCommand>();
	private final int MAX_THREADS = 5;
	private ArrayList<ExecutorThread> threads = new ArrayList<CommandManager.ExecutorThread>(
			MAX_THREADS);
	private int cmdCount = 0;
	private static CommandManager instance = null;
	private JavaPlugin plugin;
	private boolean threadsStarted = false;
	private List<String> disabledCommands;
	private List<String> prioritizedCommands;
	private HashMap<String, ACCommand> commandReplacer = new HashMap<String, ACCommand>();
	private HashMap<String, Command> pluginCommands = new HashMap<String, Command>();

	/**
	 * @return the instance
	 */
	public static CommandManager getInstance() {
		if (instance == null)
			instance = new CommandManager();
		return instance;
	}

	/**
	 * Destroy the instance
	 */
	public static void killInstance() {
		instance = null;
	}

	/**
	 * @param disabledCommands
	 *            the disabledCommands to set
	 */
	public void setDisabledCommands(List<String> disabledCommands) {
		this.disabledCommands = disabledCommands;
	}

	/**
	 * @param prioritizedCommands
	 *            the prioritizedCommands to set
	 */
	public void setPrioritizedCommands(List<String> prioritizedCommands) {
		this.prioritizedCommands = prioritizedCommands;
	}

	/**
	 * Getting the private field of a another class;
	 * 
	 * @param object
	 * @param field
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private Object getPrivateField(Object object, String field) throws SecurityException,
			NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Class<?> clazz = object.getClass();
		Field objectField = clazz.getDeclaredField(field);
		objectField.setAccessible(true);
		Object result = objectField.get(object);
		objectField.setAccessible(false);
		return result;
	}

	/**
	 * Unregister a command from bukkit.
	 * 
	 * @param cmd
	 */
	private void unRegisterBukkitCommand(PluginCommand cmd) {
		try {
			Object result = getPrivateField(plugin.getServer().getPluginManager(), "commandMap");
			SimpleCommandMap commandMap = (SimpleCommandMap) result;
			Object map = getPrivateField(commandMap, "knownCommands");
			@SuppressWarnings("unchecked")
			HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
			knownCommands.remove(cmd.getName());
			for (String alias : cmd.getAliases())
				knownCommands.remove(alias);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the ACCommand having the given alias.
	 * 
	 * @param alias
	 * @return
	 */
	public ACCommand getCommand(String alias) {
		return commandReplacer.get(alias);
	}

	/**
	 * @param plugin
	 *            the plugin to set
	 */
	public void setPlugin(JavaPlugin plugin) {
		this.plugin = plugin;
		for (Command cmd : PluginCommandYamlParser.parse(plugin))
			pluginCommands.put(cmd.getName(), cmd);
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
		ACCommand command = null;
		try {
			command = (ACCommand) clazz.newInstance();
			command.initializeCommand(plugin);
			for (String alias : pluginCommands.get(command.getCmdName()).getAliases())
				if (disabledCommands.contains(alias))
					throw new CommandDisabled("Command " + command.getCmdName()
							+ " selected to be disabled in the configuration file.");
				else if (prioritizedCommands.contains(alias))
					commandReplacer.put(alias, command);

			command.registerBukkitPerm();
			command.getPluginCommand().setExecutor(this);
			commands.put(command.getPluginCommand(), command);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (CommandDisabled e) {			
				unRegisterBukkitCommand(command.getPluginCommand());
			Logger.getLogger("Minecraft").info("[AdminCmd] " + e.getMessage());
		} catch (CommandAlreadyExist e) {
			for (String alias : pluginCommands.get(command.getCmdName()).getAliases())
				if (prioritizedCommands.contains(alias)) {
					commandReplacer.put(alias, command);
					command.registerBukkitPerm();
					command.getPluginCommand().setExecutor(this);
					commands.put(command.getPluginCommand(), command);
					return;
				}
			unRegisterBukkitCommand(command.getPluginCommand());
			Logger.getLogger("Minecraft").info("[AdminCmd] " + e.getMessage());
		} catch (CommandException e) {
			Logger.getLogger("Minecraft").info("[AdminCmd] " + e.getMessage());
		}
	}

	public void checkAlias() {
		for (String cmdName : pluginCommands.keySet()) {
			Command cmd = pluginCommands.get(cmdName);
			if (plugin.getCommand(cmd.getName()) != null) {
				cmd.getAliases().removeAll(plugin.getCommand(cmd.getName()).getAliases());
				cmd.getAliases().removeAll(prioritizedCommands);
				String aliases = "";
				for (String alias : cmd.getAliases())
					aliases += alias + ", ";
				if (!aliases.isEmpty())
					Logger.getLogger("Minecraft").info(
							"[" + plugin.getDescription().getName() + "] Disabled Alias(es) for "
									+ cmd.getName() + " : " + aliases);
			}
		}
	}

	public void stopAllExecutorThreads() {
		for (ExecutorThread t : threads) {
			t.stopThread();
		}
		threadsStarted = false;
	}

	/**
	 * Used to execute ACCommands
	 * 
	 * @param sender
	 * @param cmd
	 * @param args
	 * @return
	 */
	public boolean executeCommand(CommandSender sender, ACCommand cmd, String[] args) {
		try {
			if (cmd.permissionCheck(sender) && cmd.argsCheck(args)) {
				if (cmd.getCmdName().equals("bal_replace") || cmd.getCmdName().equals("bal_undo")
						|| cmd.getCmdName().equals("bal_extinguish"))
					plugin.getServer().getScheduler()
							.scheduleAsyncDelayedTask(plugin, new CommandTask(cmd, sender, args));
				else {
					threads.get(cmdCount).addCommand(new ACCommandContainer(sender, cmd, args));
					cmdCount++;
					if (cmdCount == MAX_THREADS)
						cmdCount = 0;
				}
				if (!cmd.getCmdName().equals("bal_repeat")) {
					if (Utils.isPlayer(sender, false))
						ACHelper.getInstance().addValue(Type.REPEAT_CMD, (Player) sender,
								new ACCommandContainer(sender, cmd, args));
					else
						ACHelper.getInstance().addValue(Type.REPEAT_CMD, "serverConsole",
								new ACCommandContainer(sender, cmd, args));
				}

				return true;
			} else
				return false;
		} catch (Throwable t) {
			Logger.getLogger("Minecraft")
					.severe("[AdminCmd] The command "
							+ cmd.getCmdName()
							+ " throw an Exception please report the log to this thread : http://forums.bukkit.org/threads/admincmd.10770");
			sender.sendMessage("[AdminCmd]"
					+ ChatColor.RED
					+ " The command "
					+ cmd.getCmdName()
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
	 * {@inheritDoc}
	 */
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		ACCommand cmd = null;
		if ((cmd = commands.get(command)) != null)
			return executeCommand(sender, cmd, args);
		else
			return false;
	}

	/**
	 * @author Balor (aka Antoine Aflalo)
	 * 
	 */
	private class ExecutorThread extends Thread {
		protected LinkedBlockingQueue<ACCommandContainer> commands;
		protected final int MAX_REQUEST = 5;
		boolean stop = false;
		Semaphore sema;
		Object threadSync = new Object();

		/**
		 * 
		 */
		public ExecutorThread() {
			commands = new LinkedBlockingQueue<ACCommandContainer>(MAX_REQUEST);
			sema = new Semaphore(0);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			String cmdname = null;
			while (true) {
				try {
					sema.acquire();
					synchronized (threadSync) {
						if (this.stop)
							break;
					}
					cmdname = commands.peek().getCmdName();
					commands.poll().execute();
				} catch (InterruptedException e) {
				} catch (Throwable t) {
					Logger.getLogger("Minecraft")
							.severe("[AdminCmd] The command "
									+ cmdname
									+ " throw an Exception please report the log to this thread : http://forums.bukkit.org/threads/admincmd.10770");
					AdminCmd.getBukkitServer()
							.broadcastMessage(
									"[AdminCmd] The command "
											+ cmdname
											+ " throw an Exception please report the log to this thread : http://forums.bukkit.org/threads/admincmd.10770");
					t.printStackTrace();
				}

			}
		}

		public synchronized void stopThread() {
			stop = true;
			sema.release();
		}

		public synchronized void addCommand(final ACCommandContainer cmd)
				throws InterruptedException {
			commands.put(cmd);
			sema.release();
		}

	}

	private class CommandTask implements Runnable {
		private ACCommand cmd;
		private CommandSender sender;
		private String[] args;

		/**
		 * 
		 */
		public CommandTask(ACCommand cmd, CommandSender sender, String[] args) {
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
