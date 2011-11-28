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
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.ACCommandContainer;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Manager.Exceptions.CommandAlreadyExist;
import be.Balor.Manager.Exceptions.CommandDisabled;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Files.FileManager;
import be.Balor.Tools.Files.PluginCommandUtil;
import be.Balor.Tools.Help.HelpLister;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.AbstractAdminCmdPlugin;
import be.Balor.bukkit.AdminCmd.AdminCmd;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class CommandManager implements CommandExecutor {
	private HashMap<Command, CoreCommand> registeredCommands = new HashMap<Command, CoreCommand>();
	private final int MAX_REQUEST = 5;
	private final int MAX_THREADS = 5;
	private ArrayList<ExecutorThread> threads = new ArrayList<CommandManager.ExecutorThread>(
			MAX_THREADS);
	private int cmdCount = 0;
	private static CommandManager instance = new CommandManager();
	private AbstractAdminCmdPlugin corePlugin;
	private boolean threadsStarted = false;
	private List<String> disabledCommands;
	private List<String> prioritizedCommands;
	private HashMap<String, List<String>> aliasCommands = new HashMap<String, List<String>>();
	private HashMap<String, CoreCommand> commandReplacer = new HashMap<String, CoreCommand>();
	private HashMap<AbstractAdminCmdPlugin, HashMap<String, Command>> pluginCommands = new HashMap<AbstractAdminCmdPlugin, HashMap<String, Command>>();
	private int execCount = 0;

	/**
	 * 
	 */
	private CommandManager() {

	}

	/**
	 * @return the instance
	 */
	public static CommandManager getInstance() {
		return instance;
	}

	/**
	 * Destroy the instance
	 */
	public static void killInstance() {
		instance = null;
	}

	public static CommandManager createInstance() {
		if (instance == null)
			instance = new CommandManager();
		return instance;
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
			Object result = getPrivateField(corePlugin.getServer().getPluginManager(), "commandMap");
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
	 * @param plugin
	 *            the plugin to set
	 */
	@SuppressWarnings("unchecked")
	public void setCorePlugin(AdminCmd plugin) {
		this.corePlugin = plugin;
		ExtendedConfiguration cmds = FileManager.getInstance().getYml("commands");
		disabledCommands = cmds.getList("disabledCommands", new LinkedList<String>());
		prioritizedCommands = cmds.getList("prioritizedCommands", new LinkedList<String>());
		ConfigurationSection alias = cmds.getConfigurationSection("alias");
		for (String cmd : alias.getKeys(false))
			aliasCommands.put(cmd,
					new ArrayList<String>(alias.getList(cmd, new ArrayList<String>())));
		startThreads();
	}

	/**
	 * Register command from plugin
	 * 
	 * @param plugin
	 */
	public void registerACPlugin(AbstractAdminCmdPlugin plugin) {
		HashMap<String, Command> commands = new HashMap<String, Command>();
		for (Command cmd : PluginCommandUtil.parse(plugin))
			commands.put(cmd.getName(), cmd);

		pluginCommands.put(plugin, new HashMap<String, Command>(commands));
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
	public void registerCommand(Class<? extends CoreCommand> clazz) {
		CoreCommand command = null;
		try {
			Utils.debug("Begin registering Command " + clazz.getName());
			command = (CoreCommand) clazz.newInstance();
			command.initializeCommand();
			checkCommand(command);
			command.registerBukkitPerm();
			command.getPluginCommand().setExecutor(instance);
			registeredCommands.put(command.getPluginCommand(), command);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (CommandDisabled e) {
			unRegisterBukkitCommand(command.getPluginCommand());
			HelpLister.getInstance().removeHelpEntry(command.getPlugin().getName(),
					command.getCmdName());
			if (ACHelper.getInstance().getConfBoolean("verboseLog"))
				ACLogger.info(e.getMessage());
		} catch (CommandAlreadyExist e) {
			boolean disableCommand = true;
			HashMap<String, Command> commands = pluginCommands.get(command.getPlugin());
			if (commands != null)
				for (String alias : commands.get(command.getCmdName()).getAliases()) {
					if (prioritizedCommands.contains(alias)) {
						commandReplacer.put(alias, command);
						disableCommand = false;
					}
					if (aliasCommands.containsKey(alias)) {
						for (String cmd : aliasCommands.get(alias))
							commandReplacer.put(cmd, command);
						disableCommand = false;
					}
				}
			if (disableCommand) {
				unRegisterBukkitCommand(command.getPluginCommand());
				HelpLister.getInstance().removeHelpEntry(command.getPlugin().getName(),
						command.getCmdName());
				if (ACHelper.getInstance().getConfBoolean("verboseLog"))
					ACLogger.info(e.getMessage());
				Utils.debug("Command Disabled");
			} else {
				command.registerBukkitPerm();
				command.getPluginCommand().setExecutor(this);
				registeredCommands.put(command.getPluginCommand(), command);
				Utils.debug("Command Prioritized but already exists");
			}
		} catch (CommandException e) {
			if (ACHelper.getInstance().getConfBoolean("verboseLog"))
				Logger.getLogger("Minecraft").info("[AdminCmd] " + e.getMessage());
		}
		Utils.debug("End registering Command " + clazz.getName());
	}

	/**
	 * UnRegister command
	 * 
	 * @param clazz
	 *            command to unregister
	 * @param plugin
	 *            plugin that want the command to be unregister. It has to be
	 *            the same that belong to the command.
	 */
	public boolean unRegisterCommand(Class<? extends CoreCommand> clazz,
			AbstractAdminCmdPlugin plugin) {
		try {
			CoreCommand command = (CoreCommand) clazz.newInstance();
			if (plugin.equals(command.getPlugin())) {
				try {
					command.initializeCommand();
					PluginCommand pCmd = command.getPluginCommand();
					registeredCommands.remove(pCmd);
					commandReplacer.remove(command.getCmdName());
					unRegisterBukkitCommand(pCmd);
				} catch (Exception e) {
					return false;
				}
				return true;

			}
		} catch (InstantiationException e) {

		} catch (IllegalAccessException e) {

		}
		return false;

	}

	/**
	 * Check the command if it have alias, prioritized or disabled.
	 * 
	 * @param command
	 * @throws CommandDisabled
	 */
	private void checkCommand(final CoreCommand command) throws CommandDisabled {
		HashMap<String, Command> commands = pluginCommands.get(command.getPlugin());
		if (commands != null)
			for (String alias : commands.get(command.getCmdName()).getAliases()) {
				if (disabledCommands.contains(alias))
					throw new CommandDisabled("Command " + command.getCmdName()
							+ " selected to be disabled in the configuration file.");
				if (prioritizedCommands.contains(alias))
					commandReplacer.put(alias, command);
				if (aliasCommands.containsKey(alias)) {
					for (String cmd : aliasCommands.get(alias))
						commandReplacer.put(cmd, command);
				}
			}
	}

	/**
	 * Check if some alias have been disabled for the registered commands
	 */
	public void checkAlias(AbstractAdminCmdPlugin plugin) {
		if (ACHelper.getInstance().getConfBoolean("verboseLog")) {
			HashMap<String, Command> commands = pluginCommands.get(plugin);
			if (commands != null)
				for (String cmdName : commands.keySet()) {
					Command cmd = commands.get(cmdName);
					if (corePlugin.getCommand(cmd.getName()) != null) {
						List<String> aliasesList = new ArrayList<String>(cmd.getAliases());
						aliasesList.removeAll(corePlugin.getCommand(cmd.getName()).getAliases());
						aliasesList.removeAll(prioritizedCommands);
						String aliases = "";
						for (String alias : aliasesList)
							aliases += alias + ", ";
						if (!aliases.isEmpty()
								&& ACHelper.getInstance().getConfBoolean("verboseLog"))
							Logger.getLogger("Minecraft").info(
									"[" + corePlugin.getDescription().getName()
											+ "] Disabled Alias(es) for " + cmd.getName() + " : "
											+ aliases);
					}
				}
		}
	}

	public void stopAllExecutorThreads() {
		for (ExecutorThread t : threads) {
			t.stopThread();
		}
		threadsStarted = false;
	}

	public boolean processCommandString(CommandSender sender, String command) {
		String[] split = command.split("\\s+");
		if (split.length == 0)
			return false;
		String cmdName = split[0].substring(1).toLowerCase();
		CoreCommand cmd = commandReplacer.get(cmdName);
		if (cmd != null) {
			if (ACHelper.getInstance().getConfBoolean("verboseLog"))
				ACLogger.info("Command " + cmdName + " intercepted.");
			return executeCommand(sender, cmd, Utils.Arrays_copyOfRange(split, 1, split.length));
		}
		return false;
	}

	/**
	 * Used to execute ACCommands
	 * 
	 * @param sender
	 * @param cmd
	 * @param args
	 * @return
	 */
	private boolean executeCommand(CommandSender sender, CoreCommand cmd, String[] args) {
		ACCommandContainer container = null;
		try {
			if (!cmd.permissionCheck(sender))
				return true;
			if (!cmd.argsCheck(args))
				return false;
			container = new ACCommandContainer(sender, cmd, args);
			if (cmd.getCmdName().equals("bal_replace") || cmd.getCmdName().equals("bal_undo")
					|| cmd.getCmdName().equals("bal_extinguish"))
				corePlugin.getServer().getScheduler()
						.scheduleSyncDelayedTask(corePlugin, new SyncCommand(container));
			else {
				threads.get(cmdCount).addCommand(container);
				cmdCount++;
				if (cmdCount == MAX_THREADS)
					cmdCount = 0;
			}
			if (!cmd.getCmdName().equals("bal_repeat")) {
				if (Utils.isPlayer(sender, false))
					ACPlayer.getPlayer(((Player) sender).getName()).setLastCmd(container);
				else
					ACPlayer.getPlayer("serverConsole").setLastCmd(container);
			}
			return true;
		} catch (Throwable t) {
			ACLogger.severe(container.debug(), t);
			Utils.broadcastMessage("[AdminCmd] " + container.debug());
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
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		CoreCommand cmd = null;
		if ((cmd = registeredCommands.get(command)) != null)
			return executeCommand(sender, cmd, args);
		else
			return false;
	}

	/**
	 * @author Balor (aka Antoine Aflalo)
	 * 
	 */
	private class ExecutorThread extends Thread {
		private final LinkedBlockingQueue<ACCommandContainer> commands;
		private boolean stop = false;
		private Semaphore sema;
		private Object threadSync = new Object();

		/**
		 * 
		 */
		public ExecutorThread() {
			super("Executor " + execCount++);
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
			ACCommandContainer current = null;
			while (true) {
				try {
					sema.acquire();
					synchronized (threadSync) {
						if (this.stop)
							break;
					}
					current = commands.poll();
					current.execute();
				} catch (InterruptedException e) {
				} catch (ConcurrentModificationException cme) {
					ACPluginManager.getScheduler().scheduleSyncDelayedTask(corePlugin,
							new SyncCommand(current));
				} catch (WorldNotLoaded e) {
					ACLogger.severe("World not Loaded", e);
					Utils.broadcastMessage("[AdminCmd] World " + e.getMessage() + " is not loaded.");
				} catch (Throwable t) {
					ACLogger.severe(current.debug(), t);
					Utils.broadcastMessage("[AdminCmd] " + current.debug());
				}

			}
		}

		public synchronized void stopThread() {
			stop = true;
			sema.release();
		}

		public synchronized void addCommand(final ACCommandContainer cmd)
				throws InterruptedException {
			cmd.processArguments();
			commands.put(cmd);
			sema.release();
		}

	}

	private class SyncCommand implements Runnable {
		private ACCommandContainer acc = null;

		public SyncCommand(ACCommandContainer acc) {
			this.acc = acc;
			acc.processArguments();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				acc.execute();
			} catch (WorldNotLoaded e) {
				ACLogger.severe("World not Loaded", e);
				Utils.broadcastMessage("[AdminCmd] World " + e.getMessage() + " is not loaded.");
			} catch (Throwable t) {
				ACLogger.severe(acc.debug(), t);
				Utils.broadcastMessage("[AdminCmd] " + acc.debug());
			}
		}

	}
}
