package com.wspfeiffer.springboot3emp.repository;


import com.wspfeiffer.springboot3emp.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "employees", path = "employees")
public interface EmployeeRepository extends JpaRepository<Employee, Long>
{
    Page<Employee> findByFirstName(String firstName, Pageable p);
    Page<Employee> findByActive(Boolean active, Pageable p);
}
