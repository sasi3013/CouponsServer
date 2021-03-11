package com.sasi.coupons.dto;

import com.sasi.coupons.entities.UserEntity;
import com.sasi.coupons.enums.UserType;

public class UserBasicInfo {

	private long id;
	private String userName;
	private UserType userType;
	private String firstName;
	private String lastName;
	private Long companyId;

	// Empty Ctor
	public UserBasicInfo() {
	}

	// Ctor for adding new user.
	public UserBasicInfo(String userName, UserType userType, String firstName, String lastName, Long companyId) {
		this.userName = userName;
		this.userType = userType;
		this.firstName = firstName;
		this.lastName = lastName;
		this.companyId = companyId;
	}

	// Ctor for extracting user from DB.
	public UserBasicInfo(long id, String userName, UserType userType, String firstName, String lastName, Long companyId) {
		this(userName, userType, firstName, lastName, companyId);
		this.id = id;
	}

	// Ctor for extracting data from UserEntity.
	public UserBasicInfo(UserEntity userEntity) {
		this.id = userEntity.getId();
		this.userName = userEntity.getUserName();
		this.userType = userEntity.getUserType();
		this.firstName = userEntity.getFirstName();
		this.lastName = userEntity.getLastName();
		this.companyId = getCompanyIdFromUserEntity(userEntity);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getCompanyIdFromUserEntity(UserEntity userEntity) {
		if (userEntity.getCompany() == null) {
			return null;
		}
		return userEntity.getCompany().getId();
	}

	@Override
	public String toString() {
		return "UserBasicInfo [id=" + id + ", userName=" + userName + ", userType=" + userType + ", firstName="
				+ firstName + ", lastName=" + lastName + ", companyId=" + companyId + "]";
	}

}
