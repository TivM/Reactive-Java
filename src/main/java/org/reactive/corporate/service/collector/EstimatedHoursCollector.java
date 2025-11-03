package org.reactive.corporate.service.collector;

import org.reactive.corporate.model.Task;
import org.reactive.corporate.model.enums.Tag;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Кастомный коллектор для сбора статистики по тегам задач.
 * Группирует задачи по тегам и суммирует оценочные часы для каждого тега.
 */
public class EstimatedHoursCollector implements Collector<Task, Map<Tag, Double>, Map<Tag, Double>> {

    private final long delayMillis;

    public EstimatedHoursCollector() {
        this(0L);
    }

    public EstimatedHoursCollector(long delayMillis) {
        this.delayMillis = Math.max(0, delayMillis);
    }
    
    @Override
    public Supplier<Map<Tag, Double>> supplier() {
        return () -> new EnumMap<>(Tag.class);
    }

    @Override
    public BiConsumer<Map<Tag, Double>, Task> accumulator() {
        return (map, task) -> {
            // Обрабатываем все теги задачи
            for (Tag tag : task.getTags()) {
                map.merge(tag, task.getEstimatedHours(delayMillis), Double::sum);
            }
        };
    }

    @Override
    public BinaryOperator<Map<Tag, Double>> combiner() {
        return (map1, map2) -> {
            // Объединяем две карты, суммируя значения для одинаковых тегов
            for (Map.Entry<Tag, Double> entry : map2.entrySet()) {
                map1.merge(entry.getKey(), entry.getValue(), Double::sum);
            }
            return map1;
        };
    }

    @Override
    public Function<Map<Tag, Double>, Map<Tag, Double>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
    }
}
