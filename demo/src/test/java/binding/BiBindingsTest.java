package binding;

import io.github.palexdev.materialfx.bindings.BiBindingHelper;
import io.github.palexdev.materialfx.bindings.BiBindingManager;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BiBindingsTest {
	private final BiBindingManager biBindingManager = BiBindingManager.instance();

	@Test
	public void biBindingTest1() {
		assertTrue(biBindingManager.isEmpty());

		IntegerProperty i1 = new SimpleIntegerProperty();
		IntegerProperty i2 = new SimpleIntegerProperty();

		biBindingManager.bindBidirectional(i1)
				.with((oldValue, newValue) -> i1.setValue(newValue))
				.to(i2, (oldValue, newValue) -> i2.setValue(newValue))
				.create();

		i1.set(10);
		assertEquals(10, i1.get());
		assertEquals(10, i2.get());

		i2.set(20);
		assertEquals(20, i1.get());
		assertEquals(20, i2.get());

		biBindingManager.unbind(i1, i2);
	}

	@Test
	public void biBindingTest2() {
		assertTrue(biBindingManager.isEmpty());

		IntegerProperty iA = new SimpleIntegerProperty();
		IntegerProperty iB = new SimpleIntegerProperty();
		IntegerProperty iC = new SimpleIntegerProperty();

		biBindingManager.bindBidirectional(iA)
				.with((oldValue, newValue) -> iA.setValue(newValue))
				.to(iB, (oldValue, newValue) -> iB.setValue(newValue))
				.create();
		biBindingManager.bindBidirectional(iA)
				.with((oldValue, newValue) -> iA.setValue(newValue))
				.to(iC, (oldValue, newValue) -> iC.setValue(newValue))
				.create();

		iA.set(8); // All properties must be 8
		assertEquals(8, iA.get());
		assertEquals(8, iB.get());
		assertEquals(8, iC.get());

		iB.set(10); // Only A and B will be 10, C will remain 8
		assertEquals(10, iA.get());
		assertEquals(10, iB.get());
		assertEquals(10, iC.get());

		iC.set(12); // Only A and C will be 12, B will remain 10
		assertEquals(12, iA.get());
		assertEquals(12, iB.get());
		assertEquals(12, iC.get());

		biBindingManager.clear(iA);
	}

	@Test
	public void testEagerBinding() {
		assertTrue(biBindingManager.isEmpty());

		IntegerProperty i1 = new SimpleIntegerProperty(5);
		IntegerProperty i2 = new SimpleIntegerProperty(10);
		IntegerProperty i3 = new SimpleIntegerProperty(15);

		biBindingManager.bindBidirectional(i1)
				.with((oldValue, newValue) -> i1.setValue(newValue))
				.to(
						Map.entry(i2, (oldValue, newValue) -> i2.setValue(newValue)),
						Map.entry(i3, (oldValue, newValue) -> i3.setValue(newValue))
				)
				.create();

		assertEquals(15, i1.get());
		assertEquals(15, i2.get());
		assertEquals(15, i3.get());

		biBindingManager.clear(i1);
		assertTrue(biBindingManager.isEmpty());
	}

	@Test
	public void testLazyBinding() {
		assertTrue(biBindingManager.isEmpty());

		IntegerProperty i1 = new SimpleIntegerProperty(5);
		IntegerProperty i2 = new SimpleIntegerProperty(10);
		IntegerProperty i3 = new SimpleIntegerProperty(15);

		biBindingManager.bindBidirectional(i1)
				.with((oldValue, newValue) -> i1.setValue(newValue))
				.to(
						Map.entry(i2, (oldValue, newValue) -> i2.setValue(newValue)),
						Map.entry(i3, (oldValue, newValue) -> i3.setValue(newValue))
				)
				.lazy()
				.create();

		assertEquals(5, i1.get());
		assertEquals(10, i2.get());
		assertEquals(15, i3.get());

		i1.set(20);
		assertEquals(20, i1.get());
		assertEquals(20, i2.get());
		assertEquals(20, i3.get());

		biBindingManager.clear(i1);
	}

	// Performance Debug //
	@Test
	public void testPerformance1() {
		assertTrue(biBindingManager.isEmpty());

		IntegerProperty i1 = new SimpleIntegerProperty();
		IntegerProperty i2 = new SimpleIntegerProperty();
		IntegerProperty i3 = new SimpleIntegerProperty();

		DebuggableBiBindingHelper<Number> helper = new DebuggableBiBindingHelper<>();
		biBindingManager.bindBidirectional(i1)
				.to(i2)
				.withHelper(helper)
				.create();

		assertEquals(0, helper.getTargetCounter());
		assertEquals(0, helper.getSourcesCounter());

		i1.set(5);
		assertEquals(5, i1.get());
		assertEquals(5, i2.get());
		assertEquals(0, helper.getTargetCounter());
		assertEquals(1, helper.getSourcesCounter());

		i2.set(10);
		assertEquals(10, i1.get());
		assertEquals(10, i2.get());
		assertEquals(1, helper.getTargetCounter());
		assertEquals(1, helper.getSourcesCounter());

		BiBindingManager.instance().clear(i1);
		assertTrue(biBindingManager.isEmpty());
	}

	@Test
	public void testPerformance2() {
		assertTrue(biBindingManager.isEmpty());

		IntegerProperty i1 = new SimpleIntegerProperty();
		IntegerProperty i2 = new SimpleIntegerProperty();
		IntegerProperty i3 = new SimpleIntegerProperty();

		DebuggableBiBindingHelper<Number> helper = new DebuggableBiBindingHelper<>();
		biBindingManager.bindBidirectional(i1)
				.with((oldValue, newValue) -> i1.setValue(newValue))
				.to(
						Map.entry(i2, (oldValue, newValue) -> i2.setValue(newValue)),
						Map.entry(i3, (oldValue, newValue) -> i3.setValue(newValue))
				)
				.withHelper(helper)
				.create();

		assertEquals(0, helper.getTargetCounter());
		assertEquals(0, helper.getSourcesCounter());

		i1.set(5);
		assertEquals(5, i1.get());
		assertEquals(5, i2.get());
		assertEquals(5, i3.get());
		assertEquals(0, helper.getTargetCounter());
		assertEquals(2, helper.getSourcesCounter());

		i2.set(10);
		assertEquals(10, i1.get());
		assertEquals(10, i2.get());
		assertEquals(10, i3.get());
		assertEquals(1, helper.getTargetCounter());
		assertEquals(3, helper.getSourcesCounter());

		i3.set(20);
		assertEquals(20, i1.get());
		assertEquals(20, i2.get());
		assertEquals(20, i3.get());
		assertEquals(2, helper.getTargetCounter());
		assertEquals(4, helper.getSourcesCounter());

		biBindingManager.clear(i1);
		assertTrue(biBindingManager.isEmpty());
	}

	@Test
	public void biBindingTest3() {
		assertTrue(biBindingManager.isEmpty());

		IntegerProperty iA = new SimpleIntegerProperty();
		IntegerProperty iB = new SimpleIntegerProperty();
		IntegerProperty iC = new SimpleIntegerProperty();

		DebuggableBiBindingHelper<Number> helper = new DebuggableBiBindingHelper<>();
		biBindingManager.bindBidirectional(iA)
				.with((oldValue, newValue) -> iA.setValue(newValue))
				.to(iB, (oldValue, newValue) -> iB.setValue(newValue))
				.withHelper(helper)
				.create();
		biBindingManager.bindBidirectional(iA)
				.with((oldValue, newValue) -> iA.setValue(newValue))
				.to(iC, (oldValue, newValue) -> iC.setValue(newValue))
				.withHelper(helper)
				.create();

		assertEquals(0, helper.getTargetCounter());
		assertEquals(0, helper.getSourcesCounter());

		iA.set(8); // All properties must be 8
		assertEquals(8, iA.get());
		assertEquals(8, iB.get());
		assertEquals(8, iC.get());
		assertEquals(0, helper.getTargetCounter());
		assertEquals(2, helper.getSourcesCounter());

		iB.set(10); // B updates A updates C
		assertEquals(10, iA.get());
		assertEquals(10, iB.get());
		assertEquals(10, iC.get());
		assertEquals(1, helper.getTargetCounter());
		assertEquals(3, helper.getSourcesCounter());

		iC.set(12); // C updates A updates B
		assertEquals(12, iA.get());
		assertEquals(12, iB.get());
		assertEquals(12, iC.get());
		assertEquals(2, helper.getTargetCounter());
		assertEquals(4, helper.getSourcesCounter());

		biBindingManager.clear(iA);
	}

	@Test
	public void testCombineHelpers() {
		IntegerProperty target = new SimpleIntegerProperty();
		IntegerProperty iA = new SimpleIntegerProperty();
		IntegerProperty iB = new SimpleIntegerProperty();
		IntegerProperty iC = new SimpleIntegerProperty();

		BiBindingHelper<Number> helper1 = new BiBindingHelper<>();
		BiBindingHelper<Number> helper2 = new BiBindingHelper<>();

		helper1.bind(target);
		helper1.addSource(iA, (oldValue, newValue) -> iA.setValue(newValue));
		helper1.addSource(iB, (oldValue, newValue) -> iB.setValue(newValue));

		helper2.bind(target);
		helper2.addSource(iA, (oldValue, newValue) -> iA.setValue(newValue));
		helper2.addSource(iC, (oldValue, newValue) -> iC.setValue(newValue));

		BiBindingHelper<Number> combined = BiBindingHelper.newFor(helper1, helper2, false);
		assertEquals(3, combined.size());

		LinkedList<WeakReference<ObservableValue<? extends Number>>> keys = combined.getUnmodifiableSources();
		assertEquals(3, keys.size());
		assertEquals(keys.getFirst().get(), iA);
		assertEquals(keys.getLast().get(), iC);
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
