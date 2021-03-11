package com.sasi.coupons.entities;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.sasi.coupons.enums.CategoryType;

@Entity
@Table(name = "coupons")
public class CouponEntity implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "price", nullable = false)
	private float price;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "start_date", nullable = false)
	private Date startDate;

	@Column(name = "end_date", nullable = false)
	private Date endDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private CategoryType category;

	@Column(name = "amount", nullable = false)
	private int amount;

	@ManyToOne
	@JoinColumn(nullable = false)
	private CompanyEntity company;

	@Column(name = "image")
	private String image;

	@OneToMany(mappedBy = "coupon", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private List<PurchaseEntity> purchases;

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

	public CompanyEntity getCompany() {
		return company;
	}

	public void setCompany(CompanyEntity company) {
		this.company = company;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<PurchaseEntity> getPurchases() {
		return purchases;
	}

	public void setPurchases(List<PurchaseEntity> purchases) {
		this.purchases = purchases;
	}

	@Override
	public String toString() {
		return "Coupon [id=" + id + ", name=" + name + ", price=" + price + ", description=" + description
				+ ", startDate=" + startDate + ", endDate=" + endDate + ", category=" + category + ", amount=" + amount
				+ ", companyId=" + company.getId() + ", image=" + image + "]";
	}

}
