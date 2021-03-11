package com.sasi.coupons.dto;


import java.sql.Date;

import com.sasi.coupons.entities.CouponEntity;
import com.sasi.coupons.enums.CategoryType;

public class CouponDto {

	private long id;
	private String name;
	private float price;
	private String description;
	private Date startDate;
	private Date endDate;
	private CategoryType category;
	private int amount;
	private long companyId;
	private String companyName;
	private String image;

	// Empty Ctor.
	public CouponDto() {
	}

	// Ctor for adding new coupon.
	public CouponDto(String name, float price, String description, Date startDate, Date endDate, CategoryType category,
			int amount, long companyId, String companyName, String image) {
		this.name = name;
		this.price = price;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.category = category;
		this.amount = amount;
		this.companyId = companyId;
		this.companyName = companyName;
		this.image = image;
	}

	// Ctor for extracting coupon from DB.
	public CouponDto(long id, String name, float price, String description, Date startDate, Date endDate,
			CategoryType category, int amount, long companyId, String companyName, String image) {
		this(name, price, description, startDate, endDate, category, amount, companyId, companyName, image);
		this.id = id;
	}

	// Ctor for extracting data from CouponEntity.
	public CouponDto(CouponEntity couponEntity) {
		this.id = couponEntity.getId();
		this.name = couponEntity.getName();
		this.price = couponEntity.getPrice();
		this.description = couponEntity.getDescription();
		this.startDate = couponEntity.getStartDate();
		this.endDate = couponEntity.getEndDate();
		this.category = couponEntity.getCategory();
		this.amount = couponEntity.getAmount();
		this.companyId = couponEntity.getCompany().getId();
		this.companyName = couponEntity.getCompany().getName();
		this.image = couponEntity.getImage();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public CategoryType getCategory() {
		return category;
	}

	public void setCategory(CategoryType category) {
		this.category = category;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "Coupon [id=" + id + ", name=" + name + ", price=" + price + ", description=" + description
				+ ", startDate=" + startDate + ", endDate=" + endDate + ", category=" + category + ", amount=" + amount
				+ ", companyId=" + companyId + ", image=" + image + "]";
	}

}
