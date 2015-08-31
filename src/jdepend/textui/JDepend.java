package jdepend.textui;

import jdepend.framework.JavaClass;
import jdepend.framework.JavaPackage;
import jdepend.framework.PackageComparator;
import jdepend.framework.PackageFilter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The <code>JDepend</code> class analyzes directories of Java class files,
 * generates metrics for each Java package, and reports the metrics in a textual
 * format.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class JDepend {

    private jdepend.framework.JDepend analyzer;

    private PrintWriter writer;

    protected NumberFormat formatter;

    /**
     * Constructs a <code>JDepend</code> instance using standard output.
     */
    public JDepend() {
        this(new PrintWriter(System.out));
    }

    /**
     * Constructs a <code>JDepend</code> instance with the specified writer.
     *
     * @param writer Writer.
     */
    public JDepend(PrintWriter writer) {
        analyzer = new jdepend.framework.JDepend();

        formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(2);

        setWriter(writer);
    }

    /**
     * Sets the output writer.
     *
     * @param writer Output writer.
     */
    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }

    protected PrintWriter getWriter() {
        return writer;
    }

    /**
     * Sets the package filter.
     *
     * @param filter Package filter.
     */
    public void setFilter(PackageFilter filter) {
        analyzer.setFilter(filter);
    }

    /**
     * Sets the comma-separated list of components.
     */
    public void setComponents(String components) {
        analyzer.setComponents(components);
    }

    /**
     * Adds the specified directory name to the collection of directories to be
     * analyzed.
     *
     * @param name Directory name.
     * @throws IOException If the directory does not exist.
     */
    public void addDirectory(String name) throws IOException {
        analyzer.addDirectory(name);
    }

    /**
     * Determines whether inner classes are analyzed.
     *
     * @param b <code>true</code> to analyze inner classes; <code>false</code>
     *          otherwise.
     */
    public void analyzeInnerClasses(boolean b) {
        analyzer.analyzeInnerClasses(b);
    }

    /**
     * Analyzes the registered directories, generates metrics for each Java
     * package, and reports the metrics.
     */
    public void analyze() {
        List<JavaPackage> packageList = new ArrayList<JavaPackage>(analyzer.analyze());

        Collections.sort(packageList, new PackageComparator(PackageComparator.byName()));

        printHeader();
        printPackages(packageList);
        printCycles(packageList);
        printSummary(packageList);
        printFooter();

        getWriter().flush();
    }

    protected void printPackages(Collection<JavaPackage> packages) {
        printPackagesHeader();

        for (JavaPackage aPackage : packages) {
            printPackage(aPackage);
        }

        printPackagesFooter();
    }

    protected void printPackage(JavaPackage jPackage) {

        printPackageHeader(jPackage);

        if (jPackage.getClasses().size() == 0) {
            printNoStats();
            printPackageFooter(jPackage);
            return;
        }

        printStatistics(jPackage);

        printSectionBreak();

        printAbstractClasses(jPackage);

        printSectionBreak();

        printConcreteClasses(jPackage);

        printSectionBreak();

        printEfferents(jPackage);

        printSectionBreak();

        printAfferents(jPackage);

        printPackageFooter(jPackage);
    }

    protected void printAbstractClasses(JavaPackage jPackage) {
        printAbstractClassesHeader();

        List<JavaClass> members = new ArrayList<JavaClass>(jPackage.getClasses());
        Collections.sort(members, new JavaClass.ClassComparator());
        for (JavaClass jClass : members) {
            if (jClass.isAbstract()) {
                printClassName(jClass);
            }
        }

        printAbstractClassesFooter();
    }

    protected void printConcreteClasses(JavaPackage jPackage) {
        printConcreteClassesHeader();

        List<JavaClass> members = new ArrayList<JavaClass>(jPackage.getClasses());
        Collections.sort(members, new JavaClass.ClassComparator());
        for (JavaClass concrete : members) {
            if (!concrete.isAbstract()) {
                printClassName(concrete);
            }
        }

        printConcreteClassesFooter();
    }

    protected void printEfferents(JavaPackage jPackage) {
        printEfferentsHeader();

        List<JavaPackage> efferents = new ArrayList<JavaPackage>(jPackage.getEfferents());
        Collections.sort(efferents, new PackageComparator(PackageComparator.byName()));
        for (JavaPackage efferent : efferents) {
            printPackageName(efferent);
        }
        if (efferents.size() == 0) {
            printEfferentsError();
        }

        printEfferentsFooter();
    }

    protected void printAfferents(JavaPackage jPackage) {
        printAfferentsHeader();

        List<JavaPackage> afferents = new ArrayList<JavaPackage>(jPackage.getAfferents());
        Collections.sort(afferents, new PackageComparator(PackageComparator.byName()));
        for (JavaPackage afferent : afferents) {
            printPackageName(afferent);
        }
        if (afferents.size() == 0) {
            printAfferentsError();
        }

        printAfferentsFooter();
    }

    protected void printCycles(Collection<JavaPackage> packages) {
        printCyclesHeader();

        for (JavaPackage aPackage : packages) {
            printCycle(aPackage);
        }

        printCyclesFooter();
    }

    protected void printCycle(JavaPackage jPackage) {

        List<JavaPackage> list = new ArrayList<JavaPackage>();
        jPackage.collectCycle(list);

        if (!jPackage.containsCycle()) {
            return;
        }

        JavaPackage cyclePackage = list.get(list.size() - 1);
        String cyclePackageName = cyclePackage.getName();

        int i = 0;
        for (JavaPackage pkg : list) {
            i++;
            if (i == 1) {
                printCycleHeader(pkg);
            } else {
                if (pkg.getName().equals(cyclePackageName)) {
                    printCycleTarget(pkg);
                } else {
                    printCycleContributor(pkg);
                }
            }
        }

        printCycleFooter();
    }

    protected void printHeader() {
        // do nothing
    }

    protected void printFooter() {
        // do nothing
    }

    protected void printPackagesHeader() {
        // do nothing
    }

    protected void printPackagesFooter() {
        // do nothing
    }

    protected void printNoStats() {
        getWriter().println(
                "No stats available: package referenced, but not analyzed.");
    }

    protected void printPackageHeader(JavaPackage jPackage) {
        getWriter().println(
                "\n--------------------------------------------------");
        getWriter().println("- Package: " + jPackage.getName());
        getWriter().println(
                "--------------------------------------------------");
    }

    protected void printPackageFooter(JavaPackage jPackage) {
        // do nothing
    }

    protected void printStatistics(JavaPackage jPackage) {
        getWriter().println("\nStats:");
        getWriter().println(tab() + "Total Classes: " + jPackage.getClassCount());
        getWriter().println(tab() + "Concrete Classes: " + jPackage.getConcreteClassCount());
        getWriter().println(tab() + "Abstract Classes: " + jPackage.getAbstractClassCount());
        getWriter().println("");
        getWriter().println(tab() + "Ca: " + jPackage.afferentCoupling());
        getWriter().println(tab() + "Ce: " + jPackage.efferentCoupling());
        getWriter().println("");
        getWriter().println(tab() + "A: " + toFormattedString(jPackage.abstractness()));
        getWriter().println(tab() + "I: " + toFormattedString(jPackage.instability()));
        getWriter().println(tab() + "D: " + toFormattedString(jPackage.distance()));
    }

    protected void printClassName(JavaClass jClass) {
        getWriter().println(tab() + jClass.getName());
    }

    protected void printPackageName(JavaPackage jPackage) {
        getWriter().println(tab() + jPackage.getName());
    }

    protected void printAbstractClassesHeader() {
        getWriter().println("Abstract Classes:");
    }

    protected void printAbstractClassesFooter() {
        // do nothing
    }

    protected void printConcreteClassesHeader() {
        getWriter().println("Concrete Classes:");
    }

    protected void printConcreteClassesFooter() {
        // do nothing
    }

    protected void printEfferentsHeader() {
        getWriter().println("Depends Upon:");
    }

    protected void printEfferentsFooter() {
        // do nothing
    }

    protected void printEfferentsError() {
        getWriter().println(tab() + "Not dependent on any packages.");
    }

    protected void printAfferentsHeader() {
        getWriter().println("Used By:");
    }

    protected void printAfferentsFooter() {
        // do nothing
    }

    protected void printAfferentsError() {
        getWriter().println(tab() + "Not used by any packages.");
    }

    protected void printCyclesHeader() {
        printSectionBreak();
        getWriter().println(
                "\n--------------------------------------------------");
        getWriter().println("- Package Dependency Cycles:");
        getWriter().println(
                "--------------------------------------------------\n");
    }

    protected void printCyclesFooter() {
        // do nothing
    }

    protected void printCycleHeader(JavaPackage jPackage) {
        getWriter().println(jPackage.getName());
        getWriter().println(tab() + "|");
    }

    protected void printCycleTarget(JavaPackage jPackage) {
        getWriter().println(tab() + "|-> " + jPackage.getName());
    }

    protected void printCycleContributor(JavaPackage jPackage) {
        getWriter().println(tab() + "|   " + jPackage.getName());
    }

    protected void printCycleFooter() {
        printSectionBreak();
    }

    protected void printSummary(Collection<JavaPackage> packages) {
        getWriter().println("\n--------------------------------------------------");
        getWriter().println("- Summary:");
        getWriter().println("--------------------------------------------------\n");

        getWriter().println("Name, Class Count, Abstract Class Count, Ca, Ce, A, I, D, V:\n");

        for (JavaPackage jPackage : packages) {
            getWriter().print(jPackage.getName() + ",");
            getWriter().print(jPackage.getClassCount() + ",");
            getWriter().print(jPackage.getAbstractClassCount() + ",");
            getWriter().print(jPackage.afferentCoupling() + ",");
            getWriter().print(jPackage.efferentCoupling() + ",");
            getWriter().print(toFormattedString(jPackage.abstractness()) + ",");
            getWriter().print(toFormattedString(jPackage.instability()) + ",");
            getWriter().print(toFormattedString(jPackage.distance()) + ",");
            getWriter().println(jPackage.getVolatility());
        }
    }

    protected void printSectionBreak() {
        getWriter().println("");
    }

    protected String toFormattedString(float f) {
        return formatter.format(f);
    }

    protected String tab() {
        return "    ";
    }

    protected String tab(int n) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < n; i++) {
            s.append(tab());
        }

        return s.toString();
    }

    protected void usage(String message) {
        if (message != null) {
            System.err.println("\n" + message);
        }
        String baseUsage = "\nJDepend ";

        System.err.println("");
        System.err.println("usage: ");
        System.err.println(baseUsage + "[-components <components>]" +
                " [-file <output file>] <directory> " +
                "[directory2 [directory 3] ...]");
        System.exit(1);
    }

    protected void instanceMain(String[] args) {

        if (args.length < 1) {
            usage("Must specify at least one directory.");
        }

        int directoryCount = 0;

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                if (args[i].equalsIgnoreCase("-file")) {

                    if (args.length <= i + 1) {
                        usage("Output file name not specified.");
                    }

                    try {
                        setWriter(new PrintWriter(new OutputStreamWriter(
                                new FileOutputStream(args[++i]), "UTF8")));
                    } catch (IOException ioe) {
                        usage(ioe.getMessage());
                    }

                } else if (args[i].equalsIgnoreCase("-components")) {
                    if (args.length <= i + 1) {
                        usage("Components not specified.");
                    }
                    setComponents(args[++i]);
                } else {
                    usage("Invalid argument: " + args[i]);
                }
            } else {
                try {
                    addDirectory(args[i]);
                    directoryCount++;
                } catch (IOException ioe) {
                    usage("Directory does not exist: " + args[i]);
                }
            }
        }

        if (directoryCount == 0) {
            usage("Must specify at least one directory.");
        }

        analyze();
    }

    public static void main(String args[]) {
        new JDepend().instanceMain(args);
    }
}
