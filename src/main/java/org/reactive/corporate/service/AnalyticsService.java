package org.reactive.corporate.service;

import org.reactive.corporate.model.Task;
import org.reactive.corporate.model.enums.Tag;
import org.reactive.corporate.service.collector.EstimatedHoursCollector;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalyticsService {

    public static Map<Tag, Double> aggregateEstimatedHoursWithCustomCollector(List<Task> tasks) {
        return tasks.stream().collect(new EstimatedHoursCollector());
    }
    
    //1 Итеративный подход для статистики по тегам
    public static Map<Tag, Double> aggregateByTagIterative(List<Task> tasks) {
        Map<Tag, Double> map = new EnumMap<>(Tag.class);
        for (Task task : tasks) {
            for (Tag tag : task.getTags()) {
                map.merge(tag, task.getEstimatedHours(), Double::sum);
            }
        }
        return map;
    }

    //2 Stream + стандартные коллекторы для статистики по тегам
    public static Map<Tag, Double> aggregateByTagStream(List<Task> tasks) {
        return tasks.stream()
                .flatMap(task -> task.getTags().stream()
                        .map(tag -> Map.entry(tag, task.getEstimatedHours())))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        () -> new EnumMap<>(Tag.class),
                        Collectors.summingDouble(Map.Entry::getValue)
                ));
    }

    //3 Кастомный коллектор для статистики по тегам
    public static Map<Tag, Double> aggregateByTagCustomCollector(List<Task> tasks) {
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