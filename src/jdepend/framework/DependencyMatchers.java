package jdepend.framework;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.*;

/**
 *
 */
public class DependencyMatchers {
    private static final Comparator<JavaPackage> PACKAGE_COMPARATOR = new Comparator<JavaPackage>() {
        public int compare(JavaPackage p1, JavaPackage p2) {
            return p2.getName().compareTo(p1.getName());
        }
    };

    private DependencyMatchers() {
    }

    public static Matcher<JDepend> matchesPackages(Object... objs) {
        return matches(DependencyConstraint.fromFields(objs));
    }

    public static Matcher<JDepend> matchesPackages(String basePackage, Object... objs) {
        return matches(DependencyConstraint.fromFields(basePackage, objs));
    }

    public static Matcher<JDepend> matches(final DependencyConstraint constraint) {
        return new TypeSafeMatcher<JDepend>() {
            private DependencyConstraint.MatchResult result;

            @Override
            protected boolean matchesSafely(JDepend item) {
                result = constraint.match(item.getPackages());
                return result.matches();
            }

            public void describeTo(Description description) {
                description.appendText("No dependency problems");
            }

            @Override
            protected void describeMismatchSafely(JDepend item, Description description) {
                if (!result.getUndefinedPackages().isEmpty()) {
                    description.appendText("Found undefined packages: ");
                    description.appendText(toString(result.getUndefinedPackages()) + "\n");
                }
                if (!result.getNonMatchingPackages().isEmpty()) {
                    description.appendText("Non matching packages:");
                    for (JavaPackage[] packs : result.getNonMatchingPackages()) {
                        description.appendText("\n" + packs[0].getName() + "\n");
                        final List<JavaPackage> exAff = (List<JavaPackage>) packs[0].getAfferents();
                        final List<JavaPackage> effAff = (List<JavaPackage>) packs[1].getAfferents();
                        final List<JavaPackage> exEff = (List<JavaPackage>) packs[0].getEfferents();
                        final List<JavaPackage> effEff = (List<JavaPackage>) packs[1].getEfferents();
                        if (!exAff.equals(effAff)) {
                            description.appendText("Expected afferents: " + toString(exAff) + "\n");
                            description.appendText("Found    afferents: " + toString(effAff) + "\n");
                        }
                        if (!exEff.equals(effEff)) {
                            description.appendText("Expected efferents: " + toString(exEff) + "\n");
                            description.appendText("Found    efferents: " + toString(effEff) + "\n");
                        }
                    }
                }
            }

            private String toString(List<JavaPackage> packs) {
                String s = "";
                for (JavaPackage pack : sorted(packs)) {
                    s += ", " + pack;
                }
                return s.length() > 0 ? s.substring(2) : s;
            }

            private boolean equals(List<JavaPackage> p1, List<JavaPackage> p2) {
                return sorted(p1).equals(sorted(p2));
            }

            private List<JavaPackage> sorted(List<JavaPackage> packs) {
                List<JavaPackage> sorted = new ArrayList<JavaPackage>(packs);
                Collections.sort(sorted, PACKAGE_COMPARATOR);
                return sorted;
            }
        };
    }

    public static Matcher<JDepend> hasNoCycles() {
        return new TypeSafeMatcher<JDepend>() {
            @Override
            protected boolean matchesSafely(JDepend item) {
                return !item.containsCycles();
            }

            public void describeTo(Description description) {
                description.appendText("No cycles");
            }

            @Override
            protected void describeMismatchSafely(JDepend item, Description description) {
                description.appendText("is cyclic");
            }
        };
    }

    public static Matcher<JDepend> hasMaxDistance(final String packageFilter, final double maxDistance) {
        return new TypeSafeMatcher<JDepend>() {
            private JavaPackage problem;

            @Override
            protected boolean matchesSafely(JDepend item) {
                for (JavaPackage pack : item.getPackages()) {
                    if (pack.getName().startsWith(packageFilter)) {
                        if (pack.distance() > maxDistance) {
                            problem = pack;
                            return false;
                        }
                    }
                }
                return true;
            }

            public void describeTo(Description description) {
                description.appendText("Distance <= " + maxDistance);
            }

            @Override
            protected void describeMismatchSafely(JDepend item, Description description) {
                description.appendText(problem.getName() + " has distance of " + problem.distance());
            }
        };
    }

    public static String distances(JDepend depend, String packageFilter) {
        StringBuilder s = new StringBuilder()
                .append("Name                                      abst  inst  dist\n")
                .append("----------------------------------------------------------\n");
        final Formatter formatter = new Formatter(s);
        for (JavaPackage pack : depend.getPackages()) {
            if (pack.getName().startsWith(packageFilter)) {
                formatter.format("%-40s: %-1.2f  %-1.2f  %-1.2f%n", pack.getName(), pack.abstractness(), pack.instability(), pack.distance());
            }
        }
        return s.toString();
    }
}
