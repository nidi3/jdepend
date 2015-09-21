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

    private final Collection<String> excludes;
    private final Collection<String> includes;

    private PackageFilter(Collection<String> excludes, Collection<String> includes) {
        this.excludes = excludes;
        this.includes = includes;
    }

    private PackageFilter() {
        this(new ArrayList<String>(), new ArrayList<String>());
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
        return new PackageFilter().excluding(new PropertyConfigurator().getFilteredPackages());
    }

    /**
     * Constructs a <code>PackageFilter</code> instance containing
     * the filters contained in the specified file.
     *
     * @param f Property file.
     */
    public static PackageFilter fromFile(File f) {
        return new PackageFilter().excluding(new PropertyConfigurator(f).getFilteredPackages());
    }

    /**
     * Indicates whether the specified package name passes this package filter.
     *
     * @param packageName Package name.
     * @return <code>true</code> if the package name should be included;
     * <code>false</code> otherwise.
     */
    public boolean accept(String packageName) {
        int longestInclude = 0, longestExclude = 0;
        for (String include : includes) {
            if (packageName.startsWith(include)) {
                longestInclude = Math.max(longestInclude, include.length());
            }
        }
        for (String exclude : excludes) {
            if (packageName.startsWith(exclude)) {
                longestExclude = Math.max(longestExclude, exclude.length());
            }
        }
        return longestInclude >= longestExclude;
    }

    public PackageFilter excluding(String... packageNames) {
        for (String packageName : packageNames) {
            add(excludes, packageName);
        }
        return this;
    }

    public PackageFilter excluding(Collection<String> packageNames) {
        for (String packageName : packageNames) {
            add(excludes, packageName);
        }
        return this;
    }

    public PackageFilter including(String... packageNames) {
        for (String packageName : packageNames) {
            add(includes, packageName);
        }
        return this;
    }

    public PackageFilter including(Collection<String> packageNames) {
        for (String packageName : packageNames) {
            add(includes, packageName);
        }
        return this;
    }

    private PackageFilter add(Collection<String> list, String packageName) {
        if (packageName.endsWith("*")) {
            packageName = packageName.substring(0, packageName.length() - 1);
        }

        if (packageName.length() > 0) {
            list.add(packageName);
        }
        return this;
    }

    public Collection<String> getExcludes() {
        return excludes;
    }

    public Collection<String> getIncludes() {
        return includes;
    }
}
