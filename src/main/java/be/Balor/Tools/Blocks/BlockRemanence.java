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
package be.Balor.Tools.Blocks;

import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class BlockRemanence {
	protected Location loc;
	private final int oldType;
	private byte data = 0;
	private boolean useData;

	/**
	 *
	 */
	public BlockRemanence(final Location loc) {
		this.loc = loc;
		final Block b = loc.getWorld().getBlockAt(loc);
		this.oldType = b.getTypeId();
		if ((useData = usesData(oldType))) {
			data = b.getState().getRawData();
		}

	}

	public Block returnToThePast() {
		final Block b = loc.getWorld().getBlockAt(loc);
		if (useData) {
			b.setTypeIdAndData(oldType, data, true);
		} else {
			b.setTypeId(oldType, true);
		}
		return b;
	}

	public void setBlockType(final int type) {
		loc.getWorld().getBlockAt(loc).setTypeId(type, true);
	}

	/**
	 * @return the oldType
	 */
	public int getOldType() {
		return oldType;
	}

	/**
	 * @return the data
	 */
	public byte getData() {
		return data;
	}

	/**
	 * Block types. From worldEdit
	 * 
	 * @author sk89q
	 */
	// TODO: Needs update
	private static boolean usesData(final int id) {
		return id == 6 // Saplings
				|| id == 8 // Water
				|| id == 9 // Water
				|| id == 10 // Lava
				|| id == 11 // Lava
				|| id == 17 // Wood
				|| id == 18 // Leaves
				|| id == 23 // Dispenser
				|| id == 25 // Note Block
				|| id == 26 // Bed
				|| id == 27 // Powered rails
				|| id == 28 // Detector rails
				|| id == 29 // Sticky piston
				|| id == 31 // Tall grass
				|| id == 33 // Piston
				|| id == 34 // Piston extension
				|| id == 35 // Wool
				|| id == 43 // Double slab
				|| id == 44 // Slab
				|| id == 50 // Torch
				|| id == 53 // Wooden stairs
				|| id == 55 // Redstone wire
				|| id == 59 // Crops
				|| id == 60 // Soil
				|| id == 61 // Furnace
				|| id == 62 // Furnace
				|| id == 63 // Sign post
				|| id == 64 // Wooden door
				|| id == 65 // Ladder
				|| id == 66 // Minecart track
				|| id == 67 // Cobblestone stairs
				|| id == 68 // Wall sign
				|| id == 69 // Lever
				|| id == 70 // Stone pressure plate
				|| id == 71 // Iron door
				|| id == 72 // Wooden pressure plate
				|| id == 75 // Redstone torch (off)
				|| id == 76 // Redstone torch (on)
				|| id == 77 // Stone button
				|| id == 78 // Snow tile
				|| id == 81 // Cactus
				|| id == 86 // Pumpkin
				|| id == 91 // Jack-o-lantern
				|| id == 92 // Cake
				|| id == 93 // Redstone repeater (off)
				|| id == 94 // Redstone repeater (on)
				|| id == 96; // Trap door
	}
}
