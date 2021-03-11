package com.sasi.coupons.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sasi.coupons.data.UserLoginData;
import com.sasi.coupons.dto.SuccessfulLoginDetails;
import com.sasi.coupons.dto.UserBasicInfo;
import com.sasi.coupons.dto.UserDto;
import com.sasi.coupons.dto.UserLoginRequest;
import com.sasi.coupons.dto.UserUpdateRequest;
import com.sasi.coupons.exceptions.ApplicationException;
import com.sasi.coupons.filters.LoginFilter;
import com.sasi.coupons.logic.UsersController;

@RestController
@RequestMapping("/users")
public class UsersApi {

	@Autowired
	private UsersController usersController;

	// No need to create default Ctor, been created automatically

	@PostMapping("/login")
	public SuccessfulLoginDetails login(@RequestBody UserLoginRequest userLoginRequest) throws ApplicationException {
		return this.usersController.login(userLoginRequest);
	}

	@PostMapping("/logout")
	public void logOut(HttpServletRequest request) throws ApplicationException {
		UserLoginData userLoginData = (UserLoginData) request.getAttribute(LoginFilter.USER_LOGIN_DATA);
		String token = request.getHeader("Authorization");
		this.usersController.logOut(userLoginData, token);
	}

	@PostMapping
	public long createUser(@RequestBody UserDto user) throws ApplicationException {
		return this.usersController.createUser(user);
	}

	@PutMapping
	public void updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request)
			throws ApplicationException {
		UserLoginData userLoginData = (UserLoginData) request.getAttribute(LoginFilter.USER_LOGIN_DATA);
		this.usersController.updateUser(userUpdateRequest, userLoginData);
	}

	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable("id") long id, HttpServletRequest request) throws ApplicationException {
		UserLoginData userLoginData = (UserLoginData) request.getAttribute(LoginFilter.USER_LOGIN_DATA);
		this.usersController.deleteUser(id, userLoginData);
	}

	@GetMapping("/{id}")
	public UserBasicInfo getUserBasicInfo(@PathVariable("id") long id) throws ApplicationException {
		return this.usersController.getUserBasicInfo(id);
	}

	@GetMapping
	public List<UserBasicInfo> getAllUsers() throws ApplicationException {
		return this.usersController.getAllUsers();
	}

}
