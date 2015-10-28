package jdepend.framework.rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
class DependencyMap {
    private final Map<String, Map<String, Set<String>>> map = new HashMap<String, Map<String, Set<String>>>();

    public DependencyMap() {
    }

    public DependencyMap with(String from, Set<String> fromClasses, String to) {
        Map<String, Set<String>> deps = map.get(from);
        if (deps == null) {
            deps = new HashMap<String, Set<String>>();
            map.put(from, deps);
        }
        Set<String> existingFromClasses = deps.get(to);
        if (existingFromClasses == null) {
            deps.put(to, new HashSet<String>(fromClasses));
        } else {
            existingFromClasses.addAll(fromClasses);
        }
        return this;
    }

    public DependencyMap without(String from, String to) {
        Map<String, Set<String>> deps = map.get(from);
        if (deps != null) {
            deps.remove(to);
            if (deps.isEmpty()) {
                map.remove(from);
            }
        }
        return this;
    }

    public DependencyMap without(DependencyMap other) {
        for (Map.Entry<String, Map<String, Set<String>>> entry : other.map.entrySet()) {
            for (String to : entry.getValue().keySet()) {
                without(entry.getKey(), to);
            }
        }
        return this;
    }

    public void merge(DependencyMap deps) {
        for (Map.Entry<String, Map<String, Set<String>>> entry : deps.map.entrySet()) {
            final Map<String, Set<String>> ds = map.get(entry.getKey());
            if (ds == null) {
                map.put(entry.getKey(), entry.getValue());
            } else {
                ds.putAll(entry.getValue());
            }
        }
    }

    public void clear() {
        map.clear();
    }

    public Set<String> getPackages() {
        return map.keySet();
    }

    /**
     * @param pack
     * @return A map with all dependencies of a given package. Key: package, Value: A set of all classes importing the package
     */
    public Map<String, Set<String>> getDependencies(String pack) {
        return map.get(pack);
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
