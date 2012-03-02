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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import be.Balor.Tools.Utils;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Help.String.ACMinecraftFontWidthCalculator;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class HelpList {
	private TreeSet<HelpEntry> pluginHelp = new TreeSet<HelpEntry>(new EntryNameComparator());
	private String pluginName;
	private CommandSender lastCommandSender;
	private List<HelpEntry> lastHelpEntries;
	private HelpEntry lastCommandSearched;

	/**
	 * 
	 */
	public HelpList(String plugin) {
		this.pluginName = plugin;
	}

	public void addEntry(HelpEntry he) {
		if (pluginHelp.contains(he))
			pluginHelp.remove(he);
		pluginHelp.add(he);
	}

	public boolean removeEntry(String commandName) {
		DebugLog.INSTANCE.info("Remove " + commandName + " help from plugin : " + pluginName);
		HelpEntry toRemove = null;
		for (HelpEntry he : pluginHelp)
			if (he.getCommandName().equals(commandName)) {
				toRemove = he;
				break;
			}
		if (toRemove != null)
			return pluginHelp.remove(toRemove);
		return false;

	}

	public HelpList(Plugin plugin) throws IllegalArgumentException {
		TreeSet<HelpEntry> list = new TreeSet<HelpEntry>(new EntryNameComparator());
		final Map<String, Map<String, Object>> cmds = plugin.getDescription().getCommands();
		this.pluginName = plugin.getDescription().getName();
		if (cmds == null)
			throw new IllegalArgumentException(pluginName + " don't have any commands to list");
		List<String> perms = new ArrayList<String>();
		try {
			for (Entry<String, Map<String, Object>> k : cmds.entrySet()) {
				final Map<String, Object> value = k.getValue();
				if (value.containsKey("permission") && value.get("permission") != null
						&& !(value.get("permission").equals("")))
					perms.add(value.get("permission").toString());
				else if (value.containsKey("permissions") && value.get("permissions") != null
						&& !(value.get("permissions").equals("")))
					perms.add(value.get("permissions").toString());
				String desc = value.get("description").toString();
				list.add(new HelpEntry(k.getKey(), desc == null ? "" : desc, new ArrayList<String>(
						perms), k.getKey()));
				perms.clear();
			}
			this.pluginHelp = list;
		} catch (Exception e) {
			ACLogger.warning("[HELP] Problem with commands of " + pluginName);
			this.pluginHelp = new TreeSet<HelpEntry>(new EntryNameComparator());
		}

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
	private void checkPermissions(CommandSender sender) {
		if (lastCommandSender != null && sender.equals(lastCommandSender))
			return;
		lastHelpEntries = new ArrayList<HelpEntry>();
		lastCommandSender = sender;
		for (HelpEntry he : pluginHelp)
			if (he.hasPerm(sender))
				lastHelpEntries.add(he);
		Collections.sort(lastHelpEntries, new EntryCommandComparator());
	}

	/**
	 * Get a list of the string to display for the wanted page, and the given
	 * user
	 * 
	 * @param page
	 *            int the wanted page
	 * @param sender
	 *            CommandSender the sender of the command
	 * @return
	 */
	public List<String> getPage(int page, CommandSender sender) {
		int entryPerPage = ConfigEnum.H_ENTRY.getInt();
		List<String> helpList = new ArrayList<String>();
		checkPermissions(sender);
		int maxPages = (int) Math.ceil(lastHelpEntries.size() / (double) entryPerPage);
		page = page > maxPages ? maxPages : page;
		int start = (page - 1) * entryPerPage;
		int end = start + entryPerPage > lastHelpEntries.size() ? lastHelpEntries.size() : start
				+ entryPerPage;
		helpList.add(ChatColor.AQUA
				+ ACMinecraftFontWidthCalculator.strPadCenterChat(ChatColor.DARK_GREEN + " "
						+ pluginName + " (" + page + "/" + maxPages + ") " + ChatColor.AQUA, '='));
		HelpEntry[] array = lastHelpEntries.toArray(new HelpEntry[] {});
		if (Utils.isPlayer(sender, false)) {
			for (int i = start; i < end; i++) {
				HelpEntry he = array[i];
				helpList.add(he.chatString());
			}
		} else {
			for (int i = start; i < end; i++) {
				HelpEntry he = array[i];
				helpList.add(he.consoleString());
			}
		}
		return helpList;

	}

	/**
	 * Get the command help of the wanted command by matching it in the list of
	 * avaible commands.
	 * 
	 * @param cmd
	 *            command to search
	 * @param sender
	 *            sender of the command (used for checking the permission)
	 * @return the chat String to display to the user, <b>null</b> if not found
	 */
	public String getCommand(String cmd, CommandSender sender) {
		HelpEntry found = null;
		if (cmd == null)
			return null;
		if (lastCommandSearched != null
				&& lastCommandSearched.getCommand().toLowerCase().startsWith(cmd.toLowerCase()))
			return lastCommandSearched.chatString();
		String lowerSearch = cmd.toLowerCase().trim();
		int delta = Integer.MAX_VALUE;
		for (HelpEntry entry : pluginHelp) {
			String str = entry.getCommand().trim();
			if (str.toLowerCase().startsWith(lowerSearch)) {
				int curDelta = str.length() - lowerSearch.length();
				if (curDelta < delta) {
					found = entry;
					delta = curDelta;
				}
				if (curDelta == 0)
					break;
			}
		}
		if (found == null) {
			DebugLog.INSTANCE.warning(pluginName + " : " + cmd + " not found.");
			return null;
		}

		if (!found.hasPerm(sender))
			return null;
		
		lastCommandSearched = found;
		return found.chatString();
	}

	private static class EntryNameComparator implements Comparator<HelpEntry> {

		boolean descending = true;

		@Override
		public int compare(HelpEntry o1, HelpEntry o2) {
			return o1.getCommandName().compareTo(o2.getCommandName()) * (descending ? 1 : -1);
		}
	}

	private static class EntryCommandComparator implements Comparator<HelpEntry> {

		boolean descending = true;

		@Override
		public int compare(HelpEntry o1, HelpEntry o2) {
			return o1.getCommand().compareTo(o2.getCommand()) * (descending ? 1 : -1);
		}
	}
}
