package jdepend.framework.rule;

import jdepend.framework.JDepend;
import jdepend.framework.PackageFilter;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static jdepend.framework.rule.CycleResult.packages;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 *
 */
public class CycleTest {
    private static final String BASE = "jdepend.framework.rule.";
    private JDepend jDepend;

    @Before
    public void analyze() throws IOException {
        jDepend = new JDepend(PackageFilter.all().excluding("java.").excluding("org"));
        jDepend.addDirectory("target/test-classes/jdepend/framework/rule");
        jDepend.analyze();
    }

    @Test
    public void cycles() throws IOException {
        final Matcher<JDepend> matcher = RuleMatchers.hasNoCycles();
        assertMatcher("Found these cyclic groups:\n" +
                        "\n" +
                        "- Group of 3: jdepend.framework.rule.a.a, jdepend.framework.rule.b.a, jdepend.framework.rule.c.a\n" +
                        "  jdepend.framework.rule.a.a ->\n" +
                        "    jdepend.framework.rule.b.a (by jdepend.framework.rule.a.a.Aa1)\n" +
                        "  jdepend.framework.rule.b.a ->\n" +
                        "    jdepend.framework.rule.a.a (by jdepend.framework.rule.b.a.Ba1)\n" +
                        "    jdepend.framework.rule.c.a (by jdepend.framework.rule.b.a.Ba2)\n" +
                        "  jdepend.framework.rule.c.a ->\n" +
                        "    jdepend.framework.rule.a.a (by jdepend.framework.rule.c.a.Ca1)\n" +
                        "    jdepend.framework.rule.b.a (by jdepend.framework.rule.c.a.Ca1)\n" +
                        "\n" +
                        "- Group of 3: jdepend.framework.rule.a, jdepend.framework.rule.b, jdepend.framework.rule.c\n" +
                        "  jdepend.framework.rule.a ->\n" +
                        "    jdepend.framework.rule.c (by jdepend.framework.rule.a.A1)\n" +
                        "  jdepend.framework.rule.b ->\n" +
                        "    jdepend.framework.rule.a (by jdepend.framework.rule.b.B1)\n" +
                        "    jdepend.framework.rule.c (by jdepend.framework.rule.b.B1)\n" +
                        "  jdepend.framework.rule.c ->\n" +
                        "    jdepend.framework.rule.a (by jdepend.framework.rule.c.C1)\n" +
                        "    jdepend.framework.rule.b (by jdepend.framework.rule.c.C1, jdepend.framework.rule.c.C2)\n",
                matcher);
    }

    @Test
    public void cyclesWithExceptions() throws IOException {
        final Matcher<JDepend> matcher = RuleMatchers.hasNoCyclesExcept(
                packages(base("a"), base("b"), base("c")),
                packages(base("a.a")),
                packages(base("b.a"), base("c.a")));
        assertMatcher("Found these cyclic groups:\n" +
                        "\n" +
                        "- Group of 3: jdepend.framework.rule.a.a, jdepend.framework.rule.b.a, jdepend.framework.rule.c.a\n" +
                        "  jdepend.framework.rule.a.a ->\n" +
                        "    jdepend.framework.rule.b.a (by jdepend.framework.rule.a.a.Aa1)\n" +
                        "  jdepend.framework.rule.b.a ->\n" +
                        "    jdepend.framework.rule.a.a (by jdepend.framework.rule.b.a.Ba1)\n" +
                        "    jdepend.framework.rule.c.a (by jdepend.framework.rule.b.a.Ba2)\n" +
                        "  jdepend.framework.rule.c.a ->\n" +
                        "    jdepend.framework.rule.a.a (by jdepend.framework.rule.c.a.Ca1)\n" +
                        "    jdepend.framework.rule.b.a (by jdepend.framework.rule.c.a.Ca1)\n",
                matcher);
    }

    private void assertMatcher(String message, Matcher<JDepend> matcher) {
        assertFalse(matcher.matches(jDepend));
        final StringDescription sd = new StringDescription();
        matcher.describeMismatch(jDepend, sd);
        assertEquals(message, sd.toString());
    }

    private static String base(String s) {
        return BASE + s;
    }
}
