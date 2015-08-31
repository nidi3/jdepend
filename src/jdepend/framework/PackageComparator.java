package jdepend.framework;

import java.util.Comparator;

/**
 * The <code>PackageComparator</code> class is a <code>Comparator</code>
 * used to compare two <code>JavaPackage</code> instances for order using a
 * sorting strategy.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class PackageComparator implements Comparator<JavaPackage> {

    private PackageComparator byWhat;

    private static PackageComparator byName;

    static {
        byName = new PackageComparator();
    }

    public static PackageComparator byName() {
        return byName;
    }

    private PackageComparator() {
    }

    public PackageComparator(PackageComparator byWhat) {
        this.byWhat = byWhat;
    }

    public PackageComparator byWhat() {
        return byWhat;
    }

    public int compare(JavaPackage p1, JavaPackage p2) {
        if (byWhat() == byName()) {
            return p1.getName().compareTo(p2.getName());
        }

        return 0;
    }
}