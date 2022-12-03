package unit.bindings;

import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.base.bindings.*;
import io.github.palexdev.mfxcore.base.bindings.base.Updater;
import io.github.palexdev.mfxcore.base.properties.SizeProperty;
import javafx.beans.property.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BidirectionalBindingsTests {
	private final MFXBindings bindings = MFXBindings.instance();

	@Test
	public void biBindingTest1() {
		assertTrue(bindings.biIsEmpty());

		IntegerProperty i1 = new SimpleIntegerProperty();
		IntegerProperty i2 = new SimpleIntegerProperty();

		bindings.bindBidirectional(i1)
				.addSource(new Source<>(i2).implicit(i1, i2))
				.get();

		i1.set(10);
		assertEquals(10, i1.get());
		assertEquals(10, i2.get());

		i2.set(20);
		assertEquals(20, i1.get());
		assertEquals(20, i2.get());

		bindings.unbindBidirectional(i1, i2);
	}

	@Test
	public void biBindingTest2() {
		assertTrue(bindings.biIsEmpty());

		IntegerProperty iA = new SimpleIntegerProperty();
		IntegerProperty iB = new SimpleIntegerProperty();
		IntegerProperty iC = new SimpleIntegerProperty();

		bindings.bindBidirectional(iA)
				.addSource(new Source<>(iB).implicit(iA, iB))
				.addSource(new Source<>(iC).implicit(iA, iC))
				.get();

		iA.set(8); // All properties must be 8
		assertEquals(8, iA.get());
		assertEquals(8, iB.get());
		assertEquals(8, iC.get());

		iB.set(10); // All properties must be 10
		assertEquals(10, iA.get());
		assertEquals(10, iB.get());
		assertEquals(10, iC.get());

		iC.set(12); // All properties must be
		assertEquals(12, iA.get());
		assertEquals(12, iB.get());
		assertEquals(12, iC.get());

		bindings.unbindBidirectional(iA);
	}

	@Test
	public void biBindingTest3() {
		assertTrue(bindings.biIsEmpty());

		SizeProperty size = new SizeProperty();
		StringProperty width = new SimpleStringProperty();
		DoubleProperty height = new SimpleDoubleProperty();

		bindings.bindBidirectional(size)
				.addSource(new MappingSource<String, Size>(width)
						.setTargetUpdater(new MappedUpdater<>(
								Mapper.<String, Size>of(s -> Size.of(Double.parseDouble(s), size.getHeight()))
										.orElse(() -> Size.of(0.0, 0.0)),
								Updater.implicit(size)
						))
						.setSourceUpdater(new MappedUpdater<>(
								Mapper.<Size, String>of(s -> String.valueOf(s.getWidth()))
										.orElse(() -> ""),
								Updater.implicit(width)
						))
				)
				.addSource(new MappingSource<Number, Size>(height)
						.setTargetUpdater(new MappedUpdater<>(
								Mapper.<Number, Size>of(n -> Size.of(size.getWidth(), n.doubleValue()))
										.orElse(() -> Size.of(0.0, 0.0)),
								Updater.implicit(size)
						))
						.setSourceUpdater(new MappedUpdater<>(
								Mapper.<Size, Number>of(Size::getHeight).orElse(() -> 0.0),
								Updater.implicit(height)
						))
				)
				.get();

		size.set(Size.of(10.0, 20.0));
		assertEquals("10.0", width.get());
		assertEquals(20.0, height.get());

		width.set("55.5");
		assertEquals(55.5, size.get().getWidth());
		assertEquals(20.0, height.get());

		height.set(80.0);
		assertEquals(80.0, size.get().getHeight());
		assertEquals("55.5", width.get());

		bindings.unbindBidirectional(size);
		assertTrue(bindings.biIsEmpty());
	}

	@Test
	public void testEagerBinding() {
		assertTrue(bindings.biIsEmpty());

		IntegerProperty i1 = new SimpleIntegerProperty(null, "1", 5);
		IntegerProperty i2 = new SimpleIntegerProperty(null, "2", 10);
		IntegerProperty i3 = new SimpleIntegerProperty(null, "3", 15);

		bindings.bindBidirectional(i1)
				.addSources(
						new Source<>(i2).implicit(i1, i2),
						new Source<>(i3).implicit(i1, i3)
				)
				.get()
				.invalidate();

		assertEquals(15, i1.get());
		assertEquals(15, i2.get());
		assertEquals(15, i3.get());

		bindings.unbindBidirectional(i1);
		assertTrue(bindings.biIsEmpty());
	}

	@Test
	public void testLazyBinding() {
		assertTrue(bindings.biIsEmpty());

		IntegerProperty i1 = new SimpleIntegerProperty(5);
		IntegerProperty i2 = new SimpleIntegerProperty(10);
		IntegerProperty i3 = new SimpleIntegerProperty(15);

		bindings.bindBidirectional(i1)
				.addSources(
						new Source<>(i2).implicit(i1, i2),
						new Source<>(i3).implicit(i1, i3)
				)
				.get();

		assertEquals(5, i1.get());
		assertEquals(10, i2.get());
		assertEquals(15, i3.get());

		i1.set(20);
		assertEquals(20, i1.get());
		assertEquals(20, i2.get());
		assertEquals(20, i3.get());

		bindings.unbindBidirectional(i1);
	}

	@Test
	public void testJavaFXBinding() {
		IntegerProperty iA = new SimpleIntegerProperty();
		IntegerProperty iB = new SimpleIntegerProperty();
		IntegerProperty iC = new SimpleIntegerProperty();

		iA.bindBidirectional(iB);
		iA.bindBidirectional(iC);

		iA.set(8); // All properties must be 8
		assertEquals(8, iA.get());
		assertEquals(8, iB.get());
		assertEquals(8, iC.get());

		iB.set(10); // B updates A updates C
		assertEquals(10, iA.get());
		assertEquals(10, iB.get());
		assertEquals(10, iC.get());

		iC.set(12); // C updates A updates B
		assertEquals(12, iA.get());
		assertEquals(12, iB.get());
		assertEquals(12, iC.get());
	}
}
