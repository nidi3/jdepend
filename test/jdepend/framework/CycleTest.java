package jdepend.framework;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class CycleTest extends JDependTestCase {
    @Test
    public void noCycles() {
        JavaPackage a = new JavaPackage("A");
        JavaPackage b = new JavaPackage("B");

        a.dependsUpon(b);

        List<JavaPackage> aCycles = new ArrayList<JavaPackage>();
        assertEquals(false, a.containsCycle());
        assertEquals(false, a.collectCycle(aCycles));
        assertListEquals(aCycles, new String[]{});

        List<JavaPackage> bCycles = new ArrayList<JavaPackage>();
        assertEquals(false, b.containsCycle());
        assertEquals(false, b.collectCycle(bCycles));
        assertListEquals(bCycles, new String[]{});
    }

    @Test
    public void node1BranchCycle2() {
        JavaPackage a = new JavaPackage("A");
        JavaPackage b = new JavaPackage("B");

        a.dependsUpon(b);
        b.dependsUpon(a);

        List<JavaPackage> aCycles = new ArrayList<JavaPackage>();
        assertEquals(true, a.containsCycle());
        assertEquals(true, a.collectCycle(aCycles));
        assertListEquals(aCycles, new String[]{"A", "B", "A"});

        List<JavaPackage> bCycles = new ArrayList<JavaPackage>();
        assertEquals(true, b.containsCycle());
        assertEquals(true, b.collectCycle(bCycles));
        assertListEquals(bCycles, new String[]{"B", "A", "B"});
    }

    @Test
    public void node1BranchCycle3() {
        JavaPackage a = new JavaPackage("A");
        JavaPackage b = new JavaPackage("B");
        JavaPackage c = new JavaPackage("C");

        a.dependsUpon(b);
        b.dependsUpon(c);
        c.dependsUpon(a);

        List<JavaPackage> aCycles = new ArrayList<JavaPackage>();
        assertEquals(true, a.containsCycle());
        assertEquals(true, a.collectCycle(aCycles));
        assertListEquals(aCycles, new String[]{"A", "B", "C", "A"});

        List<JavaPackage> bCycles = new ArrayList<JavaPackage>();
        assertEquals(true, b.containsCycle());
        assertEquals(true, b.collectCycle(bCycles));
        assertListEquals(bCycles, new String[]{"B", "C", "A", "B"});

        List<JavaPackage> cCycles = new ArrayList<JavaPackage>();
        assertEquals(true, c.containsCycle());
        assertEquals(true, c.collectCycle(cCycles));
        assertListEquals(cCycles, new String[]{"C", "A", "B", "C"});
    }

    @Test
    public void node1BranchSubCycle3() {
        JavaPackage a = new JavaPackage("A");
        JavaPackage b = new JavaPackage("B");
        JavaPackage c = new JavaPackage("C");

        a.dependsUpon(b);
        b.dependsUpon(c);
        c.dependsUpon(b);

        List<JavaPackage> aCycles = new ArrayList<JavaPackage>();
        assertEquals(true, a.containsCycle());
        assertEquals(true, a.collectCycle(aCycles));
        assertListEquals(aCycles, new String[]{"A", "B", "C", "B"});

        List<JavaPackage> bCycles = new ArrayList<JavaPackage>();
        assertEquals(true, b.containsCycle());
        assertEquals(true, b.collectCycle(bCycles));
        assertListEquals(bCycles, new String[]{"B", "C", "B"});

        List<JavaPackage> cCycles = new ArrayList<JavaPackage>();
        assertEquals(true, c.containsCycle());
        assertEquals(true, c.collectCycle(cCycles));
        assertListEquals(cCycles, new String[]{"C", "B", "C"});
    }

    @Test
    public void node2BranchCycle3() {
        JavaPackage a = new JavaPackage("A");
        JavaPackage b = new JavaPackage("B");
        JavaPackage c = new JavaPackage("C");

        a.dependsUpon(b);
        b.dependsUpon(a);

        a.dependsUpon(c);
        c.dependsUpon(a);

        List<JavaPackage> aCycles = new ArrayList<JavaPackage>();
        assertEquals(true, a.containsCycle());
        assertEquals(true, a.collectCycle(aCycles));
        assertListEquals(aCycles, new String[]{"A", "B", "A"});

        List<JavaPackage> bCycles = new ArrayList<JavaPackage>();
        assertEquals(true, b.containsCycle());
        assertEquals(true, b.collectCycle(bCycles));
        assertListEquals(bCycles, new String[]{"B", "A", "B"});

        List<JavaPackage> cCycles = new ArrayList<JavaPackage>();
        assertEquals(true, c.containsCycle());
        assertEquals(true, c.collectCycle(cCycles));
        assertListEquals(cCycles, new String[]{"C", "A", "B", "A"});
    }

    @Test
    public void node2BranchCycle5() {
        JavaPackage a = new JavaPackage("A");
        JavaPackage b = new JavaPackage("B");
        JavaPackage c = new JavaPackage("C");
        JavaPackage d = new JavaPackage("D");
        JavaPackage e = new JavaPackage("E");

        a.dependsUpon(b);
        b.dependsUpon(c);
        c.dependsUpon(a);

        a.dependsUpon(d);
        d.dependsUpon(e);
        e.dependsUpon(a);

        List<JavaPackage> aCycles = new ArrayList<JavaPackage>();
        assertEquals(true, a.containsCycle());
        assertEquals(true, a.collectCycle(aCycles));
        assertListEquals(aCycles, new String[]{"A", "B", "C", "A"});

        List<JavaPackage> bCycles = new ArrayList<JavaPackage>();
        assertEquals(true, b.containsCycle());
        assertEquals(true, b.collectCycle(bCycles));
        assertListEquals(bCycles, new String[]{"B", "C", "A", "B"});

        List<JavaPackage> cCycles = new ArrayList<JavaPackage>();
        assertEquals(true, c.containsCycle());
        assertEquals(true, c.collectCycle(cCycles));
        assertListEquals(cCycles, new String[]{"C", "A", "B", "C"});

        List<JavaPackage> dCycles = new ArrayList<JavaPackage>();
        assertEquals(true, d.containsCycle());
        assertEquals(true, d.collectCycle(dCycles));
        assertListEquals(dCycles, new String[]{"D", "E", "A", "B", "C", "A"});

        List<JavaPackage> eCycles = new ArrayList<JavaPackage>();
        assertEquals(true, e.containsCycle());
        assertEquals(true, e.collectCycle(eCycles));
        assertListEquals(eCycles, new String[]{"E", "A", "B", "C", "A"});
    }

    protected void assertListEquals(List<JavaPackage> list, String names[]) {
        assertEquals(names.length, list.size());

        for (int i = 0; i < names.length; i++) {
            assertEquals(names[i], (list.get(i)).getName());
        }
    }

    protected void printCycles(List<JavaPackage> list) {
        Iterator<JavaPackage> i = list.iterator();
        while (i.hasNext()) {
            JavaPackage p = i.next();
            if (i.hasNext()) {
                System.out.print(p.getName() + "->");
            } else {
                System.out.println(p.getName());
            }
        }
    }
}