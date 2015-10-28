package jdepend.framework.rule;

import jdepend.framework.JavaClass;
import jdepend.framework.JavaPackage;

import java.util.*;

/**
 *
 */
public class PackageRule {
    private final String name;
    private final boolean allowAll;
    private final List<String> mustDepend = new ArrayList<String>();
    private final List<String> mayDepend = new ArrayList<String>();
    private final List<String> mustNotDepend = new ArrayList<String>();

    PackageRule(String name, boolean allowAll) {
        final int starPos = name.indexOf("*");
        if (starPos >= 0 && (starPos != name.length() - 1 || !name.endsWith(".*"))) {
            throw new IllegalArgumentException("Wildcard * is only allowed at the end (e.g. java.*)");
        }
        this.name = name;
        this.allowAll = allowAll;
    }

    public static PackageRule allowAll(String name) {
        return new PackageRule(name, true);
    }

    public static PackageRule denyAll(String name) {
        return new PackageRule(name, false);
    }

    public PackageRule mustDependUpon(PackageRule... rules) {
        for (PackageRule rule : rules) {
            mustDepend.add(rule.name);
        }
        return this;
    }

    public PackageRule mayDependUpon(PackageRule... rules) {
        for (PackageRule rule : rules) {
            mayDepend.add(rule.name);
        }
        return this;
    }

    public PackageRule mustNotDependUpon(PackageRule... rules) {
        for (PackageRule rule : rules) {
            mustNotDepend.add(rule.name);
        }
        return this;
    }

    public RuleResult analyze(Collection<JavaPackage> packages) {
        final RuleResult result = new RuleResult();
        final List<JavaPackage> thisPackages = findPackages(packages, name);

        for (String must : mustDepend) {
            for (JavaPackage mustPack : findPackages(packages, must)) {
                for (JavaPackage thisPack : thisPackages) {
                    if (!hasEfferent(thisPack, mustPack.getName())) {
                        addDependency(result.missing, thisPack, mustPack);
                    }
                }
            }
        }
        if (allowAll) {
            for (String mustNot : mustNotDepend) {
                for (JavaPackage mustNotPack : findPackages(packages, mustNot)) {
                    for (JavaPackage thisPack : thisPackages) {
                        if (hasEfferent(thisPack, mustNotPack.getName()) && !hasAnyMatch(mustNotPack, mayDepend)) {
                            addDependency(result.denied, thisPack, mustNotPack);
                        }
                    }
                }
            }
        } else {
            for (JavaPackage thisPack : thisPackages) {
                for (JavaPackage dep : thisPack.getEfferents()) {
                    final boolean allowed = hasAnyMatch(dep, mustDepend) || hasAnyMatch(dep, mayDepend);
                    final boolean mustNot = hasAnyMatch(dep, mustNotDepend);
                    if (!mustNot && allowed) {
                        addDependency(result.allowed, thisPack, dep);
                    }
                    if (mustNot || !allowed) {
                        addDependency(result.denied, thisPack, dep);
                    }
                }
            }
        }

        return result;
    }

    private void addDependency(DependencyMap dependencyMap, JavaPackage from, JavaPackage to) {
        dependencyMap.with(from.getName(), findClasses(from, to), to.getName());
    }

    private boolean hasAnyMatch(JavaPackage pack, List<String> names) {
        for (String name : names) {
            if (matches(pack, name)) {
                return true;
            }
        }
        return false;
    }

    private List<JavaPackage> findPackages(Collection<JavaPackage> packages, String name) {
        final List<JavaPackage> res = new ArrayList<JavaPackage>();
        for (JavaPackage pack : packages) {
            if (matches(pack, name)) {
                res.add(pack);
            }
        }
        return res;
    }

    private boolean matches(JavaPackage pack, String name) {
        return name.endsWith(".*")
                ? pack.getName().startsWith(name.substring(0, name.length() - 1))
                : pack.getName().equals(name);
    }

    private boolean hasEfferent(JavaPackage pack, String name) {
        return !findPackages(pack.getEfferents(), name).isEmpty();
    }

    private boolean hasEfferent(JavaClass jc, String name) {
        return !findPackages(jc.getImportedPackages(), name).isEmpty();
    }

    private Set<String> findClasses(JavaPackage from, JavaPackage to) {
        final Set<String> res = new HashSet<String>();
        for (JavaClass jc : from.getClasses()) {
            if (hasEfferent(jc, to.getName())) {
                res.add(jc.getName());
            }
        }
        return res;
    }
}
