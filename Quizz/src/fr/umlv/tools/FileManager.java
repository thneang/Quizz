package fr.umlv.tools;
import java.io.File;


public class FileManager {
	
	/**
	 * Return true if the name respect convention .md
	 * @param fileName The name f the file
	 * @return boolean
	 */
	public static boolean isTxtFileName(String fileName) {
		int sizeFileName = fileName.length();
		return (!fileName.startsWith("."))
				&& fileName.substring(sizeFileName - 4).equals(".txt");
	}

	/**
	 * Return if the file is a md file and nor a directory witch end with .md
	 * @param theFile The name of the file
	 * @return boolean
	 */
	public static boolean isTxtFile(File theFile) {

		return theFile.isFile() && isTxtFileName(theFile.getName());

	}
}
