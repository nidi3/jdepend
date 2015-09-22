package jdepend.framework;

import java.util.*;

/**
 * The <code>JavaPackage</code> class represents a Java package.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class JavaPackage {

    private String name;
    private int volatility;
    private Set<JavaClass> classes;
    private List<JavaPackage> afferents;
    private List<JavaPackage> efferents;


    public JavaPackage(String name) {
        this(name, 1);
    }

    public JavaPackage(String name, int volatility) {
        this.name = name;
        setVolatility(volatility);
        classes = new HashSet<JavaClass>();
        afferents = new ArrayList<JavaPackage>();
        efferents = new ArrayList<JavaPackage>();
    }

    public String getName() {
        return name;
    }

    /**
     * @return The package's volatility (0-1).
     */
    public int getVolatility() {
        return volatility;
    }

    /**
     * @param v Volatility (0-1).
     */
    public void setVolatility(int v) {
        volatility = v;
    }

    public boolean containsCycle() {
        return collectCycle(new ArrayList<JavaPackage>());
    }

    /**
     * Collects the packages participating in the first package dependency cycle
     * detected which originates from this package.
     *
     * @param list Collecting object to be populated with the list of
     *             JavaPackage instances in a cycle.
     * @return <code>true</code> if a cycle exist; <code>false</code>
     * otherwise.
     */
    public boolean collectCycle(List<JavaPackage> list) {

        if (list.contains(this)) {
            list.add(this);
            return true;
        }

        list.add(this);

        for (JavaPackage efferent : getEfferents()) {
            if (efferent.collectCycle(list)) {
                return true;
            }
        }

        list.remove(this);

        return false;
    }

    /**
     * Collects all the packages participating in a package dependency cycle
     * which originates from this package.
     * <p>
     * This is a more exhaustive search than that employed by
     * <code>collectCycle</code>.
     * </p>
     *
     * @param list Collecting object to be populated with the list of
     *             JavaPackage instances in a cycle.
     * @return <code>true</code> if a cycle exist; <code>false</code>
     * otherwise.
     */
    public boolean collectAllCycles(List<JavaPackage> list) {

        if (list.contains(this)) {
            list.add(this);
            return true;
        }

        list.add(this);

        boolean containsCycle = false;
        for (JavaPackage efferent : getEfferents()) {
            if (efferent.collectAllCycles(list)) {
                containsCycle = true;
            }
        }

        if (containsCycle) {
            return true;
        }

        list.remove(this);
        return false;
    }

    public void addClass(JavaClass clazz) {
        classes.add(clazz);
    }

    public Collection<JavaClass> getClasses() {
        return classes;
    }

    public int getClassCount() {
        return classes.size();
    }

    public int getAbstractClassCount() {
        int count = 0;

        for (JavaClass clazz : classes) {
            if (clazz.isAbstract()) {
                count++;
            }
        }

        return count;
    }

    public int getConcreteClassCount() {
        int count = 0;

        for (JavaClass clazz : classes) {
            if (!clazz.isAbstract()) {
                count++;
            }
        }

        return count;
    }

    /**
     * Adds the specified Java package as an efferent of this package
     * and adds this package as an afferent of it.
     *
     * @param imported Java package.
     */
    public void dependsUpon(JavaPackage... imported) {
        for (final JavaPackage pack : imported) {
            addEfferent(pack);
            pack.addAfferent(this);
        }
    }

    /**
     * Adds the specified Java package as an afferent of this package.
     *
     * @param jPackage Java package.
     */
    public void addAfferent(JavaPackage jPackage) {
        if (!jPackage.getName().equals(getName())) {
            if (!afferents.contains(jPackage)) {
                afferents.add(jPackage);
            }
        }
    }

    public Collection<JavaPackage> getAfferents() {
        return afferents;
    }

    public void setAfferents(Collection<JavaPackage> afferents) {
        this.afferents = new ArrayList<JavaPackage>(afferents);
    }

    public void addEfferent(JavaPackage jPackage) {
        if (!jPackage.getName().equals(getName())) {
            if (!efferents.contains(jPackage)) {
                efferents.add(jPackage);
            }
        }
    }

    public Collection<JavaPackage> getEfferents() {
        return efferents;
    }

    public void setEfferents(Collection<JavaPackage> efferents) {
        this.efferents = new ArrayList<JavaPackage>(efferents);
    }

    /**
     * @return The afferent coupling (Ca) of this package.
     */
    public int afferentCoupling() {
        return afferents.size();
    }

    /**
     * @return The efferent coupling (Ce) of this package.
     */
    public int efferentCoupling() {
        return efferents.size();
    }

    /**
     * @return Instability (0-1).
     */
    public float instability() {
        float totalCoupling = (float) efferentCoupling() + (float) afferentCoupling();

        if (totalCoupling > 0) {
            return efferentCoupling() / totalCoupling;
        }

        return 0;
    }

    /**
     * @return The package's abstractness (0-1).
     */
    public float abstractness() {

        if (getClassCount() > 0) {
            return (float) getAbstractClassCount() / (float) getClassCount();
        }

        return 0;
    }

    /**
     * @return The package's distance from the main sequence (D).
     */
    public float distance() {
        float d = Math.abs(abstractness() + instability() - 1);
        return d * volatility;
    }

    public boolean equals(Object other) {
        if (other instanceof JavaPackage) {
            JavaPackage otherPackage = (JavaPackage) other;
            return otherPackage.getName().equals(getName());
        }
        return false;
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public String toString() {
        return name;
    }
}
