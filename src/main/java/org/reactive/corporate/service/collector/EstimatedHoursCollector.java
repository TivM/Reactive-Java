package org.reactive.corporate.service.collector;

import org.reactive.corporate.model.Task;

import java.util.DoubleSummaryStatistics;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class EstimatedHoursCollector implements Collector<Task, DoubleSummaryStatistics, DoubleSummaryStatistics> {
    @Override
    public Supplier<DoubleSummaryStatistics> supplier() {
        return DoubleSummaryStatistics::new;
    }


    @Override
    public BiConsumer<DoubleSummaryStatistics, Task> accumulator() {
        return (acc, task) -> acc.accept(task.getEstimatedHours());
    }


    @Override
    public BinaryOperator<DoubleSummaryStatistics> combiner() {
        return (a, b) -> { a.combine(b); return a; };
    }


    @Override
    public Function<DoubleSummaryStatistics, DoubleSummaryStatistics> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
    }
}
