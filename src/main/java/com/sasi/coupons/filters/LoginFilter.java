package com.sasi.coupons.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sasi.coupons.data.UserLoginData;
import com.sasi.coupons.logic.CacheController;

@Component
public class LoginFilter implements Filter {

	@Autowired
	private CacheController cacheController;

	public static final String USER_LOGIN_DATA = "userLoginData";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;

		String pageRequested = req.getRequestURL().toString();

		if (!isLoginRequired(pageRequested, req)) {
			// Moving forward to the next filter in the chain.
			chain.doFilter(request, response);
			return;
		}

		String token = req.getHeader("Authorization");

		UserLoginData userLoginData = null;
		if (token != null) {
			userLoginData = (UserLoginData) cacheController.getData(token);
		}

		// Successful Login.
		if (userLoginData != null) {

			// Sends the userLoginData with the request for further validations.
			request.setAttribute(USER_LOGIN_DATA, userLoginData);

			// Moving forward to the next filter in the chain.
			chain.doFilter(request, response);
			return;
		}

		HttpServletResponse res = (HttpServletResponse) response;

		// Status 401 = Unauthorized.
		res.setStatus(401);

	}

	private boolean isLoginRequired(String pageRequested, HttpServletRequest request) {

		// User Login
		if (pageRequested.endsWith("/login")) {
			return false;
		}

		// User Registration
		if (pageRequested.endsWith("/users") && request.getMethod().toString().equals("POST")) {
			return false;
		}

		// Seeing available coupons
		if (pageRequested.contains("/coupons") && request.getMethod().toString().equals("GET")) {
			return false;
		}

		// Seeing available companies
		if (pageRequested.endsWith("/companies") && request.getMethod().toString().equals("GET")) {
			return false;
		}

		return true;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void destroy() {

	}

}
