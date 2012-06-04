/*************************************************************************
 * This file is part of AdminCmd.
 *
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdminCmd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 **************************************************************************/

package be.Balor.Importer;

import java.io.IOException;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public interface IImport {

	/**
	 * Starts the Imports and sends messages to the user about the status.
	 */
	public abstract void initImport();

	public abstract int importUserData();

	public abstract int importWarpPoints();

	/**
	 * Gets the Spawn-point(s) and sets them in the ACWorld
	 */
	public abstract int importSpawnPoints();

	/**
	 * Imports data from normal text-files.
	 * @throws IOException
	 */
	public abstract void importTextFiles() throws IOException;

	/**
	 * Imports all data which is not covered by the above
	 */
	public abstract void importOtherFiles();

}
