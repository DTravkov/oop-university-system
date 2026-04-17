package utils;

import java.util.Objects;

public class PasswordUtils {
	public static String hashPassword(String password) {
		return String.valueOf(Objects.hash(password));
	}
}
