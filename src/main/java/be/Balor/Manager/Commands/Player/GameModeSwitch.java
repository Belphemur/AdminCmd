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

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class GameModeSwitch extends PlayerCommand {

	/**
	 *
	 */
	public GameModeSwitch() {
		super("bal_gamemode", "admincmd.player.gamemode");
		other = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
	 * CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void execute(CommandSender sender, CommandArgs args) {
		final Player target = Utils.getUser(sender, args, permNode);
		if (target == null)
			return;
		HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", Utils.getPlayerName(target));
		if (target.getGameMode() == GameMode.CREATIVE) {
			ACPluginManager.scheduleSyncTask(new Runnable() {
				@Override
				public void run() {
					target.setGameMode(GameMode.SURVIVAL);
				}
			});

			replace.put("gamemode", GameMode.SURVIVAL.toString());
			Utils.sendMessage(sender, target, "gmSwitch", replace);
		} else {
			ACPluginManager.scheduleSyncTask(new Runnable() {

				@Override
				public void run() {
					target.setGameMode(GameMode.CREATIVE);
				}
			});
			replace.put("gamemode", GameMode.CREATIVE.toString());
			Utils.sendMessage(sender, target, "gmSwitch", replace);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null;
	}

}
