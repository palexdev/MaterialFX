package io.github.palexdev.materialfx.utils;

import io.github.palexdev.materialfx.controls.base.AbstractTreeItem;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TreeItemStream {
    public static <T> Stream<AbstractTreeItem<T>> stream(AbstractTreeItem<T> rootItem) {
        return asStream(new TreeItemIterator<>(rootItem));
    }

    private static <T> Stream<AbstractTreeItem<T>> asStream(TreeItemIterator<T> iterator) {
        Iterable<AbstractTreeItem<T>> iterable = () -> iterator;

        return StreamSupport.stream(
                iterable.spliterator(),
                false
        );
    }

    public static <T> Stream<AbstractTreeItem<T>> flattenTree(final AbstractTreeItem<T> root) {
        return Stream.concat(
                Stream.of(root),
                root.getItems().stream().flatMap(TreeItemStream::flattenTree)
        );
    }
}
