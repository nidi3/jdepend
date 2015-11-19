package jdepend.framework;

import java.util.*;

/**
 * The <code>DependencyConstraint</code> class is a constraint that tests
 * whether two package-dependency graphs are equivalent.
 * <p>
 * This class is useful for writing package dependency assertions (e.g. JUnit).
 * For example, the following JUnit test will ensure that the 'ejb' and 'web'
 * packages only depend upon the 'util' package, and no others:
 * </p>
 * <pre>
 *
 * public void testDependencyConstraint() {
 *
 *     JDepend jdepend = new JDepend();
 *     jdepend.addDirectory(&quot;/path/to/classes&quot;);
 *     Collection analyzedPackages = jdepend.analyze();
 *
 *     DependencyConstraint constraint = new DependencyConstraint();
 *
 *     JavaPackage ejb = constraint.addPackage(&quot;com.xyz.ejb&quot;);
 *     JavaPackage web = constraint.addPackage(&quot;com.xyz.web&quot;);
 *     JavaPackage util = constraint.addPackage(&quot;com.xyz.util&quot;);
 *
 *     ejb.dependsUpon(util);
 *     web.dependsUpon(util);
 *
 *     assertEquals(&quot;Dependency mismatch&quot;, true, constraint
 *             .match(analyzedPackages));
 * }
 * </pre>
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */
public class DependencyConstraint {
    private final String basePackage;
    private final Map<String, JavaPackage> packages;

    public DependencyConstraint(String basePackage) {
        this.basePackage = normalize(basePackage);
        packages = new HashMap<String, JavaPackage>();
    }

    public DependencyConstraint() {
        this("");
    }

    private static String normalize(String basePackage) {
        return basePackage.length() == 0 || basePackage.endsWith(".")
                ? basePackage
                : (basePackage + ".");
    }

    public JavaPackage addPackage(String packageName) {
        final String pack = basePackage + packageName;
        JavaPackage jPackage = packages.get(pack);
        if (jPackage == null) {
            jPackage = new JavaPackage(pack);
            addPackage(jPackage);
        }
        return jPackage;
    }

    public void addPackage(JavaPackage jPackage) {
        if (!packages.containsValue(jPackage)) {
            packages.put(jPackage.getName(), jPackage);
        }
    }

    public Collection getPackages() {
        return packages.values();
    }

    /**
     * Indicates whether the specified packages match the
     * packages in this constraint.
     *
     * @return <code>true</code> if the packages match this constraint
     */
    public MatchResult match(Collection<JavaPackage> expectedPackages) {
        MatchResult res = new MatchResult();
        for (JavaPackage next : expectedPackages) {
            JavaPackage actualPackage = packages.get(next.getName());
            if (actualPackage == null) {
                res.getUndefinedPackages().add(next);
            } else if (!equalsDependencies(next, actualPackage)) {
                res.getNonMatchingPackages().add(new JavaPackage[]{actualPackage, next});
            }
        }
        return res;
    }

    public static class MatchResult {
        private List<JavaPackage> undefinedPackages = new ArrayList<JavaPackage>();
        private List<JavaPackage[]> notMatchingPackages = new ArrayList<JavaPackage[]>();

        public boolean matches() {
            return undefinedPackages.isEmpty() && notMatchingPackages.isEmpty();
        }

        public List<JavaPackage> getUndefinedPackages() {
            return undefinedPackages;
        }

        public List<JavaPackage[]> getNonMatchingPackages() {
            return notMatchingPackages;
        }
    }


    private boolean equalsDependencies(JavaPackage a, JavaPackage b) {
        return equalsAfferents(a, b) && equalsEfferents(a, b);
    }

    private boolean equalsAfferents(JavaPackage a, JavaPackage b) {
        if (a.equals(b)) {
            Collection<JavaPackage> otherAfferents = b.getAfferents();
            if (a.getAfferents().size() == otherAfferents.size()) {
                for (JavaPackage afferent : a.getAfferents()) {
                    if (!otherAfferents.contains(afferent)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean equalsEfferents(JavaPackage a, JavaPackage b) {
        if (a.equals(b)) {
            Collection<JavaPackage> otherEfferents = b.getEfferents();
            if (a.getEfferents().size() == otherEfferents.size()) {
                for (JavaPackage efferent : a.getEfferents()) {
                    if (!otherEfferents.contains(efferent)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}