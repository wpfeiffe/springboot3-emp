package com.wspfeiffer.springboot3emp.resource;

import com.wspfeiffer.springboot3emp.domain.Company;
import com.wspfeiffer.springboot3emp.repository.CompanyRepository;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/companies")
@PreAuthorize("isAuthenticated()")
public class CompanyResource {
    private final CompanyRepository companyRepository;

    public CompanyResource(CompanyRepository companyRepository)
    {
        this.companyRepository = companyRepository;
    }

    @GetMapping(value = {"", "/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed(value = "companies.get.all", description = "Get all companies")
    @Secured("ROLE_USER")
    public List<Company> all() {
        return (ArrayList<Company>) companyRepository.findAll();
    }

    @GetMapping(value = {"/pageable"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed(value = "companies.get.pageable", description = "Get pageable list of companies")
    @Secured("ROLE_USER")
    public Page<Company> all(@SortDefault(sort = "id", direction = Sort.Direction.ASC) Pageable p) {
        return (Page<Company>) companyRepository.findAll(p);
    }


    @GetMapping("/{id}")
    @Timed(value = "companies.get.single", description = "Get company by id")
    @Secured("ROLE_USER")
    public ResponseEntity<Company> getById(@PathVariable Long id) {
        Company found = companyRepository.findById(id).orElse(null);
        if (found == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(found);
    }

    @PutMapping("/{id}")
    @Timed(value = "companies.update", description = "Update company")
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured("ROLE_USER")
    public ResponseEntity<Company> update(@PathVariable Long id, @RequestBody @Valid Company company) {
        if (company.getId() == null || !company.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        Company origCompany = companyRepository.findById(id).orElse(null);
        if (origCompany == null) {
            return ResponseEntity.notFound().build();
        }
        origCompany.setCompanyName(company.getCompanyName());
        origCompany = companyRepository.save(origCompany);
        return ResponseEntity
            .ok(origCompany);
    }

    @PostMapping("")
    @Timed(value = "companies.create", description = "Create company")
    @Secured("ROLE_USER")
    public ResponseEntity<Company> create(@RequestBody @Valid Company company) {
        assert company.getId() == null;
        final Company updated = companyRepository.save(company);
        return ResponseEntity
            .ok(updated);
    }

    @DeleteMapping("/{id}")
    @Timed(value = "companies.delete", description = "Delete company")
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured("ROLE_USER")
    public ResponseEntity<Company> delete(@PathVariable Long id) {
        Company found = companyRepository.findById(id).orElse(null);
        if (found == null) {
            return ResponseEntity.notFound().build();
        }
        companyRepository.delete(found);
        return ResponseEntity.ok(found);
    }

    private URI toUri(Company company) {
        return URI.create("/company/" + company.getId());
    }
}
