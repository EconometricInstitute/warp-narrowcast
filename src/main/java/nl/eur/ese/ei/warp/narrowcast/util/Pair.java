package nl.eur.ese.ei.warp.narrowcast.util;

public record Pair<E>(E first, E second) {

    public static <E> Pair<E> of(E first, E second) {
        return new Pair<>(first, second);
    }
}
