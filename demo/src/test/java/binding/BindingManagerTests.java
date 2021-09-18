package binding;

import io.github.palexdev.materialfx.bindings.BidirectionalBindingHelper;
import io.github.palexdev.materialfx.bindings.BindingHelper;
import io.github.palexdev.materialfx.bindings.BindingManager;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
public class BindingManagerTests {

    @Test
    public void testBindWithJavFXProperties() {
        BindingManager<Number> bindingManager = new BindingManager<>();
        IntegerProperty property = new SimpleIntegerProperty() {
            @Override
            public boolean isBound() {
                return super.isBound() && !bindingManager.isIgnoreBound();
            }
        };
        IntegerProperty source = new SimpleIntegerProperty();

        bindingManager.provideHelperFactory(other -> new BindingHelper<>() {
            @Override protected void updateBound(Number newValue) { property.set(newValue.intValue()); }
        });
        bindingManager.getBindingHelper(source).bind(source);
        source.set(8);

        assertEquals(8, source.get());
        assertEquals(8, property.get());
    }

    @Test
    public void testMultipleBidirectional() {
        IntegerProperty propertyA = new SimpleIntegerProperty();
        IntegerProperty propertyB = new SimpleIntegerProperty();
        IntegerProperty propertyC = new SimpleIntegerProperty();
        BindingManager<Number> bindingManager = new BindingManager<>();
        bindingManager.provideBidirectionalHelperFactory((property) -> new BidirectionalBindingHelper<>(property) {
            @Override protected void updateThis(Number newValue) { property.setValue(newValue); }
            @Override protected void updateOther(Property<Number> other, Number newValue) { other.setValue(newValue); }
        });

        bindingManager.getBidirectionalBindingHelper(propertyA).bind(propertyB);
        bindingManager.getBidirectionalBindingHelper(propertyA).bind(propertyC);

        propertyA.set(8);
        assertEquals(8, propertyA.get());
        assertEquals(8, propertyB.get());
        assertEquals(8, propertyC.get());

        propertyB.set(10);
        assertEquals(10, propertyA.get());
        assertEquals(10, propertyB.get());
        assertEquals(8, propertyC.get());

        propertyC.set(12);
        assertEquals(12, propertyA.get());
        assertEquals(10, propertyB.get());
        assertEquals(12, propertyC.get());
    }
}
