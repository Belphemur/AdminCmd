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
package be.Balor.Tools.Configuration;

import java.io.File;

import org.yaml.snakeyaml.scanner.ScannerException;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class LoadScannerException extends ScannerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2560017720685007939L;
	private final File file;

	/**
	 * 
	 */
	public LoadScannerException(ScannerException ex, File file) {
		super(ex.getContext(), ex.getContextMark(), ex.getProblem(), ex.getProblemMark(), null);
		this.file = file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.yaml.snakeyaml.error.MarkedYAMLException#toString()
	 */
	@Override
	public String toString() {
		StringBuilder lines = new StringBuilder();
		lines.append("File : " + file.toString());
		lines.append("\n");
		lines.append(super.toString());
		return lines.toString();
	}

}
