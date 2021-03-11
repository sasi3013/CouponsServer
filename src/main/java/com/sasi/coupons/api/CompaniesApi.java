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
import org.springframework.web.bind.annotation.RestController;

import com.sasi.coupons.data.UserLoginData;
import com.sasi.coupons.dto.CompanyDto;
import com.sasi.coupons.exceptions.ApplicationException;
import com.sasi.coupons.filters.LoginFilter;
import com.sasi.coupons.logic.CompaniesController;

@RestController
@RequestMapping("/companies")
public class CompaniesApi {

	@Autowired
	private CompaniesController companiesController;

	// No need to create default Ctor, been created automatically

	@PostMapping
	public long createCompany(@RequestBody CompanyDto company) throws ApplicationException {
		return this.companiesController.createCompany(company);
	}

	@PutMapping
	public void updateCompany(@RequestBody CompanyDto company, HttpServletRequest request) throws ApplicationException {
		UserLoginData userLoginData = (UserLoginData) request.getAttribute(LoginFilter.USER_LOGIN_DATA);
		this.companiesController.updateCompany(company, userLoginData);
	}

	@DeleteMapping("/{id}")
	public void deleteCompany(@PathVariable("id") long id) throws ApplicationException {
		this.companiesController.deleteCompany(id);
	}

	@GetMapping("/{id}")
	public CompanyDto getCompany(@PathVariable("id") long id) throws ApplicationException {
		return this.companiesController.getCompanyDto(id);
	}

	@GetMapping
	public List<CompanyDto> getAllCompanies() throws ApplicationException {
		return this.companiesController.getAllCompanies();
	}

}
