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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import be.Balor.Tools.Utils;
import be.Balor.Tools.Help.String.ACMinecraftFontWidthCalculator;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class HelpList {
	private TreeSet<HelpEntry> pluginHelp = new TreeSet<HelpEntry>(new EntryComparator());
	private String pluginName;
	private CommandSender lastCommandSender;
	private TreeSet<HelpEntry> lastHelpEntries;

	/**
	 * 
	 */
	public HelpList(String plugin) {
		this.pluginName = plugin;
	}

	public void addEntry(HelpEntry he) {
		if(pluginHelp.contains(he))
			pluginHelp.remove(he);
		pluginHelp.add(he);
	}

	@SuppressWarnings("unchecked")
	public HelpList(Plugin plugin) throws IllegalArgumentException {
		TreeSet<HelpEntry> list = new TreeSet<HelpEntry>(new EntryComparator());
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
						perms)));
				perms.clear();
			}
			this.pluginHelp = list;
		} catch (Exception e) {
			System.out.print("[AdminCmd] Problem with permissions of "+pluginName);
			this.pluginHelp = new TreeSet<HelpEntry>(new EntryComparator());
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
		lastHelpEntries = new TreeSet<HelpEntry>(new EntryComparator());
		lastCommandSender = sender;
		for (HelpEntry he : pluginHelp)
			if (he.hasPerm(sender))
				lastHelpEntries.add(he);
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
		int entryPerPage = ACHelper.getInstance().getConfInt("help.entryPerPage");
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

	private static class EntryComparator implements Comparator<HelpEntry> {

		boolean descending = true;

		public EntryComparator() {
		}

		public int compare(HelpEntry o1, HelpEntry o2) {
			return o1.getCommand().compareTo(o2.getCommand()) * (descending ? 1 : -1);
		}
	}
}
