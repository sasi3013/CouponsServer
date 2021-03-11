package com.sasi.coupons.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.sasi.coupons.data.UserLoginData;
import com.sasi.coupons.dto.UserBasicInfo;
import com.sasi.coupons.dto.UserDto;
import com.sasi.coupons.entities.UserEntity;

public interface IUsersDao extends CrudRepository<UserEntity, Long> {

	public boolean existsByUserName(String userName);

	@Query("SELECT new com.sasi.coupons.data.UserLoginData(u.id, u.userType, u.company.id) "
			+ "FROM UserEntity u WHERE u.userName = :loginUserName AND u.password = :loginPassword")
	public UserLoginData login(@Param("loginUserName") String uName, @Param("loginPassword") String pWord);

	@Query("SELECT new com.sasi.coupons.dto.UserDto(u) FROM UserEntity u WHERE u.userName = :requestedUserName")
	public UserDto findUserByUserName(@Param("requestedUserName") String uName);

	@Query("SELECT new com.sasi.coupons.dto.UserBasicInfo(u) FROM UserEntity u WHERE u.id = :requestedId")
	public UserBasicInfo findUserById(@Param("requestedId") long userId);

	@Query("SELECT new com.sasi.coupons.dto.UserDto(u) FROM UserEntity u WHERE u.id = :requestedId")
	public UserDto findUserDtoById(@Param("requestedId") long userId);

	@Query("SELECT new com.sasi.coupons.dto.UserBasicInfo(u) FROM UserEntity u")
	public List<UserBasicInfo> getAllUsers();

}
