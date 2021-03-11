package com.sasi.coupons.dto;

import com.sasi.coupons.entities.CompanyEntity;

public class CompanyDto {

	private long id;
	private String name;
	private String address;
	private String phoneNumber;

	// Empty Ctor.
	public CompanyDto() {
	}

	// Ctor for adding new company.
	public CompanyDto(String name, String address, String phoneNumber) {
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
	}

	// Ctor for extracting company from DB.
	public CompanyDto(long id, String name, String address, String phoneNumber) {
		this(name, address, phoneNumber);
		this.id = id;
	}

	// Ctor for extracting data from CompanyEntity.
	public CompanyDto(CompanyEntity companyEntity) {
		this.id = companyEntity.getId();
		this.name = companyEntity.getName();
		this.address = companyEntity.getAddress();
		this.phoneNumber = companyEntity.getPhoneNumber();
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return "Company [id=" + id + ", name=" + name + ", address=" + address + ", phoneNumber=" + phoneNumber + "]";
	}

}
