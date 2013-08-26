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
package be.Balor.Tools.Help;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import be.Balor.Tools.CommandUtils.Users;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Help.String.ACMinecraftFontWidthCalculator;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
class HelpList {
	private TreeSet<HelpEntry> pluginHelp = new TreeSet<HelpEntry>(new EntryNameComparator());
	private final String pluginName;
	private CommandSender lastCommandSender;
	private Queue<HelpEntry> lastHelpEntries;
	private CmdMatch lastCommandSearched;
	private int initSize = 1;

	/**
	 *
	 */
	HelpList(final String plugin) {
		this.pluginName = plugin;
	}

	public void addEntry(final HelpEntry he) {
		if (pluginHelp.contains(he)) {
			pluginHelp.remove(he);
		}
		pluginHelp.add(he);
		updateInitSize();
	}

	private void updateInitSize() {
		final int dividedBy2 = pluginHelp.size() / 2;
		initSize = dividedBy2 >= 1 ? dividedBy2 : 1;
	}

	public boolean removeEntry(final String commandName) {
		DebugLog.beginInfo("Remove " + commandName + " help from plugin : " + pluginName);
		try {
			final HelpEntry toRemove = searchEntryFromCmdName(commandName);
			if (toRemove != null) {
				return pluginHelp.remove(toRemove);
			}
			return false;
		} finally {
			DebugLog.endInfo();
		}

	}

	/**
	 * Search an Help entry in the plugin help using the command name (ex :
	 * bal_thor)
	 * 
	 * @param commandName
	 * @return null if entry not found
	 */
	public HelpEntry searchEntryFromCmdName(final String commandName) {
		for (final HelpEntry he : pluginHelp) {
			if (he.getCommandName().equals(commandName)) {
				return he;
			}
		}
		return null;
	}

	public HelpList(final Plugin plugin) throws IllegalArgumentException {
		final TreeSet<HelpEntry> list = new TreeSet<HelpEntry>(new EntryNameComparator());
		final Map<String, Map<String, Object>> cmds = plugin.getDescription().getCommands();
		this.pluginName = plugin.getDescription().getName();
		if (cmds == null) {
			throw new IllegalArgumentException(pluginName + " don't have any commands to list");
		}
		final List<String> perms = new ArrayList<String>();
		try {
			for (final Entry<String, Map<String, Object>> k : cmds.entrySet()) {
				final Map<String, Object> value = k.getValue();
				if (value.containsKey("permission") && value.get("permission") != null && !(value.get("permission").equals(""))) {
					perms.add(value.get("permission").toString());
				} else if (value.containsKey("permissions") && value.get("permissions") != null && !(value.get("permissions").equals(""))) {
					perms.add(value.get("permissions").toString());
				}
				final String desc = value.get("description").toString();
				list.add(new HelpEntry(k.getKey(), desc == null ? "" : desc, "", new ArrayList<String>(perms), k.getKey()));
				perms.clear();
			}
			this.pluginHelp = list;
		} catch (final Exception e) {
			if (ConfigEnum.VERBOSE.getBoolean()) {
				ACLogger.warning("[HELP] Problem with commands of " + pluginName);
			}
			DebugLog.INSTANCE.warning("[HELP] " + e.toString());
			final StackTraceElement[] trace = e.getStackTrace();
			for (final StackTraceElement element : trace) {
				DebugLog.INSTANCE.warning("[HELP] " + element.toString());
			}
			this.pluginHelp = new TreeSet<HelpEntry>(new EntryNameComparator());
		}
		updateInitSize();

	}

	/**
	 * @return the pluginName
	 */
	public String getPluginName() {
		return pluginName;
	}

	/**
	 * Process all help to check get only the command that the player have
	 * access
	 * 
	 * @param sender
	 */
	private void checkPermissions(final CommandSender sender) {
		if (lastCommandSender != null && sender.equals(lastCommandSender)) {
			return;
		}

		lastHelpEntries = new PriorityQueue<HelpEntry>(initSize, new EntryCommandComparator());

		lastCommandSender = sender;
		for (final HelpEntry he : pluginHelp) {
			if (he.hasPerm(sender)) {
				lastHelpEntries.add(he);
			}
		}
	}

