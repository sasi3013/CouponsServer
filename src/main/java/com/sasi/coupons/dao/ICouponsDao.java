package com.sasi.coupons.dao;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.sasi.coupons.dto.CouponBasicInfo;
import com.sasi.coupons.dto.CouponDto;
import com.sasi.coupons.entities.CouponEntity;
import com.sasi.coupons.enums.CategoryType;

public interface ICouponsDao extends CrudRepository<CouponEntity, Long> {

	@Query("SELECT new com.sasi.coupons.dto.CouponDto(c) FROM CouponEntity c WHERE c.id = :requestedId")
	public CouponDto findCouponById(@Param("requestedId") long couponId);

	@Query("SELECT new com.sasi.coupons.dto.CouponBasicInfo(c) FROM CouponEntity c")
	public List<CouponBasicInfo> getAllCoupons();

	@Query("SELECT new com.sasi.coupons.dto.CouponBasicInfo(c) FROM CouponEntity c WHERE c.company.id = ?1")
	public List<CouponBasicInfo> getCouponsByCompanyId(long companyId);

	@Query("SELECT new com.sasi.coupons.dto.CouponBasicInfo(c) FROM CouponEntity c WHERE c.category = ?1")
	public List<CouponBasicInfo> getCouponsByCategory(CategoryType category);

	@Query("SELECT new com.sasi.coupons.dto.CouponDto(c) FROM CouponEntity c WHERE c.name = ?1 AND c.company.id = ?2")
	public CouponDto getCouponByNameAndCompanyId(String name, long companyId);

	@Transactional
	@Modifying
//	@Query("DELETE FROM CouponEntity c WHERE c.endDate < current_time()")
	public void deleteAllByEndDateBefore(Date currentTime);
	
	@Query("SELECT endDate FROM CouponEntity c WHERE c.id = :requestedId")
	public Date getEndDateFromCoupon(@Param("requestedId") long couponId);

}
