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

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

import be.Balor.Manager.ACCommand;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.AdminCmd;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class SpawnMob extends ACCommand {

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
			int distance = 0;
			try {
				nbTaped = Integer.parseInt(args[1]);
			} catch (Exception e) {
				nbTaped = 1;
			}
			try {
				distance = Integer.parseInt(args[2]);
			} catch (Exception e) {
				distance = 0;
			}

			final Player player = ((Player) sender);
			Location loc;
			if (distance == 0)
				loc = player.getTargetBlock(null, 100).getLocation().add(0, 1, 0);
			else {
				Location playerLoc = player.getLocation();
				loc = playerLoc.add(
						playerLoc
								.getDirection()
								.normalize()
								.multiply(distance)
								.toLocation(player.getWorld(), playerLoc.getYaw(),
										playerLoc.getPitch())).add(0, 1D, 0);
			}
			CreatureType ct = null;
			if (name.contains(":")) {
				String[] creatures = name.split(":");
				ct = CreatureType.fromName(creatures[0]);
				CreatureType ct2 = CreatureType.fromName(creatures[1]);
				if (ct == null) {
					replace.put("mob", creatures[0]);
					Utils.sI18n(sender, "errorMob", replace);
				}
				if (ct2 == null) {
					replace.put("mob", creatures[1]);
					Utils.sI18n(sender, "errorMob", replace);
				}
				if (ct == null || ct2 == null)
					return;
				AdminCmd.getBukkitServer()
						.getScheduler()
						.scheduleAsyncDelayedTask(ACHelper.getInstance().getPluginInstance(),
								new PassengerMob(loc, nbTaped, ct, ct2, player));
			} else {
				ct = CreatureType.fromName(name);
				if (ct == null) {
					Utils.sI18n(sender, "errorMob", replace);
					return;
				}

				AdminCmd.getBukkitServer()
						.getScheduler()
						.scheduleAsyncDelayedTask(ACHelper.getInstance().getPluginInstance(),
								new NormalMob(loc, nbTaped, ct, player));
			}
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

	protected class NormalMob implements Runnable {
		protected Location loc;
		protected int nb;
		protected CreatureType ct;
		protected Player player;

		/**
		 * 
		 */
		public NormalMob(Location loc, int nb, CreatureType ct, Player player) {
			this.loc = loc;
			this.nb = nb;
			this.ct = ct;
			this.player = player;
		}

		public void run() {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("mob", ct.getName());
			for (int i = 0; i < nb; i++) {
				loc.getWorld().spawnCreature(loc, ct);
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}
			}
			replace.put("nb", String.valueOf(nb));
			Utils.sI18n(player, "spawnMob", replace);
		}
	}

	protected class PassengerMob extends NormalMob {
		protected CreatureType passenger;

		/**
		 * @param loc
		 * @param nb
		 * @param ct
		 * @param player
		 */
		public PassengerMob(Location loc, int nb, CreatureType mount, CreatureType passenger,
				Player player) {
			super(loc, nb, mount, player);
			this.passenger = passenger;
		}

		@Override
		public void run() {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("mob", ct.getName() + "-" + passenger.getName());
			for (int i = 0; i < nb; i++) {
				loc.getWorld().spawnCreature(loc, ct)
						.setPassenger(loc.getWorld().spawnCreature(loc, passenger));
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}
			}
			replace.put("nb", String.valueOf(nb));
			Utils.sI18n(player, "spawnMob", replace);
		}

	}

}
