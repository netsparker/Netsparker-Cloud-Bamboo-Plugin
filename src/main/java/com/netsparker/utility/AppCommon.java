package com.netsparker.utility;

import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.HttpResponse;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;


public class AppCommon {
	public static List<String> getNames(Class<? extends Enum<?>> e) {
		String[] enumNames =
				Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
		return Arrays.asList(enumNames);
	}

	public static boolean IsUrlValid(String url) {
		String[] schemes = {"http", "https"}; // DEFAULT schemes = "http", "https", "ftp"
		UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_LOCAL_URLS);

		if (urlValidator.isValid(url)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isValidEmailAddress(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}

	public static boolean IsGUIDValid(String guid) {
		try {
			if (guid == null) {
				return false;
			}

			// throws IllegalArgumentException if it's not valid
			UUID.fromString(
					// fixes the guid if it doesn't contain hypens
					guid.replaceFirst(
							"(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
							"$1-$2-$3-$4-$5"));
			return true;
		} catch (IllegalArgumentException exception) {
			return false;
		}
	}

	public static boolean IsNullOrEmpty(String string) {
		return string == null || string.isEmpty();
	}

	public static URL getBaseURL(String url) throws MalformedURLException {
		return new URL(new URL(url), "/");
	}

	public static String parseResponseToString(HttpResponse response) throws IOException {
		return IOUtils.toString(response.getEntity().getContent());
	}

	public static boolean isUrlValid(String url) {
		String[] schemes = {"http", "https"}; // DEFAULT schemes = "http", "https", "ftp"
		UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_LOCAL_URLS);

		if (urlValidator.isValid(url)) {
			return true;
		} else {
			return false;
		}
	}

	public static String mapToQueryString(Map<String, String> map) {
		StringBuilder stringBuilder = new StringBuilder();
		String key;
		String value;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append("&");
			}
			try {
				key = entry.getKey();
				value = entry.getValue();
				stringBuilder.append((key != null ? URLEncoder.encode(key, "UTF-8") : ""));
				stringBuilder.append("=");
				stringBuilder.append(value != null ? URLEncoder.encode(value, "UTF-8") : "");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("This method requires UTF-8 encoding support", e);
			}
		}

		return stringBuilder.toString();
	}
}
