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

import be.Balor.Manager.CoreCommand;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Mute extends CoreCommand {

	/**
	 * 
	 */
	public Mute() {
		permNode = "admincmd.player.mute";
		cmdName = "bal_mute";
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
		Player player = sender.getServer().getPlayer(args[0]);
		if (player != null) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", player.getName());
			ACPlayer acp = ACPlayer.getPlayer(player.getName());
			if (!acp.hasPower(Type.MUTED)) {
				String msg = "Server Admin";
				if (Utils.isPlayer(sender, false))
					msg = ((Player) sender).getName();
				acp.setPower(Type.MUTED, "Muted by " + msg);
				if (!player.equals(sender))
					Utils.sI18n(sender, "muteEnabledTarget", replace);
				if (args.length >= 2) {
					Integer tmpMute = null;
					try {
						tmpMute = Integer.parseInt(args[args.length - 1]);
						final String unmute = player.getName();
						final CommandSender senderFinal = sender;
						ACPluginManager
								.getServer()
								.getScheduler()
								.scheduleAsyncDelayedTask(ACHelper.getInstance().getCoreInstance(),
										new Runnable() {

											@Override
											public void run() {
												ACPlayer.getPlayer(unmute).removePower(Type.MUTED);
												Utils.sI18n(senderFinal, "muteDisabledTarget",
														"player", unmute);
											}
										}, 20 * 60 * tmpMute);

					} catch (Exception e) {
					}
					if (tmpMute == null)
						Utils.sI18n(player, "muteEnabled");
					else
						Utils.sI18n(player, "tmpMuteEnabled", "minutes", tmpMute.toString());
				}
			} else
				Utils.sI18n(sender, "alreadyMuted");

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null && args.length >= 1;
	}

}
