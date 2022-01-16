package properties;

import io.github.palexdev.materialfx.beans.properties.synced.SynchronizedFloatProperty;
import io.github.palexdev.materialfx.utils.ExecutionUtils;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SynchronizedFloatTests {
	private final FloatProperty floatProperty = new SimpleFloatProperty();

	@BeforeEach
	public void setUp() {
		floatProperty.set(0.0F);
	}

	@Test
	public void testSync() {
		SynchronizedFloatProperty synced = new SynchronizedFloatProperty();
		synced.setAndWait(0.9F, floatProperty);
		floatProperty.set(0.7F);
		assertEquals(0.9F, synced.get());
		assertEquals(0.7F, floatProperty.get());
	}

	@Test
	public void testBind() {
		SynchronizedFloatProperty synced = new SynchronizedFloatProperty();
		synced.bind(floatProperty);
		floatProperty.set(0.8F);
		assertEquals(0.8F, synced.get());
		assertEquals(0.8F, floatProperty.get());
	}

	@Test
	public void testBindBidirectional() {
		AtomicReference<Float> aValue = new AtomicReference<>();
		AtomicReference<Float> bValue = new AtomicReference<>();

		SynchronizedFloatProperty synced = new SynchronizedFloatProperty();
		synced.bindBidirectional(floatProperty);

		ExecutionUtils.executeWhen(
				synced,
				(oldValue, newValue) -> aValue.set(newValue.floatValue()),
				false,
				(oldValue, newValue) -> newValue != null,
				true
		);
		floatProperty.set(0.8F);
		assertEquals(0.8F, aValue.get());
		assertEquals(0.8F, floatProperty.get());

		ExecutionUtils.executeWhen(
				floatProperty,
				(oldValue, newValue) -> bValue.set(newValue.floatValue()),
				false,
				(oldValue, newValue) -> newValue != null,
				true
		);
		synced.set(0.7F);
		assertEquals(0.7F, bValue.get());
		assertEquals(0.7F, synced.get());
	}

	@Test
	public void testFailSync() {
		SynchronizedFloatProperty synced1 = new SynchronizedFloatProperty();
		SynchronizedFloatProperty synced2 = new SynchronizedFloatProperty();
		synced1.setAndWait(0.1F, synced2);
		assertThrows(IllegalArgumentException.class, () -> synced2.setAndWait(0.2F, synced1));
	}
}
