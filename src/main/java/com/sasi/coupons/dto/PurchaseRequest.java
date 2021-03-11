package com.sasi.coupons.dto;

public class PurchaseRequest {

	private long id;
	private long couponId;
	private int amount;

	public PurchaseRequest() {
	}

	public PurchaseRequest(long couponId, int amount) {
		this.couponId = couponId;
		this.amount = amount;
	}

	public PurchaseRequest(long id, long couponId, int amount) {
		this(couponId, amount);
		this.id = id;
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

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "PurchaseRequest [id=" + id + ", couponId=" + couponId + ", amount=" + amount + "]";
	}

}
