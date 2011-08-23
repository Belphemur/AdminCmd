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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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
	private List<HelpEntry> pluginHelp = new ArrayList<HelpEntry>();
	private Plugin plugin;
	private CommandSender lastCommandSender;
	private List<HelpEntry> lastHelpEntries;

	public HelpList(Plugin plugin, List<HelpEntry> help) {
		this.plugin = plugin;
		this.pluginHelp = help;
	}

	@SuppressWarnings("unchecked")
	public HelpList(Plugin plugin) {
		List<HelpEntry> list = new ArrayList<HelpEntry>();
		final HashMap<String, HashMap<String, String>> cmds = (HashMap<String, HashMap<String, String>>) plugin
				.getDescription().getCommands();
		List<String> perms = new ArrayList<String>();
		for (Entry<String, HashMap<String, String>> k : cmds.entrySet()) {
			final HashMap<String, String> value = k.getValue();
			if (value.containsKey("permission") && value.get("permission") != null
					&& !(value.get("permission").equals("")))
				perms.add(value.get("permission"));
			else if (value.containsKey("permissions") && value.get("permissions") != null
					&& !(value.get("permissions").equals("")))
				perms.add(value.get("permissions"));
			list.add(new HelpEntry(k.getKey(), value.get("description"), new ArrayList<String>(
					perms)));
			perms.clear();
		}
		this.plugin = plugin;
		this.pluginHelp = list;
	}

	/**
	 * @return the pluginName
	 */
	public String getPluginName() {
		return plugin.getDescription().getName();
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
						+ plugin.getDescription().getName() + " (" + page + "/" + maxPages + ") "
						+ ChatColor.AQUA, '='));
		if (Utils.isPlayer(sender, false)) {
			for (int i = start; i < end; i++) {
				HelpEntry he = lastHelpEntries.get(i);
				helpList.add(he.chatString());
			}
		} else {
			for (int i = start; i < end; i++) {
				HelpEntry he = lastHelpEntries.get(i);
				helpList.add(he.consoleString());
			}
		}
		return helpList;

	}
}
