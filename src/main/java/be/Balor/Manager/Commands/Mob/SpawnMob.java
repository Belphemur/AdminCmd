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
package be.Balor.Manager.Commands.Mob;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

import com.Balor.bukkit.AdminCmd.AdminCmd;
import com.Balor.bukkit.AdminCmd.AdminCmdWorker;

import be.Balor.Manager.ACCommands;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class SpawnMob extends ACCommands {

	/**
	 * 
	 */
	public SpawnMob() {
		permNode = "admincmd.mob.spawn";
		cmdName = "bal_mob";
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
		if (AdminCmdWorker.getInstance().isPlayer()) {
			final String name = args[0];
			int nbTaped;
			try {
				nbTaped = Integer.parseInt(args[1]);
			} catch (Exception e) {
				nbTaped = 1;
			}
			final int nb = nbTaped;
			final CreatureType ct = CreatureType.fromName(name);
			if (ct == null) {
				sender.sendMessage(ChatColor.RED + "No such creature: " + ChatColor.WHITE + name);
				return;
			}
			final Player player = ((Player) sender);
			AdminCmd.getBukkitServer()
					.getScheduler()
					.scheduleAsyncDelayedTask(AdminCmdWorker.getInstance().getPluginInstance(),
							new Runnable() {

								@Override
								public void run() {
									for (int i = 0; i < nb; i++) {
										player.getWorld().spawnCreature(player.getLocation(), ct);
										try {
											Thread.sleep(110);
										} catch (InterruptedException e) {
											// e.printStackTrace();
										}
									}
									player.sendMessage(ChatColor.BLUE + "Spawned "
											+ ChatColor.WHITE + nb + " " + name);
								}
							});
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
