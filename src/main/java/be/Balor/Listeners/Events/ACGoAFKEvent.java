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
package be.Balor.Listeners.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Prevent that the player will go AFK, if cancelled, the player won't go AFK.
 * 
 * @author Antoine
 * 
 */
public class ACGoAFKEvent extends Event implements Cancellable {
	public enum Reason {
		AUTO, DECIDED;
	}

	private static final HandlerList handlers = new HandlerList();
	private final Reason reason;
	private String message;
	private final Player player;
	private boolean cancelled = false;

	/**
	 * @param who
	 * @param reason
	 * @param message
	 */
	public ACGoAFKEvent(final Player who, final Reason reason,
			final String message) {
		super(true);
		this.player = who;
		this.reason = reason;
		this.message = message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.event.Event#getHandlers()
	 */
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * @return the reason of the AFK
	 */
	public Reason getReason() {
		return reason;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(final String message) {
		this.message = message;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.event.Cancellable#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.event.Cancellable#setCancelled(boolean)
	 */
	@Override
	public void setCancelled(final boolean cancel) {
		this.cancelled = cancel;

	}

}
