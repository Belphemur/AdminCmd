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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.ACCommandContainer;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Manager.Exceptions.CommandAlreadyExist;
import be.Balor.Manager.Exceptions.CommandDisabled;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Configuration.ExConfigurationSection;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Files.FileManager;
import be.Balor.Tools.Files.PluginCommandUtil;
import be.Balor.Tools.Help.HelpLister;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.AbstractAdminCmdPlugin;
import be.Balor.bukkit.AdminCmd.AdminCmd;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class CommandManager implements CommandExecutor {
	private class NormalCommand implements Runnable {
		protected final ACCommandContainer acc;

		public NormalCommand(ACCommandContainer acc) {
			this.acc = acc;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				acc.processArguments();
				acc.execute();
			} catch (final ConcurrentModificationException cme) {
				ACPluginManager.getScheduler().scheduleSyncDelayedTask(corePlugin,
						new SyncCommand(acc));
			} catch (final WorldNotLoaded e) {
				ACLogger.severe("World not Loaded", e);
				Utils.broadcastMessage("[AdminCmd] World " + e.getMessage() + " is not loaded.");
			} catch (final Throwable t) {
				ACLogger.severe(acc.debug(), t);
				Utils.broadcastMessage("[AdminCmd] " + acc.debug());
			}
		}

	}

	private class SyncCommand extends NormalCommand {

		public SyncCommand(ACCommandContainer acc) {
			super(acc);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				acc.processArguments();
				acc.execute();
			} catch (final WorldNotLoaded e) {
				ACLogger.severe("World not Loaded", e);
				Utils.broadcastMessage("[AdminCmd] World " + e.getMessage() + " is not loaded.");
			} catch (final Throwable t) {
				ACLogger.severe(acc.debug(), t);
				Utils.broadcastMessage("[AdminCmd] " + acc.debug());
			}
		}

	}

	/**
	 * @return the instance
	 */
	public static CommandManager getInstance() {
		return instance;
	}

	private final HashMap<Command, CoreCommand> registeredCommands = new HashMap<Command, CoreCommand>();
	private final int MAX_THREADS = 8;
	private static CommandManager instance = new CommandManager();

	public static CommandManager createInstance() {
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

	private AbstractAdminCmdPlugin corePlugin;
	private boolean threadsStarted = false;
	private List<String> disabledCommands;

	private List<String> prioritizedCommands;

	private final HashMap<String, List<String>> aliasCommands = new HashMap<String, List<String>>();

	private final HashMap<String, CoreCommand> commandReplacer = new HashMap<String, CoreCommand>();

	private final ThreadPoolExecutor threads = new ThreadPoolExecutor(2, MAX_THREADS, 30,
			TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	private final HashMap<AbstractAdminCmdPlugin, HashMap<String, Command>> pluginCommands = new HashMap<AbstractAdminCmdPlugin, HashMap<String, Command>>();

	/**
	 * 
	 */
	private CommandManager() {

	}

	/**
	 * Check if some alias have been disabled for the registered commands
	 */
	public void checkAlias(AbstractAdminCmdPlugin plugin) {
		if (ConfigEnum.VERBOSE.getBoolean()) {
			final HashMap<String, Command> commands = pluginCommands.get(plugin);
			if (commands != null)
				for (final String cmdName : commands.keySet()) {
					final Command cmd = commands.get(cmdName);
					if (corePlugin.getCommand(cmd.getName()) != null) {
						final List<String> aliasesList = new ArrayList<String>(cmd.getAliases());
						aliasesList.removeAll(corePlugin.getCommand(cmd.getName()).getAliases());
						aliasesList.removeAll(prioritizedCommands);
						String aliases = "";
						for (final String alias : aliasesList)
							aliases += alias + ", ";
						if (!aliases.isEmpty() && ConfigEnum.VERBOSE.getBoolean())
							Logger.getLogger("Minecraft").info(
									"[" + corePlugin.getDescription().getName()
											+ "] Disabled Alias(es) for " + cmd.getName() + " : "
											+ aliases);
					}
				}
		}
	}

	/**
	 * Check the command if it have alias, prioritized or disabled.
	 * 
	 * @param command
	 * @throws CommandDisabled
	 */
	private void checkCommand(final CoreCommand command) throws CommandDisabled {
		final HashMap<String, Command> commands = pluginCommands.get(command.getPlugin());
		if (commands != null)
			for (final String alias : commands.get(command.getCmdName()).getAliases()) {
				if (disabledCommands.contains(alias))
					throw new CommandDisabled("Command " + command.getCmdName()
							+ " selected to be disabled in the configuration file.");
				if (prioritizedCommands.contains(alias))
					commandReplacer.put(alias, command);
				if (aliasCommands.containsKey(alias)) {
					for (final String cmd : aliasCommands.get(alias))
						commandReplacer.put(cmd, command);
				}
			}
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
			/*if (cmd.getCmdName().equals("bal_replace") || cmd.getCmdName().equals("bal_extinguish"))
				corePlugin.getServer().getScheduler()
						.scheduleSyncDelayedTask(corePlugin, new SyncCommand(container));
			else*/
				threads.execute(new NormalCommand(container));
			if (!cmd.getCmdName().equals("bal_repeat")) {
				if (Utils.isPlayer(sender, false))
					ACPlayer.getPlayer(((Player) sender)).setLastCmd(container);
				else
					ACPlayer.getPlayer("serverConsole").setLastCmd(container);
			}
			return true;
		} catch (final Throwable t) {
			ACLogger.severe(container.debug(), t);
			Utils.broadcastMessage("[AdminCmd] " + container.debug());
			return false;
		}
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
		final Class<?> clazz = object.getClass();
		final Field objectField = clazz.getDeclaredField(field);
		objectField.setAccessible(true);
		final Object result = objectField.get(object);
		objectField.setAccessible(false);
		return result;
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

	public boolean processCommandString(CommandSender sender, String command) {
		final String[] split = command.split("\\s+");
		if (split.length == 0)
			return false;
		final String cmdName = split[0].substring(1).toLowerCase();
		final CoreCommand cmd = commandReplacer.get(cmdName);
		if (cmd != null) {
			if (ConfigEnum.VERBOSE.getBoolean())
				ACLogger.info("Command " + cmdName + " intercepted.");
			return executeCommand(sender, cmd, Utils.Arrays_copyOfRange(split, 1, split.length));
		}
		return false;
	}

	/**
	 * Register command from plugin
	 * 
	 * @param plugin
	 */
	public void registerACPlugin(AbstractAdminCmdPlugin plugin) {
		final HashMap<String, Command> commands = new HashMap<String, Command>();
		for (final Command cmd : PluginCommandUtil.parse(plugin))
			commands.put(cmd.getName(), cmd);

		pluginCommands.put(plugin, new HashMap<String, Command>(commands));
	}

	/**
	 * Register command
	 * 
	 * @param clazz
	 */
	public boolean registerCommand(Class<? extends CoreCommand> clazz) {
		CoreCommand command = null;
		try {
			DebugLog.INSTANCE.info("Begin registering Command " + clazz.getName());
			command = clazz.newInstance();
			command.initializeCommand();
			checkCommand(command);
			command.registerBukkitPerm();
			command.getPluginCommand().setExecutor(instance);
			registeredCommands.put(command.getPluginCommand(), command);
		} catch (final InstantiationException e) {
			e.printStackTrace();
			return false;
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
			return false;
		} catch (final CommandDisabled e) {
			unRegisterBukkitCommand(command.getPluginCommand());
			HelpLister.getInstance().removeHelpEntry(command.getPlugin().getPluginName(),
					command.getCmdName());
			if (ConfigEnum.VERBOSE.getBoolean())
				ACLogger.info(e.getMessage());
			return false;
		} catch (final CommandAlreadyExist e) {
			boolean disableCommand = true;
			final HashMap<String, Command> commands = pluginCommands.get(command.getPlugin());
			if (commands != null)
				for (final String alias : commands.get(command.getCmdName()).getAliases()) {
					if (prioritizedCommands.contains(alias)) {
						commandReplacer.put(alias, command);
						disableCommand = false;
					}
					if (aliasCommands.containsKey(alias)) {
						for (final String cmd : aliasCommands.get(alias))
							commandReplacer.put(cmd, command);
						disableCommand = false;
					}
				}
			if (disableCommand) {
				unRegisterBukkitCommand(command.getPluginCommand());
				HelpLister.getInstance().removeHelpEntry(command.getPlugin().getPluginName(),
						command.getCmdName());
				if (ConfigEnum.VERBOSE.getBoolean())
					ACLogger.info(e.getMessage());
				DebugLog.INSTANCE.info("Command Disabled");
				return false;
			} else {
				command.registerBukkitPerm();
				command.getPluginCommand().setExecutor(this);
				registeredCommands.put(command.getPluginCommand(), command);
				DebugLog.INSTANCE.info("Command Prioritized but already exists");
				return true;
			}
		} catch (final CommandException e) {
			if (ConfigEnum.VERBOSE.getBoolean())
				Logger.getLogger("Minecraft").info("[AdminCmd] " + e.getMessage());
			return false;
		}
		DebugLog.INSTANCE.info("End registering Command " + clazz.getName());
		return true;
	}

	/**
	 * @param plugin
	 *            the plugin to set
	 */
	public void setCorePlugin(AdminCmd plugin) {
		this.corePlugin = plugin;
		final ExtendedConfiguration cmds = FileManager.getInstance().getYml("commands");
		disabledCommands = cmds.getStringList("disabledCommands", new LinkedList<String>());
		prioritizedCommands = cmds.getStringList("prioritizedCommands", new LinkedList<String>());
		final ExConfigurationSection alias = cmds.getConfigurationSection("alias");
		for (final String cmd : alias.getKeys(false))
			aliasCommands.put(cmd,
					new ArrayList<String>(alias.getStringList(cmd, new ArrayList<String>())));
		startThreads();
	}

	public void startThreads() {
		if (!threadsStarted) {
			threads.purge();
		}
		threadsStarted = true;
	}

	public void stopAllExecutorThreads() {
		threads.shutdown();
		threadsStarted = false;
	}

	/**
	 * Unregister a command from bukkit.
	 * 
	 * @param cmd
	 */
	private void unRegisterBukkitCommand(PluginCommand cmd) {
		try {
			final Object result = getPrivateField(corePlugin.getServer().getPluginManager(),
					"commandMap");
			final SimpleCommandMap commandMap = (SimpleCommandMap) result;
			final Object map = getPrivateField(commandMap, "knownCommands");
			@SuppressWarnings("unchecked")
			final HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
			knownCommands.remove(cmd.getName());
			for (final String alias : cmd.getAliases())
				knownCommands.remove(alias);
		} catch (final SecurityException e) {
			ACLogger.severe("Unregistering command problem", e);
		} catch (final IllegalArgumentException e) {
			ACLogger.severe("Unregistering command problem", e);
		} catch (final NoSuchFieldException e) {
			ACLogger.severe("Unregistering command problem", e);
		} catch (final IllegalAccessException e) {
			ACLogger.severe("Unregistering command problem", e);
		}catch (final Exception e) {
			ACLogger.severe("Unregistering command problem", e);
		}
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
			final CoreCommand command = clazz.newInstance();
			if (plugin.equals(command.getPlugin())) {
				try {
					command.initializeCommand();
					final PluginCommand pCmd = command.getPluginCommand();
					registeredCommands.remove(pCmd);
					commandReplacer.remove(command.getCmdName());
					unRegisterBukkitCommand(pCmd);
				} catch (final Exception e) {
					return false;
				}
				return true;

			}
		} catch (final InstantiationException e) {

		} catch (final IllegalAccessException e) {

		}
		return false;

	}

}
