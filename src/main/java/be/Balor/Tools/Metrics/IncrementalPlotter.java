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
package be.Balor.Tools.Metrics;

import java.util.concurrent.atomic.AtomicInteger;

import be.Balor.Tools.Metrics.Metrics.Plotter;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class IncrementalPlotter extends Plotter {

	private final AtomicInteger value = new AtomicInteger(0);

	/**
	 * @param name
	 */
	public IncrementalPlotter(final String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Metrics.Metrics.Plotter#getValue()
	 */
	@Override
	public int getValue() {
		return value.get();
	}

	public void increment() {
		value.incrementAndGet();
	}

}
