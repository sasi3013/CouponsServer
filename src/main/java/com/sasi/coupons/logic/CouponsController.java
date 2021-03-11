package com.sasi.coupons.logic;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.sasi.coupons.dao.ICouponsDao;
import com.sasi.coupons.data.UserLoginData;
import com.sasi.coupons.dto.CouponBasicInfo;
import com.sasi.coupons.dto.CouponDto;
import com.sasi.coupons.entities.CompanyEntity;
import com.sasi.coupons.entities.CouponEntity;
import com.sasi.coupons.enums.CategoryType;
import com.sasi.coupons.enums.ErrorType;
import com.sasi.coupons.enums.UserType;
import com.sasi.coupons.exceptions.ApplicationException;

@Controller
public class CouponsController {

	@Autowired
	private ICouponsDao couponsDao;

	@Autowired
	private CompaniesController companiesController;

	// No need to create default Ctor, been created automatically

	/**
	 * Checks validation for the given coupon info before continues to couponsDao.
	 * Will be available only for userType COMPANY.
	 * 
	 * @param couponDto
	 * @param userLoginData
	 * @return the id number that was generated for the new coupon and was returned from couponsDao.
	 * @throws ApplicationException
	 */
	public long createCoupon(CouponDto couponDto, UserLoginData userLoginData) throws ApplicationException {

		// Take the company id from the cache, so user can create coupons only for his company.
		couponDto.setCompanyId(userLoginData.getCompanyId());

		validateCouponInfo(couponDto);

		CouponEntity couponToSave = createCouponEntityFromCouponDto(couponDto);

		try {
			this.couponsDao.save(couponToSave);	

			return couponToSave.getId();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\n" + couponToSave.toString());
		}
	}

	/**
	 * Checks validation for the given coupon info before continues to CouponsDao.
	 * Will be available only for userType COMPANY.
	 * Also, can be called when a purchase been made, update or cancel, in order to update the coupons amount.
	 * 
	 * @param couponDto
	 * @param userLoginData
	 * @throws ApplicationException
	 */
	public void updateCoupon(CouponDto couponDto, UserLoginData userLoginData) throws ApplicationException {

		// In case the method been called when a purchase been made or cancelled, in order to update the coupons amount, it will NOT use the cache.
		// UserLoginData of Admin or Customer will get here only by creating, updating or canceling purchase.
		if (userLoginData.getUserType() == UserType.COMPANY) {

			// Compare the company id of the coupon with the company id of the user from the cache, so user can alter coupons only for his company.
			if (couponDto.getCompanyId() != userLoginData.getCompanyId()) {
				throw new ApplicationException(ErrorType.CAN_NOT_ALTER_COUPON, ErrorType.CAN_NOT_ALTER_COUPON.getErrorMessage());
			}
		}

		validateCouponInfo(couponDto);

		CouponEntity couponToSave = createCouponEntityFromCouponDto(couponDto);

		try {
			this.couponsDao.save(couponToSave);	

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\n" + couponToSave.toString());
		}
	}

