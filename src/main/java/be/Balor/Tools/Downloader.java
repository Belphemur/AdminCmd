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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import be.Balor.Tools.Debug.DebugLog;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public final class Downloader {
	public static void download(final String urlString, final File downloaded) throws IOException {
		BufferedOutputStream bout = null;
		BufferedInputStream in = null;
		HttpURLConnection connection = null;

		if (downloaded.getParentFile() != null && !downloaded.getParentFile().exists()) {
			downloaded.getParentFile().mkdirs();
		}
		if (!checkVersionToDownload(urlString, downloaded)) {
			return;
		}
		if (downloaded.exists()) {
			downloaded.delete();
		}
		if (!exists(urlString)) {
			throw new FileNotFoundException("The remote file " + urlString + " can't be found");
		}
		DebugLog.INSTANCE.info("Downloading file : " + urlString);
		try {

			final URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
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
			if (bout != null) {
				bout.close();
			}
			if (in != null) {
				in.close();
			}
			if (connection != null) {
				connection.disconnect();
			}
		}
		DebugLog.INSTANCE.info("File " + urlString + " downloaded");
	}

	private static final boolean checkVersionToDownload(final String fileUrl, final File download)
			throws IOException {
		final String urlString = fileUrl + ".version";
		final File versionFile = new File(download.getParent(), download.getName() + ".version");
		if (!exists(urlString)) {
			return true;
		}
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(urlString).openConnection();
			final String version = readVersion(connection.getInputStream());
			if (versionFile.exists()) {
				final String curVersion = readVersion(new FileInputStream(versionFile));
				if (curVersion.equals(version) && download.exists()) {
					return false;
				}
			}
			writeVersion(versionFile, version);
			return true;

		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}

	}

	private static boolean exists(final String URLName) {
		HttpURLConnection con = null;
		try {
			HttpURLConnection.setFollowRedirects(false);
			// note : you may also need
			// HttpURLConnection.setInstanceFollowRedirects(false)
			con = (HttpURLConnection) new URL(URLName).openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	private static String readVersion(final InputStream stream) {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		try {
			return reader.readLine();
		} catch (final IOException e) {
			return null;
		} finally {
			try {
				reader.close();
			} catch (final IOException e) {
			}
		}
	}

	private static void writeVersion(final File file, final String version) throws IOException {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(file));
			out.write(version);
			out.flush();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (final IOException e) {
			}
		}
	}
}
