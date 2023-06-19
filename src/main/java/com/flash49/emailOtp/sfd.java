package com.flash49.emailOtp;

public class sfd {
	public static void main(String[] args) {
		String text = "918057393132";
		System.out.println(trimZeros(text));
	}

	public static String trimZeros(String number) {

		// trim zeros at left
		number = number.replaceAll("^0*", "");

		// if the length is < 10, returns an empty string
		if (number.length() < 10) {
			return "";
		}

		// if the length is > 10, trims any number at left
		if (number.length() > 10) {
			number = number.replaceAll(".*(\\d{10})$", "$1");
		}

		return number;
	}
}