	/**
	 * Checks if user is authorized to delete coupon (only userType ADMIN or COMPANY).
	 * Delete all purchases of the coupon before delete coupon (Defined as cascade).
	 * 
	 * @param id
	 * @param userLoginData
	 * @throws ApplicationException
	 */
	public void deleteCoupon(long id, UserLoginData userLoginData) throws ApplicationException {

		// Creating a Coupon object for multiple actions.
		CouponDto couponToDelete;

		try {
			couponToDelete = this.couponsDao.findCouponById(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nCoupon ID: " + id);
		}

		if (couponToDelete == null) {
			throw new ApplicationException(ErrorType.COUPON_DOES_NOT_EXISTS, ErrorType.COUPON_DOES_NOT_EXISTS.getErrorMessage());
		}

		// A coupon can be deleted only by Admin or an employee of the coupon's company.
		if (userLoginData.getUserType() != UserType.ADMIN && couponToDelete.getCompanyId() != userLoginData.getCompanyId()) {
			throw new ApplicationException(ErrorType.CAN_NOT_ALTER_COUPON, ErrorType.CAN_NOT_ALTER_COUPON.getErrorMessage());
		}

		try {
			this.couponsDao.deleteById(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nCoupon ID: " + id);
		}
	}

	/**
	 * Delete all purchases of the coupons before delete coupons (Defined as cascade).
	 * Uses for TimerTask (scheduled by IninClass).
	 * 
	 * @throws ApplicationException
	 */
	public void deleteAllExpiredCoupons() throws ApplicationException {
		
		Date currentTime = new Date(System.currentTimeMillis());

		try {
			this.couponsDao.deleteAllByEndDateBefore(currentTime);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage());
		}
	}

	/**
	 * Checks if coupon with given id exists, and then get coupon as CouponDto.
	 * 
	 * @param id
	 * @return CouponDto with the given id number.
	 * @throws ApplicationException
	 */
	public CouponDto getCouponDto(long id) throws ApplicationException {

		// Extra checking to avoid SQLException
		boolean isExists = isCouponExists(id);

		if (!isExists) {
			throw new ApplicationException(ErrorType.COUPON_DOES_NOT_EXISTS, ErrorType.COUPON_DOES_NOT_EXISTS.getErrorMessage());
		}

		try {
			return this.couponsDao.findCouponById(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nCoupon ID: " + id);
		}
	}

	/**
	 * Checks if coupon with given id exists, and then get coupon as CouponEntity.
	 * Uses for Validations.
	 * 
	 * @param id
	 * @return CouponEntity with the given id number.
	 * @throws ApplicationException
	 */
	public CouponEntity getCouponEntity(long id) throws ApplicationException {

		// Extra checking to avoid SQLException
		boolean isExists = isCouponExists(id);

		if (!isExists) {
			throw new ApplicationException(ErrorType.COUPON_DOES_NOT_EXISTS, ErrorType.COUPON_DOES_NOT_EXISTS.getErrorMessage());
		}

		try {
			return this.couponsDao.findById(id).get();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nCoupon ID: " + id);
		}
	}

	/**
	 * No validation to check, continue to couponsDao.
	 * 
	 * @return ArrayList of CouponBasicInfo with all the coupons from DB.
	 * @throws ApplicationException
	 */
	public List<CouponBasicInfo> getAllCoupons() throws ApplicationException {

		try {
			return this.couponsDao.getAllCoupons();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage());
		}
	}

	/**
	 * No validation to check, continue to couponsDao.
	 * 
	 * @param companyId
	 * @return ArrayList of CouponBasicInfo with the given company id.
	 * @throws ApplicationException
	 */
	public List<CouponBasicInfo> getCouponsByCompanyId(long companyId) throws ApplicationException {

		try {
			return this.couponsDao.getCouponsByCompanyId(companyId);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nCompany ID: " + companyId);
		}
	}

	/**
	 * No validation to check, continue to couponsDao.
	 * 
	 * @param category
	 * @return ArrayList of CouponBasicInfo with the given category.
	 * @throws ApplicationException
	 */
	public List<CouponBasicInfo> getCouponsByCategory(CategoryType category) throws ApplicationException {

		try {
			return this.couponsDao.getCouponsByCategory(category);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nCategory: " + category);
		}

	}

	/**
	 * checks validation for the given coupon info before continues to couponsDao to create or update a coupon.
	 * 
	 * @param couponDto
	 * @throws ApplicationException
	 */
	private void validateCouponInfo(CouponDto couponDto) throws ApplicationException {
		if (couponDto.getName() == null) {
			throw new ApplicationException(ErrorType.INVALID_COUPON_NAME, ErrorType.INVALID_COUPON_NAME.getErrorMessage());
		}

		if (couponDto.getName().isEmpty()) {
			throw new ApplicationException(ErrorType.INVALID_COUPON_NAME, ErrorType.INVALID_COUPON_NAME.getErrorMessage());
		}

		// Check if a coupon with same company id and name already exist,
		// BUT it's not the coupon we want to update.

		// Assign coupon with the same name and company id from DB (or null if doesn't exist).
		CouponDto newCoupon = new CouponDto();

		try {
			newCoupon = this.couponsDao.getCouponByNameAndCompanyId(couponDto.getName(), couponDto.getCompanyId());

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage());
		}

		// Validation works for create AND update as well.
		if (newCoupon != null && newCoupon.getId() != couponDto.getId()) {
			throw new ApplicationException(ErrorType.COUPON_NAME_ALREADY_EXISTS, ErrorType.COUPON_NAME_ALREADY_EXISTS.getErrorMessage());
		}

		// Check if end date earlier than start date.
		if (couponDto.getEndDate().before(couponDto.getStartDate())) {
			throw new ApplicationException(ErrorType.INVALID_COUPON_DATES, ErrorType.INVALID_COUPON_DATES.getErrorMessage());
		}

		// Check if end date earlier than current time.
		if (couponDto.getEndDate().before(new Date(System.currentTimeMillis()))) {
			throw new ApplicationException(ErrorType.INVALID_COUPON_DATES, ErrorType.INVALID_COUPON_DATES.getErrorMessage());
		}

		if (couponDto.getPrice() < 0) {
			throw new ApplicationException(ErrorType.INVALID_COUPON_PRICE, ErrorType.INVALID_COUPON_PRICE.getErrorMessage());
		}

		if (couponDto.getCategory() == null) {
			throw new ApplicationException(ErrorType.INVALID_COUPON_CATEGORY, ErrorType.INVALID_COUPON_CATEGORY.getErrorMessage());
		}

		if (couponDto.getAmount() < 0) {
			throw new ApplicationException(ErrorType.INVALID_COUPON_AMOUNT, ErrorType.INVALID_COUPON_AMOUNT.getErrorMessage());
		}
	}

