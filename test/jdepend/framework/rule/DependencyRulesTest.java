package jdepend.framework.rule;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import jdepend.framework.PackageFilter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class DependencyRulesTest {
    private static final String BASE = "jdepend.framework.rule.";
    private Collection<JavaPackage> packages;

    @Before
    public void analyze() throws IOException {
        final JDepend jDepend = new JDepend(PackageFilter.all().excluding("java."));
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
        final DependencyRules dc = DependencyRules.allowAll();
        final PackageRule a = dc.addRule(base("a"));
        final PackageRule b = dc.addRule(base("b"));
        final PackageRule c = dc.addRule(base("c"));

        a.mustDependUpon(b);
        b.mustNotDependUpon(a, c).mayDependUpon(a);

        final RuleResult result = dc.analyze(packages);
        assertEquals(new RuleResult(
                        new DependencyMap(),
                        new DependencyMap().with(base("a"), set(), base("b")),
                        new DependencyMap().with(base("b"), set(base("b.B1")), base("c"))),
                result);
    }


    @Test
    public void deny() {
        final DependencyRules dc = DependencyRules.denyAll();
        final PackageRule a = dc.addRule(base("a"));
        final PackageRule b = dc.addRule(base("b"));
        final PackageRule c = dc.addRule(base("c"));

        a.mustDependUpon(b);
        b.mayDependUpon(a, c).mustNotDependUpon(a);

        final RuleResult result = dc.analyze(packages);
        assertEquals(new RuleResult(
                        new DependencyMap(),
                        new DependencyMap().with(base("a"), set(), base("b")),
                        new DependencyMap()
                                .with(base("a"), set(base("a.A1")), base("c"))
                                .with(base("b"), set(base("b.B1")), base("a"))
                                .with(base("c"), set(base("c.C1")), base("a"))
                                .with(base("c"), set(base("c.C1"), base("c.C2")), base("b"))),
                result);
    }

    @Test
    public void allowWithWildcard() {
        final DependencyRules dc = DependencyRules.allowAll();
        final PackageRule a1 = dc.addRule(base("a.a"));
        final PackageRule a = dc.addRule(base("a.*"));
        final PackageRule b = dc.addRule(base("b.*"));
        final PackageRule c = dc.addRule(base("c.*"));

        a.mustDependUpon(b);
        b.mustNotDependUpon(a, c).mayDependUpon(a1);

        final RuleResult result = dc.analyze(packages);
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
    }

    @Test
    public void denyWithWildcard() {
        final DependencyRules dc = DependencyRules.denyAll();
        final PackageRule a1 = dc.addRule(base("a.a"));
        final PackageRule a = dc.addRule(base("a.*"));
        final PackageRule b = dc.addRule(base("b.*"));
        final PackageRule c = dc.addRule(base("c.*"));

        a.mustDependUpon(b);
        b.mayDependUpon(a, c).mustNotDependUpon(a1);

        final RuleResult result = dc.analyze(packages);
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

}
