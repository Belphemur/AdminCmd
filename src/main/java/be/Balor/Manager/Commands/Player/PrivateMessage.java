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
package be.Balor.Manager.Commands.Player;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Balor.Tools.Utils;
import com.Balor.bukkit.AdminCmd.ACHelper;

import be.Balor.Manager.ACCommands;
import be.Balor.Manager.PermissionManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PrivateMessage extends ACCommands {

	/**
	 * 
	 */
	public PrivateMessage() {
		permNode = "admincmd.player.msg";
		cmdName = "bal_playermsg";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String... args) {
		Player buddy = sender.getServer().getPlayer(args[0]);
		if (buddy != null) {
			String senderPm = "";
			String msgPrefix = "[" + ChatColor.RED + "private" + ChatColor.WHITE + "] ";
			String msg = "";
			String senderName = "Server Admin";
			if (ACHelper.getInstance().isPlayer(false)) {
				Player pSender = (Player) sender;
				senderName = pSender.getName();
				if (PermissionManager.getPermission() != null) {
					String name = pSender.getName();
					String prefixstring;
					String world = "";
					world = pSender.getWorld().getName();

					try {
						prefixstring = PermissionManager.getPermission().safeGetUser(world, name)
								.getPrefix();
					} catch (Exception e) {
						String group = PermissionManager.getPermission().getGroup(world, name);
						prefixstring = PermissionManager.getPermission().getGroupPrefix(world,
								group);
					} catch (NoSuchMethodError e) {
						String group = PermissionManager.getPermission().getGroup(world, name);
						prefixstring = PermissionManager.getPermission().getGroupPrefix(world,
								group);
					}

					if (prefixstring != null && prefixstring.length() > 1) {
						String result = Utils.colorParser(prefixstring);
						if (result == null)
							senderPm = prefixstring + name + ChatColor.WHITE + " - ";
						else
							senderPm = result + name + ChatColor.WHITE + " - ";

					} else
						senderPm = pSender.getDisplayName() + " - ";
				} else
					senderPm = pSender.getDisplayName() + " - ";
			} else
				senderPm = "Server Admin" + " - ";

			for (int i = 1; i < args.length; ++i)
				msg += args[i] + " ";
			msg = msg.trim();
			String parsed = Utils.colorParser(msg);
			if (parsed == null)
				parsed = msg;
			buddy.sendMessage(msgPrefix + senderPm + parsed);
			sender.sendMessage(msgPrefix + senderPm + parsed);
			for(Player p : ACHelper.getInstance().getAllPowerUserOf("spymsg"))
				if(!p.getName().equals(senderName) && !p.getName().equals(buddy.getName()))
					p.sendMessage("[" + ChatColor.GREEN + "SpyMsg" + ChatColor.WHITE + "] "+senderName+"-"+buddy.getName()+": "+parsed);
		} else
			sender.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + args[0]
					+ ChatColor.RED + " not found!");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null && args.length >= 2;
	}

}
