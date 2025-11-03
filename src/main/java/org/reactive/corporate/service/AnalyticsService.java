package org.reactive.corporate.service;

import org.reactive.corporate.model.Task;
import org.reactive.corporate.model.enums.Tag;
import org.reactive.corporate.service.collector.EstimatedHoursCollector;
import org.reactive.corporate.service.spliterator.TaskTagSpliterator;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AnalyticsService {

    public static Map<Tag, Double> aggregateEstimatedHoursWithCustomCollector(List<Task> tasks) {
        return aggregateEstimatedHoursWithCustomCollector(tasks, 0L);
    }

    public static Map<Tag, Double> aggregateEstimatedHoursWithCustomCollector(List<Task> tasks, long delayMillis) {
        return tasks.stream().collect(new EstimatedHoursCollector(delayMillis));
    }
    
    //1 Итеративный подход для статистики по тегам
    public static Map<Tag, Double> aggregateByTagIterative(List<Task> tasks) {
        return aggregateByTagIterative(tasks, 0L);
    }

    public static Map<Tag, Double> aggregateByTagIterative(List<Task> tasks, long delayMillis) {
        Map<Tag, Double> map = new EnumMap<>(Tag.class);
        for (Task task : tasks) {
            double hours = task.getEstimatedHours(delayMillis);
            for (Tag tag : task.getTags()) {
                map.merge(tag, hours, Double::sum);
            }
        }
        return map;
    }

    //2 Stream + стандартные коллекторы для статистики по тегам
    public static Map<Tag, Double> aggregateByTagStream(List<Task> tasks) {
        return aggregateByTagStreamParallel(tasks, 0L);
    }

    public static Map<Tag, Double> aggregateByTagStreamParallel(List<Task> tasks, long delayMillis) {
        return tasks.parallelStream()
                .flatMap(task -> {
                    double hours = task.getEstimatedHours(delayMillis);
                    return task.getTags().stream().map(tag -> Map.entry(tag, hours));
                })
                .collect(Collectors.groupingByConcurrent(
                        Map.Entry::getKey,
                        ConcurrentHashMap::new,
                        Collectors.summingDouble(Map.Entry::getValue)
                ));
    }

    public static Map<Tag, Double> aggregateByTagStreamSequential(List<Task> tasks, long delayMillis) {
        return tasks.stream()
                .flatMap(task -> {
                    double hours = task.getEstimatedHours(delayMillis);
                    return task.getTags().stream().map(tag -> Map.entry(tag, hours));
                })
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        () -> new EnumMap<>(Tag.class),
                        Collectors.summingDouble(Map.Entry::getValue)
                ));
    }

    public static Map<Tag, Double> aggregateByTagStreamSequential(List<Task> tasks) {
        return aggregateByTagStreamSequential(tasks, 0L);
    }

    public static Map<Tag, Double> aggregateByTagStreamWithCustomSpliterator(List<Task> tasks, long delayMillis) {
        TaskTagSpliterator spliterator = new TaskTagSpliterator(tasks, delayMillis);
        return StreamSupport.stream(spliterator, true)
                .collect(Collectors.groupingByConcurrent(
                        Map.Entry::getKey,
                        ConcurrentHashMap::new,
                        Collectors.summingDouble(Map.Entry::getValue)
                ));
    }

    public static Map<Tag, Double> aggregateByTagStreamWithCustomSpliterator(List<Task> tasks) {
        return aggregateByTagStreamWithCustomSpliterator(tasks, 0L);
    }

    //3 Кастомный коллектор для статистики по тегам
    public static Map<Tag, Double> aggregateByTagCustomCollector(List<Task> tasks) {
        return aggregateByTagCustomCollector(tasks, 0L);
    }

    public static Map<Tag, Double> aggregateByTagCustomCollector(List<Task> tasks, long delayMillis) {
        return tasks.stream().collect(new EstimatedHoursCollector(delayMillis));
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


    /**
     * Вывод статистики по тегам
     */
    public static void printTagStatistics(List<Task> tasks) {
        Map<Tag, Double> tagStats = aggregateEstimatedHoursWithCustomCollector(tasks);
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("СТАТИСТИКА ПО ТЕГАМ ЗАДАЧ");
        System.out.println("=".repeat(50));
        
        if (tagStats.isEmpty()) {
            System.out.println("Нет данных для отображения");
            return;
        }
        
        // Сортируем по времени (убывание)
        tagStats.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .forEach(entry -> {
                    Tag tag = entry.getKey();
                    Double hours = entry.getValue();
                    System.out.printf("%-15s: %8.2f часов%n", tag, hours);
                });
        
        // Общая сводка
        double totalHours = tagStats.values().stream().mapToDouble(Double::doubleValue).sum();
        System.out.println("-".repeat(50));
        System.out.printf("ОБЩИЙ ИТОГ: %.2f часов%n", totalHours);
        System.out.println("=".repeat(50));
    }

    public static final class TimedResult<T> {
        private final T result;
        private final Duration duration;
        public TimedResult(T result, Duration duration) { this.result = result; this.duration = duration; }
        public T getResult() { return result; }
        public Duration getDuration() { return duration; }
    }
}