	/**
	 * Get a list of the string to display for the wanted page, and the given
	 * user
	 * 
	 * @param page
	 *            int the wanted page
	 * @param sender
	 *            CommandSender the sender of the command
	 * @param detailed
	 *            If true the detailed description will be displayed if one
	 *            exists
	 * @return
	 */
	public List<String> getPage(int page, final CommandSender sender, final boolean detailed) {
		final int entryPerPage = ConfigEnum.H_ENTRY.getInt();
		final List<String> helpList = new ArrayList<String>();
		checkPermissions(sender);
		final int maxPages = (int) Math.ceil(lastHelpEntries.size() / (double) entryPerPage);
		page = page > maxPages ? maxPages : page;
		final int start = (page - 1) * entryPerPage;
		final int end = start + entryPerPage > lastHelpEntries.size() ? lastHelpEntries.size() : start + entryPerPage;
		helpList.add(ChatColor.AQUA
				+ ACMinecraftFontWidthCalculator.strPadCenterChat(ChatColor.DARK_GREEN + " " + pluginName + " (" + page + "/" + maxPages + ") "
						+ ChatColor.AQUA, '='));
		final HelpEntry[] array = lastHelpEntries.toArray(new HelpEntry[] {});
		if (Users.isPlayer(sender, false)) {
			for (int i = start; i < end; i++) {
				final HelpEntry he = array[i];
				helpList.add(he.chatString(detailed));
			}
		} else {
			for (int i = start; i < end; i++) {
				final HelpEntry he = array[i];
				helpList.add(he.consoleString(detailed));
			}
		}
		return helpList;
	}

	public List<String> getPage(final int page, final CommandSender sender) {
		return getPage(page, sender, false);
	}

	/**
	 * Get the command help of the wanted command by matching it in the list of
	 * avaible commands.
	 * 
	 * @param cmd
	 *            command to search
	 * @param sender
	 *            sender of the command (used for checking the permission)
	 * @param detailed
	 *            If true the detailed description will be displayed if one
	 *            exists
	 * @return the chat String to display to the user, <b>null</b> if not found
	 */
	public List<HelpEntry> getCommandMatch(final String cmd, final CommandSender sender, final boolean detailed) {
		final List<HelpEntry> result = new ArrayList<HelpEntry>();
		if (cmd == null) {
			return null;
		}
		if (lastCommandSearched != null && lastCommandSearched.getCmd().equals(cmd)) {
			return lastCommandSearched.getResult();
		}
		final String lowerSearch = cmd.toLowerCase().trim();
		for (final HelpEntry entry : pluginHelp) {
			final String str = entry.getCommand().trim();
			if (str.toLowerCase().startsWith(lowerSearch) && entry.hasPerm(sender)) {
				result.add(entry);
			}

		}
		if (result.isEmpty()) {
			for (final HelpEntry entry : pluginHelp) {
				if (entry.hasPerm(sender) && entry.getDescription().toLowerCase().contains(lowerSearch)) {
					result.add(entry);
				}

			}
		}

		lastCommandSearched = new CmdMatch(cmd, result);
		return result;
	}

	/**
	 * Search for the commandName
	 * 
	 * @param cmd
	 * @return
	 */
	public HelpEntry getExactCommand(final String cmd) {
		for (final HelpEntry entry : pluginHelp) {
			if (!entry.getCommandName().equals(cmd)) {
				continue;
			}
			return entry;
		}
		return null;
	}

	public List<HelpEntry> getCommandMatch(final String cmd, final CommandSender sender) {
		return getCommandMatch(cmd, sender, false);
	}

	private static class EntryNameComparator implements Comparator<HelpEntry> {

		boolean descending = true;

		@Override
		public int compare(final HelpEntry o1, final HelpEntry o2) {
			return o1.getCommandName().compareTo(o2.getCommandName()) * (descending ? 1 : -1);
		}
	}

	private static class EntryCommandComparator implements Comparator<HelpEntry> {

		boolean descending = true;

		@Override
		public int compare(final HelpEntry o1, final HelpEntry o2) {
			return o1.getCommand().compareTo(o2.getCommand()) * (descending ? 1 : -1);
		}
	}
}
