package io.github.palexdev.materialfx.bindings;

import java.lang.ref.WeakReference;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A special {@link WeakHashMap} that allows to retrieve the keys ordered by insertion.
 * <p>
 * Writing a {@code LinkedWeakHashMap} would have been too much work and a very hard task.
 * For this reason this Map simply uses a {@link LinkedList} to store the keys (wrapped in {@link WeakReference}s) by
 * their insertion order.
 * <p></p>
 * Just like the {@link WeakHashMap} this list is cleared (all null references are removed) when
 * major operations occur, such as: put, putAll, combine.
 * <p></p>
 * Allows to retrieve the first and the last keys, and also a <b>copy</b> of the actual {@link LinkedList}.
 */
public class BindingsMap<K, V> extends WeakHashMap<K, V> {
	//================================================================================
	// Properties
	//================================================================================
	private LinkedList<WeakReference<K>> orderedKeys = new LinkedList<>();

	//================================================================================
	// Constructors
	//================================================================================
	public BindingsMap() {
	}

	public BindingsMap(Map<? extends K, ? extends V> m) {
		super(m);
	}

	public BindingsMap(int initialCapacity) {
		super(initialCapacity);
	}

	public BindingsMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Scans the {@link LinkedList} contains the keys references and removes the null ones.
	 * Also calls {@link #size()} to also trigger the {@link WeakHashMap} cleaning too.
	 */
	private void clearReferences() {
		orderedKeys.removeIf(reference -> reference != null && reference.get() == null);
		size();
	}

	/**
	 * Allows to combine the given {@code BindingsMap} to this one.
	 * <p>
	 * This method exists to ensure that insertion order is kept with the {@link LinkedList} but most
	 * importantly ensures that there are no duplicates in the list by using a {@link LinkedHashSet}.
	 * // TODO OTHER METHODS MUST BE CHANGED TOO!!
	 */
	public void combine(BindingsMap<K, V> source) {
		LinkedHashSet<K> uniqueKeys = Stream.concat(orderedKeys.stream(), source.orderedKeys.stream())
				.map(WeakReference::get)
				.collect(Collectors.toCollection(LinkedHashSet::new));
		orderedKeys = uniqueKeys.stream().map(WeakReference::new).collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
		clearReferences();
		for (Map.Entry<K, V> entry : source.entrySet()) {
			super.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Adds the given key to the keys {@link LinkedList}, performs {@link #clearReferences()}
	 * and then calls the super method.
	 */
	@Override
	public V put(K key, V value) {
		orderedKeys.addLast(new WeakReference<>(key));
		clearReferences();
		return super.put(key, value);
	}

	/**
	 * Overridden to call {@link #putAll(Map.Entry[])}.
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		putAll(m.entrySet().toArray(Map.Entry[]::new));
	}

	/**
	 * For each entry adds the key to the keys {@link LinkedList},
	 * then calls the super method.
	 * At the end performs {@link #clearReferences()}.
	 */
	@SafeVarargs
	public final void putAll(Map.Entry<K, V>... entries) {
		for (Map.Entry<K, V> entry : entries) {
			orderedKeys.addLast(new WeakReference<>(entry.getKey()));
			super.put(entry.getKey(), entry.getValue());
		}
		clearReferences();
	}

	/**
	 * Removes the given key from the keys {@link LinkedList}
	 * and then calls the super method.
	 */
	@Override
	public V remove(Object key) {
		orderedKeys.removeIf(reference -> reference != null && reference.get() == key);
		return super.remove(key);
	}

	/**
	 * Clears the keys {@link LinkedList} and then calls the super method.
	 */
	@Override
	public void clear() {
		orderedKeys.clear();
		super.clear();
	}

	/**
	 * UNSUPPORTED
	 */
	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return a copy of the {@link LinkedList} containing the Map's keys ordered by insertion
	 */
	public LinkedList<WeakReference<K>> unmodifiableKeysList() {
		return new LinkedList<>(orderedKeys);
	}

	/**
	 * @return the first inserted key
	 */
	public K getFirstKey() {
		if (isEmpty()) return null;
		return orderedKeys.getFirst().get();
	}

	/**
	 * @return the last inserted key
	 */
	public K getLastKey() {
		if (isEmpty()) return null;
		return orderedKeys.getLast().get();
	}
}
