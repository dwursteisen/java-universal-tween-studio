package aurelienribon.utils.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class FileUtils {
	public static String readFileToString(File file) throws IOException {
		if (!file.exists()) return "";

		char[] buffer = new char[4096];
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		while (reader.read(buffer) > 0) sb.append(buffer);
		reader.close();
		return sb.toString();
	}

	public static void writeStringToFile(String text, File file) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(text, 0, text.length());
		writer.close();
	}
}
