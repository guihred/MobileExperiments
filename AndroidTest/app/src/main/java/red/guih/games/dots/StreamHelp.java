package red.guih.games.dots;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
class StreamHelp {
    @FunctionalInterface
    interface Funct<T, R> {
        R apply(T t);
    }

    @FunctionalInterface
    interface Pred<T> {
        boolean test(T t);
    }

    static <T, E extends Comparable<E>> Comparator<T> comparing(Funct<T, E> funct) {

        return (t, t1) -> {
            E apply = funct.apply(t);
            E apply1 = funct.apply(t1);
            return apply.compareTo(apply1);
        };
    }

    static <E, T extends Collection<E>> T filter(T filter, Pred<E> pred) {


        try {
            T newInstance = (T) filter.getClass().newInstance();
            for (E e : filter) {
                if (pred.test(e)) {
                    newInstance.add(e);
                }
            }
            return newInstance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filter instanceof Set) {
            return (T) new HashSet<>();
        }


        return (T) new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    static <E, T extends Collection<E>> T filter(T filter, Pred<E> pred, int limit) {


        try {
            T newInstance = (T) filter.getClass().newInstance();
            for (E e : filter) {
                if (pred.test(e)) {
                    newInstance.add(e);
                    if (newInstance.size() >= limit) {
                        return newInstance;
                    }
                }
            }
            return newInstance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filter instanceof Set) {
            return (T) new HashSet<>();
        }


        return (T) new ArrayList<>();
    }

    static <E, X> Map<X, List<E>> groupBy(Collection<E> lista, Funct<E, X> function) {
        Map<X, List<E>> hashMap = new HashMap<>();
        for (E e : lista) {
            X apply = function.apply(e);
            if (!hashMap.containsKey(apply)) {
                hashMap.put(apply, new ArrayList<>());
            }
            hashMap.get(apply).add(e);
        }
        return hashMap;
    }

    public static <K, V> V getOrDefault(Map<K, V> map, K get, V orElse) {
        if (!map.containsKey(get)) {
            return orElse;
        }

        return map.get(get);
    }


    static <E, T extends Collection<E>> Set<E> toSet(T filter) {
        Set<E> a = new HashSet<>();
        a.addAll(filter);
        return a;
    }

    static int min(Iterable<Integer> a, int orElse) {
        int min = Integer.MAX_VALUE;

        for (Integer i : a) {
            min = i < min ? i : min;
        }

        return min == Integer.MAX_VALUE ? orElse : min;
    }

    static float min(Iterable<Float> a, float orElse) {
        float min = Float.MAX_VALUE;

        for (Float i : a) {
            min = i < min ? i : min;
        }

        return min == Float.MAX_VALUE ? orElse : min;
    }

    static <E> E min(Collection<E> a, Comparator<E> func) {
        E min = null;
        for (E e : a) {
            if (min == null) {
                min = e;
            }
            if (func.compare(min, e) > 0) {
                min = e;
            }
        }
        return min;
    }

    static <E> List<E> mins(Collection<E> a, Comparator<E> func) {
        List<E> mins = new ArrayList<>();
        for (E e : a) {
            if (mins.isEmpty()) {
                mins.add(e);
                continue;
            }
            int comparison = func.compare(mins.get(0), e);
            if (!mins.contains(e) && comparison == 0) {
                mins.add(e);
            }
            if (comparison > 0) {
                mins.clear();
                mins.add(e);
            }
        }
        return mins;
    }

    static <E, Z> List<Z> flatMap(Collection<E> filter, Funct<? super E, ? extends Collection<? extends Z>> function) {
        List<Z> a = new ArrayList<>();
        for (E e : filter) {
            Collection<? extends Z> apply = function.apply(e);

            a.addAll(apply);
        }
        return a;
    }

    static <E> List<E> distinct(Collection<E> filter) {
        return new ArrayList<>(new LinkedHashSet<>(filter));
    }


    public static <E, Z, T extends Collection<E>> List<Z> map(T filter, Funct<E, Z> function) {
        List<Z> a = new ArrayList<>();
        for (E e : filter) {
            a.add(function.apply(e));
        }
        return a;
    }

}
