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
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.mcstats.Metrics.Graph;

import be.Balor.Manager.Commands.ACCommandContainer;
import be.Balor.Manager.Commands.CommandAlias;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.CommandAlreadyExist;
import be.Balor.Manager.Exceptions.CommandDisabled;
import be.Balor.Manager.Exceptions.CommandException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Utils;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.Tools.Compatibility.Reflect.FieldUtils;
import be.Balor.Tools.Configuration.ExConfigurationSection;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Files.FileManager;
import be.Balor.Tools.Files.PluginCommandUtil;
import be.Balor.Tools.Help.HelpLister;
import be.Balor.Tools.Metrics.ClassPlotter;
import be.Balor.Tools.Metrics.IncrementalPlotter;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.AbstractAdminCmdPlugin;
import be.Balor.bukkit.AdminCmd.AdminCmd;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class CommandManager implements CommandExecutor {
	private class NormalCommand implements Runnable {
		protected final ACCommandContainer acc;

		public NormalCommand(final ACCommandContainer acc) {
			this.acc = acc;
		}

		protected void processCmd() throws PlayerNotFound,
				ActionNotPermitedException {
			DebugLog.beginInfo(acc.getCommandName());
			try {
				acc.processArguments();
				DebugLog.addInfo("[Args] " + acc.getArgumentsString());
				DebugLog.addInfo("[SENDER] " + acc.getSender());
				acc.execute();
			} catch (final RuntimeException re) {
				DebugLog.addException("Process Command Exception", re);
				throw re;
			} finally {
				DebugLog.endInfo();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				processCmd();
				plotters.get(acc.getCommandClass()).increment();
			} catch (final ConcurrentModificationException cme) {
				ACPluginManager.getScheduler().scheduleSyncDelayedTask(
						corePlugin, new SyncCommand(acc));
			} catch (final WorldNotLoaded e) {
				String message = e.getMessage();
				if (message == null || message == "") {
					message = "This world is not loaded!";
				}
				final HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("message", message);
				LocaleHelper.WORLD_NOT_LOADED.sendLocale(acc.getSender(),
						replace);
			} catch (final PlayerNotFound e) {
				e.getSender().sendMessage(e.getMessage());
			} catch (final ActionNotPermitedException e) {
				e.sendMessage();
			} catch (final NumberFormatException e) {
				LocaleManager.sI18n(acc.getSender(), "NaN", "number",
						e.getLocalizedMessage());
			} catch (final Throwable t) {
				ACLogger.severe(acc.debug(), t);
				Users.broadcastMessage("[AdminCmd] " + acc.debug());
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "NormalCommand [acc=" + acc + "]";
		}

	}

	private class SyncCommand extends NormalCommand {

		public SyncCommand(final ACCommandContainer acc) {
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
				processCmd();
			} catch (final WorldNotLoaded e) {
				ACLogger.severe("World not Loaded", e);
				Users.broadcastMessage("[AdminCmd] World " + e.getMessage()
						+ " is not loaded.");
			} catch (final Throwable t) {
				ACLogger.severe(acc.debug(), t);
				Users.broadcastMessage("[AdminCmd] " + acc.debug());
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "SyncCommand [acc=" + acc + "]";
		}

	}

	/**
	 * @return the instance
	 */
	public static CommandManager getInstance() {
		return instance;
	}

	private final HashMap<Command, CoreCommand> registeredCommands = new HashMap<Command, CoreCommand>();
	private final int MAX_THREADS = 10;
	private static CommandManager instance = new CommandManager();
	private Graph graph;

	public static CommandManager createInstance() {
		if (instance == null) {
			instance = new CommandManager();
		}
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
	private final Map<String, CommandAlias> commandsAliasReplacer = new HashMap<String, CommandAlias>();
	private final Map<String, Set<CommandAlias>> commandsAlias = new HashMap<String, Set<CommandAlias>>();
	private final ThreadPoolExecutor threads = new ThreadPoolExecutor(2,
			MAX_THREADS, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(
					true), new ThreadPoolExecutor.CallerRunsPolicy());
	private final Map<AbstractAdminCmdPlugin, Map<String, Command>> pluginCommands = new HashMap<AbstractAdminCmdPlugin, Map<String, Command>>();
	private final Map<Class<? extends CoreCommand>, IncrementalPlotter> plotters = new HashMap<Class<? extends CoreCommand>, IncrementalPlotter>();
	private final Set<String> commandsOnJoin = new HashSet<String>();

	/**
	 *
	 */
	private CommandManager() {

	}

	/**
	 * Check if some alias have been disabled for the registered commands
	 */
	public void checkAlias(final AbstractAdminCmdPlugin plugin) {
		if (ConfigEnum.VERBOSE.getBoolean()) {
			final Map<String, Command> commands = pluginCommands.get(plugin);
			if (commands != null) {
				for (final String cmdName : commands.keySet()) {
					final Command cmd = commands.get(cmdName);
					if (corePlugin.getCommand(cmd.getName()) != null) {
						final List<String> aliasesList = new ArrayList<String>(
								cmd.getAliases());
						aliasesList.removeAll(corePlugin.getCommand(
								cmd.getName()).getAliases());
						aliasesList.removeAll(prioritizedCommands);
						String aliases = "";
						for (final String alias : aliasesList) {
							aliases += alias + ", ";
						}
						if (!aliases.isEmpty()
								&& ConfigEnum.VERBOSE.getBoolean()) {
							Logger.getLogger("Minecraft").info(
									"[" + corePlugin.getDescription().getName()
											+ "] Disabled Alias(es) for "
											+ cmd.getName() + " : " + aliases);
						}
					}
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
		final Map<String, Command> commands = pluginCommands.get(command
				.getPlugin());
		if (commands != null) {
			for (final String alias : commands.get(command.getCmdName())
					.getAliases()) {
				if (disabledCommands.contains(alias)) {
					throw new CommandDisabled(
							"Command "
									+ command.getCmdName()
									+ " selected to be disabled in the configuration file.",
							command);
				}
				if (prioritizedCommands.contains(alias)) {
					final CommandAlias cmd = new CommandAlias(
							command.getCmdName(), alias, "");
					cmd.setCmd(command);
					commandsAliasReplacer.put(alias, cmd);
				}
				final Set<CommandAlias> commandAliases = commandsAlias
						.get(alias);
				if (commandAliases != null) {
					for (final CommandAlias commandAlias : commandAliases) {
						commandAlias.setCmd(command);
					}
				}
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
	private boolean executeCommand(final CommandSender sender,
			final CoreCommand cmd, final String[] args) {
		ACCommandContainer container = null;
		try {
			if (!cmd.permissionCheck(sender)) {
				return true;
			}
			if (!cmd.argsCheck(args)) {
				if (!HelpLister.getInstance().displayExactCommandHelp(sender,
						"AdminCmd", cmd.getCmdName())) {
					return false;
				}
				return true;
			}
			container = new ACCommandContainer(sender, cmd, args);
			/*
			 * if (cmd.getCmdName().equals("bal_replace") ||
			 * cmd.getCmdName().equals("bal_extinguish"))
			 * corePlugin.getServer().getScheduler()
			 * .scheduleSyncDelayedTask(corePlugin, new SyncCommand(container));
			 * else
			 */
			threads.execute(new NormalCommand(container));
			if (!cmd.getCmdName().equals("bal_repeat")) {
				if (Users.isPlayer(sender, false)) {
					ACPlayer.getPlayer(((Player) sender)).setLastCmd(container);
				} else {
					ACPlayer.getPlayer("serverConsole").setLastCmd(container);
				}
			}
			return true;
		} catch (final Throwable t) {
			ACLogger.severe(container != null ? container.debug()
					: "The container is null", t);
			Users.broadcastMessage("[AdminCmd] " + container != null ? container
					.debug()
					: cmd.getCmdName()
							+ " throw an Exception please report the log in a ticket : http://bug.admincmd.com/");
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCommand(final CommandSender sender, final Command command,
			final String label, final String[] args) {
		CoreCommand cmd = null;
		if ((cmd = registeredCommands.get(command)) != null) {
			return executeCommand(sender, cmd, args);
		} else {
			return false;
		}
	}

	/**
	 * Execute on the new player the commands found in the Commands.yml.<br />
	 * First look as a simple bukkit command, then in the alias defined by the
	 * user.
	 * 
	 * @param player
	 *            player that will execute the command.
	 */
	public void executeFirstJoinCommands(final Player player) {
		for (final String command : commandsOnJoin) {
			final String[] split = command.split("\\s+");
			if (split.length == 0) {
				continue;
			}
			final String name = split[0].substring(1).toLowerCase();
			final PluginCommand pCmd = Bukkit.getPluginCommand(name);
			if (pCmd != null) {
				pCmd.execute(player, name,
						Utils.Arrays_copyOfRange(split, 1, split.length));
			} else {
				processCommandString(player, command);
			}
		}

	}

	public boolean processCommandString(final CommandSender sender,
			final String command) {
		final String[] split = command.split("\\s+");
		if (split.length == 0) {
			return false;
		}
		final String cmdName = split[0].substring(1).toLowerCase();
		final CommandAlias cmdAlias = commandsAliasReplacer.get(cmdName);
		if (cmdAlias != null) {
			if (ConfigEnum.VERBOSE.getBoolean()) {
				ACLogger.info("Command " + cmdName + " intercepted for "
						+ cmdAlias);
			}
			final String[] cmdArgsArray = cmdAlias.processArguments(Utils
					.Arrays_copyOfRange(split, 1, split.length));
			final CoreCommand coreCmd = cmdAlias.getCmd();
			if (!coreCmd.argsCheck(cmdArgsArray)) {
				if (!HelpLister.getInstance().displayExactCommandHelp(sender,
						"AdminCmd", coreCmd.getCmdName())) {
					sender.sendMessage(coreCmd.getPluginCommand().getUsage()
							.replace("<command>", cmdName));
				}
				return true;
			}
			return executeCommand(sender, coreCmd, cmdArgsArray);
		}
		return false;
	}

	/**
	 * Register command from plugin
	 * 
	 * @param plugin
	 */
	public void registerACPlugin(final AbstractAdminCmdPlugin plugin) {
		final HashMap<String, Command> commands = new HashMap<String, Command>();
		for (final Command cmd : PluginCommandUtil.parse(plugin)) {
			commands.put(cmd.getName(), cmd);
		}

		pluginCommands.put(plugin, new HashMap<String, Command>(commands));
	}

	/**
	 * Register command
	 * 
	 * @param clazz
	 */
	public boolean registerCommand(final Class<? extends CoreCommand> clazz) {
		DebugLog.beginInfo("Register command " + clazz.getName());
		try {
			registerCommand0(clazz);
			final IncrementalPlotter plotter = new ClassPlotter(clazz);
			graph.addPlotter(plotter);
			plotters.put(clazz, plotter);
		} catch (final InstantiationException e) {
			e.printStackTrace();
			return false;
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
			return false;
		} catch (final CommandDisabled e) {
			disableCommand(e);
			return false;
		} catch (final CommandAlreadyExist e) {
			return handleExistingCommand(e);
		} catch (final CommandException e) {
			if (ConfigEnum.VERBOSE.getBoolean()) {
				Logger.getLogger("Minecraft").info(
						"[AdminCmd] " + e.getMessage());
			}
			return false;

		} finally {
			DebugLog.endInfo();
		}
		return true;
	}

	/**
	 * @param clazz
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void registerCommand0(final Class<? extends CoreCommand> clazz)
			throws InstantiationException, IllegalAccessException {
		final CoreCommand command = clazz.newInstance();
		command.initializeCommand();
		checkCommand(command);
		command.registerBukkitPerm();
		command.getPluginCommand().setExecutor(instance);
		registeredCommands.put(command.getPluginCommand(), command);

	}

	/**
	 * @param clazz
	 * @param command
	 * @param e
	 * @return
	 */
	private boolean handleExistingCommand(final CommandAlreadyExist e) {
		DebugLog.beginInfo("Handle existing command");
		try {
			final CoreCommand command = e.getCommand();
			if (checkCmdStatus(command)) {
				disableCommand(e);
				DebugLog.INSTANCE.info("Command Disabled");
				return false;
			} else {
				command.registerBukkitPerm();
				command.getPluginCommand().setExecutor(this);
				registeredCommands.put(command.getPluginCommand(), command);
				DebugLog.INSTANCE
						.info("Command Prioritized but already exists");
				final IncrementalPlotter plotter = new ClassPlotter(
						command.getClass());
				graph.addPlotter(plotter);
				plotters.put(command.getClass(), plotter);
				return true;
			}
		} finally {
			DebugLog.endInfo();
		}
	}

	/**
	 * Check if the command need to be disabled or is prioritized
	 * 
	 * @param command
	 * @return true if can be disabled.
	 */
	private boolean checkCmdStatus(final CoreCommand command) {
		final Map<String, Command> commands = pluginCommands.get(command
				.getPlugin());
		if (commands != null) {
			for (final String alias : commands.get(command.getCmdName())
					.getAliases()) {
				if (prioritizedCommands.contains(alias)) {
					final CommandAlias cmd = new CommandAlias(
							command.getCmdName(), alias, "");
					cmd.setCmd(command);
					commandsAliasReplacer.put(alias, cmd);
					return false;
				}
				final Set<CommandAlias> commandAliases = commandsAlias
						.get(alias);
				if (commandAliases != null) {
					for (final CommandAlias commandAlias : commandAliases) {
						commandAlias.setCmd(command);
					}
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param command
	 * @param exception
	 */
	private void disableCommand(final CommandException exception) {
		DebugLog.beginInfo("Disabling command");
		try {
			final CoreCommand command = exception.getCommand();
			unRegisterBukkitCommand(command.getPluginCommand());
			HelpLister.getInstance().removeHelpEntry(
					command.getPlugin().getPluginName(), command.getCmdName());
			if (ConfigEnum.VERBOSE.getBoolean()) {
				ACLogger.info(exception.getMessage());
			}
		} finally {
			DebugLog.endInfo();
		}
	}

	/**
	 * @param plugin
	 *            the plugin to set
	 */
	public void setCorePlugin(final AdminCmd plugin) {
		this.corePlugin = plugin;
		final ExtendedConfiguration cmds = FileManager.getInstance().getYml(
				"commands");
		disabledCommands = cmds.getStringList("disabledCommands",
				new LinkedList<String>());
		prioritizedCommands = cmds.getStringList("prioritizedCommands",
				new LinkedList<String>());
		commandsOnJoin.addAll(cmds.getStringList("onNewJoin"));
		final ExConfigurationSection aliases = cmds
				.getConfigurationSection("aliases");
		for (final String command : aliases.getKeys(false)) {
			Set<CommandAlias> setAliasCmd = commandsAlias.get(command);
			if (setAliasCmd == null) {
				setAliasCmd = new HashSet<CommandAlias>();
				commandsAlias.put(command, setAliasCmd);
			}
			final ConfigurationSection aliasSection = aliases
					.getConfigurationSection(command);
			for (final Entry<String, Object> alias : aliasSection.getValues(
					false).entrySet()) {
				String params;
				final Object value = alias.getValue();
				if (value instanceof ConfigurationSection) {
					final StringBuffer buffer = new StringBuffer();
					for (final Entry<String, Object> entry : ((ConfigurationSection) alias)
							.getValues(false).entrySet()) {
						buffer.append(entry.getKey()).append(':')
								.append(entry.getValue());
					}
					params = buffer.toString();
				} else {
					params = value.toString();
				}
				final CommandAlias commandAlias = new CommandAlias(command,
						alias.getKey(), params);
				commandsAliasReplacer.put(alias.getKey(), commandAlias);

				setAliasCmd.add(commandAlias);
			}

		}
		graph = plugin.getMetrics().createGraph("Commands");
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
	 * @param pCmd
	 */
	private void unRegisterBukkitCommand(final PluginCommand pCmd) {
		final CommandMap commandMap = FieldUtils.getCommandMap();
		final HashMap<String, Command> knownCommands = FieldUtils.getAttribute(
				commandMap, "knownCommands");
		PluginCommand cmd;
		final List<String> aliases = new ArrayList<String>();
		try {
			cmd = (PluginCommand) knownCommands.get(pCmd.getName());
			if (pCmd == cmd) {
				DebugLog.addInfo("Remove Command " + pCmd.getName());
				knownCommands.remove(pCmd.getName());
				aliases.addAll(pCmd.getAliases());
				pCmd.unregister(commandMap);
			} else {
				return;
			}
		} catch (final ClassCastException e) {
			DebugLog.addException("Not a Plugin Command", e);
		}
		for (final String alias : aliases) {
			try {
				cmd = (PluginCommand) knownCommands.get(alias);
				if (pCmd == cmd) {
					knownCommands.remove(alias);
				}
			} catch (final ClassCastException e) {
				DebugLog.INSTANCE.log(Level.INFO, "Not a Plugin Command", e);
			}
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
	public boolean unRegisterCommand(final Class<? extends CoreCommand> clazz,
			final AbstractAdminCmdPlugin plugin) {
		try {
			final CoreCommand command = clazz.newInstance();
			if (plugin.equals(command.getPlugin())) {
				try {
					command.initializeCommand();
					final PluginCommand pCmd = command.getPluginCommand();
					registeredCommands.remove(pCmd);
					removeFromAliasReplacer(command);
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

	private void removeFromAliasReplacer(final CoreCommand command) {
		final List<String> keysToRemove = new ArrayList<String>();
		for (final Entry<String, CommandAlias> entry : commandsAliasReplacer
				.entrySet()) {
			if (entry.getValue().getCmd().equals(command)) {
				keysToRemove.add(entry.getKey());
			}
		}
		for (final String key : keysToRemove) {
			commandsAliasReplacer.remove(key);
		}
	}

}
