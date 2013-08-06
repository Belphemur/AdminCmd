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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Tools.CommandUtils.Immunity;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Antoine
 * 
 */
public class AsUser extends PlayerCommand {

	/**
	 * 
	 */
	public AsUser() {
		super("bal_asuser", "admincmd.player.asuser");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
	 * CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		final String playerName = args.getString(0);
		final Player target = Bukkit.getPlayer(playerName);
		if (target == null) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", playerName);
			throw new PlayerNotFound(LocaleManager.I18n("playerNotFound", replace),
					sender);
		}
		if (!Immunity.checkImmunity(sender, target)) {
			throw new PlayerNotFound(LocaleManager.I18n("insufficientLvl"), sender);
		}
		final String argsString = args.concatWithout(0);
		ACPluginManager.scheduleSyncTask(new Runnable() {
			@Override
			public void run() {
				Bukkit.getServer().dispatchCommand(target, argsString);

			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null && args.length >= 2;
	}

}
