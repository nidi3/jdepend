package jdepend.framework;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class ComponentTest extends JDependTestCase {

    private JDepend jdepend;
    private static NumberFormat formatter;

    static {
        formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(2);
    }

    @Before
    public void setUp() {
        jdepend = new JDepend();
        jdepend.analyzeInnerClasses(false);
    }

    @Test
    public void jDependComponents() throws IOException {
        jdepend.setComponents("jdepend,junit,java,javax");

        jdepend.addDirectory(getBuildDir());
        jdepend.addDirectory(getTestBuildDir());

        jdepend.analyze();

        Collection packages = jdepend.getPackages();
        assertEquals(7, packages.size());

        assertJDependPackage();
        assertJUnitPackage();
        assertJavaPackage();
        assertJavaxPackage();
    }

    private void assertJDependPackage() {
        JavaPackage p = jdepend.getPackage("jdepend");
        assertEquals("jdepend", p.getName());
        assertEquals(38, p.getConcreteClassCount());
        assertEquals(8, p.getAbstractClassCount());
        assertEquals(0, p.afferentCoupling());
        assertEquals(6, p.efferentCoupling());
        assertEquals(format(0.17f), format(p.abstractness()));
        assertEquals("1", format(p.instability()));
        assertEquals(format(0.17f), format(p.distance()));
        assertEquals(1, p.getVolatility());

        Collection efferents = p.getEfferents();
        assertEquals(6, efferents.size());
        assertTrue(efferents.contains(new JavaPackage("java")));
        assertTrue(efferents.contains(new JavaPackage("javax")));
        assertTrue(efferents.contains(new JavaPackage("junit")));

        Collection afferents = p.getAfferents();
        assertEquals(0, afferents.size());
    }

    private void assertJUnitPackage() {
        JavaPackage p = jdepend.getPackage("junit");
        assertEquals("junit", p.getName());

        Collection afferents = p.getAfferents();
        assertEquals(1, afferents.size());
        assertTrue(afferents.contains(new JavaPackage("jdepend")));

        Collection efferents = p.getEfferents();
        assertEquals(0, efferents.size());
    }

    private void assertJavaPackage() {
        JavaPackage p = jdepend.getPackage("java");
        assertEquals("java", p.getName());

        Collection afferents = p.getAfferents();
        assertEquals(1, afferents.size());
        assertTrue(afferents.contains(new JavaPackage("jdepend")));

        Collection efferents = p.getEfferents();
        assertEquals(0, efferents.size());
    }

    private void assertJavaxPackage() {
        JavaPackage p = jdepend.getPackage("javax");
        assertEquals("javax", p.getName());

        Collection afferents = p.getAfferents();
        assertEquals(1, afferents.size());
        assertTrue(afferents.contains(new JavaPackage("jdepend")));

        Collection efferents = p.getEfferents();
        assertEquals(0, efferents.size());
    }

    private String format(float f) {
        return formatter.format(f);
    }
}