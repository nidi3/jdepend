package jdepend.framework;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

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

public class ExampleTest extends TestCase {

    private JDepend jdepend;

    public String jdependHomeDirectory;

    public ExampleTest(String name) {
        super(name);
    }

    protected void setUp() throws IOException {

        jdependHomeDirectory = System.getProperty("jdepend.home");
        if (jdependHomeDirectory == null) {
            fail("Property 'jdepend.home' not defined");
        }

        PackageFilter filter = new PackageFilter();
        filter.addPackage("java.*");
        filter.addPackage("javax.*");
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
    public void testOnePackageDistance() {

        double ideal = 0.0;
        double tolerance = 0.8;

        jdepend.analyze();

        JavaPackage p = jdepend.getPackage("jdepend.framework");

        assertEquals("Distance exceeded: " + p.getName(),
                ideal, p.distance(), tolerance);
    }

    /**
     * Tests that a single package does not contain any
     * package dependency cycles.
     */
    public void testOnePackageHasNoCycles() {

        jdepend.analyze();

        JavaPackage p = jdepend.getPackage("jdepend.framework");

        assertEquals("Cycles exist: " + p.getName(),
                false, p.containsCycle());
    }

    /**
     * Tests the conformance of all analyzed packages to a
     * distance from the main sequence (D) within a tolerance.
     */
    public void testAllPackagesDistance() {

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
    public void testAllPackagesHaveNoCycles() {

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
    public void testDependencyConstraint() {

        DependencyConstraint constraint = new DependencyConstraint();

        JavaPackage junitframework = constraint.addPackage("junit.framework");
        JavaPackage junitui = constraint.addPackage("junit.textui");
        JavaPackage framework = constraint.addPackage("jdepend.framework");
        JavaPackage text = constraint.addPackage("jdepend.textui");
        JavaPackage xml = constraint.addPackage("jdepend.xmlui");
        JavaPackage swing = constraint.addPackage("jdepend.swingui");
        JavaPackage orgjunitrunners = constraint.addPackage("org.junit.runners");
        JavaPackage jdependframeworkp2 = constraint.addPackage("jdepend.framework.p2");
        JavaPackage jdependframeworkp3 = constraint.addPackage("jdepend.framework.p3");
        JavaPackage jdependframeworkp1 = constraint.addPackage("jdepend.framework.p1");
        JavaPackage orgjunit = constraint.addPackage("org.junit");
        JavaPackage orghamcrest = constraint.addPackage("org.hamcrest");

        framework.dependsUpon(junitframework, orghamcrest, junitui);
        text.dependsUpon(framework);
        xml.dependsUpon(framework, text);
        swing.dependsUpon(framework);
        framework.dependsUpon(jdependframeworkp1, jdependframeworkp2, jdependframeworkp3, orgjunitrunners, orgjunit);

        jdepend.analyze();

        assertEquals("Constraint mismatch", true, jdepend.dependencyMatch(constraint));
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ExampleTest.class);
    }
}