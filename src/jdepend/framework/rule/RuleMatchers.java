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

    public static Matcher<JDepend> matches(final DependencyRules rules) {
        return new TypeSafeMatcher<JDepend>() {
            private RuleResult result;

            @Override
            protected boolean matchesSafely(JDepend item) {
                result = rules.analyze(item.getPackages());
                return result.getMissing().isEmpty() && result.getDenied().isEmpty();
            }

            public void describeTo(Description description) {
                description.appendText("Comply with rules");
            }

            @Override
            protected void describeMismatchSafely(JDepend item, Description description) {
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
                            description.appendText("  " + dep + " (by " + toString(deps.get(dep)) + ")\n");
                        }
                    }
                }
            }

            private String toString(Collection<String> packs) {
                String s = "";
                for (String pack : sorted(packs)) {
                    s += ", " + pack;
                }
                return s.length() > 0 ? s.substring(2) : s;
            }

            private List<String> sorted(Collection<String> ss) {
                final List<String> sorted = new ArrayList<String>(ss);
                Collections.sort(sorted);
                return sorted;
            }
        };
    }

}
