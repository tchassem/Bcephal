/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.moriset.bcephal.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * 
 * FileUtil This utility class is used to copy files and directories.
 *
 * @author B-Cephal Team
 * @date 24 mars 2014
 *
 */
public class FileUtil {

	public static List<String> listDirectoriesNames(String path) {
		List<String> directories = new ArrayList<String>(0);
		if (path == null)
			return directories;
		File directory = new File(path);
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory())
					directories.add(file.getName());
			}
		}
		return directories;
	}

	public static List<String> listFilesNames(String path) {
		List<String> listefiles = new ArrayList<String>(0);
		if (path == null)
			return listefiles;
		File directory = new File(path);
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile())
					listefiles.add(file.getName());
			}
		}
		return listefiles;
	}

	public static List<File> listFiles(String path) {
		List<File> listefiles = new ArrayList<File>(0);
		if (path == null)
			return listefiles;
		File directory = new File(path);
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile())
					listefiles.add(file);
			}
		}
		return listefiles;
	}

	/**
	 * Copy inStream to outStream
	 * 
	 * @param inStream
	 * @param outStream
	 * @param bufferSize
	 * @throws java.io.IOException
	 */
	public static void copy(InputStream inStream, OutputStream outStream, int bufferSize) throws IOException {
		byte[] buffer = new byte[bufferSize];
		int nbRead;
		while ((nbRead = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, nbRead);
		}
	}

	public static int renameFile(File oldFile, String newName) {
		File newFile = new File(oldFile.getParent(), newName);
		try {
			Files.move(oldFile.toPath(), newFile.toPath());
			return 0;
		} catch (FileAlreadyExistsException ex) {
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 *
	 * @param from
	 * @param to
	 * @throws java.io.IOException
	 */
	public static void copyDirectory(File from, File to) throws IOException {
		if (!to.exists()) {
			to.mkdirs();
		}
		File[] inDir = from.listFiles();
		for (int i = 0; i < inDir.length; i++) {
			File file = inDir[i];
			copy(file, new File(to, file.getName()));
		}
	}

	/**
	 *
	 * @param from
	 * @param to
	 * @throws java.io.IOException
	 */
	public static void copyFile(File from, File to) throws IOException {
		try {
			InputStream inStream = new FileInputStream(from);
			OutputStream outStream = new FileOutputStream(to);
			copy(inStream, outStream, (int) Math.min(from.length(), 4 * 1024));
			inStream.close();
			outStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void copy(File from, File to) throws IOException {
		if (from.isFile()) {
			copyFile(from, to);
		} else if (from.isDirectory()) {
			copyDirectory(from, to);
		} else {
			throw new FileNotFoundException(from.toString() + " does not exist");
		}
	}

	/**
	 * Deletes the given file
	 * 
	 * @param file File to delete
	 * @throws java.io.IOException
	 */
	public static void delete(File file) throws IOException {
		if (file.isFile()) {
			deleteFile(file);
		} else if (file.isDirectory()) {
			deleteDirectory(file);
			file.delete();
		} else {
			throw new FileNotFoundException(file.toString() + " does not exist");
		}
	}

	/**
	 *
	 * @param from
	 * @param to
	 * @throws java.io.IOException
	 */
	public static void deleteDirectory(File file) throws IOException {
		File[] inDir = file.listFiles();
		for (int i = 0; i < inDir.length; i++) {
			File f = inDir[i];
			delete(f);
		}
		file.delete();
	}

	public static void deleteOlderSubDirectory(File file) throws IOException {
		if (!file.exists() || !file.isDirectory())
			return;
		File[] inDir = file.listFiles();
		File older = null;
		for (int i = 0; i < inDir.length; i++) {
			File f = inDir[i];
			if (!f.isDirectory())
				continue;
			if (older == null || older.lastModified() > f.lastModified())
				older = f;
		}
		if (older != null)
			deleteDirectory(older);
	}

	public static File getOlderSubDirectory(File file) throws IOException {
		if (!file.exists() || !file.isDirectory())
			return null;
		File[] inDir = file.listFiles();
		File older = null;
		for (int i = 0; i < inDir.length; i++) {
			File f = inDir[i];
			if (f != null && f.isDirectory() && (older == null || older.lastModified() > f.lastModified())) {
				older = f;
			}
		}
		return older;
	}
	
	public static File getOlderSubFile(File file) throws IOException {
		if (!file.exists() || !file.isDirectory())
			return null;
		File[] inDir = file.listFiles();
		File older = null;
		for (int i = 0; i < inDir.length; i++) {
			File f = inDir[i];
			if (f != null && f.isFile() && (older == null || older.lastModified() > f.lastModified())) {
				older = f;
			}
		}
		return older;
	}
	
	

	/**
	 * Delete file
	 * 
	 * @param file
	 * @throws java.io.IOException
	 */
	public static void deleteFile(File file) throws IOException {
		System.out.println(file.getPath() + " - " + file.delete());
	}

	public static void copyStringPath(String source, String target) throws IOException {
		Path source1 = Paths.get(source);
		Path target1 = Paths.get(target);
		java.nio.file.Files.copy(source1, target1, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
	}

	public static String getFileNameWithoutExtension(String filePath) {
		String name = new java.io.File(filePath).getName();
		int pos = name.lastIndexOf(".");
		if (pos > 0) {
			name = name.substring(0, pos);
			return name;
		}
		return name;
	}

	public static String getFileExtension(String filePath) {
		String name = new java.io.File(filePath).getName();
		int pos = name.lastIndexOf(".");
		if (pos > 0) {
			name = name.substring(pos, name.length());
			return name;
		}
		return null;
	}

	public static void CloseFile(String filePath) {
		FileOutputStream fileToClose = null;
		try {
			fileToClose = new FileOutputStream(filePath);
			fileToClose.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static boolean hasFolderFileNamed(File Folder, String fileName, String extension) {
		File[] inDir = Folder.listFiles();
		for (int i = 0; i < inDir.length; i++) {
			File file = inDir[i];
			if (file.isDirectory())
				continue;
			try {
				if (!hasSameExtension(file.getName(), extension))
					continue;
				if (getFileNameWithoutExtension(file.getName()).equalsIgnoreCase(fileName))
					return true;
			} catch (Exception ex) {

			}
		}
		return false;
	}

	public static boolean hasSameExtension(String filePath, String extension) {
		return getFileExtension(filePath).equalsIgnoreCase(extension);
	}

	public static boolean FileExists(String folder, String fileName) {
		if (fileName == null)
			return false;
		if (new File(folder).isDirectory()) {
			List<String> listFiles = listFilesNames(folder);
			for (int i = 0; i < listFiles.size(); i++) {
				String name = getFileNameWithoutExtension(listFiles.get(i));
				if (name.equals(fileName))
					return true;
			}
		}
		return false;
	}

	public static File createTempFile(String prefix, String suffix) throws IOException {
		String tempPath = getTempPath();
		File tempDir = new File(tempPath);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}
		return File.createTempFile(prefix, suffix, tempDir);
	}

	public static String getTempPath() {
		String os = "os.name";
		os = System.getProperty(os);
		String path = null;
		if (os.toUpperCase().contains("Windows".toUpperCase())) {
			path = System.getenv("ALLUSERSPROFILE");
		} else {
			path = System.getenv("XDG_PUBLICSHARE_DIR");
			if (path == null) {
				// path = System.getenv("XDG_DATA_DIRS");
				Path npath = Paths.get(System.getProperty("user.home"));
				path = FilenameUtils.separatorsToSystem(npath.toString());
			}
			path = FilenameUtils.separatorsToSystem(path.toString());
		}
		path += File.separator + "bcephal";
		File file = new File(path);
		if (!file.exists()) {
			try {

				FileUtils.forceMkdir(file);
			} catch (IOException e) {
				System.out.println("create dir fail");
				e.printStackTrace();
			}
		}
		return path;
	}
}
