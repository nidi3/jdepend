package jdepend.framework.rule;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import jdepend.framework.PackageFilter;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 *
 */
public class DependencyRulesTest {
    private static final String BASE = "jdepend.framework.rule.";
    private JDepend jDepend;
    private Collection<JavaPackage> packages;

    @Before
    public void analyze() throws IOException {
        jDepend = new JDepend(PackageFilter.all().excluding("java."));
        jDepend.addDirectory("target/test-classes/jdepend/framework/rule");
        packages = jDepend.analyze();

    }

    @Test(expected = IllegalArgumentException.class)
    public void wildcardNotAtEnd() {
        PackageRule.allowAll("a*b");
    }

    @Test(expected = IllegalArgumentException.class)
    public void wildcardWithoutPriorDot() {
        PackageRule.allowAll("a*");
    }

    @Test
    public void allow() {
        final DependencyRules rules = DependencyRules.allowAll();
        final PackageRule a = rules.addRule(base("a"));
        final PackageRule b = rules.addRule(base("b"));
        final PackageRule c = rules.addRule(base("c"));

        a.mustDependUpon(b);
        b.mustNotDependUpon(a, c).mayDependUpon(a);

        class JdependFrameworkRule implements RuleDefiner {
            PackageRule a, b, c;

            public void defineRules() {
                a.mustDependUpon(b);
                b.mustNotDependUpon(a, c).mayDependUpon(a);
            }
        }
        final DependencyRules rules2 = DependencyRules.allowAll().withRules(new JdependFrameworkRule());

        final RuleResult result = rules.analyze(packages);
        assertEquals(result, rules2.analyze(packages));
        assertEquals(new RuleResult(
                        new DependencyMap(),
                        new DependencyMap().with(base("a"), set(), base("b")),
                        new DependencyMap().with(base("b"), set(base("b.B1")), base("c"))),
                result);
        assertMatcher("\n" +
                        "Found missing dependencies:\n" +
                        "jdepend.framework.rule.a ->\n" +
                        "  jdepend.framework.rule.b\n" +
                        "\n" +
                        "Found forbidden dependencies:\n" +
                        "jdepend.framework.rule.b ->\n" +
                        "  jdepend.framework.rule.c (by jdepend.framework.rule.b.B1)\n",
                rules);
    }

    @Test
    public void deny() {
        final DependencyRules rules = DependencyRules.denyAll();
        final PackageRule a = rules.addRule(base("a"));
        final PackageRule b = rules.addRule(base("b"));
        final PackageRule c = rules.addRule(base("c"));

        a.mustDependUpon(b);
        b.mayDependUpon(a, c).mustNotDependUpon(a);

        final RuleResult result = rules.analyze(packages);
        assertEquals(new RuleResult(
                        new DependencyMap(),
                        new DependencyMap().with(base("a"), set(), base("b")),
                        new DependencyMap()
                                .with(base("a"), set(base("a.A1")), base("c"))
                                .with(base("b"), set(base("b.B1")), base("a"))
                                .with(base("c"), set(base("c.C1")), base("a"))
                                .with(base("c"), set(base("c.C1"), base("c.C2")), base("b"))),
                result);
        assertMatcher("\n" +
                        "Found missing dependencies:\n" +
                        "jdepend.framework.rule.a ->\n" +
                        "  jdepend.framework.rule.b\n" +
                        "\n" +
                        "Found forbidden dependencies:\n" +
                        "jdepend.framework.rule.a ->\n" +
                        "  jdepend.framework.rule.c (by jdepend.framework.rule.a.A1)\n" +
                        "jdepend.framework.rule.b ->\n" +
                        "  jdepend.framework.rule.a (by jdepend.framework.rule.b.B1)\n" +
                        "jdepend.framework.rule.c ->\n" +
                        "  jdepend.framework.rule.a (by jdepend.framework.rule.c.C1)\n" +
                        "  jdepend.framework.rule.b (by jdepend.framework.rule.c.C1, jdepend.framework.rule.c.C2)\n",
                rules);
    }

