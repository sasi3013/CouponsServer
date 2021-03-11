package com.sasi.coupons.enums;

public enum ErrorType {

	GENERAL_ERROR(601, "General error", true),
	LOGIN_FAILED(602, "Login failed. Please try again.", false),
	USERNAME_ALREADY_EXISTS(603, "User name already exists", false),
	INVALID_USERNAME(604, "User name can NOT be null/empty or too short", false),
	INVALID_PASSWORD(605, "Password can NOT be null/empty or too short", false),
	INVALID_USER_TYPE(606, "Must enter user type", false),
	USER_DOES_NOT_EXISTS(607, "User does NOT exists", false),
	COMPANY_NAME_ALREADY_EXISTS(608, "Company name already exists", false),
	INVALID_COMPANY_NAME(609, "Company name can NOT be null/empty or too short", false),
	MUST_ENTER_ADDRESS(610, "Can NOT insert null/empty address", false),
	MUST_ENTER_PHONE_NUMBER(611, "Can NOT insert null/empty phone number", false),
	COMPANY_DOES_NOT_EXISTS(612, "Company does NOT exists", false),
	COUPON_DOES_NOT_EXISTS(613, "Coupon does NOT exists", false),
	INVALID_COUPON_NAME(614, "Coupon name can NOT be null/empty", false),
	COUPON_NAME_ALREADY_EXISTS(615, "Company already have existing coupon with this name", false),
	INVALID_COUPON_PRICE(616, "Coupon price can NOT be negative", false),
	INVALID_COUPON_DATES(617, "Invalid coupon Dates", false),
	INVALID_COUPON_CATEGORY(618, "Must enter coupon category", false),
	INVALID_COUPON_AMOUNT(619, "Coupon amount can NOT be negative", false),
	INVALID_PURCHASE_AMOUNT(620, "Purchase amount must be above 0", false),
	NOT_ENOUGH_COUPONS_LEFT(621, "Not enough coupons left to purchase the amount requested", false),
	PURCHASE_DOES_NOT_EXISTS(622, "Purchase does NOT exists", false),
	COUPON_EXPIRED(623, "The coupon is expired", false),
	CAN_NOT_CANCEL_PURCHASE(624, "You can NOT cancel purchase of expired coupon", false),
	CAN_NOT_ALTER_COUPON(625, "You can NOT alter coupons of another company", false),
	CAN_NOT_ALTER_USER(626, "You can NOT alter another user", false),
	CAN_NOT_ALTER_COMPANY(627, "You can NOT alter another company", false),
	CAN_NOT_ALTER_PURCHASE(628, "You do NOT have the authorization to alter the purchase", false);

	private int errorNumber;
	private String errorMessage;
	private boolean isPrintStackTrace;

	private ErrorType(int errorNumber, String errorMessage) {
		this.errorNumber = errorNumber;
		this.errorMessage = errorMessage;
	}

	private ErrorType(int errorNumber, String errorMessage, boolean isPrintStackTrace) {
		this(errorNumber, errorMessage);
		this.isPrintStackTrace = isPrintStackTrace;
	}

	public int getErrorNumber() {
		return errorNumber;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public boolean isPrintStackTrace() {
		return isPrintStackTrace;
	}

}
