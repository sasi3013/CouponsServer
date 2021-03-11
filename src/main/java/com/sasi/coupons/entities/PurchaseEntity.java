package com.sasi.coupons.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "purchases")
public class PurchaseEntity implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@ManyToOne
	@JoinColumn(nullable = false)
	private CouponEntity coupon;

	@ManyToOne
	@JoinColumn(nullable = false)
	private UserEntity user;

	@Column(name = "amount", nullable = false)
	private int amount;

	@Column(name = "timestamp", nullable = false)
	private Timestamp timestamp;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public CouponEntity getCoupon() {
		return coupon;
	}

	public void setCoupon(CouponEntity coupon) {
		this.coupon = coupon;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
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
		return "Purchase [id=" + id + ", couponId=" + coupon.getId() + ", userId=" + user.getId() + ", amount=" + amount + ", timestamp="
				+ timestamp + "]";
	}

}
