package unit;

import io.github.palexdev.mfxcore.collections.ObservableCircularQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ObservableCircularQueueTest {
    private ObservableCircularQueue<Integer> queue;

    @BeforeEach
    void setup() {
        queue = new ObservableCircularQueue<>(5);
    }

    @Test
    void testAdd() {
        for (int i = 0; i < 10; i++) {
            queue.add(i);
        }
        assertQueue();
    }

    @Test
    void testAddAll() {
        Integer[] vals = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        queue.addAll(vals);
        assertQueue();
    }

    @Test
    void testAddAll2() {
        List<Integer> vals = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        queue.addAll(vals);
        assertQueue();
    }

    @Test
    void testAddAll3() {
        List<Integer> vals = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        queue.addAll(0, vals);
        assertQueue();
    }

    @Test
    void testSetAll() {
        Integer[] vals = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        queue.setAll(vals);
        assertQueue();
    }

    @Test
    void testSetAll2() {
        List<Integer> vals = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        queue.setAll(vals);
        assertQueue();
    }

    void assertQueue() {
        assertEquals(5, queue.size());
        for (int i = 5; i < 10; i++) {
            assertEquals(i, queue.get(i - 5));
        }
    }
}
