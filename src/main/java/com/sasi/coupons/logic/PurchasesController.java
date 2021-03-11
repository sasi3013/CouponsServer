package com.sasi.coupons.logic;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.sasi.coupons.dao.IPurchasesDao;
import com.sasi.coupons.data.UserLoginData;
import com.sasi.coupons.dto.CouponDto;
import com.sasi.coupons.dto.PurchaseRequest;
import com.sasi.coupons.dto.PurchasedCoupon;
import com.sasi.coupons.entities.CouponEntity;
import com.sasi.coupons.entities.PurchaseEntity;
import com.sasi.coupons.entities.UserEntity;
import com.sasi.coupons.enums.ErrorType;
import com.sasi.coupons.enums.UserType;
import com.sasi.coupons.exceptions.ApplicationException;

@Controller
public class PurchasesController {

	@Autowired
	private IPurchasesDao purchasesDao;

	@Autowired
	private CouponsController couponsController;

	@Autowired
	private UsersController usersController;

	// No need to create default Ctor, been created automatically

	/**
	 * Checks validation for the given purchase info and updates the amount of the remaining coupons before continues to complete the purchases.
	 * Will be available only for userType CUSTOMER.
	 * 
	 * @param purchaseRequest
	 * @param userLoginData 
	 * @return the id number that was generated for the new purchase and was returned from purchasesDao.
	 * @throws ApplicationException
	 */
	public long createPurchase(PurchaseRequest purchaseRequest, UserLoginData userLoginData) throws ApplicationException {

		// Checks purchase info.
		validatePurchaseInfo(purchaseRequest);

		// Checks if enough coupons available.
		// If everything is good, decrease amount of purchased coupons from coupons amount.		
		updateCouponsAmountForCreatePurchase(purchaseRequest, userLoginData);

		PurchaseEntity purchaseToSave = createPurchaseEntityFromPurchaseRequest(purchaseRequest, userLoginData);

		try {
			this.purchasesDao.save(purchaseToSave);

			return purchaseToSave.getId();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\n" + purchaseToSave.toString());
		}
	}

	/**
	 * Checks validation for the given purchase info and updates the amount of the remaining coupons before continues to complete the purchases.
	 * 
	 * @param purchaseRequest
	 * @param userLoginData
	 * @throws ApplicationException
	 */
	public void updatePurchase(PurchaseRequest purchaseRequest, UserLoginData userLoginData) throws ApplicationException {

		// Checks purchase info.
		validatePurchaseInfo(purchaseRequest);

		PurchaseEntity purchaseToSave = createPurchaseEntityFromPurchaseRequest(purchaseRequest, userLoginData);

		// Validate user's authorization.
		validatePurchaseChangingAuthorization(purchaseToSave, userLoginData);

		// Changes the coupons amount according to the purchase amount update. (Check if enough coupons available as well).
		// Add/Subtract only the difference from the original purchase.
		updateCouponsAmountForUpdatePurchase(purchaseToSave, userLoginData);

		try {
			this.purchasesDao.save(purchaseToSave);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\n" + purchaseToSave.toString());
		}
	}

