package properties;

import io.github.palexdev.materialfx.beans.properties.synced.SynchronizedObjectProperty;
import io.github.palexdev.materialfx.demo.model.Person;
import io.github.palexdev.materialfx.utils.ExecutionUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class SynchronizedObjectTests {
	private final ObjectProperty<Person> objectProperty = new SimpleObjectProperty<>();

	@BeforeEach
	public void setUp() {
		objectProperty.set(null);
	}

	@Test
	public void testSync() {
		SynchronizedObjectProperty<Person> synced = new SynchronizedObjectProperty<>();
		synced.setAndWait(new Person("Jack"), objectProperty);
		objectProperty.set(new Person("Rose"));
		assertEquals("Jack", synced.get().getName());
		assertEquals("Rose", objectProperty.get().getName());
	}

	@Test
	public void testBind() {
		SynchronizedObjectProperty<Person> synced = new SynchronizedObjectProperty<>();
		synced.bind(objectProperty);
		objectProperty.set(new Person("Mark"));
		assertEquals("Mark", synced.get().getName());
		assertEquals("Mark", objectProperty.get().getName());
	}

	@Test
	public void testBindBidirectional() {
		AtomicReference<Person> aValue = new AtomicReference<>();
		AtomicReference<Person> bValue = new AtomicReference<>();

		SynchronizedObjectProperty<Person> synced = new SynchronizedObjectProperty<>();
		synced.bindBidirectional(objectProperty);

		ExecutionUtils.executeWhen(
				synced,
				(oldValue, newValue) -> aValue.set(newValue),
				false,
				(oldValue, newValue) -> newValue != null,
				true
		);
		objectProperty.set(new Person("Jack"));
		assertEquals("Jack", aValue.get().getName());
		assertEquals("Jack", objectProperty.get().getName());

		ExecutionUtils.executeWhen(
				objectProperty,
				(oldValue, newValue) -> bValue.set(newValue),
				false,
				(oldValue, newValue) -> newValue != null,
				true
		);
		synced.set(new Person("Rose"));
		assertEquals("Rose", bValue.get().getName());
		assertEquals("Rose", synced.get().getName());
	}

	@Test
	public void testFailSync() {
		SynchronizedObjectProperty<Person> synced1 = new SynchronizedObjectProperty<>();
		SynchronizedObjectProperty<Person> synced2 = new SynchronizedObjectProperty<>();
		synced1.setAndWait(new Person("Mark"), synced2);
		assertThrows(IllegalArgumentException.class, () -> synced2.setAndWait(new Person("Leia"), synced1));
	}
}
