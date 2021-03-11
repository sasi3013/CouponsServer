package com.sasi.coupons.logic;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.sasi.coupons.dao.IUsersDao;
import com.sasi.coupons.data.UserLoginData;
import com.sasi.coupons.dto.SuccessfulLoginDetails;
import com.sasi.coupons.dto.UserBasicInfo;
import com.sasi.coupons.dto.UserDto;
import com.sasi.coupons.dto.UserLoginRequest;
import com.sasi.coupons.dto.UserUpdateRequest;
import com.sasi.coupons.entities.CompanyEntity;
import com.sasi.coupons.entities.UserEntity;
import com.sasi.coupons.enums.ErrorType;
import com.sasi.coupons.enums.UserType;
import com.sasi.coupons.exceptions.ApplicationException;

@Controller
public class UsersController {

	@Autowired
	private IUsersDao usersDao;

	@Autowired
	private CompaniesController companiesController;

	@Autowired
	private CacheController cacheController;

	private static final String ENCRYPTION_TOKEN_SALT = "ASJHDGDUVTSJGFDS-35468#$&^%!@*";

	// No need to create default Ctor, been created automatically

	/**
	 * Validates the user's login info. In case of successful login, saves a Token
	 * and UserLoginData (sensitive info) in the cache.
	 * 
	 * @param userLoginRequest
	 * @return successfulLoginDetails
	 * @throws ApplicationException
	 */
	public SuccessfulLoginDetails login(UserLoginRequest userLoginRequest) throws ApplicationException {

		// Turn the password to hash code in order to compare it with the hash in the
		// DB.
		userLoginRequest.setPassword(String.valueOf(userLoginRequest.getPassword().hashCode()));

		// Extracting info from userLoginRequest
		String userName = userLoginRequest.getUserName();
		String password = userLoginRequest.getPassword();

		UserLoginData userLoginData;

		try {
			userLoginData = this.usersDao.login(userName, password);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					ErrorType.GENERAL_ERROR.getErrorMessage() + "\n" + userLoginRequest.toString());
		}

		if (userLoginData == null) {
			throw new ApplicationException(ErrorType.LOGIN_FAILED, ErrorType.LOGIN_FAILED.getErrorMessage());
		}

		String token = generateToken(userLoginRequest);

		// Adding the successful login data with the generated token as it's key into
		// the cacheController's hash map.
		this.cacheController.putData(token, userLoginData);
		this.cacheController.putTokenToIdLink(userLoginData.getId(), token);

		SuccessfulLoginDetails successfulLoginDetails = new SuccessfulLoginDetails(userLoginData.getId(),
				userLoginData.getCompanyId(), token, userLoginData.getUserType());

