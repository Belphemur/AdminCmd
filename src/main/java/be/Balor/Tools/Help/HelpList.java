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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import be.Balor.Tools.Utils;
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

	@SuppressWarnings("unchecked")
	public HelpList(Plugin plugin) throws IllegalArgumentException {
		TreeSet<HelpEntry> list = new TreeSet<HelpEntry>(new EntryNameComparator());
		final HashMap<String, HashMap<String, String>> cmds = (HashMap<String, HashMap<String, String>>) plugin
				.getDescription().getCommands();
		this.pluginName = plugin.getDescription().getName();
		if (cmds == null)
			throw new IllegalArgumentException(pluginName + " don't have any commands to list");
		List<String> perms = new ArrayList<String>();
		try {
			for (Entry<String, HashMap<String, String>> k : cmds.entrySet()) {
				final HashMap<String, String> value = k.getValue();
				if (value.containsKey("permission") && value.get("permission") != null
						&& !(value.get("permission").equals("")))
					perms.add(value.get("permission"));
				else if (value.containsKey("permissions") && value.get("permissions") != null
						&& !(value.get("permissions").equals("")))
					perms.add(value.get("permissions"));
				String desc = value.get("description");
				list.add(new HelpEntry(k.getKey(), desc == null ? "" : desc, new ArrayList<String>(
						perms), k.getKey()));
				perms.clear();
			}
			this.pluginHelp = list;
		} catch (Exception e) {
			System.out.print("[AdminCmd] Problem with permissions of " + pluginName);
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
