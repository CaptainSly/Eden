package io.azraein.eden;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EdenUtils {

	public static String generateEncryptedPassword(String password) {
		String encryptedPassword = null;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] sha = md.digest(password.getBytes(StandardCharsets.UTF_8));

			BigInteger number = new BigInteger(1, sha);
			StringBuilder hexString = new StringBuilder(number.toString(16));

			while (hexString.length() < 32) {
				hexString.insert(0, '0');
			}

			encryptedPassword = hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return encryptedPassword;
	}

}
