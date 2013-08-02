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
package be.Balor.Tools.CommandUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Tools.SimplifiedLocation;
import be.Balor.Tools.SynchronizedStack;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Blocks.BlockRemanence;
import be.Balor.Tools.Blocks.IBlockRemanenceFactory;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Threads.CheckingBlockTask;
import be.Balor.Tools.Threads.ReplaceBlockTask;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Antoine
 * 
 */
public final class Blocks {

	/**
	 * 
	 */
	private Blocks() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Check if the block is a fluid.
	 * 
	 * @param loc
	 * @return
	 */
	public static boolean isFluid(final Location loc) {
		final Block b = loc.getWorld().getBlockAt(loc);
		if (b == null) {
			return false;
		}
		return b.getType() == Material.WATER
				|| b.getType() == Material.STATIONARY_WATER
				|| b.getType() == Material.LAVA
				|| b.getType() == Material.STATIONARY_LAVA;
	}

	public static Integer replaceBlockByAir(final CommandSender sender,
			final CommandArgs args, final List<Material> mat,
			final int defaultRadius) {
		if (Users.isPlayer(sender)) {
			int radius = defaultRadius;
			if (args.length >= 1) {
				try {
					radius = args.getInt(0);
				} catch (final NumberFormatException e) {
					if (args.length >= 2) {
						try {
							radius = args.getInt(1);
						} catch (final NumberFormatException e2) {
	
						}
					}
				}
	
			}
			final String playername = ((Player) sender).getName();
			IBlockRemanenceFactory.FACTORY.setPlayerName(playername);
			Stack<BlockRemanence> blocks;
			final Block block = ((Player) sender).getLocation().getBlock();
			if (mat.contains(Material.LAVA) || mat.contains(Material.WATER)) {
				blocks = Blocks.drainFluid(playername, block, radius);
			} else {
				blocks = Blocks.replaceInCuboid(playername, mat, block, radius);
			}
			if (!blocks.isEmpty()) {
				ACHelper.getInstance().addInUndoQueue(playername, blocks);
			}
			return blocks.size();
		}
		return null;
	}

	/**
	 * Because water and lava are fluid, using another algo to "delete"
	 * 
	 * @param block
	 * @param radius
	 * @return
	 */
	public static Stack<BlockRemanence> drainFluid(final String playername,
			final Block block, final int radius) {
		final Stack<BlockRemanence> blocks = new Stack<BlockRemanence>();
		final Stack<SimplifiedLocation> processQueue = new Stack<SimplifiedLocation>();
		BlockRemanence current = null;
		final World w = block.getWorld();
		final Location start = block.getLocation();
		final HashSet<SimplifiedLocation> visited = new HashSet<SimplifiedLocation>();
		final Stack<BlockRemanence> blocksCache = new Stack<BlockRemanence>();
		for (int x = block.getX() - 2; x <= block.getX() + 2; x++) {
			for (int z = block.getZ() - 2; z <= block.getZ() + 2; z++) {
				for (int y = block.getY() - 2; y <= block.getY() + 2; y++) {
					final SimplifiedLocation newPos = new SimplifiedLocation(w,
							x, y, z);
					if (isFluid(newPos) && !visited.contains(newPos)) {
						visited.add(newPos);
						processQueue.push(newPos);
						current = IBlockRemanenceFactory.FACTORY
								.createBlockRemanence(newPos);
						blocks.push(current);
						blocksCache.push(current);
						if (blocksCache.size() == Utils.MAX_BLOCKS) {
							ACPluginManager.getScheduler()
									.scheduleSyncDelayedTask(
											ACHelper.getInstance()
													.getCoreInstance(),
											new ReplaceBlockTask(blocksCache));
						}
					}
	
				}
			}
		}
		while (!processQueue.isEmpty()) {
			final SimplifiedLocation loc = processQueue.pop();
			for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++) {
				for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
					for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
						final SimplifiedLocation newPos = new SimplifiedLocation(
								w, x, y, z);
						if (!visited.contains(newPos) && isFluid(newPos)
								&& start.distance(newPos) < radius) {
							processQueue.push(newPos);
							current = IBlockRemanenceFactory.FACTORY
									.createBlockRemanence(newPos);
							blocks.push(current);
							blocksCache.push(current);
							if (blocksCache.size() == Utils.MAX_BLOCKS) {
								ACPluginManager.getScheduler()
										.scheduleSyncDelayedTask(
												ACHelper.getInstance()
														.getCoreInstance(),
												new ReplaceBlockTask(
														blocksCache));
							}
							visited.add(newPos);
						}
					}
	
				}
			}
		}
		ACPluginManager.getScheduler().scheduleSyncDelayedTask(
				ACHelper.getInstance().getCoreInstance(),
				new ReplaceBlockTask(blocksCache));
		return blocks;
	}

	/**
	 * Replace all the chosen material in the cuboid region.
	 * 
	 * @param mat
	 * @param block
	 * @param radius
	 * @return
	 */
	public static Stack<BlockRemanence> replaceInCuboid(
			final String playername, final List<Material> mat,
			final Block block, final int radius) {
		final Stack<BlockRemanence> blocks = new SynchronizedStack<BlockRemanence>();
		final Stack<BlockRemanence> blocksCache = new SynchronizedStack<BlockRemanence>();
		final int limitX = block.getX() + radius;
		final int limitY = block.getY() + radius;
		final int limitZ = block.getZ() + radius;
		BlockRemanence br = null;
		final Semaphore sema = new Semaphore(0, true);
		final List<SimplifiedLocation> okBlocks = new ArrayList<SimplifiedLocation>(
				50);
		ACPluginManager.scheduleSyncTask(new CheckingBlockTask(sema, okBlocks,
				block, radius, limitY, limitX, limitZ, mat));
		try {
			sema.acquire();
		} catch (final InterruptedException e) {
			DebugLog.INSTANCE.log(Level.SEVERE,
					"Problem with acquiring the semaphore", e);
		}
		for (final SimplifiedLocation loc : okBlocks) {
			br = IBlockRemanenceFactory.FACTORY.createBlockRemanence(loc);
			blocks.push(br);
			blocksCache.push(br);
			if (blocksCache.size() == Utils.MAX_BLOCKS) {
				ACPluginManager.getScheduler().scheduleSyncDelayedTask(
						ACHelper.getInstance().getCoreInstance(),
						new ReplaceBlockTask(blocksCache), 1);
			}
		}
		ACPluginManager.getScheduler().scheduleSyncDelayedTask(
				ACHelper.getInstance().getCoreInstance(),
				new ReplaceBlockTask(blocksCache), 1);
		return blocks;
	}

}
