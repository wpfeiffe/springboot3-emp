package com.wspfeiffer.springboot3emp.resource;

import com.wspfeiffer.springboot3emp.domain.Employee;
import com.wspfeiffer.springboot3emp.repository.EmployeeRepository;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@PreAuthorize("isAuthenticated()")
public class EmployeeResource
{
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeResource.class);

    private final EmployeeRepository employeeRepository;
    private final MeterRegistry meterRegistry;

    public EmployeeResource(EmployeeRepository employeeRepository,
                            MeterRegistry meterRegistry) {
        this.employeeRepository = employeeRepository;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping(value = {"", "/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed("employees.get.all")
    @Secured("ROLE_USER")
    public List<Employee> all() {
        return (ArrayList<Employee>) employeeRepository.findAll();
    }

    @GetMapping(value = {"/pageable"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed("employees.get.pageable")
    @Secured("ROLE_USER")
    public Page<Employee> all(@SortDefault(sort = "id", direction = Sort.Direction.ASC) Pageable p) {
        return (Page<Employee>) employeeRepository.findAll(p);
    }

    @GetMapping("/{id}")
    @Timed("employees.get.single")
    @Secured("ROLE_USER")
    public ResponseEntity<Employee> getById(@PathVariable Long id) {
            Employee found = employeeRepository.findById(id).orElse(null);
            if (found == null)
            {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(found);
    }

    @GetMapping("/findBy")
    @Timed("employees.findby")
    @Secured("ROLE_USER")
    public ResponseEntity<Page<Employee>> getFindBy(Pageable p, @RequestParam Optional<String> firstName, @RequestParam Optional<Boolean> active) {
        if ((firstName.isEmpty() && active.isEmpty()) || (firstName.isPresent() && active.isPresent())) {
            return ResponseEntity.badRequest().build();
        }
        else if (firstName.isPresent()) {
           Page<Employee> employees = employeeRepository.findByFirstName(firstName.get(), p);
            if (employees == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(employees);
        }
        else {
            Page<Employee> employees = employeeRepository.findByActive(active.get(), p);
            if (employees == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(employees);
        }
    }

    @PutMapping("/{id}")
    @Timed("employees.update")
    @Transactional(value = Transactional.TxType.REQUIRED)
    @Secured("ROLE_USER")
    public ResponseEntity<Employee> update(@PathVariable Long id, @RequestBody @Valid Employee employee) {
        if (employee.getId() == null || !employee.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        Employee origEmployee = employeeRepository.findById(id).orElse(null);
        if (origEmployee == null) {
            return ResponseEntity.notFound().build();
        }
        origEmployee.setTitle(employee.getTitle());
        origEmployee.setFirstName(employee.getFirstName());
        origEmployee.setLastName(employee.getLastName());
        origEmployee.setStartDate(employee.getStartDate());
        origEmployee.setActive(employee.isActive());
        origEmployee = employeeRepository.save(origEmployee);
        return ResponseEntity
            .ok(origEmployee);
    }

    @PostMapping("/")
    @Timed("employees.create")
    @Secured("ROLE_USER")
    public ResponseEntity<Employee> create(@RequestBody @Valid Employee employee) {
        assert employee.getId() == null;
        final Employee updated = employeeRepository.save(employee);
        return ResponseEntity
            .ok(updated);
    }

    @DeleteMapping("/{id}")
    @Timed(value = "employees.delete", description = "Delete employee")
    @Transactional(value = Transactional.TxType.REQUIRED)
    @Secured("ROLE_USER")
    public ResponseEntity<Employee> delete(@PathVariable Long id) {
        Employee found = employeeRepository.findById(id).orElse(null);
        if (found == null) {
            return ResponseEntity.notFound().build();
        }
        employeeRepository.delete(found);
        return ResponseEntity.ok(found);
    }

    private URI toUri(Employee employee) {
        return URI.create("/api/employees/" + employee.getId());
    }
}
