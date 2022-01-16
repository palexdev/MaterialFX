package properties;

import io.github.palexdev.materialfx.beans.properties.synced.SynchronizedIntegerProperty;
import io.github.palexdev.materialfx.utils.ExecutionUtils;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SynchronizedIntegerTests {
	private final IntegerProperty integerProperty = new SimpleIntegerProperty();

	@BeforeEach
	public void setUp() {
		integerProperty.set(0);
	}

	@Test
	public void testSync() {
		SynchronizedIntegerProperty synced = new SynchronizedIntegerProperty();
		synced.setAndWait(9, integerProperty);
		integerProperty.set(7);
		assertEquals(9, synced.get());
		assertEquals(7, integerProperty.get());
	}

	@Test
	public void testBind() {
		SynchronizedIntegerProperty synced = new SynchronizedIntegerProperty();
		synced.bind(integerProperty);
		integerProperty.set(8);
		assertEquals(8, synced.get());
		assertEquals(8, integerProperty.get());
	}

	@Test
	public void testBindBidirectional() {
		AtomicInteger aValue = new AtomicInteger();
		AtomicInteger bValue = new AtomicInteger();

		SynchronizedIntegerProperty synced = new SynchronizedIntegerProperty();
		synced.bindBidirectional(integerProperty);

		ExecutionUtils.executeWhen(
				synced,
				(oldValue, newValue) -> aValue.set(newValue.intValue()),
				false,
				(oldValue, newValue) -> newValue != null,
				true
		);
		integerProperty.set(8);
		assertEquals(8, aValue.get());
		assertEquals(8, integerProperty.get());

		ExecutionUtils.executeWhen(
				integerProperty,
				(oldValue, newValue) -> bValue.set(newValue.intValue()),
				false,
				(oldValue, newValue) -> newValue != null,
				true
		);
		synced.set(7);
		assertEquals(7, bValue.get());
		assertEquals(7, synced.get());
	}

	@Test
	public void testFailSync() {
		SynchronizedIntegerProperty synced1 = new SynchronizedIntegerProperty();
		SynchronizedIntegerProperty synced2 = new SynchronizedIntegerProperty();
		synced1.setAndWait(1, synced2);
		assertThrows(IllegalArgumentException.class, () -> synced2.setAndWait(2, synced1));
	}

	@Test
	public void testChain1() {
		AtomicInteger a1 = new AtomicInteger();
		AtomicInteger a2 = new AtomicInteger();
		AtomicInteger a3 = new AtomicInteger();

		SynchronizedIntegerProperty synced1 = new SynchronizedIntegerProperty();
		SynchronizedIntegerProperty synced2 = new SynchronizedIntegerProperty();
		synced1.addListener((observable, oldValue, newValue) -> {
			a1.set(newValue.intValue());
			System.out.println(newValue);
		});
		synced2.addListener((observable, oldValue, newValue) -> {
			a2.set(newValue.intValue());
			System.out.println(newValue);
		});
		integerProperty.addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				a3.set(newValue.intValue());
				System.out.println(newValue);
				integerProperty.removeListener(this);
			}
		});

		synced1.setAndWait(8, synced2);
		synced2.setAndWait(10, integerProperty);
		integerProperty.set(12);

		assertEquals(8, a1.get());
		assertEquals(10, a2.get());
		assertEquals(12, a3.get());
	}

	@Test
	public void testChain2() {
		SynchronizedIntegerProperty synced1 = new SynchronizedIntegerProperty();
		SynchronizedIntegerProperty synced2 = new SynchronizedIntegerProperty();
		synced1.setAndWait(8, synced2);
		assertThrows(IllegalArgumentException.class, () -> synced2.setAndWait(10, synced1));
	}

	@Test
	public void testOverrideWait() {
		SynchronizedIntegerProperty synced1 = new SynchronizedIntegerProperty();
		SynchronizedIntegerProperty synced2 = new SynchronizedIntegerProperty();
		synced1.setAndWait(8, synced2);
		assertThrows(IllegalStateException.class, () -> synced1.setAndWait(10, integerProperty));
	}
}