	/**
	 * Checks if purchase with given id exists before delete purchase.
	 * 
	 * @param id
	 * @throws ApplicationException
	 */
	public void deletePurchase(long id) throws ApplicationException {

		// Extra checking to avoid SQLException
		boolean isExists = isPurchaseExists(id);

		if (!isExists) {
			throw new ApplicationException(ErrorType.PURCHASE_DOES_NOT_EXISTS, ErrorType.PURCHASE_DOES_NOT_EXISTS.getErrorMessage());
		}

		try {
			this.purchasesDao.deleteById(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nPurchase ID: " + id);
		}
	}

	/**
	 * Checks validation to cancel purchase and updates the amount of the remaining coupons before continues to complete the cancellation.
	 * Will not be available after the purchased coupon is expired.
	 * 
	 * @param id
	 * @param userLoginData
	 * @throws ApplicationException
	 */
	public void cancelPurchase(long id, UserLoginData userLoginData) throws ApplicationException {

		// Extra checking to avoid SQLException
		boolean isExists = isPurchaseExists(id);

		if (!isExists) {
			throw new ApplicationException(ErrorType.PURCHASE_DOES_NOT_EXISTS, ErrorType.PURCHASE_DOES_NOT_EXISTS.getErrorMessage());
		}

		PurchaseEntity purchaseEntity;

		try {
			purchaseEntity = this.purchasesDao.findById(id).get();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nPurchase ID: " + id);
		}

		// Validate user's authorization.
		validatePurchaseChangingAuthorization(purchaseEntity, userLoginData);

		// Changes the coupons amount according to the purchase amount that been cancelled (return to the stock).
		// Add the amount of cancelled purchased coupons to the coupons amount.
		updateCouponsAmountForCancelPurchase(purchaseEntity, userLoginData);

		try {
			this.purchasesDao.deleteById(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nPurchase ID: " + id);
		}
	}

	/**
	 * Checks if purchase with given id exists, and then get purchase as PurchasedCoupon object.
	 * 
	 * @param id
	 * @return PurchasedCoupon with the given id number.
	 * @throws ApplicationException
	 */
	public PurchasedCoupon getPurchase(long id) throws ApplicationException {

		// Extra checking to avoid SQLException
		boolean isExists = isPurchaseExists(id);

		if (!isExists) {
			throw new ApplicationException(ErrorType.PURCHASE_DOES_NOT_EXISTS, ErrorType.PURCHASE_DOES_NOT_EXISTS.getErrorMessage());
		}

		try {
			return this.purchasesDao.findPurchaseById(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nPurchase ID: " + id);
		}
	}

	/**
	 * No validation to check, continue to purchasesDao.
	 * 
	 * @return List of PurchasedCoupon with all the purchases from DB.
	 * @throws ApplicationException
	 */
	public List<PurchasedCoupon> getAllPurchases() throws ApplicationException {

		try {
			return this.purchasesDao.getAllPurchases();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage());
		}
	}

	/**
	 * No validation to check, continue to purchasesDao.
	 * 
	 * @param userId
	 * @return List of PurchasedCoupon with the purchases made by the given user id.
	 * @throws ApplicationException
	 */
	public List<PurchasedCoupon> getPurchasesByUserId(long userId) throws ApplicationException {

		try {
			return this.purchasesDao.getPurchasesByUserId(userId);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nUser ID: " + userId);
		}
	}

	/**
	 * No validation to check, continue to purchasesDao.
	 * 
	 * @param companyId
	 * @return List of PurchasedCoupon with the purchases made with the given company id.
	 * @throws ApplicationException
	 */
	public List<PurchasedCoupon> getPurchasesByCompanyId(long companyId) throws ApplicationException {

		try {
			return this.purchasesDao.getPurchasesByCompanyId(companyId);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nCompany ID: " + companyId);
		}
	}

	/**
	 * No validation to check, continue to purchasesDao.
	 * 
	 * @param couponId
	 * @return List of PurchasedCoupon with the purchases made with the given coupon id.
	 * @throws ApplicationException
	 */
	public List<PurchasedCoupon> getPurchasesByCouponId(long couponId) throws ApplicationException {

		try {
			return this.purchasesDao.getPurchasesByCouponId(couponId);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nCoupon ID: " + couponId);
		}
	}

	/**
	 * No validation to check, continue to purchasesDao.
	 * 
	 * @param userId
	 * @param maxPrice
	 * @return List of PurchasedCoupon with the purchases made below the max price and made by user id.
	 * @throws ApplicationException
	 */
	public List<PurchasedCoupon> getPurchasedCouponsByMaxPrice(long userId, float maxPrice) throws ApplicationException {

		try {
			return this.purchasesDao.getPurchasedCouponsByMaxPrice(userId, maxPrice);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage());
		}
	}

	/**
	 * Checks validation for the given purchase info before continues to purchasesDao to create or update a purchase.
	 * 
	 * @param purchaseRequest
	 * @throws ApplicationException
	 */
	private void validatePurchaseInfo(PurchaseRequest purchaseRequest) throws ApplicationException {

		boolean isExists = this.couponsController.isCouponExists(purchaseRequest.getCouponId());

		if (!isExists) {
			throw new ApplicationException(ErrorType.COUPON_DOES_NOT_EXISTS, ErrorType.COUPON_DOES_NOT_EXISTS.getErrorMessage());
		}

		Date endDate;

		try {
			endDate = this.couponsController.getEndDateFromCoupon(purchaseRequest.getCouponId());

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage());
		}

		// Check if the coupon is expired.
		if (endDate.before(new Date(System.currentTimeMillis()))) {
			throw new ApplicationException(ErrorType.COUPON_EXPIRED, ErrorType.COUPON_EXPIRED.getErrorMessage());
		}

		// Amount to purchase must be above 0.
		if (purchaseRequest.getAmount() <= 0) {
			throw new ApplicationException(ErrorType.INVALID_PURCHASE_AMOUNT, ErrorType.INVALID_PURCHASE_AMOUNT.getErrorMessage());
		}
	}

