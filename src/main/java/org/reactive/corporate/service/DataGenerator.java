package org.reactive.corporate.service;

import org.reactive.corporate.model.Task;
import org.reactive.corporate.model.Worker;
import org.reactive.corporate.model.dto.MetaInfo;
import org.reactive.corporate.model.enums.Priority;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {
    private static final String[] SAMPLE_TITLES = {
            "Update client API", "Fix DB connection leak", "Implement feature X", "Refactor module Y",
            "Deploy to staging", "Write unit tests", "Investigate production error", "Prepare demo"
    };
    private static final String[] SAMPLE_TAGS = {"backend", "frontend", "bug", "urgent", "deploy", "research"};
    private static final String[] DEPARTMENTS = {"Engineering", "QA", "DevOps", "Product"};
    private static final String[] NAMES = {"Ivan Petrov", "Olga Smirnova", "John Doe", "Jane Roe", "Max Mustermann"};


    public List<Worker> generateWorkers(int count) {
        List<Worker> list = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            String name = NAMES[ThreadLocalRandom.current().nextInt(NAMES.length)] + " " + i;
            String dept = DEPARTMENTS[ThreadLocalRandom.current().nextInt(DEPARTMENTS.length)];
            list.add(new Worker(i, name, dept));
        }
        return list;
    }


    public List<Task> generateTasks(int count, List<Worker> workers) {
        List<Task> tasks = new ArrayList<>(count);
        int workersSize = workers.size();
        for (int i = 1; i <= count; i++) {
            var rnd = ThreadLocalRandom.current();
            String title = SAMPLE_TITLES[rnd.nextInt(SAMPLE_TITLES.length)] + " #" + i;
            LocalDateTime due = LocalDateTime.ofEpochSecond(rnd.nextLong(InstantBounds.pastEpoch(), InstantBounds.futureEpoch()), 0, ZoneOffset.UTC);
            Priority p = Priority.values()[rnd.nextInt(Priority.values().length)];
            MetaInfo meta = new MetaInfo("system", LocalDateTime.now().minusDays(rnd.nextInt(0, 30)));
            List<String> tags = new ArrayList<>();
            int tagsCount = rnd.nextInt(1, 4);
            for (int t = 0; t < tagsCount; t++) tags.add(SAMPLE_TAGS[rnd.nextInt(SAMPLE_TAGS.length)]);
            double est = Math.round((rnd.nextDouble(0.5, 40.0)) * 10.0) / 10.0; // hours
            boolean completed = rnd.nextBoolean();
            int assignee = workers.get(rnd.nextInt(workersSize)).getId();
            tasks.add(new Task(i, title, due, p, meta, tags, est, completed, assignee));
        }
        return tasks;
    }


    // small helper for epoch bounds
    private static class InstantBounds {
        static long pastEpoch() { // 30 days ago
            return LocalDateTime.now().minusDays(30).toEpochSecond(ZoneOffset.UTC);
        }
        static long futureEpoch() { // 30 days from now
            return LocalDateTime.now().plusDays(30).toEpochSecond(ZoneOffset.UTC);
        }
    }
}