		return successfulLoginDetails;
	}

	/**
	 * Generates an encrypt Token. Used in case of a successful login.
	 * 
	 * @param userLoginRequest
	 * @return generated Token
	 */
	private String generateToken(UserLoginRequest userLoginRequest) {

		// Combination of Strings to build the hash from.
		String text = userLoginRequest.getUserName() + Calendar.getInstance().getTime().toString()
				+ ENCRYPTION_TOKEN_SALT;

		// Turn the long text into hash code (hash code returns as int)
		int token = text.hashCode();

		// Turn the hash code into a String.
		return String.valueOf(token);
	}

	/**
	 * Delete the user's data from the cache on log out.
	 * 
	 * @param id
	 * @param token
	 */
	public void logOut(UserLoginData userLoginData, String token) {
		long id = userLoginData.getId();
		this.cacheController.deleteData(id, token);
	}

	/**
	 * Checks validation for the given user info before continues to usersDao.
	 * 
	 * @param userDto
	 * @return the id number that was generated for the new user and was returned
	 *         from usersDao.
	 * @throws ApplicationException
	 */
	public long createUser(UserDto userDto) throws ApplicationException {

		validateUserInfo(userDto);

		// Saves the password as hash instead of the actual password.
		int hashedPassword = userDto.getPassword().hashCode();
		String password = String.valueOf(hashedPassword);
		userDto.setPassword(password);

		// Customers will fill the registration form without entering the userType.
		if (userDto.getUserType() == null) {
			userDto.setUserType(UserType.CUSTOMER);
		}

		boolean isExists;

		try {
			isExists = this.usersDao.existsByUserName(userDto.getUserName());

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					ErrorType.GENERAL_ERROR.getErrorMessage() + "\n" + userDto.toString());
		}

		if (isExists) {
			throw new ApplicationException(ErrorType.USERNAME_ALREADY_EXISTS,
					ErrorType.USERNAME_ALREADY_EXISTS.getErrorMessage());
		}

		UserEntity userToSave = createUserEntityFromUserDto(userDto);

		try {
			this.usersDao.save(userToSave);

			return userToSave.getId();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					ErrorType.GENERAL_ERROR.getErrorMessage() + "\n" + userToSave.toString());
		}
	}

	/**
	 * Checks validation for the given user info before continues to usersDao.
	 * 
	 * @param userUpdateRequest
	 * @param userLoginData
	 * @throws ApplicationException
	 */
	public void updateUser(UserUpdateRequest userUpdateRequest, UserLoginData userLoginData)
			throws ApplicationException {

		UserDto userForUpdateRequest = createUserDtoForUpdateRequest(userUpdateRequest, userLoginData);

		validateUserInfo(userForUpdateRequest);

		UserDto userForValidation;

		try {
			userForValidation = this.usersDao.findUserByUserName(userForUpdateRequest.getUserName());

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					ErrorType.GENERAL_ERROR.getErrorMessage() + "\n" + userForUpdateRequest.toString());
		}

		// Checks if user name exist, but it's not the name of the user we want to
		// update.
		if (userForValidation != null && userForValidation.getId() != userForUpdateRequest.getId()) {
			throw new ApplicationException(ErrorType.USERNAME_ALREADY_EXISTS,
					ErrorType.USERNAME_ALREADY_EXISTS.getErrorMessage());
		}

		UserEntity userToSave = createUserEntityFromUserDto(userForUpdateRequest);

		// Save the password as hash instead of the actual password.
		int hashedPassword = userToSave.getPassword().hashCode();
		String password = String.valueOf(hashedPassword);
		userToSave.setPassword(password);

		try {
			this.usersDao.save(userToSave);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					ErrorType.GENERAL_ERROR.getErrorMessage() + "\n" + userToSave.toString());
		}
	}

	/**
	 * Delete all purchases of the user before delete user (Defined as cascade).
	 * 
	 * @param id
	 * @param userLoginData
	 * @throws ApplicationException
	 */
	public void deleteUser(long id, UserLoginData userLoginData) throws ApplicationException {

		boolean isExists = isUserExists(id);

		if (!isExists) {
			throw new ApplicationException(ErrorType.USER_DOES_NOT_EXISTS,
					ErrorType.USER_DOES_NOT_EXISTS.getErrorMessage());
		}

		if (userLoginData.getUserType() != UserType.ADMIN && userLoginData.getId() != id) {
			throw new ApplicationException(ErrorType.CAN_NOT_ALTER_USER,
					ErrorType.CAN_NOT_ALTER_USER.getErrorMessage());
		}

		try {
			this.usersDao.deleteById(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					ErrorType.GENERAL_ERROR.getErrorMessage() + "\nUser ID: " + id);
		}
		// Delete the user's data from the cache
		this.cacheController.deleteAllUserData(id);
	}

	/**
	 * Checks if user with given id exists, and then get user as UserBasicInfo.
	 * 
	 * @param id
	 * @return UserBasicInfo with the given id number.
	 * @throws ApplicationException
	 */
	public UserBasicInfo getUserBasicInfo(long id) throws ApplicationException {

		// Extra checking to avoid SQLException
		boolean isExists = isUserExists(id);

		if (!isExists) {
			throw new ApplicationException(ErrorType.USER_DOES_NOT_EXISTS,
					ErrorType.USER_DOES_NOT_EXISTS.getErrorMessage());
		}

		try {
			return this.usersDao.findUserById(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					ErrorType.GENERAL_ERROR.getErrorMessage() + "\nUser ID: " + id);
		}
	}

	public UserDto getUser(long id) throws ApplicationException {

		// Extra checking to avoid SQLException
		boolean isExists = isUserExists(id);

		if (!isExists) {
			throw new ApplicationException(ErrorType.USER_DOES_NOT_EXISTS,
					ErrorType.USER_DOES_NOT_EXISTS.getErrorMessage());
		}

		try {
			return this.usersDao.findUserDtoById(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					ErrorType.GENERAL_ERROR.getErrorMessage() + "\nUser ID: " + id);
		}
	}

	/**
	 * Checks if user with given id exists, and then get user as UserEntity. Uses
	 * for Validations.
	 * 
	 * @param id
	 * @return the userEntity with the given id number.
	 * @throws ApplicationException
	 */
	public UserEntity getUserEntity(long id) throws ApplicationException {

		// Extra checking to avoid SQLException
		boolean isExists = isUserExists(id);

		if (!isExists) {
			throw new ApplicationException(ErrorType.USER_DOES_NOT_EXISTS,
					ErrorType.USER_DOES_NOT_EXISTS.getErrorMessage());
		}

		try {
			return this.usersDao.findById(id).get();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					ErrorType.GENERAL_ERROR.getErrorMessage() + "\nUser ID: " + id);
		}
	}

	/**
	 * No validation to check, continue to usersDao.
	 * 
	 * @return List of UserBasicInfo with all the users from DB.
	 * @throws ApplicationException
	 */
	public List<UserBasicInfo> getAllUsers() throws ApplicationException {

		try {
			return this.usersDao.getAllUsers();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage());
		}
	}

	/**
	 * Checks validation for the given user info before continues to usersDao to
	 * create or update a user.
	 * 
	 * @param userDto
	 * @throws ApplicationException
	 */
	private void validateUserInfo(UserDto userDto) throws ApplicationException {
		if (userDto.getUserName() == null) {
			throw new ApplicationException(ErrorType.INVALID_USERNAME, ErrorType.INVALID_USERNAME.getErrorMessage());
		}

		if (userDto.getUserName().isEmpty()) {
			throw new ApplicationException(ErrorType.INVALID_USERNAME, ErrorType.INVALID_USERNAME.getErrorMessage());
		}

		if (userDto.getUserName().length() < 2) {
			throw new ApplicationException(ErrorType.INVALID_USERNAME, ErrorType.INVALID_USERNAME.getErrorMessage());
		}

		if (userDto.getPassword() == null) {
			throw new ApplicationException(ErrorType.INVALID_PASSWORD, ErrorType.INVALID_PASSWORD.getErrorMessage());
		}

		if (userDto.getPassword().length() < 4) {
			throw new ApplicationException(ErrorType.INVALID_PASSWORD, ErrorType.INVALID_PASSWORD.getErrorMessage());
		}
	}

	/**
	 * Create a UserEntity object from the UserDto object we received with the
	 * request from the client, in order to send it to the DB.
	 * 
	 * @param userDto
	 * @return userEntity
	 * @throws ApplicationException
	 */
	private UserEntity createUserEntityFromUserDto(UserDto userDto) throws ApplicationException {

		UserEntity userEntity = new UserEntity();

		userEntity.setId(userDto.getId());
		userEntity.setUserName(userDto.getUserName());
		userEntity.setPassword(userDto.getPassword());
		userEntity.setUserType(userDto.getUserType());
		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());

		CompanyEntity companyEntity;
		if (userDto.getCompanyId() == null) {
			companyEntity = null;
		} else {

			companyEntity = this.companiesController.getCompanyEntity(userDto.getCompanyId());
		}

		userEntity.setCompany(companyEntity);

		return userEntity;
	}

	/**
	 * Create a UserDto object from the UserUpdateRequest object we received with
	 * the request from the client. Get the "sensitive" info from the cache instead
	 * of getting it from the user.
	 * 
	 * @param userUpdateRequest
	 * @param userLoginData
	 * @return userDto
	 */
	private UserDto createUserDtoForUpdateRequest(UserUpdateRequest userUpdateRequest, UserLoginData userLoginData) {

		UserDto userDto = new UserDto();

		userDto.setId(userLoginData.getId());
		userDto.setUserType(userLoginData.getUserType());
		userDto.setCompanyId(userLoginData.getCompanyId());
		userDto.setUserName(userUpdateRequest.getUserName());
		userDto.setPassword(userUpdateRequest.getPassword());
		userDto.setFirstName(userUpdateRequest.getFirstName());
		userDto.setLastName(userUpdateRequest.getLastName());

		return userDto;
	}

	/**
	 * Checks if a user with given id exists in the DB.
	 * 
	 * @param id
	 * @return true if user exists or false if user does not exists.
	 * @throws ApplicationException
	 */
	private boolean isUserExists(long id) throws ApplicationException {

		try {
			return this.usersDao.existsById(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					ErrorType.GENERAL_ERROR.getErrorMessage() + "\nUser ID: " + id);
		}
	}

}
