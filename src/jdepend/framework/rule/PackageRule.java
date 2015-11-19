package jdepend.framework.rule;

import jdepend.framework.JavaPackage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public boolean matches(JavaPackage pack) {
        return pack.isMatchedBy(name);
    }

    public RuleResult analyze(Collection<JavaPackage> packages) {
        final RuleResult result = new RuleResult();
        final List<JavaPackage> thisPackages = DependencyMap.selectMatchingPackages(packages, name);
        if (thisPackages.isEmpty()) {
            result.notExisting.add(name);
        }

        for (String must : mustDepend) {
            for (JavaPackage mustPack : DependencyMap.selectMatchingPackages(packages, must)) {
                for (JavaPackage thisPack : thisPackages) {
                    if (!hasEfferent(thisPack, mustPack.getName())) {
                        result.missing.with(thisPack, mustPack);
                    }
                }
            }
        }
        if (allowAll) {
            for (String mustNot : mustNotDepend) {
                for (JavaPackage mustNotPack : DependencyMap.selectMatchingPackages(packages, mustNot)) {
                    for (JavaPackage thisPack : thisPackages) {
                        if (hasEfferent(thisPack, mustNotPack.getName()) && !hasAnyMatch(mustNotPack, mayDepend)) {
                            result.denied.with(thisPack, mustNotPack);
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
                        result.allowed.with(thisPack, dep);
                    }
                    if (mustNot || !allowed) {
                        result.denied.with(thisPack, dep);
                    }
                }
            }
        }

        return result;
    }

    private static boolean hasAnyMatch(JavaPackage pack, List<String> names) {
        for (String name : names) {
            if (pack.isMatchedBy(name)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasEfferent(JavaPackage pack, String name) {
        return !DependencyMap.selectMatchingPackages(pack.getEfferents(), name).isEmpty();
    }

}
