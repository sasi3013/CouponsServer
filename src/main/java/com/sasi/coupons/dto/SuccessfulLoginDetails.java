package com.sasi.coupons.dto;

import com.sasi.coupons.enums.UserType;

public class SuccessfulLoginDetails {

	private long userId;
	private Long companyId;
	private String token;
	private UserType userType;

	public SuccessfulLoginDetails() {
	}

	public SuccessfulLoginDetails(long userId, Long companyId, String token, UserType userType) {
		this.companyId = companyId;
		this.userId = userId;
		this.token = token;
		this.userType = userType;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

}
