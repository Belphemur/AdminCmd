/*This file is part of AdminCmd.

    AdminCmd is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AdminCmd is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.*/
package be.Balor.World.sql;

import java.util.Map;
import java.util.Set;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;

import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Tools.Warp;
import be.Balor.Tools.Files.ObjectContainer;
import be.Balor.World.ACWorld;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class SQLWorld extends ACWorld {
	static {}
	/**
	 * @param world
	 */
	public SQLWorld(final World world) {
		super(world);
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setSpawn(org.bukkit.Location)
	 */
	@Override
	public void setSpawn(final Location loc) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getSpawn()
	 */
	@Override
	public Location getSpawn() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getDifficulty()
	 */
	@Override
	public Difficulty getDifficulty() throws WorldNotLoaded {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setDifficulty(org.bukkit.Difficulty)
	 */
	@Override
	public void setDifficulty(final Difficulty dif) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#addWarp(java.lang.String,
	 * org.bukkit.Location)
	 */
	@Override
	public void addWarp(final String name, final Location loc) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getWarp(java.lang.String)
	 */
	@Override
	public Warp getWarp(final String name) throws WorldNotLoaded,
			IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getWarpList()
	 */
	@Override
	public Set<String> getWarpList() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#removeWarp(java.lang.String)
	 */
	@Override
	public void removeWarp(final String name) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setInformation(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setInformation(final String info, final Object value) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#removeInformation(java.lang.String)
	 */
	@Override
	public void removeInformation(final String info) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getInformation(java.lang.String)
	 */
	@Override
	public ObjectContainer getInformation(final String info) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getInformations()
	 */
	@Override
	public Map<String, String> getInformations() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#forceSave()
	 */
	@Override
	protected void forceSave() {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setMobLimit(java.lang.String, int)
	 */
	@Override
	public void setMobLimit(final String mob, final int limit) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#removeMobLimit(java.lang.String)
	 */
	@Override
	public boolean removeMobLimit(final String mob) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getMobLimit(java.lang.String)
	 */
	@Override
	public int getMobLimit(final String mob) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getMobLimitList()
	 */
	@Override
	public Set<String> getMobLimitList() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getGroupSpawn(java.lang.String)
	 */
	@Override
	public Location getGroupSpawn(final String group) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setGroupSpawn(java.lang.String,
	 * org.bukkit.Location)
	 */
	@Override
	public void setGroupSpawn(final String group, final Location spawn) {
		// TODO Auto-generated method stub

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getGroupSpawns()
	 */
	@Override
	protected Map<String, Location> getGroupSpawns() {
		// TODO Auto-generated method stub
		return null;
	}

}
