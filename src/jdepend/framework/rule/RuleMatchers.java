package jdepend.framework.rule;

import jdepend.framework.JDepend;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.*;

/**
 *
 */
public class RuleMatchers {
    private RuleMatchers() {
    }

    public static Matcher<JDepend> matchesRules(final DependencyRules rules) {
        return new RuleMatcher(rules, false, false);
    }

    public static Matcher<JDepend> matchesExactly(final DependencyRules rules) {
        return new RuleMatcher(rules, true, true);
    }

    public static Matcher<JDepend> matchesIgnoringNonExisting(final DependencyRules rules) {
        return new RuleMatcher(rules, false, true);
    }

    public static Matcher<JDepend> matchesIgnoringUndefined(final DependencyRules rules) {
        return new RuleMatcher(rules, true, false);
    }

    public static Matcher<JDepend> hasNoCycles() {
        return new CycleMatcher();
    }

    public static Matcher<JDepend> hasNoCyclesExcept(Set<String>... cyclicGroups) {
        return new CycleMatcher(cyclicGroups);
    }

    private static class RuleMatcher extends TypeSafeMatcher<JDepend> {
        private final DependencyRules rules;
        private final boolean nonExisting;
        private final boolean undefined;
        private RuleResult result;

        public RuleMatcher(DependencyRules rules, boolean nonExisting, boolean undefined) {
            this.rules = rules;
            this.nonExisting = nonExisting;
            this.undefined = undefined;
        }

        @Override
        protected boolean matchesSafely(JDepend item) {
            result = rules.analyzeRules(item.getPackages());
            return result.getMissing().isEmpty() && result.getDenied().isEmpty() &&
                    (result.getNotExisting().isEmpty() || !nonExisting) &&
                    (result.getUndefined().isEmpty() || !undefined);
        }

        public void describeTo(Description description) {
            description.appendText("Comply with rules");
        }

        @Override
        protected void describeMismatchSafely(JDepend item, Description description) {
            if (nonExisting && !result.getNotExisting().isEmpty()) {
                description.appendText("\nDefined, but not existing packages:\n");
                description.appendText(join(sorted(result.getNotExisting())) + "\n");
            }
            if (undefined && !result.getUndefined().isEmpty()) {
                description.appendText("\nFound packages which are not defined:\n");
                description.appendText(join(sorted(result.getUndefined())) + "\n");
            }
            if (!result.getMissing().isEmpty()) {
                description.appendText("\nFound missing dependencies:\n");
                for (String pack : sorted(result.getMissing().getPackages())) {
                    description.appendText(pack + " ->\n");
                    for (final String dep : sorted(result.getMissing().getDependencies(pack).keySet())) {
                        description.appendText("  " + dep + "\n");
                    }
                }
            }
            if (!result.getDenied().isEmpty()) {
                description.appendText("\nFound forbidden dependencies:\n");
                for (String pack : sorted(result.getDenied().getPackages())) {
                    description.appendText(pack + " ->\n");
                    final Map<String, Set<String>> deps = result.getDenied().getDependencies(pack);
                    for (final String dep : sorted(deps.keySet())) {
                        description.appendText("  " + dep + " (by " + join(deps.get(dep)) + ")\n");
                    }
                }
            }
        }
    }

    private static class CycleMatcher extends TypeSafeMatcher<JDepend> {
        private final Set<String>[] exceptions;
        private CycleResult result;

        public CycleMatcher(Set<String>... exceptions) {
            this.exceptions = exceptions;
        }

        @Override
        protected boolean matchesSafely(JDepend item) {
            result = DependencyRules.analyzeCycles(item.getPackages());
            return result.isEmptyExcept(exceptions);
        }

        public void describeTo(Description description) {
            description.appendText("Does not have cycles");
        }

        @Override
        protected void describeMismatchSafely(JDepend item, Description description) {
            if (!result.isEmptyExcept(exceptions)) {
                description.appendText("Found these cyclic groups:\n");
                for (DependencyMap cycle : result.getCyclesExcept(exceptions)) {
                    description.appendText("\n- Group of " + cycle.getPackages().size() + ": " + join(sorted(cycle.getPackages())) + "\n");
                    for (String pack : sorted(cycle.getPackages())) {
                        description.appendText("  " + pack + " ->\n");
                        final Map<String, Set<String>> deps = cycle.getDependencies(pack);
                        for (final String dep : sorted(deps.keySet())) {
                            description.appendText("    " + dep + " (by " + join(deps.get(dep)) + ")\n");
                        }
                    }
                }
            }
        }
    }

    private static String join(Collection<String> packs) {
        String s = "";
        for (String pack : sorted(packs)) {
            s += ", " + pack;
        }
        return s.length() > 0 ? s.substring(2) : s;
    }

    private static List<String> sorted(Collection<String> ss) {
        final List<String> sorted = new ArrayList<String>(ss);
        Collections.sort(sorted);
        return sorted;
    }

}
