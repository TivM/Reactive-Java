package org.reactive.corporate.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Employee {

    private Integer id;

    private String name;

    private Integer age;

    private String division;

    private Admin admin;
}
