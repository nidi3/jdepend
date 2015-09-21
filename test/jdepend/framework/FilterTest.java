package jdepend.framework;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class FilterTest extends JDependTestCase {

    public FilterTest(String name) {
        super(name);
    }

    protected void setUp() {
        super.setUp();
        System.setProperty("user.home", getTestDataDir());
    }

    protected void tearDown() {
        super.tearDown();
    }

    public void testDefault() {
        PackageFilter filter = PackageFilter.fromProperties();
        assertEquals(5, filter.getFilters().size());
        assertFiltersExist(filter);
    }

    public void testFile() throws IOException {
        String filterFile = getTestDataDir() + "jdepend.properties";
        PackageFilter filter = PackageFilter.fromFile(new File(filterFile));
        assertEquals(5, filter.getFilters().size());
        assertFiltersExist(filter);
    }

    public void testCollection() throws IOException {
        Collection<String> filters = Arrays.asList("java.*", "javax.*", "sun.*", "com.sun.*", "com.xyz.tests.*");
        PackageFilter filter = PackageFilter.fromNames(filters);
        assertEquals(5, filter.getFilters().size());
        assertFiltersExist(filter);
    }

    public void testCollectionSubset() {
        Collection<String> filters = new ArrayList<String>();
        filters.add("com.xyz");
        PackageFilter filter = PackageFilter.fromNames(filters);
        assertEquals(1, filter.getFilters().size());
    }

    public void testAccept() {
        final PackageFilter filter = PackageFilter.fromNames("a").accepting();
        assertTrue(filter.accept("a"));
        assertFalse(filter.accept("b"));
    }

    private void assertFiltersExist(PackageFilter filter) {
        assertFalse(filter.accept("java.lang"));
        assertFalse(filter.accept("javax.ejb"));
        assertTrue(filter.accept("com.xyz.tests"));
        assertFalse(filter.accept("com.xyz.tests.a"));
        assertTrue(filter.accept("com.xyz.ejb"));
    }
}