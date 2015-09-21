package jdepend.framework;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The <code>PackageFilter</code> class is used to filter imported
 * package names.
 * <p/>
 * The default filter contains any packages declared in the
 * <code>jdepend.properties</code> file, if such a file exists
 * either in the user's home directory or somewhere in the classpath.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class PackageFilter {

    private final Collection<String> filtered;
    private final boolean include;

    private PackageFilter(Collection<String> filtered, boolean include) {
        this.filtered = filtered;
        this.include = include;
    }

    private PackageFilter() {
        this(new ArrayList<String>(), false);
    }

    public static PackageFilter empty() {
        return new PackageFilter();
    }

    /**
     * Constructs a <code>PackageFilter</code> instance containing
     * the filters specified in the <code>jdepend.properties</code> file,
     * if it exists.
     */
    public static PackageFilter fromProperties() {
        return new PackageFilter().withPackages(new PropertyConfigurator().getFilteredPackages());
    }

    /**
     * Constructs a <code>PackageFilter</code> instance containing
     * the filters contained in the specified file.
     *
     * @param f Property file.
     */
    public static PackageFilter fromFile(File f) {
        return new PackageFilter().withPackages(new PropertyConfigurator(f).getFilteredPackages());
    }

    /**
     * Constructs a <code>PackageFilter</code> instance with the
     * specified collection of package names to filter.
     *
     * @param packageNames Package names to filter.
     */
    public static PackageFilter fromNames(String... packageNames) {
        return new PackageFilter().withPackages(packageNames);
    }

    /**
     * Constructs a <code>PackageFilter</code> instance with the
     * specified collection of package names to filter.
     *
     * @param packageNames Package names to filter.
     */
    public static PackageFilter fromNames(Collection<String> packageNames) {
        return new PackageFilter().withPackages(packageNames);
    }

    /**
     * Returns the collection of filtered package names.
     *
     * @return Filtered package names.
     */
    public Collection<String> getFilters() {
        return filtered;
    }

    /**
     * Indicates whether the specified package name passes this package filter.
     *
     * @param packageName Package name.
     * @return <code>true</code> if the package name should be included;
     * <code>false</code> otherwise.
     */
    public boolean accept(String packageName) {
        for (String nameToFilter : getFilters()) {
            if (packageName.startsWith(nameToFilter)) {
                return include;
            }
        }

        return !include;
    }

    public PackageFilter accepting() {
        return new PackageFilter(filtered, true);
    }

    public PackageFilter withPackages(String... packageNames) {
        for (String packageName : packageNames) {
            withPackage(packageName);
        }
        return this;
    }

    public PackageFilter withPackages(Collection<String> packageNames) {
        for (String packageName : packageNames) {
            withPackage(packageName);
        }
        return this;
    }

    public PackageFilter withPackage(String packageName) {
        if (packageName.endsWith("*")) {
            packageName = packageName.substring(0, packageName.length() - 1);
        }

        if (packageName.length() > 0) {
            getFilters().add(packageName);
        }
        return this;
    }
}
