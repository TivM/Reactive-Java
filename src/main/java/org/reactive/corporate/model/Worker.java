package org.reactive.corporate.model;

import java.util.Objects;

public class Worker {
    private final int id;
    private final String fullName;
    private final String department;


    public Worker(int id, String fullName, String department) {
        this.id = id;
        this.fullName = Objects.requireNonNull(fullName);
        this.department = Objects.requireNonNull(department);
    }


    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getDepartment() { return department; }
}
