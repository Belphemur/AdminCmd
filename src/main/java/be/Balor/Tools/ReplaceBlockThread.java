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
package be.Balor.Tools;

import java.util.Stack;

import com.google.common.util.concurrent.Monitor;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ReplaceBlockThread extends Thread {
	private Object threadSync = new Object();
	private boolean stop = false;
	private Monitor monitor = new Monitor();
	private final static int MAX_BLOCKS = 5;
	private Stack<BlockRemanence> blocks = new Stack<BlockRemanence>();
	private Monitor.Guard guard = new Monitor.Guard(monitor) {

		public boolean isSatisfied() {
			if (stop)
				return true;
			return blocks.size() == MAX_BLOCKS;

		}
	};

	public synchronized void stopThread() {
		stop = true;
	}

	public synchronized void addBlockRemanence(BlockRemanence blk) {
		blocks.add(blk);
	}
	public synchronized void flushBlocks()
	{
		while (!blocks.empty())
			blocks.pop().setBlockType(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

		while (true) {
			try {
				monitor.enterWhen(guard);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				flushBlocks();
				synchronized (threadSync) {
					if (stop)
						break;
				}
			} finally {
				monitor.leave();
			}
		}
	}
}
