package com.sasi.coupons.logic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.sasi.coupons.dao.ICompaniesDao;
import com.sasi.coupons.data.UserLoginData;
import com.sasi.coupons.dto.CompanyDto;
import com.sasi.coupons.entities.CompanyEntity;
import com.sasi.coupons.enums.ErrorType;
import com.sasi.coupons.enums.UserType;
import com.sasi.coupons.exceptions.ApplicationException;

@Controller
public class CompaniesController {

	@Autowired
	private ICompaniesDao companiesDao;

	// No need to create default Ctor, been created automatically

	/**
	 * Checks validation for the given company info before continues to companiesDao.
	 * Will be available only for userType ADMIN.
	 * 
	 * @param companyDto
	 * @return the id number that was generated for the new company and was returned from companiesDao.
	 * @throws ApplicationException
	 */
	public long createCompany(CompanyDto companyDto) throws ApplicationException {

		validateCompanyInfo(companyDto);

		boolean isExists;

		try {
			isExists = this.companiesDao.existsByName(companyDto.getName());

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\n" + companyDto.toString());
		}

		if (isExists) {
			throw new ApplicationException(ErrorType.COMPANY_NAME_ALREADY_EXISTS, ErrorType.COMPANY_NAME_ALREADY_EXISTS.getErrorMessage());
		}

		CompanyEntity companyToSave = createCompanyEntityFromCompanyDto(companyDto);

		try {
			this.companiesDao.save(companyToSave);	

			return companyToSave.getId();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\n" + companyToSave.toString());
		}
	}

	/**
	 * Checks validation for the given company info before continues to companiesDao.
	 * Will be available only for userType COMPANY or ADMIN.
	 * 
	 * @param companyDto
	 * @param userLoginData
	 * @throws ApplicationException
	 */
	public void updateCompany(CompanyDto companyDto, UserLoginData userLoginData) throws ApplicationException {

		validateCompanyInfo(companyDto);

		CompanyDto companyForValidation;

		try {
			companyForValidation = this.companiesDao.findCompanyByName(companyDto.getName());

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\n" + companyDto.toString());
		}

		// Checks if company name exist, but it's not the name of the company we want to update.
		if (companyForValidation != null && companyForValidation.getId() != companyDto.getId()) {
			throw new ApplicationException(ErrorType.COMPANY_NAME_ALREADY_EXISTS, ErrorType.COMPANY_NAME_ALREADY_EXISTS.getErrorMessage());
		}

		if (userLoginData.getUserType() != UserType.ADMIN && userLoginData.getCompanyId() != companyDto.getId()) {
			throw new ApplicationException(ErrorType.CAN_NOT_ALTER_COMPANY, ErrorType.CAN_NOT_ALTER_COMPANY.getErrorMessage());
		}

		CompanyEntity companyToSave = createCompanyEntityFromCompanyDto(companyDto);

		try {
			this.companiesDao.save(companyToSave);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\n" + companyToSave.toString());
		}
	}

