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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import be.Balor.Tools.Debug.DebugLog;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public final class Downloader {
	/**
	 * Download a file from a url to the given path.
	 * 
	 * @param urlString
	 * @param downloaded
	 * @return the downloaded file.
	 * @throws IOException
	 */
	public static void download(final String urlString, final File downloaded) throws IOException {
		download(urlString, downloaded, false);
	}

	public static void download(final String urlString, final File downloaded,
			final boolean override) throws IOException {
		BufferedOutputStream bout = null;
		BufferedInputStream in = null;
		;
		if (downloaded.getParentFile() != null && !downloaded.getParentFile().exists()) {
			downloaded.getParentFile().mkdirs();
		}
		if (downloaded.exists() && !override)
			return;
		DebugLog.INSTANCE.info("Downloading file : " + urlString);
		try {

			final URL url = new URL(urlString);
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			final int filesize = connection.getContentLength();
			float totalDataRead = 0;
			in = new BufferedInputStream(connection.getInputStream());
			final FileOutputStream fos = new FileOutputStream(downloaded);
			final byte[] data = new byte[1024];
			bout = new BufferedOutputStream(fos, data.length);
			int i = 0;
			int oldPercent = 0;
			while ((i = in.read(data, 0, data.length)) >= 0) {
				totalDataRead = totalDataRead + i;
				bout.write(data, 0, i);
				final int percent = (int) ((totalDataRead * 100) / filesize);
				if (percent - oldPercent >= 10) {
					DebugLog.INSTANCE.info("Downloaded at " + percent + "%");
					oldPercent = percent;
				}
			}
		} finally {
			if (bout != null)
				bout.close();
			if (in != null)
				in.close();
		}
		DebugLog.INSTANCE.info("File " + urlString + " downloaded");
	}
}
