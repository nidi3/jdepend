package jdepend.framework;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static jdepend.framework.DependencyMatchers.matchesPackages;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class ConstraintTest extends JDependTestCase {

    private JDepend jDepend;

    @Before
    public void setUp() {
        PackageFilter filter = PackageFilter.all().excludingProperties().excluding("java.*", "javax.*");
        jDepend = new JDepend(filter);
    }

    @Test
    public void matchPass() {
        DependencyConstraint constraint = new DependencyConstraint();

        JavaPackage expectedA = constraint.addPackage("A");
        JavaPackage expectedB = constraint.addPackage("B");

        expectedA.dependsUpon(expectedB);

        JavaPackage actualA = new JavaPackage("A");
        JavaPackage actualB = new JavaPackage("B");

        actualA.dependsUpon(actualB);

        jDepend.addPackage(actualA);
        jDepend.addPackage(actualB);

        assertEquals(true, jDepend.dependencyMatch(constraint));
    }

    @Test
    public void matchFail() {
        DependencyConstraint constraint = new DependencyConstraint();

        JavaPackage expectedA = constraint.addPackage("A");
        JavaPackage expectedB = constraint.addPackage("B");
        JavaPackage expectedC = constraint.addPackage("C");

        expectedA.dependsUpon(expectedB);

        JavaPackage actualA = new JavaPackage("A");
        JavaPackage actualB = new JavaPackage("B");
        JavaPackage actualC = new JavaPackage("C");

        actualA.dependsUpon(actualB);
        actualA.dependsUpon(actualC);

        jDepend.addPackage(actualA);
        jDepend.addPackage(actualB);
        jDepend.addPackage(actualC);

        assertEquals(false, jDepend.dependencyMatch(constraint));
    }

    @Test
    public void jDependConstraints() throws IOException {
        jDepend.addDirectory(getBuildDir());
        jDepend.addDirectory(getTestBuildDir());

        jDepend.analyze();

        class Junit {
            JavaPackage framework;
        }
        class Org {
            JavaPackage junit, junitRunners, hamcrest;
        }
        final Junit junit = new Junit();
        final Org org = new Org();

        class Jdepend implements DependencyDefiner {
            JavaPackage framework,frameworkRule, textui, xmlui, swingui,
                    frameworkP1, frameworkP2, frameworkP3, frameworkP4,
                    frameworkP4P1, frameworkP4P2, frameworkP4P3, frameworkP4P4, frameworkP4P5,
                    frameworkP4P6, frameworkP4P7, frameworkP4P8, frameworkP4P9, frameworkP4P10;

            public void dependUpon() {
                framework.dependsUpon(org.hamcrest);
                frameworkRule.dependsUpon(org.junit,framework);
                textui.dependsUpon(framework);
                xmlui.dependsUpon(framework, textui);
                swingui.dependsUpon(framework);
                framework.dependsUpon(frameworkP1, frameworkP2, frameworkP3, org.junitRunners, org.junit);
                frameworkP4.dependsUpon(frameworkP4P1, frameworkP4P2, frameworkP4P4, frameworkP4P5,
                        frameworkP4P6, frameworkP4P7, frameworkP4P8, frameworkP4P9, frameworkP4P10);
            }
        }
        assertThat(jDepend, matchesPackages(junit, org, new Jdepend()));
    }
}