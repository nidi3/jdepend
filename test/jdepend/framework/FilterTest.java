package jdepend.framework;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class FilterTest extends JDependTestCase {

    @Before
    public void setUp() {
        System.setProperty("user.home", getTestDataDir());
    }

    @Test
    public void defaul() {
        PackageFilter filter = PackageFilter.all().excludingProperties();
        assertEquals(5, filter.getFilters().size());
        assertFiltersExist(filter);
    }

    @Test
    public void file() throws IOException {
        String filterFile = getTestDataDir() + "jdepend.properties";
        PackageFilter filter = PackageFilter.all().excludingFile(new File(filterFile));
        assertEquals(5, filter.getFilters().size());
        assertFiltersExist(filter);
    }

    @Test
    public void collection() throws IOException {
        Collection<String> filters = Arrays.asList("java.*", "javax.*", "sun.*", "com.sun.*", "com.xyz.tests.*");
        PackageFilter filter = PackageFilter.all().excluding(filters);
        assertEquals(5, filter.getFilters().size());
        assertFiltersExist(filter);
    }

    @Test
    public void collectionSubset() {
        Collection<String> filters = new ArrayList<String>();
        filters.add("com.xyz");
        PackageFilter filter = PackageFilter.all().excluding(filters);
        assertEquals(1, filter.getFilters().size());
    }

    @Test
    public void accept() {
        final PackageFilter filter = PackageFilter.all().excluding("a.b.c").including("a.b").excluding("a");
        assertFalse(filter.accept("a"));
        assertTrue(filter.accept("a.b"));
        assertTrue(filter.accept("a.b.d"));
        assertFalse(filter.accept("a.b.c"));
        assertFalse(filter.accept("a.c"));
    }

    private void assertFiltersExist(PackageFilter filter) {
        assertFalse(filter.accept("java.lang"));
        assertFalse(filter.accept("javax.ejb"));
        assertTrue(filter.accept("com.xyz.tests"));
        assertFalse(filter.accept("com.xyz.tests.a"));
        assertTrue(filter.accept("com.xyz.ejb"));
    }
}