	/**
	 * Checks if the amount of available coupons is not less than the amount that user wants to buy.
	 * In case the amount of available coupons is good:
	 * Updates the coupon's amount for the coupon that been purchased. (Decrease amount of purchased coupons from coupons amount).
	 * 
	 * @param purchaseRequest
	 * @param userLoginData
	 * @throws ApplicationException
	 */
	private void updateCouponsAmountForCreatePurchase(PurchaseRequest purchaseRequest, UserLoginData userLoginData) throws ApplicationException {

		// Creating a Coupon object for multiple actions.
		CouponDto coupon = new CouponDto();

		try {
			coupon = this.couponsController.getCouponDto(purchaseRequest.getCouponId());

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage());
		}

		// Check if enough coupons available.
		if (coupon.getAmount() < purchaseRequest.getAmount()) {
			throw new ApplicationException(ErrorType.NOT_ENOUGH_COUPONS_LEFT, ErrorType.NOT_ENOUGH_COUPONS_LEFT.getErrorMessage() + "\nAmount of available coupons: "
					+ coupon.getAmount());
		}

		// Update coupon's amount
		coupon.setAmount(coupon.getAmount() - purchaseRequest.getAmount());

		this.couponsController.updateCoupon(coupon, userLoginData);
	}

	/**
	 * Changes the coupons amount according to the purchase amount update.
	 * In case the amount of available coupons is good:
	 * Updates the coupon's amount for the coupon that been updated. (Decrease or increase ONLY THE DIFFERENCE amount of purchased coupons from coupons amount).
	 * 
	 * @param purchaseEntity
	 * @param userLoginData
	 * @throws ApplicationException
	 */
	private void updateCouponsAmountForUpdatePurchase(PurchaseEntity purchaseEntity, UserLoginData userLoginData) throws ApplicationException {

		// Creating a Coupon object for multiple actions.
		CouponDto coupon = new CouponDto();

		try {
			coupon = this.couponsController.getCouponDto(purchaseEntity.getCoupon().getId());

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage());
		}

		// The amount of coupons that was purchased BEFORE the update.
		PurchaseEntity oldPurchaseEntity;

		try {
			oldPurchaseEntity = this.purchasesDao.findById(purchaseEntity.getId()).get();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage());
		}

		int oldPurchasedCouponsAmount = oldPurchaseEntity.getAmount();

		// Saves the amount for a relevant exception message.
		int couponAmount = coupon.getAmount();

		// Change the coupons amount according to the purchase amount update.
		// Add/Subtract only the difference from the original purchase amount.
		coupon.setAmount(coupon.getAmount() + (oldPurchasedCouponsAmount - purchaseEntity.getAmount()));

		// Check if enough coupons available
		if (coupon.getAmount() < 0) {
			throw new ApplicationException(ErrorType.NOT_ENOUGH_COUPONS_LEFT, ErrorType.NOT_ENOUGH_COUPONS_LEFT.getErrorMessage() + "\nAmount of available coupons: "
					+ couponAmount);
		}

		this.couponsController.updateCoupon(coupon, userLoginData);
	}

	/**
	 * Updates the coupon's amount for the coupon that been purchased.
	 * 
	 * @param purchaseEntity
	 * @param userLoginData
	 * @throws ApplicationException
	 */
	private void updateCouponsAmountForCancelPurchase(PurchaseEntity purchaseEntity, UserLoginData userLoginData) throws ApplicationException {

		// Creating a Coupon object for multiple actions.
		// Before got here we confirmed that the purchase is exist, therefore the coupon also exist.
		CouponDto coupon = new CouponDto();

		try {
			coupon = this.couponsController.getCouponDto(purchaseEntity.getCoupon().getId());

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage());
		}

		// Check if coupon is expired but have not been deleted by TimerTask yet (Can NOT cancel in this case).
		if (coupon.getEndDate().before(new Date(System.currentTimeMillis()))) {
			throw new ApplicationException(ErrorType.CAN_NOT_CANCEL_PURCHASE, ErrorType.CAN_NOT_CANCEL_PURCHASE.getErrorMessage());
		}

		// Updates the coupons amount according to the purchase amount that been cancelled (return to the stock).
		// Add the amount of cancelled purchased coupons to the coupons amount.
		coupon.setAmount(coupon.getAmount() + purchaseEntity.getAmount());

		this.couponsController.updateCoupon(coupon, userLoginData);
	}

	/**
	 * Validate if the user who request to cancel or update a purchase has the authorization to do that.
	 * A purchase can be altered by the CUSTOMER who made it, the relevant COMPANY representative or ADMIN.
	 * 
	 * @param purchaseEntity
	 * @param userLoginData
	 * @throws ApplicationException
	 */
	private void validatePurchaseChangingAuthorization(PurchaseEntity purchaseEntity, UserLoginData userLoginData) throws ApplicationException {

		// User is Admin.
		if (userLoginData.getUserType() == UserType.ADMIN) {
			return;		
		}

		// User is Customer who made the purchase.
		if (purchaseEntity.getUser().getId() == userLoginData.getId()) {
			return;			
		}

		// User is Company representative.
		if (userLoginData.getUserType() == UserType.COMPANY && purchaseEntity.getCoupon().getCompany().getId() == userLoginData.getCompanyId()) {
			return;
		}

		throw new ApplicationException(ErrorType.CAN_NOT_ALTER_PURCHASE, ErrorType.CAN_NOT_ALTER_PURCHASE.getErrorMessage());			

	}

	/**
	 * Create a PurchaseEntity object from purchaseRequest object we received with the request from the client.
	 * Get the "sensitive" info from the cache instead of getting it from the user.
	 * 
	 * @param purchaseRequest
	 * @param userLoginData
	 * @return purchaseEntity
	 * @throws ApplicationException
	 */
	private PurchaseEntity createPurchaseEntityFromPurchaseRequest(PurchaseRequest purchaseRequest, UserLoginData userLoginData) throws ApplicationException {

		PurchaseEntity purchaseEntity = new PurchaseEntity();

		purchaseEntity.setId(purchaseRequest.getId());
		purchaseEntity.setAmount(purchaseRequest.getAmount());

		CouponEntity coupon;

		try {
			coupon = this.couponsController.getCouponEntity(purchaseRequest.getCouponId());

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage());
		}

		purchaseEntity.setCoupon(coupon);

		UserEntity user;

		try {
			user = this.usersController.getUserEntity(userLoginData.getId());

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage());
		}

		purchaseEntity.setUser(user);

		purchaseEntity.setTimestamp(new Timestamp(System.currentTimeMillis()));

		return purchaseEntity;
	}

	/**
	 * Checks if a purchase with given id exists in the DB.
	 * 
	 * @param id
	 * @return true if purchase exists or false if purchase does not exists.
	 * @throws ApplicationException
	 */
	private boolean isPurchaseExists(long id) throws ApplicationException {

		try {
			return this.purchasesDao.existsById(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nPurchase ID: " + id);
		}
	}

}
