package jdepend.framework.rule;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class CycleResult {
    final Set<DependencyMap> cycles;

    public CycleResult() {
        cycles = new HashSet<DependencyMap>();
    }

    public boolean isEmpty() {
        return cycles.isEmpty();
    }

    public boolean isEmptyExcept(Set<String>... exceptions) {
        return getCyclesExcept(exceptions).isEmpty();
    }

    public Set<DependencyMap> getCyclesExcept(Set<String>... exceptions) {
        final Set<DependencyMap> res = new HashSet<DependencyMap>();
        for (DependencyMap cycle : cycles) {
            boolean excepted = false;
            for (Set<String> exception : exceptions) {
                if (exception.containsAll(cycle.getPackages())) {
                    excepted = true;
                    break;
                }
            }
            if (!excepted) {
                res.add(cycle);
            }
        }
        return res;
    }

    public static Set<String> packages(String... packs){
        final HashSet<String> res = new HashSet<String>();
        for(String pack:packs){
            res.add(pack);
        }
        return res;
    }
}
