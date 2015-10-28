package jdepend.framework;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class FileManagerTest extends JDependTestCase {

    private FileManager fileManager;

    @Before
    public void setUp() {
        fileManager = new FileManager();
        fileManager.acceptInnerClasses(false);
    }

    @Test
    public void emptyFileManager() {
        assertEquals(0, fileManager.extractFiles().size());
    }

    @Test
    public void buildDirectory() throws IOException {
        fileManager.addDirectory(getBuildDir());
        fileManager.addDirectory(getTestBuildDir());
        assertEquals(78, fileManager.extractFiles().size());
    }

    @Test(expected = IOException.class)
    public void nonExistentDirectory() throws IOException {
        fileManager.addDirectory(getBuildDir() + "junk");
    }

    @Test(expected = IOException.class)
    public void invalidDirectory() throws IOException {
        String file = getTestDir() + getPackageSubDir() + "ExampleTest.java";
        fileManager.addDirectory(file);
    }

    @Test
    public void classFile() throws IOException {
        File f = new File(getBuildDir() + getPackageSubDir() + "JDepend.class");
        assertEquals(true, new FileManager().acceptClassFile(f));
    }

    @Test
    public void nonExistentClassFile() {
        File f = new File(getBuildDir() + "JDepend.class");
        assertEquals(false, new FileManager().acceptClassFile(f));
    }

    @Test
    public void invalidClassFile() {
        File f = new File(getHomeDir() + "build.xml");
        assertEquals(false, new FileManager().acceptClassFile(f));
    }

    @Test
    public void jar() throws IOException {
        File f = File.createTempFile("bogus", ".jar", new File(getTestDataDir()));
        fileManager.addDirectory(f.getPath());
        f.deleteOnExit();
    }

    @Test
    public void zip() throws IOException {
        File f = File.createTempFile("bogus", ".zip", new File(getTestDataDir()));
        fileManager.addDirectory(f.getPath());
        f.deleteOnExit();
    }

    @Test
    public void war() throws IOException {
        File f = File.createTempFile("bogus", ".war", new File(getTestDataDir()));
        fileManager.addDirectory(f.getPath());
        f.deleteOnExit();
    }
}