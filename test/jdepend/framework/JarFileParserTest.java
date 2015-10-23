package jdepend.framework;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class JarFileParserTest extends JDependTestCase {

    private File jarFile;
    private File zipFile;

    @Before
    public void setUp() {
        jarFile = new File(getTestDataDir() + "test.jar");
        zipFile = new File(getTestDataDir() + "test.zip");
    }

    @Test(expected = IOException.class)
    public void invalidJarFile() throws IOException {
        JavaClassBuilder builder = new JavaClassBuilder();
        File bogusFile = new File(getTestDataDir() + "bogus.jar");
        builder.buildClasses(bogusFile);
    }

    @Test(expected = IOException.class)
    public void testInvalidZipFile() throws IOException {
        JavaClassBuilder builder = new JavaClassBuilder();
        File bogusFile = new File(getTestDataDir() + "bogus.zip");
        builder.buildClasses(bogusFile);
    }

    @Test
    public void jarFile() throws IOException {
        JavaClassBuilder builder = new JavaClassBuilder();

        Collection classes = builder.buildClasses(jarFile);
        assertEquals(5, classes.size());

        assertClassesExist(classes);
        assertInnerClassesExist(classes);
    }

    @Test
    public void jarFileWithoutInnerClasses() throws IOException {
        FileManager fm = new FileManager();
        fm.acceptInnerClasses(false);

        JavaClassBuilder builder = new JavaClassBuilder(fm);

        Collection classes = builder.buildClasses(jarFile);
        assertEquals(4, classes.size());

        assertClassesExist(classes);
    }

    @Test
    public void zipFile() throws IOException {
        JavaClassBuilder builder = new JavaClassBuilder();

        Collection classes = builder.buildClasses(zipFile);
        assertEquals(5, classes.size());

        assertClassesExist(classes);
        assertInnerClassesExist(classes);
    }

    @Test
    public void zipFileWithoutInnerClasses() throws IOException {
        FileManager fm = new FileManager();
        fm.acceptInnerClasses(false);

        JavaClassBuilder builder = new JavaClassBuilder(fm);

        Collection classes = builder.buildClasses(zipFile);
        assertEquals(4, classes.size());

        assertClassesExist(classes);
    }

    @Test
    public void countClasses() throws IOException {
        JDepend jdepend = new JDepend();
        jdepend.addDirectory(getTestDataDir());

        jdepend.analyzeInnerClasses(true);
        assertEquals(10, jdepend.countClasses());

        jdepend.analyzeInnerClasses(false);
        assertEquals(8, jdepend.countClasses());
    }

    private void assertClassesExist(Collection classes) {
        assertTrue(classes.contains(new JavaClass("jdepend.framework.ExampleAbstractClass")));
        assertTrue(classes.contains(new JavaClass("jdepend.framework.ExampleInterface")));
        assertTrue(classes.contains(new JavaClass("jdepend.framework.ExampleConcreteClass")));
    }

    private void assertInnerClassesExist(Collection classes) {
        assertTrue(classes.contains(new JavaClass("jdepend.framework.ExampleConcreteClass$ExampleInnerClass")));
    }
}