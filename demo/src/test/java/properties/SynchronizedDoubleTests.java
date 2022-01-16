package properties;

import io.github.palexdev.materialfx.beans.properties.synced.SynchronizedDoubleProperty;
import io.github.palexdev.materialfx.utils.ExecutionUtils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(ApplicationExtension.class)
public class SynchronizedDoubleTests {
	private final DoubleProperty doubleProperty = new SimpleDoubleProperty();

	@BeforeEach
	public void setUp() {
		doubleProperty.set(0.0);
	}

	@Test
	public void testSync() {
		SynchronizedDoubleProperty synced = new SynchronizedDoubleProperty();
		synced.setAndWait(9.9, doubleProperty);
		doubleProperty.set(7.5);
		assertEquals(9.9, synced.get());
		assertEquals(7.5, doubleProperty.get());
	}

	@Test
	public void testBind() {
		SynchronizedDoubleProperty synced = new SynchronizedDoubleProperty();
		synced.bind(doubleProperty);
		doubleProperty.set(8.8);
		assertEquals(8.8, synced.get());
		assertEquals(8.8, doubleProperty.get());
	}

	@Test
	public void testBindBidirectional() {
		AtomicReference<Double> aValue = new AtomicReference<>();
		AtomicReference<Double> bValue = new AtomicReference<>();

		SynchronizedDoubleProperty synced = new SynchronizedDoubleProperty();
		synced.bindBidirectional(doubleProperty);

		ExecutionUtils.executeWhen(
				synced,
				(oldValue, newValue) -> aValue.set(newValue.doubleValue()),
				false,
				(oldValue, newValue) -> newValue != null,
				true
		);
		doubleProperty.set(8.5);
		assertEquals(8.5, aValue.get());
		assertEquals(8.5, doubleProperty.get());

		ExecutionUtils.executeWhen(
				doubleProperty,
				(oldValue, newValue) -> bValue.set(newValue.doubleValue()),
				false,
				(oldValue, newValue) -> newValue != null,
				true
		);
		synced.set(7.5);
		assertEquals(7.5, bValue.get());
		assertEquals(7.5, synced.get());
	}

	@Test
	public void testFailSync() {
		SynchronizedDoubleProperty synced1 = new SynchronizedDoubleProperty();
		SynchronizedDoubleProperty synced2 = new SynchronizedDoubleProperty();
		synced1.setAndWait(0.36, synced2);
		assertThrows(IllegalArgumentException.class, () -> synced2.setAndWait(0.56, synced1));
	}
}
