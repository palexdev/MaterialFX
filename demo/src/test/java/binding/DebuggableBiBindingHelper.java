package binding;

import io.github.palexdev.materialfx.bindings.BiBindingHelper;
import javafx.beans.value.ObservableValue;

import java.util.function.BiConsumer;

public class DebuggableBiBindingHelper<T> extends BiBindingHelper<T> {
	private int targetCounter = 0;
	private int sourcesCounter = 0;

	@Override
	protected void beforeUpdateTarget() {
		targetCounter++;
	}

	@Override
	protected void updateSource(ObservableValue<? extends T> source, BiConsumer<T, T> updater, T oldValue, T newValue) {
		sourcesCounter++;
		super.updateSource(source, updater, oldValue, newValue);
	}

	public int getTargetCounter() {
		return targetCounter;
	}

	public int getSourcesCounter() {
		return sourcesCounter;
	}

	public void resetCounters() {
		targetCounter = 0;
		sourcesCounter = 0;
	}
}
