package com.sasi.coupons.dto;


import java.sql.Date;

import com.sasi.coupons.entities.CouponEntity;
import com.sasi.coupons.enums.CategoryType;

public class CouponBasicInfo {

	private long id;
	private String name;
	private float price;
	private Date endDate;
	private String companyName;
	private CategoryType category;
	private String image;

	public CouponBasicInfo() {
	}

	public CouponBasicInfo(long id, String name, float price, Date endDate, String companyName, CategoryType category, String image) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.endDate = endDate;
		this.companyName = companyName;
		this.category = category;
		this.image = image;
	}

	public CouponBasicInfo(CouponEntity couponEntity) {
		this.id = couponEntity.getId();
		this.name = couponEntity.getName();
		this.price = couponEntity.getPrice();
		this.endDate = couponEntity.getEndDate();
		this.companyName = couponEntity.getCompany().getName();
		this.category = couponEntity.getCategory();
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

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public CategoryType getCategory() {
		return category;
	}

	public void setCategory(CategoryType category) {
		this.category = category;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "CouponBasicInfo [name=" + name + ", price=" + price + ", endDate=" + endDate + ", companyName=" + companyName + "]";
	}

}
