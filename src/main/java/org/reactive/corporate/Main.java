package org.reactive.corporate;

import org.reactive.corporate.model.Task;
import org.reactive.corporate.model.Worker;
import org.reactive.corporate.model.enums.Tag;
import org.reactive.corporate.service.AnalyticsService;
import org.reactive.corporate.service.DataGenerator;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        DataGenerator gen = new DataGenerator();


        List<Worker> workers = gen.generateWorkers(50);


        int[] sizes = {5_000, 50_000, 250_000};
        for (int size : sizes) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("ТЕСТИРОВАНИЕ ПРОИЗВОДИТЕЛЬНОСТИ - " + size + " задач");
            System.out.println("=".repeat(60));
            List<Task> tasks = gen.generateTasks(size, workers);

            // 1. Итеративный подход для статистики по тегам
            var iterative = AnalyticsService.timeExecution(() -> AnalyticsService.aggregateByTagIterative(tasks));
            System.out.printf("1. Итеративный подход:     %6d ms | Результат: %s\n", 
                    iterative.getDuration().toMillis(), limitTagMapSample(iterative.getResult()));

            // 2. Stream + стандартные коллекторы для статистики по тегам
            var stream = AnalyticsService.timeExecution(() -> AnalyticsService.aggregateByTagStream(tasks));
            System.out.printf("2. Stream + коллекторы:    %6d ms | Результат: %s\n", 
                    stream.getDuration().toMillis(), limitTagMapSample(stream.getResult()));

            // 3. Кастомный коллектор для статистики по тегам
            var custom = AnalyticsService.timeExecution(() -> AnalyticsService.aggregateByTagCustomCollector(tasks));
            System.out.printf("3. Кастомный коллектор:    %6d ms | Результат: %s\n", 
                    custom.getDuration().toMillis(), limitTagMapSample(custom.getResult()));

//            // Сравнение результатов
//            System.out.println("\nСравнение результатов:");
//            System.out.printf("Итеративный == Stream:     %s\n",
//                    iterative.getResult().equals(stream.getResult()) ? "✓ СОВПАДАЕТ" : "✗ НЕ СОВПАДАЕТ");
//            System.out.printf("Итеративный == Кастомный:  %s\n",
//                    iterative.getResult().equals(custom.getResult()) ? "✓ СОВПАДАЕТ" : "✗ НЕ СОВПАДАЕТ");
//            System.out.printf("Stream == Кастомный:       %s\n",
//                    stream.getResult().equals(custom.getResult()) ? "✓ СОВПАДАЕТ" : "✗ НЕ СОВПАДАЕТ");
        }

//        // Демонстрация вывода статистики по тегам
//        System.out.println("\n" + "=".repeat(60));
//        System.out.println("ДЕМОНСТРАЦИЯ СТАТИСТИКИ ПО ТЕГАМ");
//        System.out.println("=".repeat(60));
        
        List<Task> demoTasks = gen.generateTasks(1000, workers);
        AnalyticsService.printTagStatistics(demoTasks);

        System.out.println("\nDone.");
    }



    private static String limitTagMapSample(Map<Tag, Double> map) {
        StringBuilder sb = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<Tag, Double> e : map.entrySet()) {
            if (i++ > 0) sb.append(", ");
            sb.append(e.getKey()).append("=").append(String.format("%.2fч", e.getValue()));
            if (i >= 4) break;
        }
        sb.append("}");
        return sb.toString();
    }


}
