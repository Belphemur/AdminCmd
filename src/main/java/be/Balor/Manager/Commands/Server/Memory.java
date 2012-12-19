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
import net.minecraft.server.EntityAnimal;
import net.minecraft.server.EntityBoat;
import net.minecraft.server.EntityExperienceOrb;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;
import net.minecraft.server.EntityMinecart;
import net.minecraft.server.EntityMonster;
import net.minecraft.server.EntityPainting;
import net.minecraft.server.EntityVillager;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Manager.Permissions.PermChild;
import be.Balor.Manager.Permissions.PermParent;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Memory extends ServerCommand {
	private final PermChild full, animal, xp, item, mob, npc, cart, boat,
			vehicle;

	/**
     *
     */
	public Memory() {
		permNode = "admincmd.server.memory";
		cmdName = "bal_memory";
		full = new PermChild(permNode + ".full");
		animal = new PermChild(permNode + ".animal");
		mob = new PermChild(permNode + ".mob");
		item = new PermChild(permNode + ".item");
		xp = new PermChild(permNode + ".xp");
		npc = new PermChild(permNode + ".npc");
		cart = new PermChild(permNode + ".cart");
		boat = new PermChild(permNode + ".boat");
		vehicle = new PermChild(permNode + ".vehicle");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		if (args.hasFlag('f')
				&& !PermissionManager.hasPerm(sender, full.getBukkitPerm())) {
			return;
		}
		if (args.hasFlag('a')
				&& !PermissionManager.hasPerm(sender, animal.getBukkitPerm())) {
			return;
		}
		if (args.hasFlag('m')
				&& !PermissionManager.hasPerm(sender, mob.getBukkitPerm())) {
			return;
		}
		if (args.hasFlag('i')
				&& !PermissionManager.hasPerm(sender, item.getBukkitPerm())) {
			return;
		}
		if (args.hasFlag('x')
				&& !PermissionManager.hasPerm(sender, xp.getBukkitPerm())) {
			return;
		}
		if (args.hasFlag('n')
				&& !PermissionManager.hasPerm(sender, npc.getBukkitPerm())) {
			return;
		}
		if (args.hasFlag('c')
				&& !PermissionManager.hasPerm(sender, cart.getBukkitPerm())) {
			return;
		}
		if (args.hasFlag('b')
				&& !PermissionManager.hasPerm(sender, boat.getBukkitPerm())) {
			return;
		}
		if (args.hasFlag('v')
				&& !PermissionManager.hasPerm(sender, vehicle.getBukkitPerm())) {
			return;
		}
		if (args.hasFlag('f') || args.hasFlag('x') || args.hasFlag('i')
				|| args.hasFlag('m') || args.hasFlag('a') || args.hasFlag('n')
				|| args.hasFlag('v') || args.hasFlag('c') || args.hasFlag('b')) {
			int count = 0;
			final HashMap<String, List<Entity>> entityList = new HashMap<String, List<Entity>>(
					sender.getServer().getWorlds().size());
			final List<World> worlds = sender.getServer().getWorlds();
			final Semaphore sema = new Semaphore(0);

			ACPluginManager.getScheduler().scheduleSyncDelayedTask(plugin,
					new Runnable() {
						@SuppressWarnings("unchecked")
						@Override
						public void run() {
							for (final World w : worlds) {
								final net.minecraft.server.World cWorld = ((CraftWorld) w)
										.getHandle();
								synchronized (cWorld.entityList) {
									entityList.put(w.getName(),
											new ArrayList<Entity>(
													cWorld.entityList));
									sema.release();
								}

							}
						}
					});
			for (final World w : worlds) {
				try {
					sema.acquire();
				} catch (final InterruptedException e) {}
				for (final Entity entity : entityList.get(w.getName())) {
					if (dontKill(args, entity)) {
						continue;
					}
					entity.die();
					count++;
				}
			}
			System.gc();
			sender.sendMessage("Freed Entities : " + count);
			try {
				Thread.sleep(500);
			} catch (final InterruptedException e) {}
		}
		final long usedMB = (Runtime.getRuntime().totalMemory() - Runtime
				.getRuntime().freeMemory()) / 1024L / 1024L;
		sender.sendMessage(ChatColor.GOLD + "Max Memory : " + ChatColor.WHITE
				+ Runtime.getRuntime().maxMemory() / 1024L / 1024L + "MB");
		sender.sendMessage(ChatColor.DARK_RED + "Used Memory : "
				+ ChatColor.WHITE + usedMB + "MB");
		sender.sendMessage(ChatColor.DARK_GREEN + "Free Memory : "
				+ ChatColor.WHITE + Runtime.getRuntime().freeMemory() / 1024L
				/ 1024L + "MB");
		for (final World w : sender.getServer().getWorlds()) {
			sender.sendMessage(w.getEnvironment() + " \"" + w.getName()
					+ "\": " + w.getLoadedChunks().length + " chunks, "
					+ w.getEntities().size() + " entities");
		}

		// Code for TPS from here on
		long delay = 40L;
		if (args.length >= 1) {
			try {
				delay = args.getLong(0);
			} catch (final NumberFormatException e) {
				final HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("number", args.getString(0));
				Utils.sI18n(sender, "NaN", replace);
				return;
			}
		}
		if (delay < 20L) {
			delay = 20L;
		}
		final World world = ACPluginManager.getServer().getWorlds().get(0);
		ACPluginManager.getScheduler().scheduleSyncDelayedTask(
				ACHelper.getInstance().getCoreInstance(),
				new CheckTicks(System.nanoTime(), world, world.getFullTime(),
						sender), delay);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#registerBukkitPerm()
	 */
	@Override
	public void registerBukkitPerm() {
		final PermParent parent = new PermParent(permNode + ".*");
		plugin.getPermissionLinker().addChildPermParent(parent, permParent);
		final PermChild child = new PermChild(permNode, bukkitDefault);
		parent.addChild(child).addChild(mob).addChild(animal).addChild(xp)
				.addChild(item).addChild(full).addChild(npc).addChild(vehicle)
				.addChild(cart).addChild(boat);
		bukkitPerm = child.getBukkitPerm();
	}

	private class CheckTicks implements Runnable {

		protected long oldNanoTime;
		protected long elapsedNanoTime;
		protected World world;
		protected long start;
		protected double ticksPerSecond;
		protected long elapsedTicks;
		protected CommandSender sender;

		public CheckTicks(final long oldNanoTime, final World world,
				final long start, final CommandSender sender) {
			this.oldNanoTime = oldNanoTime;
			this.world = world;
			this.start = start;
			this.sender = sender;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			elapsedNanoTime = System.nanoTime() - oldNanoTime;
			elapsedTicks = world.getFullTime() - start;
			ticksPerSecond = (elapsedTicks * 1000000000.0) / elapsedNanoTime;
			sender.sendMessage("[AdminCmd] TPS: " + ticksPerSecond
					+ " | Ticks elapsed: " + elapsedTicks + " | Nano Time:"
					+ elapsedNanoTime);
		}
	}

	private boolean dontKill(final CommandArgs args, final Entity toKill) {
		boolean dontKill = true;
		if (args.hasFlag('f')) {
			dontKill = (toKill instanceof EntityHuman || toKill instanceof EntityPainting);
		}
		if (args.hasFlag('x')) {
			dontKill = !(toKill instanceof EntityExperienceOrb);
		}
		if (args.hasFlag('i')) {
			dontKill = !(toKill instanceof EntityItem);
		}
		if (args.hasFlag('m')) {
			dontKill = !(toKill instanceof EntityMonster);
		}
		if (args.hasFlag('a')) {
			dontKill = !(toKill instanceof EntityAnimal);
		}
		if (args.hasFlag('n')) {
			dontKill = !(toKill instanceof EntityVillager);
		}
		if (args.hasFlag('c')) {
			dontKill = !(toKill instanceof EntityMinecart);
		}
		if (args.hasFlag('b')) {
			dontKill = !(toKill instanceof EntityBoat);
		}
		if (args.hasFlag('v')) {
			dontKill = !(toKill instanceof EntityMinecart || toKill instanceof EntityBoat);
		}
		return dontKill;
	}
}
