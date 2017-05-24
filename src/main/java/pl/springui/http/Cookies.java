package pl.springui.http;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Cookies {

	/**
	 * As long as a request may take
	 */
	private static final int MAX_COOKIE_AGE = 60 * 60;

	public static Cookie getCookie(HttpServletRequest req, String name) {
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return cookie;
				}
			}
		}
		return null;
	}

	public static void set(HttpServletResponse response, String name, String value) {
		Cookie userCookie = new Cookie(name, value);
		userCookie.setMaxAge(MAX_COOKIE_AGE);
		// need to be visible for JS
		userCookie.setSecure(false);
		// userCookie.setMaxAge(60 * 60 * 24 * 365); // Store cookie for 1 year
		response.addCookie(userCookie);

	}
}
