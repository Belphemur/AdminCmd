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

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

import com.Balor.Tools.Utils;
import com.Balor.bukkit.AdminCmd.AdminCmd;
import com.Balor.bukkit.AdminCmd.ACHelper;

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
		if (Utils.isPlayer(sender)) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			final String name = args[0];
			replace.put("mob", name);
			int nbTaped;
			try {
				nbTaped = Integer.parseInt(args[1]);
			} catch (Exception e) {
				nbTaped = 1;
			}
			final int nb = nbTaped;
			final CreatureType ct = CreatureType.fromName(name);
			if (ct == null) {
				Utils.sI18n(sender, "errorMob");
				return;
			}
			final Player player = ((Player) sender);
			AdminCmd.getBukkitServer()
					.getScheduler()
					.scheduleAsyncDelayedTask(ACHelper.getInstance().getPluginInstance(),
							new Runnable() {

								public void run() {
									for (int i = 0; i < nb; i++) {
										player.getWorld().spawnCreature(player.getLocation(), ct);
										try {
											Thread.sleep(5);
										} catch (InterruptedException e) {
											// e.printStackTrace();
										}
									}
									replace.put("nb", String.valueOf(nb));
									Utils.sI18n(player, "spawnMob", replace);
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
