package be.Balor.Tools.Configuration.File.Unicode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class UnicodeUtil {

	public static final byte[] UTF8_BOMS = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

	public static void saveUTF8File(final File file, final String data, final boolean append)
			throws IOException {
		BufferedWriter bw = null;
		OutputStreamWriter osw = null;

		final FileOutputStream fos = new FileOutputStream(file, append);
		try {
			// write UTF8 BOM mark if file is empty
			if (file.length() < 1) {
				fos.write(UTF8_BOMS);
			}

			osw = new OutputStreamWriter(fos, "UTF-8");
			bw = new BufferedWriter(osw);
			if (data != null) {
				bw.write(data);
			}
		} finally {
			try {
				bw.close();
				fos.close();
			} catch (final Exception ex) {
			}
		}
	}

}
