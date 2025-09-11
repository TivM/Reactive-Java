package org.reactive.corporate.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Admin {
    private Integer id;

    private String name;

    private Integer age;

    private String division;

    private Set<Employee> employees = new HashSet<>();

    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.setAdmin(this);
    }

}
