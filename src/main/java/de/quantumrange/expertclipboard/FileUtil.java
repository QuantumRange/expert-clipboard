package de.quantumrange.expertclipboard;

import java.io.*;

public class FileUtil {

	public static void write(File file, String content) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String read(File file) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			StringBuilder builder = new StringBuilder();

			boolean first = true;
			String line;
			while ((line = reader.readLine()) != null) {
				if (!first) builder.append('\n');
				builder.append(line);
				first = false;
			}

			reader.close();

			return builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
