package properties;

import io.github.palexdev.materialfx.beans.properties.synced.SynchronizedBooleanProperty;
import io.github.palexdev.materialfx.utils.ExecutionUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class SynchronizedBooleanTests {
	private final BooleanProperty booleanProperty = new SimpleBooleanProperty();

	@BeforeEach
	public void setUp() {
		booleanProperty.set(false);
	}

	@Test
	public void testSync() {
		SynchronizedBooleanProperty synced = new SynchronizedBooleanProperty();
		synced.setAndWait(true, booleanProperty);
		booleanProperty.set(true);
		assertTrue(synced.get());
		assertTrue(booleanProperty.get());
	}

	@Test
	public void testBind1() {
		AtomicBoolean changed = new AtomicBoolean(false);
		SynchronizedBooleanProperty synced = new SynchronizedBooleanProperty();
		synced.bind(booleanProperty);
		synced.addListener((observable, oldValue, newValue) -> changed.set(true));
		booleanProperty.set(true);
		assertTrue(changed.get());
	}

	@Test
	public void testBind2() {
		Throwable th = null;

		SynchronizedBooleanProperty synced = new SynchronizedBooleanProperty();
		synced.bind(booleanProperty);

		try {
			synced.setAndWait(true, booleanProperty);
		} catch (Exception ex) {
			th = ex;
		}

		assertNotNull(th);
		assertEquals("A bound value cannot be set!", th.getMessage());
		assertFalse(synced.isWaiting());
	}

	@Test
	public void testBindBidirectional() {
		AtomicBoolean aValue = new AtomicBoolean();
		AtomicBoolean bValue = new AtomicBoolean();

		SynchronizedBooleanProperty synced = new SynchronizedBooleanProperty();
		synced.bindBidirectional(booleanProperty);

		ExecutionUtils.executeWhen(
				synced,
				(oldValue, newValue) -> aValue.set(newValue),
				false,
				(oldValue, newValue) -> newValue != null,
				true
		);
		booleanProperty.set(true);
		assertTrue(aValue.get());
		assertTrue(booleanProperty.get());
		synced.set(false); // Need to reset in order to fire change event

		ExecutionUtils.executeWhen(
				booleanProperty,
				(oldValue, newValue) -> bValue.set(newValue),
				false,
				(oldValue, newValue) -> newValue != null,
				true
		);
		synced.set(true);
		assertTrue(bValue.get());
		assertTrue(synced.get());
	}

	@Test
	public void testFailSync() {
		SynchronizedBooleanProperty synced1 = new SynchronizedBooleanProperty();
		SynchronizedBooleanProperty synced2 = new SynchronizedBooleanProperty();
		synced1.setAndWait(true, synced2);
		assertThrows(IllegalArgumentException.class, () -> synced2.setAndWait(true, synced1));
	}
}
