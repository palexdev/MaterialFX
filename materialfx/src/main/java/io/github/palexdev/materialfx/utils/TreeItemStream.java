package io.github.palexdev.materialfx.utils;

import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeItem;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TreeItemStream {
    public static <T> Stream<AbstractMFXTreeItem<T>> stream(AbstractMFXTreeItem<T> item) {
        return asStream(new TreeItemIterator<>(item));
    }

    private static <T> Stream<AbstractMFXTreeItem<T>> asStream(TreeItemIterator<T> iterator) {
        Iterable<AbstractMFXTreeItem<T>> iterable = () -> iterator;

        return StreamSupport.stream(
                iterable.spliterator(),
                false
        );
    }

    public static <T> Stream<AbstractMFXTreeItem<T>> flattenTree(final AbstractMFXTreeItem<T> item) {
        return Stream.concat(
                Stream.of(item),
                item.getItems().stream().flatMap(TreeItemStream::flattenTree)
        );
    }
}
