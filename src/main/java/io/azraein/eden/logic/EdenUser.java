package io.azraein.eden.logic;

import java.util.Random;

import io.azraein.eden.EdenUtils;

public class EdenUser {

	// User Information
	private String userName;
	private String userEncryptedPass;
	private EdenAccountType userAccountType;

	// Employee Information
	private String userEmployeeName;
	private final String userEmployeeEmail;
	private final int userEmployeeNumber;

	// Eden Desktop Information
	// TODO: Need different types of info for the desktop

	// Eden Messaging Information
	// TODO: need different types of info for the messaging system

	public EdenUser(String userName, String userPassword, EdenAccountType userAccountType) {
		this.userName = userName;
		this.userEncryptedPass = EdenUtils.generateEncryptedPassword(userPassword);
		this.userAccountType = userAccountType;
		this.userEmployeeNumber = generateEmployeeNumber();
		this.userEmployeeEmail = userName + "@anzelElectronics.org";

	}
	
	public EdenUser(String userName, String userPassword, EdenAccountType userAccountType, int userEmployeeNumber, String userEmployeeEmail, String userEmployeeName) {
		this.userName = userName;
		this.userEncryptedPass = userPassword;
		this.userAccountType = userAccountType;
		this.userEmployeeNumber = userEmployeeNumber;
		this.userEmployeeEmail = userEmployeeEmail;
		this.userEmployeeName = userEmployeeName;
	}

	private int generateEmployeeNumber() {
		Random rand = new Random();
		int min = 10000;
		int max = 99999;

		int employeeNumber = rand.nextInt((max - min) + 1) + min;
		return employeeNumber;
	}

	public void setUserEmployeeName(String userEmployeeName) {
		this.userEmployeeName = userEmployeeName;
	}

	public EdenAccountType getUserAccountType() {
		return userAccountType;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserEncryptedPass() {
		return userEncryptedPass;
	}

	public String getUserEmployeeEmail() {
		return userEmployeeEmail;
	}

	public String getUserEmployeeName() {
		return userEmployeeName;
	}

	public int getUserEmployeeNumber() {
		return userEmployeeNumber;
	}

}
