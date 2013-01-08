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
package be.Balor.Player;

import java.util.Map.Entry;

import org.bukkit.Location;

import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Tools.Type;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;

/**
 * @author Antoine
 * 
 */
public class PlayerConvertTask implements Runnable {

	private final IPlayerFactory newFactory;
	private final ACPlayer oldPlayer;

	/**
	 * @param oldFactory
	 * @param newFactory
	 * @param name
	 */
	public PlayerConvertTask(final IPlayerFactory oldFactory,
			final IPlayerFactory newFactory, final String name) {
		super();
		this.newFactory = newFactory;
		this.oldPlayer = oldFactory.createPlayer(name);
	}

	public PlayerConvertTask(final IPlayerFactory newFactory,
			final ACPlayer oldPlayer) {
		super();
		this.newFactory = newFactory;
		this.oldPlayer = oldPlayer;
		this.oldPlayer.forceSave();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		final String name = oldPlayer.getName();
		ACLogger.info("Convert player : " + name);
		DebugLog.INSTANCE.info("Convert player : " + name);
		newFactory.addExistingPlayer(name);
		final ACPlayer newPlayer = newFactory.createPlayer(name);
		DebugLog.INSTANCE.info("Convert lastLoc");
		newPlayer.setLastLocation(oldPlayer.getLastLocation());
		DebugLog.INSTANCE.info("Convert presentation");
		newPlayer.setPresentation(oldPlayer.getPresentation());
		DebugLog.INSTANCE.info("Convert homes");
		for (final String home : oldPlayer.getHomeList()) {
			try {
				final Location homeLoc = oldPlayer.getHome(home);
				if (homeLoc == null) {
					ACLogger.warning("The home "
							+ home
							+ " of player "
							+ name
							+ " has not been converted because the world is not loaded.");
				} else {
					newPlayer.setHome(home, homeLoc);
				}
			} catch (final WorldNotLoaded e) {
				ACLogger.warning("The home " + home + " of player " + name
						+ " has not been converted because the world "
						+ e.getMessage() + " don't exist anymore");
			} catch (final Exception e) {
				ACLogger.warning("The home " + home + " of player " + name
						+ " has not been converted because : " + e.getMessage());
			}
		}
		DebugLog.INSTANCE.info("Convert Powers");
		for (final Entry<Type, Object> entry : oldPlayer.getPowers().entrySet()) {
			newPlayer.setPower(entry.getKey(), entry.getValue());
		}
		DebugLog.INSTANCE.info("Convert Custom Powers");
		for (final Entry<String, Object> entry : oldPlayer.getCustomPowers()
				.entrySet()) {
			newPlayer.setCustomPower(entry.getKey(), entry.getValue());
		}
		DebugLog.INSTANCE.info("Convert Infos");
		for (final String info : oldPlayer.getInformationsList()) {
			if (info.equals("lastLoc") || info.equals("presentation")) {
				continue;
			}
			newPlayer.setInformation(info, oldPlayer.getInformation(info)
					.getObj());
		}
		DebugLog.INSTANCE.info("Convert Kit");
		for (final String kit : oldPlayer.getKitUseList()) {
			newPlayer.setLastKitUse(kit, oldPlayer.getLastKitUse(kit));
		}
		newPlayer.forceSave();

	}
}
