/**
 * 
 */
package com.moriset.bcephal.project.archive;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import com.moriset.bcephal.utils.BcephalException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * ProjectFileUtil
 *
 * @author B-Cephal Team
 * @date 10 août 2017
 *
 */
@Component
@ConfigurationProperties(prefix = "bcephal.project")
@Data
@Slf4j
public class ProjectFileUtil {

	/**
	 * B-CEPHAL project file name
	 */
	public final String PROJECT_FILE_NAME = "project.bcephal";
	public final String WORKSPACE_FILE_NAME = "workspace.bcephal";
	public final String WORKSPACE_DIR_NAME = "workspace";

	private String DataDir;
	private String archiveDir;

	public boolean hasBcephalProjectFile(String projectPath) {
		try {
			return new java.io.File(FilenameUtils.concat(projectPath, PROJECT_FILE_NAME)).exists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Read project code from project file.
	 * 
	 * @param projectPath project file path
	 * @return Project code
	 */
	public String readBcephalProjectFileCode(String projectPath) {
		BufferedReader reader = null;
		try {
			Path path = Paths.get(projectPath, PROJECT_FILE_NAME);			
			Resource resource = new UrlResource(path.toUri());
			if (resource.exists()) {
				java.io.File bcephalFile = resource.getFile();
				reader = new BufferedReader(new FileReader(bcephalFile));
				String line = reader.readLine();
				return line != null ? line : "";
			} else {
				throw new BcephalException("File not found : " + path);
			}
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.debug("Unable to read project code form project file", e);
			e.printStackTrace();
			throw new BcephalException("Unable to read project code form project file.");
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
			}
		}
	}

	public boolean writeBcephalProjectFile(String projectPath, String projectCode, String projectName) throws Exception {
		log.trace("Try to write bcephal project file : {} - {}", projectName, projectPath);
		BufferedWriter writer = null;
		try {
			Path path = Paths.get(projectPath, PROJECT_FILE_NAME);
			Resource resource = new UrlResource(path.toUri());
			writer = new BufferedWriter(new FileWriter(resource.getFile()));
			writer.write(projectCode);
			log.trace("Bcephal project file successfully writed : {} - {}", projectName, resource.getFile().getPath());
			return true;
		} catch (Exception e) {
			log.error("Unable to write bcephal project file : {} - {}", projectName, projectPath, e);
			throw new BcephalException(
					"Unable to write bcephal project file : ".concat(projectName).concat(" - ").concat(projectPath));
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (Exception e) {
			}
		}
	}

	public boolean changeBcephalProjectFile(String projectPath, String code) {
		BufferedWriter writer = null;
		try {
			Path path = Paths.get(projectPath, PROJECT_FILE_NAME);
			Resource resource = new UrlResource(path.toUri());
			writer = new BufferedWriter(new FileWriter(resource.getFile()));
			writer.write(code);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
			}
		}
		return false;
	}

	/**
	 * write file
	 * 
	 * @param fileName
	 * @param sourceExtension
	 * @param content
	 */
	public void writeDeletedFileProject(String path, String fileName) {
		if (path != null) {
			try {
				java.io.File newFile = new java.io.File(path, fileName);
				newFile.createNewFile();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public List<String> listDirectoriesNames(String path) {
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

	private String getWorkspacePath(String path, String userName, boolean createdDir) {
		String workspacePath = Paths.get(path, WORKSPACE_DIR_NAME).toString();
		workspacePath = FilenameUtils.concat(workspacePath, userName);
		if (createdDir) {
			File file = new File(workspacePath);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		return Paths.get(workspacePath, WORKSPACE_FILE_NAME).toString();
	}

	public boolean saveWorkspace(String path, String userName, String projectNames) {
		BufferedWriter writer = null;
		try {
			java.io.File bcephalFile = new java.io.File(getWorkspacePath(path, userName, true));
			writer = new BufferedWriter(new FileWriter(bcephalFile));
			writer.write(projectNames);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
			}
		}
		return false;

	}

	public String getWorkspace(String path, String userName) {
		BufferedInputStream reader = null;
		try {
			java.io.File bcephalFile = new java.io.File(getWorkspacePath(path, userName, false));
			if (bcephalFile.exists()) {
				reader = new BufferedInputStream(new FileInputStream(bcephalFile));
				byte[] line = reader.readAllBytes();
				return new String(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

//	public static void deleteDirectoryLegacyIO(File file) {
//		if (file != null && file.exists()) {
//			File[] list = file.listFiles();
//			if (list != null) {
//				for (File temp : list) {
//					deleteDirectoryLegacyIO(temp);
//				}
//			}
//			if (file.delete()) {
//				log.trace("Delete : %s%n", file);
//			} else {
//				log.trace("Unable to delete file or directory : %s%n", file);
//			}
//		}
//	}
}
