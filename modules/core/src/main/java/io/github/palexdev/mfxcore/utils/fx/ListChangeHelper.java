/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.utils.fx;

import java.util.*;
import java.util.function.Consumer;

import io.github.palexdev.mfxcore.base.Disposable;
import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

/// A helper class whose goal is to simplify the "interactions" with [Change].
///
/// This is capable of handling permutations, replacements, removals and additions through actions that you can specify
/// with the relative setters.
///
/// To attach the listener to the source [ObservableList], it's enough to call [#init()].
///
/// Once this is not needed anymore, it should be properly disposed by invoking [#dispose()].
public class ListChangeHelper<E> implements Disposable {
    //================================================================================
    // Properties
    //================================================================================
    private ObservableList<E> source;
    private ListChangeListener<? super E> listener;
    private int lastSize;

    // Actions
    private Consumer<Map<Integer, Integer>> onPermutation = fn -> {};
    private Consumer<Integer> onReplace = replaced -> {};
    private Consumer<NavigableSet<Integer>> onRemoved = removed -> {};
    private Consumer<IntegerRange> onAdded = added -> {};
    private Runnable onClear = () -> {};

    //================================================================================
    // Constructors
    //================================================================================
    public ListChangeHelper(ObservableList<E> source) {
        this.source = source;
        lastSize = source.size();
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Builds the [ListChangeListener] and attaches it to the given source [ObservableList].
    public ListChangeHelper<E> init() {
        if (listener == null) {
            listener = this::onChanged;
            source.addListener(listener);
        }
        return this;
    }

    /// This is the core method invoked by the [ListChangeListener] built by the [#init()] method.
    ///
    /// In the following lines, I'm going to describe what this does, how and why.
    /// It's responsible for handling the following changes:
    ///  1) Permutations: a map is built containing the original index mapped to the new one, see [#buildPermutationMap(Change)],
    /// the result is passed to the consumer, [#getOnPermutation()]
    ///  2) Replacements: this one is tricky because as usual JavaFX code is pure garbage. Every replacement is treated
    /// both as a removal and an addition, which indeed makes sense, but it just complicates things for no reason whatsoever.
    /// Essentially, there are two cases: a single `set()` operation leads to [Change#getFrom()] and
    /// [Change#getTo()] to have the same value (easy to handle), and multiple sets `setAll()` which is simply
    /// a `clear()` operation followed by an `addAll()` operation. The latter is trickier to catch, and this class
    /// handles such case as a clear operation, a no-arg action, [#getOnClear()]
    ///  3) Removals: this! This is not even tricky, it's the pure definition of `garbage`. There are two cases
    /// here too. The first case is about single or contiguous removals, very easy to handle. The second case is the
    /// shitty one, and it's about sparse removals (e.g. [0, 1, 4, 7, 9]). Not only every removal is handled as a single
    /// change, but the [Change#getFrom()] and [Change#getTo()] are fucking messed up.
    /// Let's consider the previous example: removals at indexes `[A, B, D, F, I]`. The first removal is contiguous
    /// `[A, B]` and so the range will be `[0, 1]`. The second removal is treated as a separate change, and the
    /// range will be... exactly `[1, 1]`, so fucking obvious and convenient right? Essentially, for every removal,
    /// the range is shifted by the number of previously removed items, and this is a direct consequence of treating sparse
    /// removals as separate changes.
    /// This helper handles this garbage by reconstructing the sequence. The result is a `Set` of indexes at which the
    /// removals originally occurred, so if we consider the previous example, the `Set` will look like this:
    /// `[0, 1, 3, 8]`. The action takes this `Set` as the only arg, [#getOnRemoved()].
    ///  4) Additions: these are very easy to handle as they can only be contiguous, for this reason the action only
    /// accepts the range, [#getOnAdded()]
    public void onChanged(Change<? extends E> c) {
        int removedSize = 0;
        NavigableSet<Integer> removed = new TreeSet<>();

        while (c.next()) {
            if (c.wasPermutated()) {
                Map<Integer, Integer> map = buildPermutationMap(c);
                onPermutation.accept(map);
                continue;
            }
            if (c.wasReplaced()) {
                if (c.getList().isEmpty() || lastSize == c.getRemovedSize()) {
                    onClear.run();
                } else {
                    onReplace.accept(c.getFrom());
                }
                continue;
            }
            if (c.wasRemoved()) {
                if (c.getList().isEmpty()) {
                    onClear.run();
                    continue;
                }

                IntegerRange range = computeRemovedRange(c, removedSize);
                removed.addAll(IntegerRange.expandRangeToSet(range));
                removedSize += c.getRemovedSize();
                continue;
            }
            if (c.wasAdded()) {
                onAdded.accept(IntegerRange.of(c.getFrom(), c.getTo() - 1));
            }
        }
        if (!removed.isEmpty()) onRemoved.accept(removed);
        lastSize = c.getList().size();
    }

    /// This is responsible for correctly reconstructing a sequence of sparse removals as detailed here: [#onChanged(Change)].
    private IntegerRange computeRemovedRange(Change<? extends E> c, int toOffset) {
        int from = c.getTo() + toOffset;
        int to = c.getFrom() + (c.getRemovedSize() - 1) + toOffset;
        return IntegerRange.of(from, to);
    }

    /// Removes the listener from the source and sets the latter to `null`.
    @Override
    public void dispose() {
        if (listener != null) source.removeListener(listener);
        source = null;
    }

    //================================================================================
    // Utility Methods
    //================================================================================

    /// A generic utility that given a [Change] builds a map containing the old indexes
    /// (in the range given by [Change#getFrom()] and [Change#getTo()]) mapped to their permutation, found
    /// by calling [Change#getPermutation(int)].
    public static <E> Map<Integer, Integer> buildPermutationMap(Change<? extends E> c) {
        if (!c.wasPermutated()) return Map.of();
        IntegerRange range = IntegerRange.of(c.getFrom(), c.getTo() - 1);
        Map<Integer, Integer> map = new HashMap<>();
        for (Integer index : range) {
            map.put(index, c.getPermutation(index));
        }
        return map;
    }

    /// Shifts the pre-addition indexes given the range of added items in the source list. This piece of information is
    /// enough as indexes that come after the insertion point (given by [IntegerRange#getMin()]) just need to be
    /// shifted by the number of added items (given by [IntegerRange#diff()] + 1).
    public static List<Integer> shiftOnAdd(Collection<Integer> src, IntegerRange range) {
        List<Integer> srcL = src instanceof List ?
            (List<Integer>) src :
            new ArrayList<>(src);

        int offset = range.getMin();
        int addedSize = range.diff() + 1;
        for (int i = 0; i < srcL.size(); i++) {
            Integer origin = srcL.get(i);
            if (origin < offset) continue;
            int updated = origin + addedSize;
            srcL.set(i, updated);
        }
        return srcL;
    }

    /// Shifts the pre-removal indexes given the indexes at which removals occurred in the source list and the first
    /// removal index. This kind of shift is the most complex and expensive.
    ///
    /// Unlike additions, removals in a list may not be contiguous, e.g. \[2, 7, 1, 5, 10]. Which means that the simplest
    /// approach to shift every index would be to iterate over each of them and count all the removed indexes that are smaller.
    ///
    ///
    /// This, however, uses another approach. By first sorting the removed indexes, computes each shift by using
    /// [Collections#binarySearch(List, Object)].
    public static List<Integer> shiftOnRemove(Collection<Integer> src, Collection<Integer> removed, int from) {
        List<Integer> srcL = src instanceof List ?
            (List<Integer>) src :
            new ArrayList<>(src);

        // Sort removals
        List<Integer> sorted = removed instanceof List ?
            (List<Integer>) removed :
            new ArrayList<>(removed);
        Collections.sort(sorted);

        List<Integer> result = new ArrayList<>();
        for (Integer origin : srcL) {
            // Skip removals
            if (removed.contains(origin)) continue;

            // Copy indexes before removal point
            if (origin < from) {
                result.add(origin);
                continue;
            }

            // Use binary search to find shift
            int shift = Collections.binarySearch(sorted, origin);
            if (shift < 0) shift = -shift - 1; // Convert to index if negative
            int updated = Math.max(origin - shift, 0);
            result.add(updated);
        }
        return result;
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /// @return the source [ObservableList] on which the lister is attached, see [#init()]
    public ObservableList<E> getSource() {
        return source;
    }

    /// @return the action performed when a permutation change happens in the source list, see [#onChanged(Change)]
    public Consumer<Map<Integer, Integer>> getOnPermutation() {
        return onPermutation;
    }

    /// Sets the action performed when a permutation change happens in the source list, see [#onChanged(Change)]
    public ListChangeHelper<E> setOnPermutation(Consumer<Map<Integer, Integer>> onPermutation) {
        this.onPermutation = Optional.ofNullable(onPermutation)
            .orElse(fn -> {});
        return this;
    }

    /// @return the action performed when a replacement change happens in the source list, see [#onChanged(Change)]
    public Consumer<Integer> getOnReplace() {
        return onReplace;
    }

    /// Sets the action performed when a replacement change happens in the source list, see [#onChanged(Change)]
    public ListChangeHelper<E> setOnReplace(Consumer<Integer> onReplace) {
        this.onReplace = Optional.ofNullable(onReplace)
            .orElse(c -> {});
        return this;
    }

    /// @return the action performed when a removal change happens in the source list, see [#onChanged(Change)]
    public Consumer<NavigableSet<Integer>> getOnRemoved() {
        return onRemoved;
    }

    /// Sets the action performed when a removal change happens in the source list, see [#onChanged(Change)]
    public ListChangeHelper<E> setOnRemoved(Consumer<NavigableSet<Integer>> onRemoved) {
        this.onRemoved = Optional.ofNullable(onRemoved)
            .orElse(c -> {});
        return this;
    }

    /// @return the action performed when an addition change happens in the source list, see [#onChanged(Change)]
    public Consumer<IntegerRange> getOnAdded() {
        return onAdded;
    }

    /// Sets the action performed when an addition change happens in the source list, see [#onChanged(Change)]
    public ListChangeHelper<E> setOnAdded(Consumer<IntegerRange> onAdded) {
        this.onAdded = Optional.ofNullable(onAdded)
            .orElse(c -> {});
        return this;
    }

    /// @return the action performed when a clear change happens in the source list, see [#onChanged(Change)]
    public Runnable getOnClear() {
        return onClear;
    }

    /// Sets the action performed when a clear change happens in the source list, see [#onChanged(Change)]
    public ListChangeHelper<E> setOnClear(Runnable onClear) {
        this.onClear = Optional.ofNullable(onClear)
            .orElse(() -> {});
        return this;
    }
}