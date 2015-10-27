package jdepend.framework.rule;

import jdepend.framework.JavaPackage;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class DependencyRulesTest {
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
        final PackageRule a = dc.addRule("a");
        final PackageRule b = dc.addRule("b");
        final PackageRule c = dc.addRule("c");

        a.mustDependUpon(b);
        b.mustNotDependUpon(a, c).mayDependUpon(a);

        final JavaPackage ea = new JavaPackage("a");
        final JavaPackage eb = new JavaPackage("b");
        final JavaPackage ec = new JavaPackage("c");
        ea.dependsUpon(ec);
        eb.dependsUpon(ea, ec);
        ec.dependsUpon(ea, eb);

        final RuleResult result = dc.analyze(Arrays.asList(ea, eb, ec));
        assertEquals(new RuleResult(
                        new DependencyMap(),
                        new DependencyMap().with("a", "b"),
                        new DependencyMap().with("b", "c")),
                result);
    }

    @Test
    public void deny() {
        final DependencyRules dc = DependencyRules.denyAll();
        final PackageRule a = dc.addRule("a");
        final PackageRule b = dc.addRule("b");
        final PackageRule c = dc.addRule("c");

        a.mustDependUpon(b);
        b.mayDependUpon(a, c).mustNotDependUpon(a);

        final JavaPackage ea = new JavaPackage("a");
        final JavaPackage eb = new JavaPackage("b");
        final JavaPackage ec = new JavaPackage("c");
        ea.dependsUpon(ec);
        eb.dependsUpon(ea, ec);
        ec.dependsUpon(ea, eb);

        final RuleResult result = dc.analyze(Arrays.asList(ea, eb, ec));
        assertEquals(new RuleResult(
                        new DependencyMap(),
                        new DependencyMap().with("a", "b"),
                        new DependencyMap().with("a", "c").with("b", "a").with("c", "a", "b")),
                result);
    }

    @Test
    public void allowWithWildcard() {
        final DependencyRules dc = DependencyRules.allowAll();
        final PackageRule a1 = dc.addRule("a.1");
        final PackageRule a = dc.addRule("a.*");
        final PackageRule b = dc.addRule("b.*");
        final PackageRule c = dc.addRule("c.*");

        a.mustDependUpon(b);
        b.mustNotDependUpon(a, c).mayDependUpon(a1);

        final JavaPackage ea1 = new JavaPackage("a.1");
        final JavaPackage ea2 = new JavaPackage("a.2");
        final JavaPackage eb1 = new JavaPackage("b.1");
        final JavaPackage eb2 = new JavaPackage("b.2");
        final JavaPackage ec1 = new JavaPackage("c.1");
        final JavaPackage ec2 = new JavaPackage("c.2");
        ea1.dependsUpon(eb1); //missing a.1->b.2, a.2->b.1, a.2->b.2
        eb1.dependsUpon(ea1, ea2); //denied b.1->a.2
        eb2.dependsUpon(ec1, ec2); //denied b.2->c.1,b.2->c.2

        final RuleResult result = dc.analyze(Arrays.asList(ea1, ea2, eb1, eb2, ec1, ec2));
        assertEquals(new RuleResult(
                        new DependencyMap(),
                        new DependencyMap().with("a.1", "b.2").with("a.2", "b.1", "b.2"),
                        new DependencyMap().with("b.1", "a.2").with("b.2", "c.1", "c.2")),
                result);
    }

    @Test
    public void denyWithWildcard() {
        final DependencyRules dc = DependencyRules.denyAll();
        final PackageRule a1 = dc.addRule("a.1");
        final PackageRule a = dc.addRule("a.*");
        final PackageRule b = dc.addRule("b.*");
        final PackageRule c = dc.addRule("c.*");

        a.mustDependUpon(b);
        b.mayDependUpon(a, c).mustNotDependUpon(a1);

        final JavaPackage ea1 = new JavaPackage("a.1");
        final JavaPackage ea2 = new JavaPackage("a.2");
        final JavaPackage eb1 = new JavaPackage("b.1");
        final JavaPackage eb2 = new JavaPackage("b.2");
        final JavaPackage ec1 = new JavaPackage("c.1");
        final JavaPackage ec2 = new JavaPackage("c.2");
        //constraint a1 denies a.1->b.1, but a.mustDependUpon(b) overrules this
        ea1.dependsUpon(eb1); //missing a.1->b.2, a.2->b.1, a.2->b.2,
        eb1.dependsUpon(ea1, ea2, ec1, ec2); //denied b.1->a.1
        ec1.dependsUpon(ea1, eb1); //denied c.1->a.1,c.1->b.1


        final RuleResult result = dc.analyze(Arrays.asList(ea1, ea2, eb1, eb2, ec1, ec2));
        assertEquals(new RuleResult(
                        new DependencyMap(),
                        new DependencyMap().with("a.1", "b.2").with("a.2", "b.1", "b.2"),
                        new DependencyMap().with("b.1", "a.1").with("c.1", "a.1", "b.1")),
                result);
    }

}
