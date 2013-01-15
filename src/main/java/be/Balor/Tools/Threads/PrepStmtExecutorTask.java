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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import be.Balor.Tools.Debug.DebugLog;

/**
 * @author Antoine
 * 
 */
public class PrepStmtExecutorTask implements Runnable {
	private final LinkedBlockingQueue<PreparedStatement> preparedStatments = new LinkedBlockingQueue<PreparedStatement>();
	private final Lock lock = new ReentrantLock();

	public void addPreparedStmt(final PreparedStatement stmt) {
		lock.lock();
		try {
			preparedStatments.add(stmt);
		} finally {
			lock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		lock.lock();
		try {
			DebugLog.INSTANCE.info("Begin Execution of preparedStmt with "
					+ preparedStatments.size() + " stmt(s)");
			while (!preparedStatments.isEmpty()) {
				final PreparedStatement prepStmt = preparedStatments.poll();
				try {
					prepStmt.executeUpdate();
					prepStmt.close();
				} catch (final SQLException e) {
					DebugLog.INSTANCE.log(Level.WARNING,
							"Problem with a prepared statement :", e);
				}
			}
			DebugLog.INSTANCE.info("All PreparedStmt executed.");

		} finally {
			lock.unlock();
		}

	}

}
