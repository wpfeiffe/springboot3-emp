package com.wspfeiffer.springboot3emp.repository;


import com.wspfeiffer.springboot3emp.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "companies", path = "companies")
public interface CompanyRepository extends JpaRepository<Company, Long>
{
}
