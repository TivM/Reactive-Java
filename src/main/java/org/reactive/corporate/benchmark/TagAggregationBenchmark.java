package org.reactive.corporate.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.reactive.corporate.model.Task;
import org.reactive.corporate.model.Worker;
import org.reactive.corporate.model.enums.Tag;
import org.reactive.corporate.service.AnalyticsService;
import org.reactive.corporate.service.DataGenerator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1, warmups = 1)
@State(Scope.Benchmark)
public class TagAggregationBenchmark {

    @Param({"1000", "5000", "20000", "30000", "40000", "50000"})
    public int taskCount;

    @Param({"0", "5"})
    public long delayMillis;

    private List<Task> tasks;

    @Setup(Level.Trial)
    public void setup() {
        DataGenerator generator = new DataGenerator();
        List<Worker> workers = generator.generateWorkers(Math.max(10, taskCount / 10));
        tasks = generator.generateTasks(taskCount, workers);
    }

    @Benchmark
    public Map<Tag, Double> sequentialStream() {
        return AnalyticsService.aggregateByTagStreamSequential(tasks, delayMillis);
    }

    @Benchmark
    public Map<Tag, Double> parallelStream() {
        return AnalyticsService.aggregateByTagStreamParallel(tasks, delayMillis);
    }

    @Benchmark
    public Map<Tag, Double> customSpliteratorParallel() {
        return AnalyticsService.aggregateByTagStreamWithCustomSpliterator(tasks, delayMillis);
    }
}

