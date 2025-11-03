package org.reactive.corporate.service.spliterator;

import org.reactive.corporate.model.Task;
import org.reactive.corporate.model.enums.Tag;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;

public final class TaskTagSpliterator implements Spliterator<Map.Entry<Tag, Double>> {

    private final List<Task> tasks;
    private final long delayMillis;

    //Текущий индекс задачи в списке
    private int currentIndex;
    private final int endExclusive;
    private Iterator<Tag> tagIterator;
    private double currentTaskHours;
    private long estimatedRemaining;

    public TaskTagSpliterator(List<Task> tasks, long delayMillis) {
        this(tasks, 0, tasks.size(), delayMillis);
    }

    private TaskTagSpliterator(List<Task> tasks, int start, int end, long delayMillis) {
        this.tasks = tasks;
        this.delayMillis = delayMillis;
        this.currentIndex = start;
        this.endExclusive = end;
        this.estimatedRemaining = estimateEntries(start, end);
    }

    private long estimateEntries(int start, int end) {
        long count = 0;
        for (int i = start; i < end; i++) {
            count += tasks.get(i).getTags().size();
        }
        return Math.max(count, 0);
    }

    @Override
    public boolean tryAdvance(Consumer<? super Map.Entry<Tag, Double>> action) {
        while (true) {
            if (tagIterator != null && tagIterator.hasNext()) {
                Tag tag = tagIterator.next();
                action.accept(Map.entry(tag, currentTaskHours));
                estimatedRemaining--;
                return true;
            }

            if (currentIndex >= endExclusive) {
                tagIterator = null;
                return false;
            }

            Task task = tasks.get(currentIndex++);
            currentTaskHours = task.getEstimatedHours(delayMillis);
            tagIterator = task.getTags().iterator();
            if (!tagIterator.hasNext()) {
                tagIterator = null;
            }
        }
    }

    @Override
    public Spliterator<Map.Entry<Tag, Double>> trySplit() {
        if (currentIndex >= endExclusive - 1) {
            return null;
        }
        if (tagIterator != null && tagIterator.hasNext()) {
            return null;
        }

        int mid = currentIndex + (endExclusive - currentIndex) / 2;
        if (mid == currentIndex) {
            return null;
        }

        TaskTagSpliterator prefix = new TaskTagSpliterator(tasks, currentIndex, mid, delayMillis);
        this.estimatedRemaining -= prefix.estimatedRemaining;
        this.currentIndex = mid;
        return prefix;
    }

    @Override
    public long estimateSize() {
        return Math.max(estimatedRemaining, 0);
    }

    @Override
    public int characteristics() {
        return Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.NONNULL;
    }
}


