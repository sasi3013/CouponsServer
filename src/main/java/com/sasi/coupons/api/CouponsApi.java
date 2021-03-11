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
import com.sasi.coupons.dto.CouponBasicInfo;
import com.sasi.coupons.dto.CouponDto;
import com.sasi.coupons.enums.CategoryType;
import com.sasi.coupons.exceptions.ApplicationException;
import com.sasi.coupons.filters.LoginFilter;
import com.sasi.coupons.logic.CouponsController;

@RestController
@RequestMapping("/coupons")
public class CouponsApi {

	@Autowired
	private CouponsController couponsController;

	// No need to create default Ctor, been created automatically

	@PostMapping
	public long createCoupon(@RequestBody CouponDto coupon, HttpServletRequest request) throws ApplicationException {
		UserLoginData userLoginData = (UserLoginData) request.getAttribute(LoginFilter.USER_LOGIN_DATA);
		return this.couponsController.createCoupon(coupon, userLoginData);
	}

	@PutMapping
	public void updateCoupon(@RequestBody CouponDto coupon, HttpServletRequest request) throws ApplicationException {
		UserLoginData userLoginData = (UserLoginData) request.getAttribute(LoginFilter.USER_LOGIN_DATA);
		this.couponsController.updateCoupon(coupon, userLoginData);
	}

	@DeleteMapping("/{id}")
	public void deleteCoupon(@PathVariable("id") long id, HttpServletRequest request) throws ApplicationException {
		UserLoginData userLoginData = (UserLoginData) request.getAttribute(LoginFilter.USER_LOGIN_DATA);
		this.couponsController.deleteCoupon(id, userLoginData);
	}

	@GetMapping("/{id}")
	public CouponDto getCoupon(@PathVariable("id") long id) throws ApplicationException {
		return this.couponsController.getCouponDto(id);
	}

	@GetMapping
	public List<CouponBasicInfo> getAllCoupons() throws ApplicationException {
		return this.couponsController.getAllCoupons();
	}

	@GetMapping("/byCompanyId")
	public List<CouponBasicInfo> getCouponsByCompanyId(@RequestParam("companyId") long companyId) throws ApplicationException {
		return this.couponsController.getCouponsByCompanyId(companyId);
	}

	@GetMapping("/byCategory")
	public List<CouponBasicInfo> getCouponsByCategory(@RequestParam("category") CategoryType category) throws ApplicationException {
		return this.couponsController.getCouponsByCategory(category);
	}

}
