package jdepend.framework.rule;

import jdepend.framework.JavaClass;
import jdepend.framework.JavaPackage;

import java.util.*;

/**
 *
 */
public class DependencyMap {
    private final Map<String, Map<String, Set<String>>> map = new LinkedHashMap<String, Map<String, Set<String>>>();

    public DependencyMap() {
    }

    public void with(JavaPackage from, JavaPackage to) {
        with(from.getName(), findClasses(from, to), to.getName());
    }

    private static Set<String> findClasses(JavaPackage from, JavaPackage to) {
        final Set<String> res = new HashSet<String>();
        for (JavaClass jc : from.getClasses()) {
            if (hasEfferent(jc, to.getName())) {
                res.add(jc.getName());
            }
        }
        return res;
    }

    private static boolean hasEfferent(JavaClass jc, String name) {
        return !selectMatchingPackages(jc.getImportedPackages(), name).isEmpty();
    }

    static List<JavaPackage> selectMatchingPackages(Collection<JavaPackage> packages, String name) {
        final List<JavaPackage> res = new ArrayList<JavaPackage>();
        for (JavaPackage pack : packages) {
            if (pack.isMatchedBy(name)) {
                res.add(pack);
            }
        }
        return res;
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

    public boolean isEmpty() {
        return map.isEmpty();
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
