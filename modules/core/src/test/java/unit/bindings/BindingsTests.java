package unit.bindings;

import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.base.bindings.*;
import io.github.palexdev.mfxcore.base.bindings.base.Updater;
import io.github.palexdev.mfxcore.base.properties.SizeProperty;
import io.github.palexdev.mfxcore.enums.BindingState;
import javafx.beans.property.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class BindingsTests {
	private final MFXBindings bindings = MFXBindings.instance();

	@AfterEach
	void cleanUp() {
		bindings.dispose();
	}

	@Test
	void testBinding1() {
		IntegerProperty i1 = new SimpleIntegerProperty();
		IntegerProperty i2 = new SimpleIntegerProperty();

		bindings.bind().target(i1).source(i2).get();
		i2.set(5);
		assertEquals(5, i1.get());
	}

	@Test
	void testBinding2() {
		SizeProperty size = new SizeProperty();
		StringProperty toString = new SimpleStringProperty();

		Function<Size, String> fn = s -> "W:%f\nH:%f".formatted(s.getWidth(), s.getHeight());

		bindings.<String>bind()
				.target(toString)
				.source(new MappingSource<Size, String>(size).setTargetUpdater(
						new MappedUpdater<>(
								Mapper.of(fn),
								Updater.implicit(toString)
						)
				))
				.get();

		size.set(Size.of(45.45, 10.0));
		assertEquals(fn.apply(size.get()), toString.get());
	}

	@Test
	void testBinding3() {
		SizeProperty size = new SizeProperty();
		StringProperty toString = new SimpleStringProperty();

		Function<Size, String> fn = s -> "W:%f\nH:%f".formatted(s.getWidth(), s.getHeight());

		bindings.bind(toString)
				.source(new MappingSource<Size, String>(size).setTargetUpdater(
						new MappedUpdater<>(
								Mapper.of(fn).orElse(() -> ""),
								Updater.implicit(toString)
						)
				))
				.get()
				.invalidate();

		assertEquals("", toString.get());
		size.set(Size.of(45.45, 10.0));
		assertEquals(fn.apply(size.get()), toString.get());
	}


	@Test
	void testBinding4() {
		SizeProperty size = new SizeProperty();
		StringProperty toString = new SimpleStringProperty();
		DoubleProperty scale = new SimpleDoubleProperty();

		Function<Size, String> fn = s -> "W:%f\nH:%f".formatted(s.getWidth() * scale.get(), s.getHeight() * scale.get());
		bindings.bind(toString)
				.source(new MappingSource.Builder<Size, String>()
						.observable(size)
						.targetUpdater(new MappedUpdater<>(
								Mapper.of(fn).orElse(() -> ""),
								Updater.implicit(toString)
						))
				)
				.addTargetInvalidatingSource(scale)
				.get()
				.invalidate();

		assertEquals("", toString.get());
		size.set(Size.of(50.50, 10.5));
		assertEquals("W:%f\nH:%f".formatted(0.0, 0.0), toString.get());

		scale.set(1.0);
		assertEquals(fn.apply(size.get()), toString.get());

		scale.set(2.0);
		assertEquals(fn.apply(size.get()), toString.get());
	}

	@Test
	public void testBinding5() {
		assertTrue(bindings.isEmpty());

		IntegerProperty source = new SimpleIntegerProperty();
		IntegerProperty target = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindings.isBound(this) && !bindings.isIgnoreBinding(this);
			}
		};

		bindings.bind().target(target).source(source).get(); // Implicit Updater
		source.set(1);
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		assertThrows(RuntimeException.class, () -> target.set(5));
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		bindings.unbind(target);
	}

	@Test
	public void testBinding6() {
		assertTrue(bindings.isEmpty());

		ReadOnlyIntegerWrapper source = new ReadOnlyIntegerWrapper();
		IntegerProperty target = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindings.isBound(this) && !bindings.isIgnoreBinding(this);
			}
		};

		bindings.bind().target(target).source(source).get(); // Implicit Updater
		source.set(1);
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		assertThrows(RuntimeException.class, () -> target.set(5));
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		bindings.unbind(target);
	}

	@Test
	public void testBindAndUnbind() {
		assertTrue(bindings.isEmpty());

		IntegerProperty source = new SimpleIntegerProperty();
		IntegerProperty target = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindings.isBound(this) && !bindings.isIgnoreBinding(this);
			}
		};

		bindings.bind().target(target).source(source).get(); // Implicit Updater
		source.set(1);
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		bindings.unbind(target);
		assertFalse(bindings.isBound(target));

		target.set(5);
		assertEquals(1, source.get());
		assertEquals(5, target.get());

		bindings.unbind(target);
	}

	@Test
	public void testReadOnly1() {
		assertTrue(bindings.isEmpty());

		IntegerProperty source = new SimpleIntegerProperty();
		ReadOnlyIntegerWrapper target = new ReadOnlyIntegerWrapper() {
			@Override
			public boolean isBound() {
				return (bindings.isBound(this) || bindings.isBound(getReadOnlyProperty())) && !bindings.isIgnoreBinding(getReadOnlyProperty());
			}
		};

		bindings.bind(target.getReadOnlyProperty())
				.source(Source.of(source).setTargetUpdater(Updater.implicit(target)))
				.get();
		assertTrue(target.isBound());

		source.set(1);
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		bindings.unbindReadOnly(target, false);
		assertFalse(target.isBound());

		target.set(5);
		assertEquals(1, source.get());
		assertEquals(5, target.get());

		bindings.unbindReadOnly(target, false);
	}

	@Test
	public void testReadOnly2() {
		assertTrue(bindings.isEmpty());

		ReadOnlyIntegerWrapper source = new ReadOnlyIntegerWrapper();
		ReadOnlyIntegerWrapper target = new ReadOnlyIntegerWrapper() {
			@Override
			public boolean isBound() {
				return (bindings.isBound(this) || bindings.isBound(getReadOnlyProperty())) && !bindings.isIgnoreBinding(getReadOnlyProperty());
			}
		};

		bindings.bind(target.getReadOnlyProperty())
				.source(Source.of(source.getReadOnlyProperty()).setTargetUpdater(Updater.implicit(target)))
				.get();
		assertTrue(target.isBound());

		source.set(1);
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		bindings.unbindReadOnly(target, false);
		assertFalse(target.isBound());

		target.set(5);
		assertEquals(1, source.get());
		assertEquals(5, target.get());

		bindings.unbindReadOnly(target, false);
	}

	@Test
	public void testMultipleBindings() {
		assertTrue(bindings.isEmpty());

		IntegerProperty i1 = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindings.isBound(this) && !bindings.isIgnoreBinding(this);
			}
		};
		IntegerProperty i2 = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindings.isBound(this) && !bindings.isIgnoreBinding(this);
			}
		};
		IntegerProperty i3 = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindings.isBound(this) && !bindings.isIgnoreBinding(this);
			}
		};

		bindings.bind()
				.target(i3)
				.source(i2) // Implicit Updater
				.get();
		bindings.bind()
				.target(i2)
				.source(i1) // Implicit Updater
				.get();

		i1.set(5);
		assertEquals(5, i1.get());
		assertEquals(5, i2.get());
		assertEquals(5, i3.get());

		assertThrows(RuntimeException.class, () -> i2.set(3));
		assertThrows(RuntimeException.class, () -> i3.set(3));

		bindings.dispose();
		assertTrue(bindings.isEmpty());

		bindings.bind()
				.target(i3)
				.source(i2) // Implicit Updater
				.get();
		bindings.bind()
				.target(i1)
				.source(i2) // Implicit Updater
				.get();

		i2.set(10);
		assertEquals(10, i1.get());
		assertEquals(10, i2.get());
		assertEquals(10, i3.get());

		bindings.dispose();
	}

	@Test
	public void testOverrideBinding() {
		assertTrue(bindings.isEmpty());

		IntegerProperty i1 = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindings.isBound(this) && !bindings.isIgnoreBinding(this);
			}
		};
		IntegerProperty i2 = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindings.isBound(this) && !bindings.isIgnoreBinding(this);
			}
		};
		IntegerProperty i3 = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindings.isBound(this) && !bindings.isIgnoreBinding(this);
			}
		};


		Binding<Number> b1 = bindings.bind(i2)
				.source(new MappingSource<Number, Number>(i1)
						.setTargetUpdater(new MappedUpdater<>(
								Mapper.of(v -> v.intValue() * 2),
								Updater.implicit(i2)
						)))
				.get();
		assertEquals(1, bindings.size());

		Binding<Number> b2 = bindings.bind(i2)
				.source(new MappingSource<Number, Number>(i3)
						.setTargetUpdater(new MappedUpdater<>(
								Mapper.of(v -> v.intValue() * 3),
								Updater.implicit(i2)
						)))
				.get();
		assertEquals(1, bindings.size());
		assertEquals(BindingState.DISPOSED, b1.state());
		assertEquals(BindingState.BOUND, b2.state());

		i1.set(50);
		assertEquals(50, i1.get());
		assertEquals(0, i2.get());
		assertEquals(0, i3.get());

		assertThrows(RuntimeException.class, () -> i2.set(100));

		i3.set(10);
		assertEquals(50, i1.get());
		assertEquals(30, i2.get());
		assertEquals(10, i3.get());

		bindings.dispose();
	}

	@Test
	public void testWithGeneric() {
		assertTrue(bindings.isEmpty());

		IntegerProperty source = new SimpleIntegerProperty();
		IntegerProperty target = new SimpleIntegerProperty() {
			@Override
			public boolean isBound() {
				return bindings.isBound(this) && !bindings.isIgnoreBinding(this);
			}
		};

		bindings.bind(target).source(source).get(); // Implicit Updater

		source.set(1);
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		assertThrows(RuntimeException.class, () -> target.set(5));
		assertEquals(1, source.get());
		assertEquals(1, target.get());

		bindings.unbind(target);
	}
}
