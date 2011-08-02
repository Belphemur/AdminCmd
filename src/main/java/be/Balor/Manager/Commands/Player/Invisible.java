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

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;


import be.Balor.Manager.ACCommands;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Utils;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Invisible extends ACCommands {

	/**
	 * 
	 */
	public Invisible() {
		permNode = "admincmd.player.invisible";
		cmdName = "bal_invisible";
		other = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(CommandSender sender, String... args) {
		Player target = Utils.getUser(sender, args, permNode);
		HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", target.getName());
		if (!InvisibleWorker.getInstance().hasInvisiblePowers(target.getName())) {
			InvisibleWorker.getInstance().vanish(target);
			Utils.sI18n(target, "invisibleEnabled");
			if (!target.equals(sender))
				Utils.sI18n(sender, "invisibleEnabledTarget", replace);
		} else {
			InvisibleWorker.getInstance().reappear(target);
			Utils.sI18n(target, "invisibleDisabled");
			if (!target.equals(sender))
				Utils.sI18n(sender, "invisibleDisabledTarget", replace);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null;
	}
	/* (non-Javadoc)
	 * @see be.Balor.Manager.ACCommands#registerBukkitPerm()
	 */
	@Override
	public void registerBukkitPerm() {
		super.registerBukkitPerm();
		PermissionManager.getInstance().addPermChild("admincmd.invisible.notatarget", PermissionDefault.OP);
		PermissionManager.getInstance().addPermChild("admincmd.invisible.cansee", PermissionDefault.OP);
	}

}
