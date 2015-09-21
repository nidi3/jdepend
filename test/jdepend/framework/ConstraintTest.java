package jdepend.framework;

import java.io.IOException;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class ConstraintTest extends JDependTestCase {

    private JDepend jDepend;

    public ConstraintTest(String name) {
        super(name);
    }

    protected void setUp() {
        super.setUp();
        PackageFilter filter = PackageFilter.fromProperties().withPackages("java.*","javax.*");
        jDepend = new JDepend(filter);
    }

    public void testMatchPass() {

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

    public void testMatchFail() {

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

    public void testJDependConstraints() throws IOException {

        jDepend.addDirectory(getBuildDir());
        jDepend.addDirectory(getTestBuildDir());

        jDepend.analyze();

        class Junit {
            JavaPackage framework, textui;
        }
        class Jdepend {
            JavaPackage framework, textui, xmlui, swingui, frameworkP1, frameworkP2, frameworkP3;
        }
        class Org {
            JavaPackage junit, junitRunners, hamcrest;
        }
        final Junit junit = new Junit();
        final Jdepend jdepend = new Jdepend();
        final Org org = new Org();
        DependencyConstraint constraint = DependencyConstraint.fromFields(junit, jdepend, org);

        jdepend.framework.dependsUpon(junit.framework, org.hamcrest, junit.textui);
        jdepend.textui.dependsUpon(jdepend.framework);
        jdepend.xmlui.dependsUpon(jdepend.framework, jdepend.textui);
        jdepend.swingui.dependsUpon(jdepend.framework);
        jdepend.framework.dependsUpon(jdepend.frameworkP1, jdepend.frameworkP2, jdepend.frameworkP3, org.junitRunners, org.junit);

        assertEquals(true, jDepend.dependencyMatch(constraint));
    }
}