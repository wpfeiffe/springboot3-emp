package com.wspfeiffer.springboot3emp.config;

import com.wspfeiffer.springboot3emp.domain.Company;
import com.wspfeiffer.springboot3emp.domain.Department;
import com.wspfeiffer.springboot3emp.domain.Employee;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestRepositoryConfig implements RepositoryRestConfigurer {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,
                                                     CorsRegistry corsRegistry) {
        config.exposeIdsFor(Company.class);
        config.exposeIdsFor(Department.class);
        config.exposeIdsFor(Employee.class);
        config.setBasePath("/repo");
    }
}
