package properties;

import io.github.palexdev.materialfx.beans.properties.synced.SynchronizedLongProperty;
import io.github.palexdev.materialfx.utils.ExecutionUtils;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SynchronizedLongTests {
	private final LongProperty longProperty = new SimpleLongProperty();

	@BeforeEach
	public void setUp() {
		longProperty.set(0L);
	}

	@Test
	public void testSync() {
		SynchronizedLongProperty synced = new SynchronizedLongProperty();
		synced.setAndWait(9L, longProperty);
		longProperty.set(7L);
		assertEquals(9L, synced.get());
		assertEquals(7L, longProperty.get());
	}

	@Test
	public void testBind() {
		SynchronizedLongProperty synced = new SynchronizedLongProperty();
		synced.bind(longProperty);
		longProperty.set(8L);
		assertEquals(8L, synced.get());
		assertEquals(8L, longProperty.get());
	}

	@Test
	public void testBindBidirectional() {
		AtomicLong aValue = new AtomicLong();
		AtomicLong bValue = new AtomicLong();

		SynchronizedLongProperty synced = new SynchronizedLongProperty();
		synced.bindBidirectional(longProperty);

		ExecutionUtils.executeWhen(
				synced,
				(oldValue, newValue) -> aValue.set(newValue.longValue()),
				false,
				(oldValue, newValue) -> newValue != null,
				true
		);
		longProperty.set(8L);
		assertEquals(8L, aValue.get());
		assertEquals(8L, longProperty.get());

		ExecutionUtils.executeWhen(
				longProperty,
				(oldValue, newValue) -> bValue.set(newValue.longValue()),
				false,
				(oldValue, newValue) -> newValue != null,
				true
		);
		synced.set(7L);
		assertEquals(7L, bValue.get());
		assertEquals(7L, synced.get());
	}

	@Test
	public void testFailSync() {
		SynchronizedLongProperty synced1 = new SynchronizedLongProperty();
		SynchronizedLongProperty synced2 = new SynchronizedLongProperty();
		synced1.setAndWait(1L, synced2);
		assertThrows(IllegalArgumentException.class, () -> synced2.setAndWait(2L, synced1));
	}
}
