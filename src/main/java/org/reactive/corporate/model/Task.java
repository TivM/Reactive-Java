package org.reactive.corporate.model;

import org.reactive.corporate.model.dto.MetaInfo;
import org.reactive.corporate.model.enums.Priority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Task {
    private final int id;
    private final String title;
    private final LocalDateTime dueDate;
    private final Priority priority;
    private final MetaInfo meta;
    private final List<String> tags;

    private final double estimatedHours;
    private final boolean completed;
    private final int assigneeWorkerId;


    public Task(int id, String title, LocalDateTime dueDate, Priority priority, MetaInfo meta, List<String> tags,
                double estimatedHours, boolean completed, int assigneeWorkerId) {
        this.id = id;
        this.title = Objects.requireNonNull(title);
        this.dueDate = Objects.requireNonNull(dueDate);
        this.priority = Objects.requireNonNull(priority);
        this.meta = Objects.requireNonNull(meta);
        this.tags = List.copyOf(tags);
        this.estimatedHours = estimatedHours;
        this.completed = completed;
        this.assigneeWorkerId = assigneeWorkerId;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public LocalDateTime getDueDate() { return dueDate; }
    public Priority getPriority() { return priority; }
    public MetaInfo getMeta() { return meta; }
    public List<String> getTags() { return tags; }
    public double getEstimatedHours() { return estimatedHours; }
    public boolean isCompleted() { return completed; }
    public int getAssigneeWorkerId() { return assigneeWorkerId; }
}
