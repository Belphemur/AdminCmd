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
package be.Balor.World;

import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.World;

import be.Balor.Tools.Warp;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;

/**
 * @author Antoine
 * 
 */
public class WorldConvertTask implements Runnable {
	private final AbstractWorldFactory oldFactory, newFactory;
	private final World world;

	/**
	 * @param oldFactory
	 * @param newFactory
	 * @param world
	 */
	public WorldConvertTask(final AbstractWorldFactory oldFactory,
			final AbstractWorldFactory newFactory, final World world) {
		super();
		this.oldFactory = oldFactory;
		this.newFactory = newFactory;
		this.world = world;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		final String worldName = world.getName();
		DebugLog.beginInfo("Begin conversion of " + worldName);
		final ACWorld newWorld = newFactory.createWorld(world);
		final ACWorld oldWorld = oldFactory.createWorld(world);
		ACLogger.info("Converting World : " + worldName);
		DebugLog.addInfo("Convert Difficulty");
		newWorld.setDifficulty(oldWorld.getDifficulty());
		DebugLog.addInfo("Convert Default Spawn");
		newWorld.setSpawn(oldWorld.getSpawn());
		DebugLog.addInfo("Convert Warps");
		for (final String warp : oldWorld.getWarpList()) {
			final Warp w = oldWorld.getWarp(warp);
			newWorld.addWarp(w.name, w.loc);
		}
		DebugLog.addInfo("Convert Informations");
		for (final Entry<String, Object> info : oldWorld.getInformations()
				.entrySet()) {
			newWorld.setInformation(info.getKey(), info.getValue());
		}
		DebugLog.addInfo("Convert MobLimit");
		for (final Entry<String, Integer> ml : oldWorld.getMobLimits()
				.entrySet()) {
			newWorld.setMobLimit(ml.getKey(), ml.getValue());
		}
		DebugLog.addInfo("Convert groupSpawn");
		for (final Entry<String, Location> groupSpawn : oldWorld
				.getGroupSpawns().entrySet()) {
			newWorld.setGroupSpawn(groupSpawn.getKey(), groupSpawn.getValue());
		}
		DebugLog.endInfo();

	}

}
