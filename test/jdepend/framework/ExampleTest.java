package jdepend.framework;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * The <code>ExampleTest</code> is an example <code>TestCase</code>
 * that demonstrates tests for measuring the distance from the
 * main sequence (D), package dependency constraints, and the
 * existence of cyclic package dependencies.
 * <p/>
 * This test analyzes the JDepend class files.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class ExampleTest {

    private JDepend jdepend;

    public String jdependHomeDirectory;

    @Before
    public void setUp() throws IOException {
        jdependHomeDirectory = System.getProperty("jdepend.home");
        if (jdependHomeDirectory == null) {
            fail("Property 'jdepend.home' not defined");
        }

        PackageFilter filter = PackageFilter.all().excludingProperties().excluding("java.*", "javax.*");
        jdepend = new JDepend(filter);

        String classesDir = jdependHomeDirectory + File.separator + "target/classes";
        String testClassesDir = jdependHomeDirectory + File.separator + "target/test-classes";

        jdepend.addDirectory(classesDir);
        jdepend.addDirectory(testClassesDir);
    }

    /**
     * Tests the conformance of a single package to a distance
     * from the main sequence (D) within a tolerance.
     */
    @Test
    public void onePackageDistance() {
        double ideal = 0.0;
        double tolerance = 0.8;

        jdepend.analyze();

        JavaPackage p = jdepend.getPackage("jdepend.framework");

        assertEquals("Distance exceeded: " + p.getName(), ideal, p.distance(), tolerance);
    }

    /**
     * Tests that a single package does not contain any
     * package dependency cycles.
     */
    @Test
    public void onePackageHasNoCycles() {
        jdepend.analyze();

        JavaPackage p = jdepend.getPackage("jdepend.framework");

        assertEquals("Cycles exist: " + p.getName(), false, p.containsCycle());
    }

    /**
     * Tests the conformance of all analyzed packages to a
     * distance from the main sequence (D) within a tolerance.
     */
    @Test
    public void allPackagesDistance() {
        double ideal = 0.0;
        double tolerance = 1.0;

        Collection packages = jdepend.analyze();

        for (Object aPackage : packages) {
            JavaPackage p = (JavaPackage) aPackage;
            assertEquals("Distance exceeded: " + p.getName(), ideal, p.distance(), tolerance);
        }
    }

    /**
     * Tests that a package dependency cycle does not exist
     * for any of the analyzed packages.
     */
    @Test
    public void allPackagesHaveNoCycles() {
        Collection packages = jdepend.analyze();

        assertEquals("Cycles exist", false, jdepend.containsCycles());
    }

    /**
     * Tests that a package dependency constraint is matched
     * for the analyzed packages.
     * <p/>
     * Fails if any package dependency other than those declared
     * in the dependency constraints are detected.
     */
    @Test
    public void dependencyConstraint() {
        DependencyConstraint constraint = new DependencyConstraint();

        JavaPackage framework = constraint.addPackage("jdepend.framework");
        JavaPackage text = constraint.addPackage("jdepend.textui");
        JavaPackage xml = constraint.addPackage("jdepend.xmlui");
        JavaPackage swing = constraint.addPackage("jdepend.swingui");
        JavaPackage orgjunitrunners = constraint.addPackage("org.junit.runners");
        JavaPackage jdependframeworkp2 = constraint.addPackage("jdepend.framework.p2");
        JavaPackage jdependframeworkp3 = constraint.addPackage("jdepend.framework.p3");
        JavaPackage jdependframeworkp1 = constraint.addPackage("jdepend.framework.p1");
        JavaPackage jdependframeworkp4 = constraint.addPackage("jdepend.framework.p4");
        JavaPackage jdependframeworkp4p1 = constraint.addPackage("jdepend.framework.p4.p1");
        JavaPackage jdependframeworkp4p2 = constraint.addPackage("jdepend.framework.p4.p2");
        JavaPackage jdependframeworkp4p3 = constraint.addPackage("jdepend.framework.p4.p3");
        JavaPackage jdependframeworkp4p4 = constraint.addPackage("jdepend.framework.p4.p5");
        JavaPackage jdependframeworkp4p5 = constraint.addPackage("jdepend.framework.p4.p4");
        JavaPackage jdependframeworkp4p6 = constraint.addPackage("jdepend.framework.p4.p6");
        JavaPackage jdependframeworkp4p7 = constraint.addPackage("jdepend.framework.p4.p7");
        JavaPackage jdependframeworkp4p8 = constraint.addPackage("jdepend.framework.p4.p8");
        JavaPackage jdependframeworkp4p9 = constraint.addPackage("jdepend.framework.p4.p9");
        JavaPackage jdependframeworkp4p10 = constraint.addPackage("jdepend.framework.p4.p10");
        JavaPackage orgjunit = constraint.addPackage("org.junit");
        JavaPackage orghamcrest = constraint.addPackage("org.hamcrest");

        framework.dependsUpon(orghamcrest);
        text.dependsUpon(framework);
        xml.dependsUpon(framework, text);
        swing.dependsUpon(framework);
        framework.dependsUpon(jdependframeworkp1, jdependframeworkp2, jdependframeworkp3, orgjunitrunners, orgjunit);
        jdependframeworkp4.dependsUpon(jdependframeworkp4p1,jdependframeworkp4p2,jdependframeworkp4p4,jdependframeworkp4p5,
                jdependframeworkp4p6,jdependframeworkp4p7,jdependframeworkp4p8,jdependframeworkp4p9,jdependframeworkp4p10);

        jdepend.analyze();

        assertEquals("Constraint mismatch", true, jdepend.dependencyMatch(constraint));
    }
}