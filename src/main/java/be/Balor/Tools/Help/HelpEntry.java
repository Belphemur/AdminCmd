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

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Help.String.ACMinecraftFontWidthCalculator;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class HelpEntry {
	private String command;
	private String description;
	private List<String> permissions;

	public HelpEntry(String command, String description, List<String> permissions) {
		this.command = command;
		this.description = description;
		this.permissions = permissions;
	}

	public boolean hasPerm(CommandSender p) {
		for (String perm : permissions) {
			if (perm.equals("OP"))
				if (Utils.isPlayer(p, false))
					return p.isOp();
				else
					return true;
			return PermissionManager.hasPerm(p, perm, false);
		}
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("%s/%s%s : %s%s", ChatColor.GOLD, command, ChatColor.WHITE.toString(),
						ChatColor.WHITE, description).replace("[", ChatColor.RED + "[")
				.replace("]", "]" + ChatColor.GOLD);
	}

	public String chatString() {
		String line = getFormatedCmd();

		int sizeRemaining = ACMinecraftFontWidthCalculator.chatwidth
				- ACMinecraftFontWidthCalculator.getStringWidth(line);
		int descriptionSize = ACMinecraftFontWidthCalculator.strLen(description);
		line += ACMinecraftFontWidthCalculator.strPadLeftChat(
				description.replace("[", ChatColor.DARK_RED + "[").replace("]",
						"]" + ChatColor.WHITE), sizeRemaining, ' ');

		if (ACHelper.getInstance().getConfBoolean("help.shortenEntries")) {
			return ACMinecraftFontWidthCalculator.strChatTrim(line);
		} else if (sizeRemaining > descriptionSize
				|| !ACHelper.getInstance().getConfBoolean("help.useWordWrap")) {
			return line;
		} else if (ACHelper.getInstance().getConfBoolean("help.wordWrapRight")) {
			return ACMinecraftFontWidthCalculator.strChatWordWrapRight(line, 10, ' ', ':');
		} else {
			return ACMinecraftFontWidthCalculator.strChatWordWrap(line, 10);
		}
	}

	public String consoleString() {
		int width = System.getProperty("os.name").startsWith("Windows") ? 80 - 17 : 90;
		String line = getFormatedCmd();

		int sizeRemaining = width - ACMinecraftFontWidthCalculator.strLen(line);
		int descriptionSize = ACMinecraftFontWidthCalculator.strLen(description);
		line += ACMinecraftFontWidthCalculator.unformattedPadLeft(
				description.replace("[", ChatColor.DARK_RED + "[").replace("]",
						"]" + ChatColor.WHITE), sizeRemaining, ' ');

		if (ACHelper.getInstance().getConfBoolean("help.shortenEntries")) {
			return ACMinecraftFontWidthCalculator.strTrim(line, width);
		} else if (sizeRemaining > descriptionSize
				|| !ACHelper.getInstance().getConfBoolean("help.useWordWrap")) {
			return line;
		} else if (ACHelper.getInstance().getConfBoolean("help.wordWrapRight")) {
			return ACMinecraftFontWidthCalculator.strWordWrapRight(line, width, 10, ' ', ':');
		} else {
			return ACMinecraftFontWidthCalculator.strWordWrap(line, width, 10);
		}

	}

	private String getFormatedCmd() {
		return String.format("%s/%s%s :", ChatColor.GOLD,
				command.replace("[", ChatColor.DARK_RED + "[").replace("]", "]" + ChatColor.GOLD),
				ChatColor.WHITE);
	}

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}
}
