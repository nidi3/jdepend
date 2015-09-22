package jdepend.framework;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * The <code>PackageFilter</code> class is used to filter imported
 * package names.
 * A Package Filter is constructed like this:
 * <pre>PackageFilter filter = PackageFilter.all().excluding(...).including(...)...</pre>
 * The filter executes all excluding/including entries in the order they are defined.
 * The first one that matches is used as the result of the filter.
 * If no entry matches, the Filter accepts a package.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class PackageFilter {
    private final Collection<Filter> filters;

    private PackageFilter(Collection<Filter> filters) {
        this.filters = filters;
    }

    private PackageFilter() {
        this(new ArrayList<Filter>());
    }

    public static PackageFilter all() {
        return new PackageFilter();
    }

    /**
     * Add a <code>Filter</code> rejecting the entries specified in the
     * <code>jdepend.properties</code> file, if it exists.
     */
    public PackageFilter excludingProperties() {
        return excluding(new PropertyConfigurator().getFilteredPackages());
    }

    /**
     * Add a <code>Filter</code> accepting the entries specified in the
     * <code>jdepend.properties</code> file, if it exists.
     */
    public PackageFilter includingProperties() {
        return including(new PropertyConfigurator().getFilteredPackages());
    }

    /**
     * Add a <code>Filter</code> rejecting the entries in the specified properties file.
     *
     * @param f Property file.
     */
    public PackageFilter excludingFile(File f) {
        return excluding(new PropertyConfigurator(f).getFilteredPackages());
    }

    /**
     * Add a <code>Filter</code> accepting the entries in the specified properties file.
     *
     * @param f Property file.
     */
    public PackageFilter including(File f) {
        return including(new PropertyConfigurator(f).getFilteredPackages());
    }

    public PackageFilter excluding(String... packageNames) {
        return add(Arrays.asList(packageNames), false);
    }

    public PackageFilter excluding(Collection<String> packageNames) {
        return add(packageNames, false);
    }

    public PackageFilter excludingRest() {
        return excluding("");
    }

    public PackageFilter including(String... packageNames) {
        return add(Arrays.asList(packageNames), true);
    }

    public PackageFilter including(Collection<String> packageNames) {
        return add(packageNames, true);
    }

    /**
     * Indicates whether the specified package name passes this package filter.
     *
     * @param packageName Package name.
     * @return <code>true</code> if the package name should be included;
     * <code>false</code> otherwise.
     */
    public boolean accept(String packageName) {
        for (Filter filter : filters) {
            if (packageName.startsWith(filter.name)) {
                return filter.include;
            }
        }
        return true;
    }

    private PackageFilter add(Collection<String> packageNames, boolean include) {
        for (final String packageName : packageNames) {
            add(packageName, include);
        }
        return this;
    }

    private PackageFilter add(String packageName, boolean include) {
        if (packageName.endsWith("*")) {
            packageName = packageName.substring(0, packageName.length() - 1);
        }
        filters.add(new Filter(packageName, include));
        return this;
    }

    public Collection<Filter> getFilters() {
        return filters;
    }

    public static class Filter {
        public final String name;
        public final boolean include;

        public Filter(String name, boolean include) {
            this.name = name;
            this.include = include;
        }
    }
}
