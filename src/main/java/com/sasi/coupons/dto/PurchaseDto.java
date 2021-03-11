package com.sasi.coupons.dto;

import java.sql.Timestamp;

import com.sasi.coupons.entities.PurchaseEntity;

public class PurchaseDto {

	private long id;
	private long couponId;
	private long userId;
	private int amount;
	private Timestamp timestamp;

	// Empty Ctor.
	public PurchaseDto() {
	}

	// Ctor for adding new purchase.
	public PurchaseDto(long couponId, long userId, int amount, Timestamp timestamp) {
		this.couponId = couponId;
		this.userId = userId;
		this.amount = amount;
		this.timestamp = timestamp;
	}

	// Ctor for extracting purchase from DB.
	public PurchaseDto(long id, long couponId, long userId, int amount, Timestamp timestamp) {
		this(couponId, userId, amount, timestamp);
		this.id = id;
	}

	// Ctor for extracting data from PurchaseEntity.
	public PurchaseDto(PurchaseEntity purchaseEntity) {
		this.id = purchaseEntity.getId();
		this.couponId = purchaseEntity.getCoupon().getId();
		this.userId = purchaseEntity.getUser().getId();
		this.amount = purchaseEntity.getAmount();
		this.timestamp = purchaseEntity.getTimestamp();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCouponId() {
		return couponId;
	}

	public void setCouponId(long couponId) {
		this.couponId = couponId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Purchase [id=" + id + ", couponId=" + couponId + ", userId=" + userId + ", amount=" + amount
				+ ", timestamp=" + timestamp + "]";
	}

}
