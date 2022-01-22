package binding;

import io.github.palexdev.materialfx.bindings.BindingManager;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BindingsTest {
	private final BindingManager bindingManager = BindingManager.instance();

	@Test
	public void testBinding1() {
		assertTrue(bindingManager.isEmpty());

		IntegerProperty source = new SimpleIntegerProperty();
		IntegerProperty target = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindingManager.isBound(this) && !bindingManager.isIgnoreBinding(this);
			}
		};

		bindingManager.bind(target)
				.to(source)
				.with((oldValue, newValue) -> target.setValue(newValue))
				.create();

		source.set(1);
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		assertThrows(RuntimeException.class, () -> target.set(5));
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		bindingManager.unbind(target);
	}

	@Test
	public void testBinding2() {
		assertTrue(bindingManager.isEmpty());

		ReadOnlyIntegerWrapper source = new ReadOnlyIntegerWrapper();
		IntegerProperty target = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindingManager.isBound(this) && !bindingManager.isIgnoreBinding(this);
			}
		};

		bindingManager.bind(target)
				.to(source)
				.with((oldValue, newValue) -> target.setValue(newValue))
				.create();

		source.set(1);
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		assertThrows(RuntimeException.class, () -> target.set(5));
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		bindingManager.unbind(target);
	}

	@Test
	public void testBindAndUnbind() {
		assertTrue(bindingManager.isEmpty());

		IntegerProperty source = new SimpleIntegerProperty();
		IntegerProperty target = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindingManager.isBound(this) && !bindingManager.isIgnoreBinding(this);
			}
		};

		bindingManager.bind(target)
				.to(source)
				.with((oldValue, newValue) -> target.setValue(newValue))
				.create();

		source.set(1);
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		bindingManager.unbind(target);
		assertFalse(bindingManager.isBound(target));

		target.set(5);
		assertEquals(1, source.get());
		assertEquals(5, target.get());

		bindingManager.unbind(target);
	}

	@Test
	public void testReadOnly1() {
		assertTrue(bindingManager.isEmpty());

		IntegerProperty source = new SimpleIntegerProperty();
		ReadOnlyIntegerWrapper target = new ReadOnlyIntegerWrapper() {
			@Override
			public boolean isBound() {
				return (bindingManager.isBound(this) || bindingManager.isBound(getReadOnlyProperty())) && !bindingManager.isIgnoreBinding(getReadOnlyProperty());
			}
		};

		bindingManager.bind(target.getReadOnlyProperty())
				.to(source)
				.with((oldValue, newValue) -> target.setValue(newValue))
				.create();
		assertTrue(target.isBound());

		source.set(1);
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		bindingManager.unbindReadOnly(target);
		assertFalse(target.isBound());

		target.set(5);
		assertEquals(1, source.get());
		assertEquals(5, target.get());

		bindingManager.unbindReadOnly(target);
	}

	@Test
	public void testReadOnly2() {
		assertTrue(bindingManager.isEmpty());

		ReadOnlyIntegerWrapper source = new ReadOnlyIntegerWrapper();
		ReadOnlyIntegerWrapper target = new ReadOnlyIntegerWrapper() {
			@Override
			public boolean isBound() {
				return (bindingManager.isBound(this) || bindingManager.isBound(getReadOnlyProperty())) && !bindingManager.isIgnoreBinding(getReadOnlyProperty());
			}
		};

		bindingManager.bind(target.getReadOnlyProperty())
				.to(source.getReadOnlyProperty())
				.with((oldValue, newValue) -> target.setValue(newValue))
				.create();
		assertTrue(target.isBound());

		source.set(1);
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		bindingManager.unbindReadOnly(target);
		assertFalse(target.isBound());

		target.set(5);
		assertEquals(1, source.get());
		assertEquals(5, target.get());

		bindingManager.unbindReadOnly(target);
	}

	@Test
	public void testMultipleBindings() {
		assertTrue(bindingManager.isEmpty());

		IntegerProperty i1 = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindingManager.isBound(this) && !bindingManager.isIgnoreBinding(this);
			}
		};
		IntegerProperty i2 = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindingManager.isBound(this) && !bindingManager.isIgnoreBinding(this);
			}
		};
		IntegerProperty i3 = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindingManager.isBound(this) && !bindingManager.isIgnoreBinding(this);
			}
		};

		bindingManager.bind(i3)
				.to(i2)
				.with((oldValue, newValue) -> i3.setValue(newValue))
				.create();
		bindingManager.bind(i2)
				.to(i1)
				.with((oldValue, newValue) -> i2.setValue(newValue))
				.create();

		i1.set(5);
		assertEquals(5, i1.get());
		assertEquals(5, i2.get());
		assertEquals(5, i3.get());

		assertThrows(RuntimeException.class, () -> i2.set(3));
		assertThrows(RuntimeException.class, () -> i3.set(3));

		bindingManager.dispose();
		assertTrue(bindingManager.isEmpty());

		bindingManager.bind(i3)
				.to(i2)
				.with((oldValue, newValue) -> i3.setValue(newValue))
				.create();
		bindingManager.bind(i1)
				.to(i2)
				.with((oldValue, newValue) -> i1.setValue(newValue))
				.create();

		i2.set(10);
		assertEquals(10, i1.get());
		assertEquals(10, i2.get());
		assertEquals(10, i3.get());

		bindingManager.dispose();
	}

	@Test
	public void testOverrideBinding() {
		assertTrue(bindingManager.isEmpty());

		IntegerProperty i1 = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindingManager.isBound(this) && !bindingManager.isIgnoreBinding(this);
			}
		};
		IntegerProperty i2 = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindingManager.isBound(this) && !bindingManager.isIgnoreBinding(this);
			}
		};
		IntegerProperty i3 = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindingManager.isBound(this) && !bindingManager.isIgnoreBinding(this);
			}
		};

		bindingManager.bind(i2)
				.to(i1)
				.with((oldValue, newValue) -> i2.set(newValue.intValue() * 2))
				.create();
		bindingManager.bind(i2)
				.to(i3)
				.with((oldValue, newValue) -> i2.set(newValue.intValue() * 3))
				.create();

		i1.set(50);
		assertEquals(50, i1.get());
		assertEquals(0, i2.get());
		assertEquals(0, i3.get());

		assertThrows(RuntimeException.class, () -> i2.set(100));

		i3.set(10);
		assertEquals(50, i1.get());
		assertEquals(30, i2.get());
		assertEquals(10, i3.get());

		bindingManager.dispose();
	}

	@Test
	public void testWithGeneric() {
		assertTrue(bindingManager.isEmpty());

		IntegerProperty source = new SimpleIntegerProperty();
		IntegerProperty target = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindingManager.isBound(this) && !bindingManager.isIgnoreBinding(this);
			}
		};

		bindingManager.bind(target)
				.to(source)
				.with((oldValue, newValue) -> target.setValue(newValue))
				.create();

		source.set(1);
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		assertThrows(RuntimeException.class, () -> target.set(5));
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		bindingManager.unbind(target);
	}
}