	/**
	 * Create a CouponEntity object from the CouponDto object we received with the request from the client, in order to send it to the DB.
	 * 
	 * @param couponDto
	 * @return couponEntity
	 * @throws ApplicationException
	 */
	private CouponEntity createCouponEntityFromCouponDto(CouponDto couponDto) throws ApplicationException {

		CouponEntity couponEntity = new CouponEntity();

		couponEntity.setId(couponDto.getId());
		couponEntity.setName(couponDto.getName());
		couponEntity.setPrice(couponDto.getPrice());
		couponEntity.setDescription(couponDto.getDescription());
		couponEntity.setStartDate(couponDto.getStartDate());
		couponEntity.setEndDate(couponDto.getEndDate());
		couponEntity.setCategory(couponDto.getCategory());
		couponEntity.setAmount(couponDto.getAmount());

		CompanyEntity companyEntity = this.companiesController.getCompanyEntity(couponDto.getCompanyId());
		couponEntity.setCompany(companyEntity);
		couponEntity.setImage(couponDto.getImage());

		return couponEntity;
	}

	/**
	 * Checks if a coupon with given id exists in the DB.
	 * Used also by PurchasesController for updates coupon's amount before cancel a purchase.
	 * 
	 * @param id
	 * @return true if coupon exists or false if coupon does not exists.
	 * @throws ApplicationException
	 */
	public boolean isCouponExists(long id) throws ApplicationException {

		try {
			return this.couponsDao.existsById(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nCoupon ID: " + id);
		}
	}

	/**
	 * Gets the coupon's end date. Uses for validations where there's no need to pull the entire coupon.
	 * 
	 * @param id
	 * @return
	 * @throws ApplicationException
	 */
	public Date getEndDateFromCoupon (long id) throws ApplicationException {

		try {
			return this.couponsDao.getEndDateFromCoupon(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nCoupon ID: " + id);
		}
	}

}
