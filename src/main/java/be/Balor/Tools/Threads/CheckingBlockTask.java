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
package be.Balor.Tools.Threads;

import java.util.List;
import java.util.concurrent.Semaphore;

import org.bukkit.Material;
import org.bukkit.block.Block;

import be.Balor.Tools.SimplifiedLocation;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class CheckingBlockTask implements Runnable {

	private final Semaphore sema;
	private final List<SimplifiedLocation> okBlocks;
	private final Block block;
	private final int radius, limitY, limitX, limitZ;
	private final List<Material> mat;

	/**
	 * @param sema
	 * @param okBlocks
	 * @param block
	 * @param radius
	 * @param limitY
	 * @param limitX
	 * @param limitZ
	 * @param mat
	 */
	public CheckingBlockTask(final Semaphore sema,
			final List<SimplifiedLocation> okBlocks, final Block block,
			final int radius, final int limitY, final int limitX,
			final int limitZ, final List<Material> mat) {
		super();
		this.sema = sema;
		this.okBlocks = okBlocks;
		this.block = block;
		this.radius = radius;
		this.limitY = limitY;
		this.limitX = limitX;
		this.limitZ = limitZ;
		this.mat = mat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {

			for (int y = block.getY() - radius; y <= limitY; y++) {
				for (int x = block.getX() - radius; x <= limitX; x++) {
					for (int z = block.getZ() - radius; z <= limitZ; z++) {
						if (!mat.contains(Material.getMaterial(block.getWorld()
								.getBlockTypeIdAt(x, y, z)))) {
							continue;
						}
						okBlocks.add(new SimplifiedLocation(block.getWorld(),
								x, y, z));
					}
				}
			}
		} finally {
			sema.release();
		}

	}

}
