/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.moriset.bcephal.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 
 * ZipUnzip This utility class is used to zip and uzip files.
 *
 * @author B-Cephal Team
 * @date 24 mars 2014
 *
 */
public class ZipUnzip {

	/**
	 * Zip a src file and copy it to dst file. If the dst file does not exist,
	 * it is created
	 * 
	 * @param src
	 *            source directory
	 * @param dst
	 *            destination directory.
	 */
	public static void zipFile(java.io.File src, java.io.File dst) throws FileNotFoundException, IOException {
		ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(dst));
		zip.setMethod(ZipOutputStream.DEFLATED);
		zip.setLevel(Deflater.BEST_COMPRESSION);
		zipDirectory(src, zip, "");
		zip.close();
	}

	private static void zipDirectory(java.io.File directory, ZipOutputStream zip, String directoryName)
			throws FileNotFoundException, IOException {
		String[] listFile = directory.list();
		for (int i = 0; i < listFile.length; i++) {
			java.io.File file = Paths.get(directory.getPath() , listFile[i]).toFile();
			if (!file.exists())
				continue;
			if (file.isDirectory()) {
				String dirName = directoryName + "/" + file.getName();
				zipDirectory(file, zip, dirName);
			} else {
				try {
					final int BUFFER_SIZE = 5 * 1024;
					FileInputStream in = new FileInputStream(file);
					byte data[] = new byte[BUFFER_SIZE];
					int count;			

					ZipEntry entry = new ZipEntry(directoryName + java.io.File.separator + file.getName());
					entry.setTime(file.lastModified());
					zip.putNextEntry(entry);
					 while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
						 zip.write(data, 0, count);
					 }
					 in.close();
					zip.closeEntry();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}


	
	/**
	 * Unzip file.
	 * 
	 * @param src
	 * @param dst
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	public static void unZipFile(java.io.File src, java.io.File dst) throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(src);
	  	ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
	  	BufferedOutputStream dest = null;
		ZipEntry entry;
		java.io.File file;
		final int BUFFER_SIZE = 5 * 1024;

		while ((entry = zis.getNextEntry()) != null) {
			file = Paths.get(dst.getPath(), entry.getName().trim()).toFile();
			if (entry.isDirectory()) {
				file.mkdirs();
				continue;
			} else {
				 int count;
				 byte data[] = new byte[BUFFER_SIZE];
				 file.getParentFile().mkdirs();
				 FileOutputStream fos = new FileOutputStream(file);
				 dest = new BufferedOutputStream(fos, BUFFER_SIZE);
				 while ((count = zis.read(data, 0, BUFFER_SIZE)) != -1) {
	                dest.write(data, 0, count);
				 }
				 dest.flush();
				 dest.close();
				 fos.close();
			}
		}
		 zis.close();
		 fis.close();
	 }
	
	/*public static void unZipFile(java.io.File src, java.io.File dst) throws FileNotFoundException, IOException {
	ZipFile zipFile = new ZipFile(src);
	Enumeration<?> entries = zipFile.entries();
	ZipEntry entry;
	java.io.File file;

	while (entries.hasMoreElements()) {
		entry = (ZipEntry) entries.nextElement();
		file = new java.io.File(FilenameUtils.separatorsToSystem(dst.getPath() + java.io.File.separator + entry.getName().trim()));

		if (entry.isDirectory()) {
			file.mkdirs();
			continue;
		} else {
			file.getParentFile().mkdirs();
			file.createNewFile();
			int i = 0;
			byte[] bytes = new byte[1024];
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
			BufferedInputStream in = new BufferedInputStream(zipFile.getInputStream(entry));
			while ((i = in.read(bytes)) != -1) {
				out.write(bytes, 0, i);
			}
			in.close();
			out.flush();
			out.close();
		}
	}
		zipFile.close();
	}
	*/
}
