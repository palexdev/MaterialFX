package properties;

import io.github.palexdev.materialfx.beans.properties.synced.SynchronizedObjectProperty;
import io.github.palexdev.materialfx.demo.model.SimplePerson;
import io.github.palexdev.materialfx.utils.ExecutionUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SynchronizedObjectTests {
	private final ObjectProperty<SimplePerson> objectProperty = new SimpleObjectProperty<>();

	@BeforeEach
	public void setUp() {
		objectProperty.set(null);
	}

	@Test
	public void testSync() {
		SynchronizedObjectProperty<SimplePerson> synced = new SynchronizedObjectProperty<>();
		synced.setAndWait(new SimplePerson("Jack"), objectProperty);
		objectProperty.set(new SimplePerson("Rose"));
		assertEquals("Jack", synced.get().getName());
		assertEquals("Rose", objectProperty.get().getName());
	}

	@Test
	public void testBind() {
		SynchronizedObjectProperty<SimplePerson> synced = new SynchronizedObjectProperty<>();
		synced.bind(objectProperty);
		objectProperty.set(new SimplePerson("Mark"));
		assertEquals("Mark", synced.get().getName());
		assertEquals("Mark", objectProperty.get().getName());
	}

	@Test
	public void testBindBidirectional() {
		AtomicReference<SimplePerson> aValue = new AtomicReference<>();
		AtomicReference<SimplePerson> bValue = new AtomicReference<>();

		SynchronizedObjectProperty<SimplePerson> synced = new SynchronizedObjectProperty<>();
		synced.bindBidirectional(objectProperty);

		ExecutionUtils.executeWhen(
				synced,
				(oldValue, newValue) -> aValue.set(newValue),
				false,
				(oldValue, newValue) -> newValue != null,
				true
		);
		objectProperty.set(new SimplePerson("Jack"));
		assertEquals("Jack", aValue.get().getName());
		assertEquals("Jack", objectProperty.get().getName());

		ExecutionUtils.executeWhen(
				objectProperty,
				(oldValue, newValue) -> bValue.set(newValue),
				false,
				(oldValue, newValue) -> newValue != null,
				true
		);
		synced.set(new SimplePerson("Rose"));
		assertEquals("Rose", bValue.get().getName());
		assertEquals("Rose", synced.get().getName());
	}

	@Test
	public void testFailSync() {
		SynchronizedObjectProperty<SimplePerson> synced1 = new SynchronizedObjectProperty<>();
		SynchronizedObjectProperty<SimplePerson> synced2 = new SynchronizedObjectProperty<>();
		synced1.setAndWait(new SimplePerson("Mark"), synced2);
		assertThrows(IllegalArgumentException.class, () -> synced2.setAndWait(new SimplePerson("Leia"), synced1));
	}
}
