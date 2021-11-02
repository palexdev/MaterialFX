package io.github.palexdev.materialfx.collections;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings({"unchecked", "NullableProblems"})
public class TransformableListWrapper<T> extends AbstractList<T> implements ObservableList<T> {
    private final ObservableList<T> source;
    private final TransformableList<T> transformableList;

    public TransformableListWrapper(ObservableList<T> source) {
        this.source = source;
        this.transformableList = new TransformableList<>(source);
    }

    @Override
    public void addListener(ListChangeListener<? super T> listener) {
        transformableList.addListener(listener);
    }

    @Override
    public void removeListener(ListChangeListener<? super T> listener) {
        transformableList.removeListener(listener);
    }

    @Override
    public boolean add(T t) {
        return source.add(t);
    }

    @Override
    public T set(int index, T element) {
        return source.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        source.add(index, element);
    }

    @Override
    public T remove(int index) {
        return source.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return transformableList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return transformableList.lastIndexOf(o);
    }

    @Override
    public void clear() {
        source.clear();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return source.addAll(index, c);
    }

    @Override
    public boolean addAll(T... elements) {
        return source.addAll(elements);
    }

    @Override
    public boolean setAll(T... elements) {
        return source.setAll(elements);
    }

    @Override
    public boolean setAll(Collection<? extends T> col) {
        return source.setAll(col);
    }

    @Override
    public boolean removeAll(T... elements) {
        return source.removeAll(elements);
    }

    @Override
    public boolean retainAll(T... elements) {
        return source.retainAll(elements);
    }

    @Override
    public void remove(int from, int to) {
        source.remove(from, to);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        transformableList.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        transformableList.removeListener(listener);
    }

    @Override
    public T get(int index) {
        return transformableList.get(index);
    }

    @Override
    public int size() {
        return transformableList.size();
    }

    public ObservableList<? extends T> getSource() {
        return transformableList.getSource();
    }

    public int viewToSource(int index) {
        return transformableList.viewToSource(index);
    }

    public int sourceToView(int index) {
        return transformableList.sourceToView(index);
    }

    public Predicate<? super T> getPredicate() {
        return transformableList.getPredicate();
    }

    public ObjectProperty<Predicate<? super T>> predicateProperty() {
        return transformableList.predicateProperty();
    }

    public void setPredicate(Predicate<? super T> predicate) {
        transformableList.setPredicate(predicate);
    }

    public Comparator<? super T> getComparator() {
        return transformableList.getComparator();
    }

    public ObjectProperty<Comparator<? super T>> comparatorProperty() {
        return transformableList.comparatorProperty();
    }

    public void setComparator(Comparator<? super T> comparator) {
        transformableList.setComparator(comparator);
    }

    public void setComparator(Comparator<? super T> sorter, boolean reversed) {
        transformableList.setComparator(sorter, reversed);
    }

    public boolean isReversed() {
        return transformableList.isReversed();
    }

    public void setReversed(boolean reversed) {
        transformableList.setReversed(reversed);
    }
}
