package selection;

import io.github.palexdev.materialfx.demo.model.SimplePerson;
import io.github.palexdev.materialfx.selection.SingleSelectionModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class SingleSelectionModelTests {
    private final ListProperty<SimplePerson> people1 = new SimpleListProperty<>(
            FXCollections.observableArrayList(
                    new SimplePerson("Jack"),
                    new SimplePerson("Mark"),
                    new SimplePerson("Linda"),
                    new SimplePerson("Marty"),
                    new SimplePerson("Lily"),
                    new SimplePerson("Sam"))
    );

    private final ListProperty<SimplePerson> people2 = new SimpleListProperty<>(
            FXCollections.observableArrayList(
                    new SimplePerson("Mark"),
                    new SimplePerson("Roberto"),
                    new SimplePerson("Alex"),
                    new SimplePerson("Samantha"),
                    new SimplePerson("Elyse"),
                    new SimplePerson("Mark"),
                    new SimplePerson("Sam"),
                    new SimplePerson("Jennifer"),
                    new SimplePerson("Alex"),
                    new SimplePerson("Rocky"),
                    new SimplePerson("Phil"))
    );

    private final SingleSelectionModel<SimplePerson> model1 = new SingleSelectionModel<>(people1);
    private final SingleSelectionModel<SimplePerson> model2 = new SingleSelectionModel<>(people2);

    @BeforeEach
    public void setUp() {
        model1.unbind();
        model1.unbindBidirectional();
        model1.clearSelection();

        model2.unbind();
        model2.unbindBidirectional();
        model2.clearSelection();
    }

    @Test
    public void testIndexSelection1() {
        model1.selectIndex(0);
        assertEquals(0, model1.getSelectedIndex());
        assertEquals("Jack", model1.getSelectedItem().getName());
    }

    @Test
    public void testIndexSelection2() {
        assertThrows(IndexOutOfBoundsException.class, () -> model1.selectIndex(10));
        assertEquals(-1, model1.getSelectedIndex());
    }

    @Test
    public void testIndexSelection3() {
        assertThrows(IndexOutOfBoundsException.class, () -> model1.selectIndex(-1));
        assertEquals(-1, model1.getSelectedIndex());
    }

    @Test
    public void testItemSelection1() {
        model1.selectItem(new SimplePerson("Mark"));
        assertEquals(1, model1.getSelectedIndex());
        assertEquals("Mark", model1.getSelectedItem().getName());
    }

    @Test
    public void testItemSelection2() {
        assertThrows(IllegalArgumentException.class, () -> model1.selectItem(new SimplePerson("Unexisting")));
    }

    @Test
    public void testBindIndexProperty1() {
        IntegerProperty property = new SimpleIntegerProperty();
        model1.bindIndex(property, i -> model1.getUnmodifiableItems().get(i));
        property.set(2);
        assertEquals(2, model1.getSelectedIndex());
        assertEquals("Linda", model1.getSelectedItem().getName());
    }

    @Test
    public void testBindIndexProperty2() {
        IntegerProperty property = new SimpleIntegerProperty();
        model1.bindIndex(property, i -> model1.getUnmodifiableItems().get(i));
        assertThrows(IllegalStateException.class, () -> model1.selectIndex(2));
    }

    @Test
    public void testBindIndexProperty3() {
        AtomicReference<Throwable> ex = new AtomicReference<>();
        IntegerProperty property = new SimpleIntegerProperty();
        model1.bindIndex(property, i -> {
            try {
                return model1.getUnmodifiableItems().get(i);
            } catch (Exception exception) {
                ex.set(exception);
            }
            return null;
        });
        property.set(6);
        assertNotNull(ex.get());
    }

    @Test
    public void testBindIndexProperty4() {
        model1.selectIndex(0);

        AtomicReference<Throwable> ex = new AtomicReference<>();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> ex.set(e));

        IntegerProperty property = new SimpleIntegerProperty();
        model1.bindIndex(property, i -> model1.getUnmodifiableItems().get(i));
        property.set(-1);

        assertNotNull(ex.get());
        assertEquals(0, model1.getSelectedIndex());
        assertEquals("Jack", model1.getSelectedItem().getName());
    }

    @Test
    public void testBindIndex1() {
        model1.bindIndex(model2);
        model2.selectIndex(2);

        assertEquals(2, model1.getSelectedIndex());
        assertEquals(2, model2.getSelectedIndex());

        assertEquals("Linda", model1.getSelectedItem().getName());
        assertEquals("Alex", model2.getSelectedItem().getName());
    }

    @Test
    public void testBindIndex2() {
        model1.bindIndex(model2);
        assertThrows(IllegalStateException.class, () -> model1.selectIndex(2));
    }

    @Test
    public void testBindIndex3() {
        AtomicReference<Throwable> ex = new AtomicReference<>();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> ex.set(e));

        model1.bindIndex(model2);
        model2.selectIndex(8);

        assertEquals(-1, model1.getSelectedIndex());
        assertNull(model1.getSelectedItem());
        assertEquals(8, model2.getSelectedIndex());
        assertEquals("Alex", model2.getSelectedItem().getName());

        assertNotNull(ex.get());
    }

    @Test
    public void testBindIndex4() {
        model1.bindIndex(model2);
        model2.selectIndex(0);
        assertThrows(IndexOutOfBoundsException.class, () -> model2.selectIndex(-1));

        assertEquals(0, model1.getSelectedIndex());
        assertEquals("Jack", model1.getSelectedItem().getName());
        assertEquals(0, model2.getSelectedIndex());
        assertEquals("Mark", model2.getSelectedItem().getName());
    }

    @Test
    public void testBindIndexBidirectionalProperty1() {
        IntegerProperty property = new SimpleIntegerProperty();
        model1.bindIndexBidirectional(property, i -> model1.getUnmodifiableItems().get(i), (clearing, i, other) -> other.setValue(i));

        property.set(1);
        assertEquals(1, model1.getSelectedIndex());
        assertEquals("Mark", model1.getSelectedItem().getName());

        model1.selectIndex(5);
        assertEquals("Sam", model1.getSelectedItem().getName());
        assertEquals(5, property.get());

        model1.selectItem(new SimplePerson("Lily"));
        assertEquals(4, model1.getSelectedIndex());
        assertEquals(4, property.get());
    }

    @Test
    public void testBindIndexBidirectionalProperty2() {
        AtomicReference<Throwable> reference = new AtomicReference<>();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> reference.set(e));

        IntegerProperty property = new SimpleIntegerProperty();
        model1.bindIndexBidirectional(property, i -> model1.getUnmodifiableItems().get(i), (clearing, i, other) -> other.setValue(i));

        property.set(1);
        assertEquals(1, model1.getSelectedIndex());
        assertEquals("Mark", model1.getSelectedItem().getName());

        property.set(-1);
        assertEquals(-1, property.get());
        assertEquals(1, model1.getSelectedIndex());
        assertEquals("Mark", model1.getSelectedItem().getName());

        assertNotNull(reference.get());
    }

    @Test
    public void testBindIndexBidirectionalProperty3() {
        IntegerProperty property = new SimpleIntegerProperty();
        model1.bindIndexBidirectional(property, i -> model1.getUnmodifiableItems().get(i), (clearing, i, other) -> other.setValue(i));

        assertThrows(IndexOutOfBoundsException.class, () -> model1.selectIndex(8));
        assertEquals(0, property.get());
        assertEquals(-1, model1.getSelectedIndex());
        assertNull(model1.getSelectedItem());
    }

    @Test
    public void testBindIndexBidirectional1() {
        model1.bindIndexBidirectional(model2);

        model2.selectIndex(1);
        assertEquals("Mark", model1.getSelectedItem().getName());
        assertEquals("Roberto", model2.getSelectedItem().getName());

        model1.selectIndex(3);
        assertEquals("Marty", model1.getSelectedItem().getName());
        assertEquals("Samantha", model2.getSelectedItem().getName());
    }

    @Test
    public void testBindIndexBidirectional2() {
        AtomicReference<Throwable> reference = new AtomicReference<>();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> reference.set(e));
        model1.bindIndexBidirectional(model2);

        model2.selectIndex(8);
        assertNotNull(reference.get());
        assertEquals("Alex", model2.getSelectedItem().getName());
        assertEquals(-1, model1.getSelectedIndex());
        assertNull(model1.getSelectedItem());

        model1.selectIndex(0);
        assertEquals("Jack", model1.getSelectedItem().getName());
        assertEquals("Mark", model2.getSelectedItem().getName());
    }

    @Test
    public void testBindIndexBidirectional3() {
        AtomicReference<Throwable> reference = new AtomicReference<>();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> reference.set(e));
        model2.bindIndexBidirectional(model1);

        model1.selectIndex(0);
        assertEquals("Jack", model1.getSelectedItem().getName());
        assertEquals("Mark", model2.getSelectedItem().getName());

        model2.selectIndex(8);
        assertNotNull(reference.get());
        assertEquals("Alex", model2.getSelectedItem().getName());
        assertEquals(0, model1.getSelectedIndex());
        assertEquals("Jack", model1.getSelectedItem().getName());
    }

    @Test
    public void testBindItemProperty1() {
        ObjectProperty<SimplePerson> property = new SimpleObjectProperty<>();
        model1.bindItem(property, simplePerson -> model1.getUnmodifiableItems().indexOf(simplePerson));

        property.set(new SimplePerson("Mark"));
        assertEquals(1, model1.getSelectedIndex());
        assertEquals("Mark", model1.getSelectedItem().getName());
    }

    @Test
    public void testBindItemProperty2() {
        ObjectProperty<SimplePerson> property = new SimpleObjectProperty<>();
        model2.bindItem(property, simplePerson -> model2.getUnmodifiableItems().indexOf(simplePerson));

        property.set(new SimplePerson("Alex"));
        assertEquals(2, model2.getSelectedIndex());
        assertEquals("Alex", model2.getSelectedItem().getName());
    }

    @Test
    public void testBindItemProperty3() {
        AtomicReference<Throwable> reference = new AtomicReference<>();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> reference.set(e));

        ObjectProperty<SimplePerson> property = new SimpleObjectProperty<>();
        model2.bindItem(property, simplePerson -> model2.getUnmodifiableItems().indexOf(simplePerson));

        property.set(new SimplePerson("Unexisting"));
        assertEquals("Unexisting", property.get().getName());
        assertEquals(-1, model2.getSelectedIndex());
        assertNull(model2.getSelectedItem());
        assertNotNull(reference.get());
    }

    @Test
    public void testBindItem1() {
        model1.bindItem(model2);
        model2.selectItem(new SimplePerson("Mark"));
        assertEquals(1, model1.getSelectedIndex());
        assertEquals(0, model2.getSelectedIndex());
    }

    @Test
    public void testBindItem2() {
        model1.bindItem(model2);
        assertThrows(IllegalStateException.class, () -> model1.selectItem(new SimplePerson("Mark")));
    }

    @Test
    public void testBindItem3() {
        AtomicReference<Throwable> reference = new AtomicReference<>();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> reference.set(e));

        model1.bindItem(model2);
        model2.selectItem(new SimplePerson("Alex"));
        assertEquals(-1, model1.getSelectedIndex());
        assertNull(model1.getSelectedItem());
        assertEquals(2, model2.getSelectedIndex());
        assertEquals("Alex", model2.getSelectedItem().getName());
        assertNotNull(reference.get());
    }

    @Test
    public void testBindItemPropertyBidirectional1() {
        ObjectProperty<SimplePerson> property = new SimpleObjectProperty<>();
        model1.bindItemBidirectional(property, simplePerson -> model1.getUnmodifiableItems().indexOf(simplePerson), (clearing, simplePerson, other) -> other.setValue(simplePerson));

        property.set(new SimplePerson("Linda"));
        assertEquals(2, model1.getSelectedIndex());
        assertEquals("Linda", model1.getSelectedItem().getName());
    }

    @Test
    public void testBindItemPropertyBidirectional2() {
        AtomicReference<Throwable> reference = new AtomicReference<>();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> reference.set(e));

        ObjectProperty<SimplePerson> property = new SimpleObjectProperty<>();
        model1.bindItemBidirectional(property, simplePerson -> model1.getUnmodifiableItems().indexOf(simplePerson), (clearing, simplePerson, other) -> other.setValue(simplePerson));

        property.set(new SimplePerson("Unexisting"));
        assertNotNull(reference.get());
    }

    @Test
    public void testBindItemPropertyBidirectional3() {
        ObjectProperty<SimplePerson> property = new SimpleObjectProperty<>();
        model1.bindItemBidirectional(property, simplePerson -> model1.getUnmodifiableItems().indexOf(simplePerson), (clearing, simplePerson, other) -> other.setValue(simplePerson));

        property.set(new SimplePerson("Mark"));
        assertEquals(1, model1.getSelectedIndex());

        model1.selectItem(new SimplePerson("Linda"));
        assertEquals(2, model1.getSelectedIndex());
        assertEquals("Linda", model1.getSelectedItem().getName());
        assertEquals("Linda", property.get().getName());
    }

    @Test
    public void testBindItemBidirectional1() {
        model1.bindItemBidirectional(model2);

        model2.selectItem(new SimplePerson("Mark"));
        assertEquals(1, model1.getSelectedIndex());
        assertEquals(0, model2.getSelectedIndex());

        model1.selectItem(new SimplePerson("Sam"));
        assertEquals(5, model1.getSelectedIndex());
        assertEquals(6, model2.getSelectedIndex());
    }

    @Test
    public void testBindItemBidirectional2() {
        AtomicReference<Throwable> reference = new AtomicReference<>();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> reference.set(e));
        model1.bindItemBidirectional(model2);

        model1.selectItem(new SimplePerson("Sam"));
        assertEquals(5, model1.getSelectedIndex());
        assertEquals(6, model2.getSelectedIndex());

        model2.selectItem(new SimplePerson("Alex"));
        assertNotNull(reference.get());
        assertEquals(5, model1.getSelectedIndex());
        assertEquals("Sam", model1.getSelectedItem().getName());
        assertEquals(2, model2.getSelectedIndex());
        assertEquals("Alex", model2.getSelectedItem().getName());
    }

    @Test
    public void testBindItemBidirectional3() {
        AtomicReference<Throwable> reference = new AtomicReference<>();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> reference.set(e));
        model1.bindItemBidirectional(model2);

        model2.selectItem(new SimplePerson("Alex"));
        assertNotNull(reference.get());
        assertEquals(-1, model1.getSelectedIndex());
        assertNull(model1.getSelectedItem());
        assertEquals(2, model2.getSelectedIndex());
        assertEquals("Alex", model2.getSelectedItem().getName());

        model1.selectItem(new SimplePerson("Sam"));
        assertEquals(5, model1.getSelectedIndex());
        assertEquals(6, model2.getSelectedIndex());

    }

    @Test
    public void testClearSelection1() {
        model1.selectIndex(0);
        model1.clearSelection();
        assertEquals(-1, model1.getSelectedIndex());
        assertNull(model1.getSelectedItem());
    }

    @Test
    public void testClearSelection2() {
        model1.bindIndexBidirectional(model2);
        model1.selectIndex(0);
        model1.clearSelection();

        assertEquals(-1, model1.getSelectedIndex());
        assertNull(model1.getSelectedItem());
        assertEquals(-1, model2.getSelectedIndex());
        assertNull(model2.getSelectedItem());
    }

    @Test
    public void testClearSelection3() {
        model1.bindIndex(model2);
        model2.selectIndex(0);
        assertThrows(IllegalStateException.class, model1::clearSelection);
    }

    @Test
    public void testClearSelection4() {
        model1.bindItemBidirectional(model2);
        model2.selectItem(new SimplePerson("Mark"));

        assertEquals(1, model1.getSelectedIndex());
        assertEquals("Mark", model1.getSelectedItem().getName());
        assertEquals(0, model2.getSelectedIndex());
        assertEquals("Mark", model2.getSelectedItem().getName());

        model1.clearSelection();

        assertEquals(-1, model1.getSelectedIndex());
        assertNull(model1.getSelectedItem());
        assertEquals(-1, model2.getSelectedIndex());
        assertNull(model2.getSelectedItem());
    }

    @Test
    public void testClearSelection5() {
        AtomicReference<Throwable> reference = new AtomicReference<>();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> reference.set(e));
        IntegerProperty property = new SimpleIntegerProperty();
        model1.bindIndexBidirectional(property, i -> model1.getUnmodifiableItems().get(i), (clearing, i, otherProperty) -> property.set(i));
        model1.selectIndex(1);

        assertEquals(1, property.get());
        assertEquals("Mark", model1.getSelectedItem().getName());

        model1.clearSelection();
        assertEquals(-1, property.get());
        assertEquals(-1, model1.getSelectedIndex());
        assertNull(model1.getSelectedItem());

        model1.selectIndex(1);
        property.set(-1);
        assertNotNull(reference.get());
        assertEquals(-1, property.get());
        assertEquals(1, model1.getSelectedIndex());
        assertEquals("Mark", model1.getSelectedItem().getName());
    }

    @Test
    public void testClearSelection6() {
        AtomicReference<Throwable> reference = new AtomicReference<>();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> reference.set(e));
        ObjectProperty<SimplePerson> property = new SimpleObjectProperty<>();
        model1.bindItemBidirectional(property, v -> model1.getUnmodifiableItems().indexOf(v), (clearing, v, otherProperty) -> property.set(v));
        model1.selectIndex(1);

        assertEquals("Mark", property.get().getName());
        assertEquals(1, model1.getSelectedIndex());

        model1.clearSelection();
        assertNull(property.get());
        assertEquals(-1, model1.getSelectedIndex());
        assertNull(model1.getSelectedItem());

        model1.selectIndex(1);
        property.set(null);
        assertNotNull(reference.get());
        assertNull(property.get());
        assertEquals(1, model1.getSelectedIndex());
        assertEquals("Mark", model1.getSelectedItem().getName());
    }
}