    @Test
    public void allowWithWildcard() {
        final DependencyRules rules = DependencyRules.allowAll();
        final PackageRule a1 = rules.addRule(base("a.a"));
        final PackageRule a = rules.addRule(base("a.*"));
        final PackageRule b = rules.addRule(base("b.*"));
        final PackageRule c = rules.addRule(base("c.*"));

        a.mustDependUpon(b);
        b.mustNotDependUpon(a, c).mayDependUpon(a1);

        final RuleResult result = rules.analyze(packages);
        final DependencyRules rules2 = DependencyRules.allowAll().withRules("jdepend.framework.rule", new RuleDefiner() {
            PackageRule aA, a_, b_, c_;

            @Override
            public void defineRules() {
                a_.mustDependUpon(b_);
                b_.mustNotDependUpon(a_, c_).mayDependUpon(aA);
            }
        });
        assertEquals(result, rules2.analyze(packages));
        assertEquals(new RuleResult(
                        new DependencyMap(),
                        new DependencyMap()
                                .with(base("a.a"), set(), base("b.b"))
                                .with(base("a.b"), set(), base("b.a"))
                                .with(base("a.b"), set(), base("b.b")),
                        new DependencyMap()
                                .with(base("b.a"), set(base("b.a.Ba1")), base("a.b"))
                                .with(base("b.a"), set(base("b.a.Ba2")), base("c.b"))
                                .with(base("b.a"), set(base("b.a.Ba2")), base("c.a"))
                                .with(base("b.b"), set(base("b.b.Bb1")), base("c.a"))
                                .with(base("b.b"), set(base("b.b.Bb1")), base("c.b"))),
                result);
        assertMatcher("\n" +
                        "Found missing dependencies:\n" +
                        "jdepend.framework.rule.a.a ->\n" +
                        "  jdepend.framework.rule.b.b\n" +
                        "jdepend.framework.rule.a.b ->\n" +
                        "  jdepend.framework.rule.b.a\n" +
                        "  jdepend.framework.rule.b.b\n" +
                        "\n" +
                        "Found forbidden dependencies:\n" +
                        "jdepend.framework.rule.b.a ->\n" +
                        "  jdepend.framework.rule.a.b (by jdepend.framework.rule.b.a.Ba1)\n" +
                        "  jdepend.framework.rule.c.a (by jdepend.framework.rule.b.a.Ba2)\n" +
                        "  jdepend.framework.rule.c.b (by jdepend.framework.rule.b.a.Ba2)\n" +
                        "jdepend.framework.rule.b.b ->\n" +
                        "  jdepend.framework.rule.c.a (by jdepend.framework.rule.b.b.Bb1)\n" +
                        "  jdepend.framework.rule.c.b (by jdepend.framework.rule.b.b.Bb1)\n",
                rules);
    }

    @Test
    public void denyWithWildcard() {
        final DependencyRules rules = DependencyRules.denyAll();
        final PackageRule a1 = rules.addRule(base("a.a"));
        final PackageRule a = rules.addRule(base("a.*"));
        final PackageRule b = rules.addRule(base("b.*"));
        final PackageRule c = rules.addRule(base("c.*"));

        a.mustDependUpon(b);
        b.mayDependUpon(a, c).mustNotDependUpon(a1);

        final RuleResult result = rules.analyze(packages);
        assertEquals(new RuleResult(
                        new DependencyMap(),
                        new DependencyMap()
                                .with(base("a.a"), set(), base("b.b"))
                                .with(base("a.b"), set(), base("b.a"))
                                .with(base("a.b"), set(), base("b.b")),
                        new DependencyMap()
                                .with(base("b.a"), set(base("b.a.Ba1")), base("a.a"))
                                .with(base("c.a"), set(base("c.a.Ca1")), base("a.a"))
                                .with(base("c.a"), set(base("c.a.Ca1")), base("b.a"))),
                result);
        assertMatcher("\n" +
                        "Found missing dependencies:\n" +
                        "jdepend.framework.rule.a.a ->\n" +
                        "  jdepend.framework.rule.b.b\n" +
                        "jdepend.framework.rule.a.b ->\n" +
                        "  jdepend.framework.rule.b.a\n" +
                        "  jdepend.framework.rule.b.b\n" +
                        "\n" +
                        "Found forbidden dependencies:\n" +
                        "jdepend.framework.rule.b.a ->\n" +
                        "  jdepend.framework.rule.a.a (by jdepend.framework.rule.b.a.Ba1)\n" +
                        "jdepend.framework.rule.c.a ->\n" +
                        "  jdepend.framework.rule.a.a (by jdepend.framework.rule.c.a.Ca1)\n" +
                        "  jdepend.framework.rule.b.a (by jdepend.framework.rule.c.a.Ca1)\n",
                rules);
    }

    private String base(String s) {
        return BASE + s;
    }

    private Set<String> set(String... ss) {
        final Set<String> res = new HashSet<String>();
        for (String s : ss) {
            res.add(s);
        }
        return res;
    }

    private void assertMatcher(String message, DependencyRules rules) {
        final Matcher<JDepend> matcher = RuleMatchers.matches(rules);
        assertFalse(matcher.matches(jDepend));
        final StringDescription sd = new StringDescription();
        matcher.describeMismatch(jDepend, sd);
        assertEquals(message, sd.toString());
    }
}
