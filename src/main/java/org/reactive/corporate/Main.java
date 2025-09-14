package org.reactive.corporate;

import org.reactive.corporate.model.Task;
import org.reactive.corporate.model.Worker;
import org.reactive.corporate.model.enums.Priority;
import org.reactive.corporate.service.AnalyticsService;
import org.reactive.corporate.service.DataGenerator;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        DataGenerator gen = new DataGenerator();


        List<Worker> workers = gen.generateWorkers(50);


        int[] sizes = {5_000, 50_000, 250_000};
        for (int size : sizes) {
            System.out.println("--- Running with tasks = " + size + " ---");
            List<Task> tasks = gen.generateTasks(size, workers);


            // Iterative
            var iterative = AnalyticsService.timeExecution(() -> AnalyticsService.aggregateByPriorityIterative(tasks));
            System.out.printf("Iterative: duration=%d ms. Result sample: %s\n", iterative.getDuration().toMillis(), limitMapSample(iterative.getResult()));


            // Stream + collectors
            var stream = AnalyticsService.timeExecution(() -> AnalyticsService.aggregateByPriorityStream(tasks));
            System.out.printf("Stream+Collectors: duration=%d ms. Result sample: %s\n", stream.getDuration().toMillis(), limitMapSample(stream.getResult()));


            // Custom collector (global summary)
            var custom = AnalyticsService.timeExecution(() -> AnalyticsService.aggregateEstimatedHoursWithCustomCollector(tasks));
            DoubleSummaryStatistics stats = custom.getResult();
            System.out.printf("CustomCollector: duration=%d ms. count=%d, sum=%.2f, avg=%.2f\n",
                    custom.getDuration().toMillis(), stats.getCount(), stats.getSum(), stats.getAverage());


            System.out.println();
        }


        System.out.println("Done.");
    }


    private static String limitMapSample(Map<Priority, Double> map) {
        StringBuilder sb = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<Priority, Double> e : map.entrySet()) {
            if (i++ > 0) sb.append(", ");
            sb.append(e.getKey()).append("=").append(String.format("%.2f", e.getValue()));
            if (i >= 4) break;
        }
        sb.append("}");
        return sb.toString();
    }

}
