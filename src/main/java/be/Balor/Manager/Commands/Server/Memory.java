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
package be.Balor.Manager.Commands.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Memory extends CoreCommand {

	/**
	 * 
	 */
	public Memory() {
		permNode = "admincmd.server.memory";
		cmdName = "bal_memory";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(CommandSender sender, CommandArgs args) {
		if (args.hasFlag('f')) {
			int count = 0;
			final HashMap<String, List<Entity>> entityList = new HashMap<String, List<Entity>>(
					sender.getServer().getWorlds().size());
			final List<World> worlds = sender.getServer().getWorlds();
			final Semaphore sema = new Semaphore(0);

			ACPluginManager.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				@SuppressWarnings("unchecked")
				@Override
				public void run() {
					for (World w : worlds) {
						final net.minecraft.server.World cWorld = ((CraftWorld) w).getHandle();
						synchronized (cWorld.entityList) {
							entityList.put(w.getName(), new ArrayList<Entity>(cWorld.entityList));
							sema.release();
						}
						
					}
				}
			});

			for (World w : worlds) {
				try {
					sema.acquire();
				} catch (InterruptedException e) {
				}
				for (Entity entity : entityList.get(w.getName())) {
					if (entity instanceof EntityHuman)
						continue;
					entity.die();
					count++;
				}
			}
			System.gc();
			sender.sendMessage("Freed Entity : " + count);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
		long usedMB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L;
		sender.sendMessage(ChatColor.GOLD + "Max Memory : " + ChatColor.WHITE
				+ Runtime.getRuntime().maxMemory() / 1024L / 1024L + "MB");
		sender.sendMessage(ChatColor.DARK_RED + "Used Memory : " + ChatColor.WHITE + usedMB + "MB");
		sender.sendMessage(ChatColor.DARK_GREEN + "Free Memory : " + ChatColor.WHITE
				+ Runtime.getRuntime().freeMemory() / 1024L / 1024L + "MB");
		for (World w : sender.getServer().getWorlds()) {
			sender.sendMessage(w.getEnvironment() + " \"" + w.getName() + "\": "
					+ w.getLoadedChunks().length + " chunks, " + w.getEntities().size()
					+ " entities");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return true;
	}

}
