package collections;

import io.github.palexdev.materialfx.collections.TransformableList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class TransformableListTest {
	private final ObservableList<String> source = FXCollections.observableArrayList("A", "B", "C", "D", "E");

	@Test
	public void sortTest1() {
		TransformableList<String> transformed = new TransformableList<>(source);
		transformed.setComparator(Comparator.reverseOrder(), true);

		assertEquals(transformed.get(4), "A");
		assertEquals(transformed.indexOf("E"), 0);
		assertEquals(transformed.viewToSource(0), 4);
		assertEquals(transformed.sourceToView(0), 4);
	}

	@Test
	public void sortAndFilterTest1() {
		TransformableList<String> transformed = new TransformableList<>(source);
		transformed.setComparator(Comparator.reverseOrder(), true);
		transformed.setPredicate(s -> s.equals("A") || s.equals("C") || s.equals("E"));

		assertThrows(IndexOutOfBoundsException.class, () -> transformed.get(4));
		assertEquals(transformed.get(1), "C");
		assertEquals(transformed.indexOf("E"), 0);
		assertEquals(transformed.viewToSource(1), 2);
		assertEquals(transformed.sourceToView(1), -1);
	}

	@Test
	public void testJavaFX1() {
		SortedList<String> sorted = new SortedList<>(source);
		sorted.setComparator(Comparator.reverseOrder());

		assertEquals(sorted.get(4), "A");
		assertEquals(sorted.indexOf("E"), 0);
		assertEquals(sorted.getSourceIndex(0), 4);
		assertEquals(sorted.getViewIndex(0), 4);
	}

	@Test
	public void testJavaFX2() {
		SortedList<String> sorted = new SortedList<>(source);
		sorted.setComparator(Comparator.reverseOrder());

		FilteredList<String> filtered = new FilteredList<>(sorted);
		filtered.setPredicate(s -> s.equals("A") || s.equals("C") || s.equals("E"));

		assertThrows(IndexOutOfBoundsException.class, () -> filtered.get(4));
		assertEquals(filtered.get(1), "C");
		assertEquals(filtered.indexOf("E"), 0);
		assertEquals(filtered.getSourceIndex(1), 2);
		assertTrue(filtered.getViewIndex(1) < 0);
	}
}
