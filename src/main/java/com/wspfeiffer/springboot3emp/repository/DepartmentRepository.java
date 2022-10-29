package com.wspfeiffer.springboot3emp.repository;


import com.wspfeiffer.springboot3emp.domain.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "departments", path = "departments")
public interface DepartmentRepository extends JpaRepository<Department, Long>
{
    default Page<Department> findByDeptCode(Pageable p, String deptCode) {
        return null;
    }
}
