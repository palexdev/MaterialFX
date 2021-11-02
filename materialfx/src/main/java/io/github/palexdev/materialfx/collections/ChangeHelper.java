package io.github.palexdev.materialfx.collections;

import java.util.Arrays;
import java.util.List;

class ChangeHelper {
    ChangeHelper() {}

    public static String addRemoveChangeToString(int from, int to, List<?> list, List<?> removed) {
        StringBuilder sb = new StringBuilder();
        if (removed.isEmpty()) {
            sb.append(list.subList(from, to));
            sb.append(" added at ").append(from);
        } else {
            sb.append(removed);
            if (from == to) {
                sb.append(" removed at ").append(from);
            } else {
                sb.append(" replaced by ");
                sb.append(list.subList(from, to));
                sb.append(" at ").append(from);
            }
        }

        return sb.toString();
    }

    public static String permChangeToString(int[] permutation) {
        return "permutated by " + Arrays.toString(permutation);
    }

    public static String updateChangeToString(int from, int to) {
        return "updated at range [" + from + ", " + to + ")";
    }
}
