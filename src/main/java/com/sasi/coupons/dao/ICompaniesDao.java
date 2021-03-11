package com.sasi.coupons.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.sasi.coupons.dto.CompanyDto;
import com.sasi.coupons.entities.CompanyEntity;

public interface ICompaniesDao extends CrudRepository<CompanyEntity, Long> {

	public boolean existsByName(String name);

	@Query("SELECT new com.sasi.coupons.dto.CompanyDto(c) FROM CompanyEntity c WHERE c.name = :companyName")
	public CompanyDto findCompanyByName(@Param("companyName") String compName);

	@Query("SELECT new com.sasi.coupons.dto.CompanyDto(c) FROM CompanyEntity c WHERE c.id = :companyId")
	public CompanyDto findCompanyById(@Param("companyId") long compId);

	@Query("SELECT new com.sasi.coupons.dto.CompanyDto(c) FROM CompanyEntity c")
	public List<CompanyDto> getAllCompanies();

}