	/**
	 * Delete all coupons and purchases of the company before delete company (Defined as cascade).
	 * Will be available only for userType ADMIN.
	 * 
	 * @param id
	 * @throws ApplicationException
	 */
	public void deleteCompany(long id) throws ApplicationException {

		boolean isExists;

		// Extra checking to avoid SQLException
		try {
			isExists = this.companiesDao.existsById(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nCompany ID: " + id);
		}

		if (!isExists) {
			throw new ApplicationException(ErrorType.COMPANY_DOES_NOT_EXISTS, ErrorType.COMPANY_DOES_NOT_EXISTS.getErrorMessage());
		}

		try {
			this.companiesDao.deleteById(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nCompany ID: " + id);
		}
	}

	/**
	 * Checks if company with given id exists, and then get company as CompanyDto.
	 * 
	 * @param id
	 * @return companyDto with the given id number.
	 * @throws ApplicationException
	 */
	public CompanyDto getCompanyDto(long id) throws ApplicationException {

		boolean isExists;

		// Extra checking to avoid SQLException
		try {
			isExists = this.companiesDao.existsById(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nCompany ID: " + id);
		}

		if (!isExists) {
			throw new ApplicationException(ErrorType.COMPANY_DOES_NOT_EXISTS, ErrorType.COMPANY_DOES_NOT_EXISTS.getErrorMessage());
		}

		try {
			return this.companiesDao.findCompanyById(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nCompany ID: " + id);
		}
	}

	/**
	 * Checks if company with given id exists, and then get company as CompanyEntity.
	 * Uses for Validations.
	 * 
	 * @param id
	 * @return companyEntity with the given id number.
	 * @throws ApplicationException
	 */
	public CompanyEntity getCompanyEntity(long id) throws ApplicationException {

		boolean isExists;

		// Extra checking to avoid SQLException
		try {
			isExists = this.companiesDao.existsById(id);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nCompany ID: " + id);
		}

		if (!isExists) {
			throw new ApplicationException(ErrorType.COMPANY_DOES_NOT_EXISTS, ErrorType.COMPANY_DOES_NOT_EXISTS.getErrorMessage());
		}

		try {
			return this.companiesDao.findById(id).get();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage() + "\nCompany ID: " + id);
		}
	}

	/**
	 * No validation to check, continue to companiesDao.
	 * 
	 * @return List of CompanyDto with all the companies from DB.
	 * @throws ApplicationException
	 */
	public List<CompanyDto> getAllCompanies() throws ApplicationException {

		try {
			return this.companiesDao.getAllCompanies();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR, ErrorType.GENERAL_ERROR.getErrorMessage());
		}
	}

	/**
	 * Checks validation for the given company info before continues to CompaniesDao to create or update a company.
	 * 
	 * @param companyDto
	 * @throws ApplicationException
	 */
	private void validateCompanyInfo(CompanyDto companyDto) throws ApplicationException {
		if (companyDto.getName() == null) {
			throw new ApplicationException(ErrorType.INVALID_COMPANY_NAME, ErrorType.INVALID_COMPANY_NAME.getErrorMessage());
		}

		if (companyDto.getName().isEmpty()) {
			throw new ApplicationException(ErrorType.INVALID_COMPANY_NAME, ErrorType.INVALID_COMPANY_NAME.getErrorMessage());
		}

		if (companyDto.getName().length() < 2) {
			throw new ApplicationException(ErrorType.INVALID_COMPANY_NAME, ErrorType.INVALID_COMPANY_NAME.getErrorMessage());
		}

		if (companyDto.getAddress() == null) {
			throw new ApplicationException(ErrorType.MUST_ENTER_ADDRESS, ErrorType.MUST_ENTER_ADDRESS.getErrorMessage());
		}

		if (companyDto.getAddress().isEmpty()) {
			throw new ApplicationException(ErrorType.MUST_ENTER_ADDRESS, ErrorType.MUST_ENTER_ADDRESS.getErrorMessage());
		}

		if (companyDto.getPhoneNumber() == null) {
			throw new ApplicationException(ErrorType.MUST_ENTER_PHONE_NUMBER, ErrorType.MUST_ENTER_PHONE_NUMBER.getErrorMessage());
		}

		if (companyDto.getPhoneNumber().isEmpty()) {
			throw new ApplicationException(ErrorType.MUST_ENTER_PHONE_NUMBER, ErrorType.MUST_ENTER_PHONE_NUMBER.getErrorMessage());
		}
	}

	/**
	 * Create a CompanyEntity object from the CompanyDto object we received with the request from the client, in order to send it to the DB.
	 * 
	 * @param companyDto
	 * @return CompanyEntity
	 */
	private CompanyEntity createCompanyEntityFromCompanyDto(CompanyDto companyDto) {

		CompanyEntity companyEntity = new CompanyEntity();

		companyEntity.setId(companyDto.getId());
		companyEntity.setName(companyDto.getName());
		companyEntity.setAddress(companyDto.getAddress());
		companyEntity.setPhoneNumber(companyDto.getPhoneNumber());

		return companyEntity;

	}

}
