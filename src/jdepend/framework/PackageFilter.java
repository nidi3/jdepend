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

    /**
     * Constructs a <code>PackageFilter</code> instance containing
     * the filters specified in the <code>jdepend.properties</code> file,
     * if it exists.
     */
    public PackageFilter() {
        this(new ArrayList<String>());
        PropertyConfigurator config = new PropertyConfigurator();
        addPackages(config.getFilteredPackages());
    }

    /**
     * Constructs a <code>PackageFilter</code> instance containing
     * the filters contained in the specified file.
     *
     * @param f Property file.
     */
    public PackageFilter(File f) {
        this(new ArrayList<String>());
        PropertyConfigurator config = new PropertyConfigurator(f);
        addPackages(config.getFilteredPackages());
    }

    /**
     * Constructs a <code>PackageFilter</code> instance with the
     * specified collection of package names to filter.
     *
     * @param packageNames Package names to filter.
     */
    public PackageFilter(String... packageNames) {
        filtered = new ArrayList<String>();
        addPackages(packageNames);
    }

    /**
     * Constructs a <code>PackageFilter</code> instance with the
     * specified collection of package names to filter.
     *
     * @param packageNames Package names to filter.
     */
    public PackageFilter(Collection<String> packageNames) {
        filtered = new ArrayList<String>();
        addPackages(packageNames);
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
                return false;
            }
        }

        return true;
    }

    public void addPackages(String... packageNames) {
        for (String packageName : packageNames) {
            addPackage(packageName);
        }
    }

    public void addPackages(Collection<String> packageNames) {
        for (String packageName : packageNames) {
            addPackage(packageName);
        }
    }

    public void addPackage(String packageName) {
        if (packageName.endsWith("*")) {
            packageName = packageName.substring(0, packageName.length() - 1);
        }

        if (packageName.length() > 0) {
            getFilters().add(packageName);
        }
    }
}
