package pro.fazeclan.river.jarona.util;

@FunctionalInterface
public interface QuadFunction<O, S, T, F, V> {

    V apply(O o, S s, T t, F f);

}
