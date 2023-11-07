package io.azraein.eden.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EdenFileUtils {

	public static final String EDEN_FILE_LOCATION = "X:/AnzelElectronics/Eden/";

	private static final String EMPLOYEE_MAIN_FILE = EDEN_FILE_LOCATION + "employees.emp";

	private static final String[] EDEN_DEFAULT_EMPLOYEE_FOLDERS = { "Documents", "Media", "Projects" };

	public static void createEdenUser(String edenUserName, String edenPassword, String edenEmployeeName) {
		// Create new EdenUser to generate data that's needed. Then set Employee Name
		EdenUser newEdenUser = new EdenUser(edenUserName, edenPassword, EdenAccountType.USER);
		newEdenUser.setUserEmployeeName(edenEmployeeName);

		createEdenUser(newEdenUser);
	}

	public static void createEdenUser(EdenUser newEdenUser) {
		// TODO: Fix the fact that it'll continue appending and appending everytime it's
		// launched even if the data already exists. Got check to see if their already
		// saved.

		// Create User Folder, and then create directories inside.
		String edenUserFolderPath = EDEN_FILE_LOCATION + "employees/" + newEdenUser.getUserName() + "/";
		new File(edenUserFolderPath).mkdirs();

		for (int i = 0; i < EDEN_DEFAULT_EMPLOYEE_FOLDERS.length; i++)
			new File(edenUserFolderPath + EDEN_DEFAULT_EMPLOYEE_FOLDERS[i]).mkdir();

		// Add the user to the Employee Main File
		File employeeMainFile = new File(EMPLOYEE_MAIN_FILE);
		if (!employeeMainFile.exists()) {
			// Employee Main File doesn't exist, time to make it.
			String edenUserAsString = getEdenUserAsString(newEdenUser);

			try {
				FileWriter fw = new FileWriter(employeeMainFile);
				fw.write(edenUserAsString);
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			// It does exist, we're going to overwrite the file and append the new user to
			// it.
			String employeeFile = getFileAsString(employeeMainFile);
			String edenUserAsString = getEdenUserAsString(newEdenUser);
			employeeFile += "\n" + edenUserAsString;

			try {
				FileWriter fw = new FileWriter(employeeMainFile);
				fw.write(employeeFile);
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getFileAsString(File file) {
		StringBuilder fileBuilder = new StringBuilder();

		String line;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			while ((line = br.readLine()) != null) {
				fileBuilder.append(line).append(System.lineSeparator());
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileBuilder.toString();
	}

	public static void saveTextFile(String content, File file) {
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(content);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File[] getUserDirectoryFiles(EdenUser edenUser) {
		File file;

		if (edenUser != null) {
			file = new File(EDEN_FILE_LOCATION + "employees/" + edenUser.getUserName());
			return file.listFiles();
		} else
			return null;
	}

	private static String getEdenUserAsString(EdenUser edenUser) {
		StringBuilder edenUserString = new StringBuilder();

		edenUserString.append("{\n");
		edenUserString.append("	username:" + edenUser.getUserName() + ";\n");
		edenUserString.append("	password:" + edenUser.getUserEncryptedPass() + ";\n");
		edenUserString.append("	userAccountType:" + edenUser.getUserAccountType().ordinal() + ";\n");
		edenUserString.append("	userEmployeeName:" + edenUser.getUserEmployeeName() + ";\n");
		edenUserString.append("	userEmployeeEmail:" + edenUser.getUserEmployeeEmail() + ";\n");
		edenUserString.append("	userEmployeeNumber:" + edenUser.getUserEmployeeNumber() + ";\n");
		edenUserString.append("}");

		return edenUserString.toString();
	}

}
