package com.sasi.coupons.dto;

import java.sql.Timestamp;

import com.sasi.coupons.entities.PurchaseEntity;

public class PurchasedCoupon {

	private long id;
	private long userId;
	private long couponId;
	private String couponName;
	private String couponDescription;
	private String companyName;
	private float price;
	private int amountPurchased;
	private Timestamp timestamp;

	public PurchasedCoupon() {
	}

	public PurchasedCoupon(long id, long userId, long couponId, String couponName, String couponDescription, String companyName,
			float price, int amountPurchased, Timestamp timestamp) {
		this.id = id;
		this.userId = userId;
		this.couponId = couponId;
		this.couponName = couponName;
		this.couponDescription = couponDescription;
		this.companyName = companyName;
		this.price = price;
		this.amountPurchased = amountPurchased;
		this.timestamp = timestamp;
	}

	// Ctor for pulling data from PurchaseEntity.
	public PurchasedCoupon(PurchaseEntity purchaseEntity) {
		this.id = purchaseEntity.getId();
		this.userId = purchaseEntity.getUser().getId();
		this.couponId = purchaseEntity.getCoupon().getId();
		this.couponName = purchaseEntity.getCoupon().getName();
		this.couponDescription = purchaseEntity.getCoupon().getDescription();
		this.companyName = purchaseEntity.getCoupon().getCompany().getName();
		this.price = purchaseEntity.getCoupon().getPrice();
		this.amountPurchased = purchaseEntity.getAmount();
		this.timestamp = purchaseEntity.getTimestamp();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public long getCouponId() {
		return couponId;
	}

	public void setCouponId(long couponId) {
		this.couponId = couponId;
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	public String getCouponDescription() {
		return couponDescription;
	}

	public void setCouponDescription(String couponDescription) {
		this.couponDescription = couponDescription;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getAmountPurchased() {
		return amountPurchased;
	}

	public void setAmountPurchased(int amountPurchased) {
		this.amountPurchased = amountPurchased;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "PurchasedCoupon [id=" + id + ", userId=" + userId + ", CouponName=" + couponName
				+ ", couponDescription=" + couponDescription + ", companyName=" + companyName + ", price=" + price
				+ ", amountPurchased=" + amountPurchased + ", timestamp=" + timestamp + "]";
	}

}
