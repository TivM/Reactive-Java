package org.reactive.corporate.service;

import org.reactive.corporate.model.Task;
import org.reactive.corporate.model.enums.Priority;
import org.reactive.corporate.service.collector.EstimatedHoursCollector;

import java.time.Duration;
import java.time.Instant;
import java.util.DoubleSummaryStatistics;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalyticsService {

    // 3.1 Iterative (loop) - compute sum of estimated hours per priority
    public static Map<Priority, Double> aggregateByPriorityIterative(List<Task> tasks) {
        Map<Priority, Double> map = new EnumMap<>(Priority.class);
        for (Priority p : Priority.values()) map.put(p, 0.0);
        for (Task t : tasks) {
            map.merge(t.getPriority(), t.getEstimatedHours(), Double::sum);
        }
        return map;
    }

    // 3.2 Stream + collectors (groupingBy)
    public static Map<Priority, Double> aggregateByPriorityStream(List<Task> tasks) {
        return tasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority,
                        () -> new EnumMap<>(Priority.class),
                        Collectors.summingDouble(Task::getEstimatedHours)));
    }


    // 3.3 Stream + custom collector (global summary)
    public static DoubleSummaryStatistics aggregateEstimatedHoursWithCustomCollector(List<Task> tasks) {
        return tasks.stream().collect(new EstimatedHoursCollector());
    }


    // Generic runner to time functions
    public interface SupplierWithResult<T> {
        T get();
    }


    public static <T> TimedResult<T> timeExecution(SupplierWithResult<T> supplier) {
        Instant start = Instant.now();
        T result = supplier.get();
        Instant end = Instant.now();
        return new TimedResult<>(result, Duration.between(start, end));
    }


    public static final class TimedResult<T> {
        private final T result;
        private final Duration duration;
        public TimedResult(T result, Duration duration) { this.result = result; this.duration = duration; }
        public T getResult() { return result; }
        public Duration getDuration() { return duration; }
    }
}