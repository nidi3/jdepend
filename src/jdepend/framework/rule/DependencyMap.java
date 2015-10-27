package jdepend.framework.rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
class DependencyMap {
    private final Map<String, Set<String>> map = new HashMap<String, Set<String>>();

    public DependencyMap() {
    }

    public DependencyMap with(String from, String... tos) {
        for (String to : tos) {
            with(from, to);
        }
        return this;
    }

    public DependencyMap with(String from, String to) {
        Set<String> deps = map.get(from);
        if (deps == null) {
            deps = new HashSet<String>();
            map.put(from, deps);
        }
        deps.add(to);
        return this;
    }

    public DependencyMap without(String from, String to) {
        Set<String> deps = map.get(from);
        if (deps != null) {
            deps.remove(to);
            if (deps.isEmpty()) {
                map.remove(from);
            }
        }
        return this;
    }

    public DependencyMap without(DependencyMap other) {
        for (Map.Entry<String, Set<String>> entry : other.map.entrySet()) {
            for (String to : entry.getValue()) {
                without(entry.getKey(), to);
            }
        }
        return this;
    }

    public void merge(DependencyMap deps) {
        for (Map.Entry<String, Set<String>> entry : deps.map.entrySet()) {
            final Set<String> ds = map.get(entry.getKey());
            if (ds == null) {
                map.put(entry.getKey(), entry.getValue());
            } else {
                ds.addAll(entry.getValue());
            }
        }
    }

    public void clear() {
        map.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DependencyMap that = (DependencyMap) o;

        return map.equals(that.map);

    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public String toString() {
        return map.toString();
    }

}
