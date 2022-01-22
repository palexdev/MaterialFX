package properties;

import io.github.palexdev.materialfx.beans.properties.synced.SynchronizedStringProperty;
import io.github.palexdev.materialfx.utils.ExecutionUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SynchronizedStringTests {
	private final StringProperty stringProperty = new SimpleStringProperty();

	@BeforeEach
	public void setUp() {
		stringProperty.set(null);
	}

	@Test
	public void testSync() {
		SynchronizedStringProperty synced = new SynchronizedStringProperty();
		synced.setAndWait("SString", stringProperty);
		stringProperty.set("PString");
		assertEquals("SString", synced.get());
		assertEquals("PString", stringProperty.get());
	}

	@Test
	public void testBind() {
		SynchronizedStringProperty synced = new SynchronizedStringProperty();
		synced.bind(stringProperty);
		stringProperty.set("BString");
		assertEquals("BString", synced.get());
		assertEquals("BString", stringProperty.get());
	}

	@Test
	public void testBindBidirectional() {
		AtomicReference<String> aValue = new AtomicReference<>();
		AtomicReference<String> bValue = new AtomicReference<>();

		SynchronizedStringProperty synced = new SynchronizedStringProperty();
		synced.bindBidirectional(stringProperty);

		ExecutionUtils.executeWhen(
				synced,
				(oldValue, newValue) -> aValue.set(newValue),
				false,
				(oldValue, newValue) -> newValue != null,
				true
		);
		stringProperty.set("PString");
		assertEquals("PString", aValue.get());
		assertEquals("PString", stringProperty.get());

		ExecutionUtils.executeWhen(
				stringProperty,
				(oldValue, newValue) -> bValue.set(newValue),
				false,
				(oldValue, newValue) -> newValue != null,
				true
		);
		synced.set("SString");
		assertEquals("SString", bValue.get());
		assertEquals("SString", synced.get());
	}

	@Test
	public void testFailSync() {
		SynchronizedStringProperty synced1 = new SynchronizedStringProperty();
		SynchronizedStringProperty synced2 = new SynchronizedStringProperty();
		synced1.setAndWait("SS1", synced2);
		assertThrows(IllegalArgumentException.class, () -> synced2.setAndWait("SS2", synced1));
	}
}
