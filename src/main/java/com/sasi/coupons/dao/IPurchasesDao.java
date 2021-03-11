package com.sasi.coupons.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.sasi.coupons.dto.PurchasedCoupon;
import com.sasi.coupons.entities.PurchaseEntity;

public interface IPurchasesDao extends CrudRepository<PurchaseEntity, Long> {

	@Query("SELECT new com.sasi.coupons.dto.PurchasedCoupon(p) FROM PurchaseEntity p WHERE p.id = :requestedId")
	public PurchasedCoupon findPurchaseById(@Param("requestedId") long purchaseId);

	@Query("SELECT new com.sasi.coupons.dto.PurchasedCoupon(p) FROM PurchaseEntity p")
	public List<PurchasedCoupon> getAllPurchases();

	@Query("SELECT new com.sasi.coupons.dto.PurchasedCoupon(p) FROM PurchaseEntity p WHERE p.user.id = ?1")
	public List<PurchasedCoupon> getPurchasesByUserId(long userId);

	@Query("SELECT new com.sasi.coupons.dto.PurchasedCoupon(p) FROM PurchaseEntity p WHERE p.coupon.company.id = ?1")
	public List<PurchasedCoupon> getPurchasesByCompanyId(long companyId);

	@Query("SELECT new com.sasi.coupons.dto.PurchasedCoupon(p) FROM PurchaseEntity p WHERE p.coupon.id = ?1")
	public List<PurchasedCoupon> getPurchasesByCouponId(long couponId);

	@Query("SELECT new com.sasi.coupons.dto.PurchasedCoupon(p) FROM PurchaseEntity p WHERE p.user.id = ?1 AND p.coupon.price <= ?2")
	public List<PurchasedCoupon> getPurchasedCouponsByMaxPrice(long userId, float maxPrice);

}
