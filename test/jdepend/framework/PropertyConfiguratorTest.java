package jdepend.framework;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class PropertyConfiguratorTest extends JDependTestCase {

    @Before
    public void setUp() {
        System.setProperty("user.home", getTestDataDir());
    }

    @Test
    public void defaultFilters() {
        PropertyConfigurator c = new PropertyConfigurator();
        assertFiltersExist(c.getFilteredPackages());
        assertFalse(c.getAnalyzeInnerClasses());
    }

    @Test
    public void filtersFromFile() throws IOException {
        String file = getTestDataDir() + "jdepend.properties";

        PropertyConfigurator c = new PropertyConfigurator(new File(file));

        assertFiltersExist(c.getFilteredPackages());
        assertFalse(c.getAnalyzeInnerClasses());
    }

    private void assertFiltersExist(Collection filters) {
        assertEquals(5, filters.size());
        assertTrue(filters.contains("java.*"));
        assertTrue(filters.contains("javax.*"));
        assertTrue(filters.contains("sun.*"));
        assertTrue(filters.contains("com.sun.*"));
        assertTrue(filters.contains("com.xyz.tests.*"));
    }

    @Test
    public void defaultPackages() throws IOException {
        JDepend j = new JDepend();

        JavaPackage pkg = j.getPackage("com.xyz.a.neverchanges");
        assertNotNull(pkg);
        assertEquals(0, pkg.getVolatility());

        pkg = j.getPackage("com.xyz.b.neverchanges");
        assertNotNull(pkg);
        assertEquals(0, pkg.getVolatility());

        pkg = j.getPackage("com.xyz.c.neverchanges");
        assertNull(pkg);
    }
}