package com.sasi.coupons.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sasi.coupons.data.UserLoginData;
import com.sasi.coupons.dto.PurchaseRequest;
import com.sasi.coupons.dto.PurchasedCoupon;
import com.sasi.coupons.exceptions.ApplicationException;
import com.sasi.coupons.filters.LoginFilter;
import com.sasi.coupons.logic.PurchasesController;

@RestController
@RequestMapping("/purchases")
public class PurchasesApi {

	@Autowired
	private PurchasesController purchasesController;

	// No need to create default Ctor, been created automatically

	@PostMapping
	public long createPurchase(@RequestBody PurchaseRequest purchaseRequest, HttpServletRequest request) throws ApplicationException {
		UserLoginData userLoginData = (UserLoginData) request.getAttribute(LoginFilter.USER_LOGIN_DATA);
		return this.purchasesController.createPurchase(purchaseRequest, userLoginData);
	}

	@PutMapping
	public void updatePurchase(@RequestBody PurchaseRequest purchaseRequest, HttpServletRequest request) throws ApplicationException {
		UserLoginData userLoginData = (UserLoginData) request.getAttribute(LoginFilter.USER_LOGIN_DATA);
		this.purchasesController.updatePurchase(purchaseRequest, userLoginData);
	}

	@DeleteMapping("/{id}")
	public void deletePurchase(@PathVariable("id") long id) throws ApplicationException {
		this.purchasesController.deletePurchase(id);
	}

	@DeleteMapping("/cancel/{id}")
	public void cancelPurchase(@PathVariable("id") long id, HttpServletRequest request) throws ApplicationException {
		UserLoginData userLoginData = (UserLoginData) request.getAttribute(LoginFilter.USER_LOGIN_DATA);
		this.purchasesController.cancelPurchase(id, userLoginData);
	}

	@GetMapping("/{id}")
	public PurchasedCoupon getPurchase(@PathVariable("id") long id) throws ApplicationException {
		return this.purchasesController.getPurchase(id);
	}

	@GetMapping
	public List<PurchasedCoupon> getAllPurchases() throws ApplicationException {
		return this.purchasesController.getAllPurchases();
	}

	@GetMapping("/byUserId")
	public List<PurchasedCoupon> getPurchasesByUserId(@RequestParam("userId") long userId) throws ApplicationException {
		return this.purchasesController.getPurchasesByUserId(userId);
	}

	@GetMapping("/byCompanyId")
	public List<PurchasedCoupon> getPurchasesByCompanyId(@RequestParam("companyId") long companyId) throws ApplicationException {
		return this.purchasesController.getPurchasesByCompanyId(companyId);
	}

	@GetMapping("/byCouponId")
	public List<PurchasedCoupon> getPurchasesByCouponId(@RequestParam("couponId") long couponId) throws ApplicationException {
		return this.purchasesController.getPurchasesByCouponId(couponId);
	}

	@GetMapping("/byMaxPrice")
	public List<PurchasedCoupon> getPurchasedCouponsByMaxPrice(@RequestParam("userId") long userId, @RequestParam("maxPrice") float maxPrice) throws ApplicationException {
		return this.purchasesController.getPurchasedCouponsByMaxPrice(userId, maxPrice);
	}

}
