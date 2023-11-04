package io.azraein.eden.logic;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Eden {

	private Map<Integer, EdenUser> edenUsers;

	private final ObjectProperty<EdenUser> currentEdenUser = new SimpleObjectProperty<>(null);

	public Eden() {
		edenUsers = new HashMap<>();

		{
			// Create Dummy Users
			EdenUser userTed = new EdenUser("tedNug15", "sunshine95", EdenAccountType.USER);
			EdenUser userMax = new EdenUser("maxAngl12", "aprilshowers23", EdenAccountType.USER);
			EdenUser userJames = new EdenUser("jamesAldr87", "freeBeers821", EdenAccountType.USER);
			EdenUser userSara = new EdenUser("saraCass64", "farts$#!t", EdenAccountType.USER);
			EdenUser userAlice = new EdenUser("aliceFar42", "tacobell4lyfe", EdenAccountType.USER);
			EdenUser userBill = new EdenUser("billClasp62", "dontuseme", EdenAccountType.USER);

			
			
			// Put them in lists
			edenUsers.put(userTed.getUserEmployeeNumber(), userTed);
			edenUsers.put(userMax.getUserEmployeeNumber(), userMax);
			edenUsers.put(userJames.getUserEmployeeNumber(), userJames);
			edenUsers.put(userSara.getUserEmployeeNumber(), userSara);
			edenUsers.put(userAlice.getUserEmployeeNumber(), userAlice);
			edenUsers.put(userBill.getUserEmployeeNumber(), userBill);
		}
		
		for (EdenUser user : edenUsers.values())
			EdenFileUtils.createEdenUser(user);

	}

	public void setCurrentLoggedInUser(EdenUser edenUser) {
		currentEdenUser.set(edenUser);
	}

	public EdenUser getEdenUser(int edenUserEmployeeNumber) {
		return edenUsers.get(edenUserEmployeeNumber);
	}

	public EdenUser getEdenUserFromUsername(String username) {
		for (EdenUser user : edenUsers.values()) {
			if (user.getUserName().equals(username))
				return user;
		}

		return null;
	}

	public EdenUser getCurrentLoggedInUser() {
		return currentEdenUser.get();
	}

	public Map<Integer, EdenUser> getEdenUsers() {
		return edenUsers;
	}

	public ObjectProperty<EdenUser> getCurrentEdenUser() {
		return currentEdenUser;
	}

}
