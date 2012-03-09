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

import java.util.Stack;

import be.Balor.Tools.SynchronizedStack;
import be.Balor.Tools.Blocks.BlockRemanence;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ReplaceBlockTask implements Runnable {
	protected Stack<BlockRemanence> blocks = new SynchronizedStack<BlockRemanence>();

	public ReplaceBlockTask() {

	}

	/**
	 * @param blocks
	 */
	public ReplaceBlockTask(final Stack<BlockRemanence> blocks) {
		super();
		while (!blocks.empty())
			this.blocks.add(blocks.pop());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (!blocks.empty())
			blocks.pop().setBlockType(0);
	}
}
