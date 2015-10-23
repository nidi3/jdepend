package jdepend.framework;

import org.junit.After;
import org.junit.Before;

import java.io.File;

import static org.junit.Assert.fail;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class JDependTestCase {

    private String homeDir;
    private String testDir;
    private String testDataDir;
    private String buildDir;
    private String testBuildDir;
    private String packageSubDir;
    private String originalUserHome;

    @Before
    public void start() {
        System.setProperty("jdepend.home", ".");

        homeDir = System.getProperty("jdepend.home");
        if (homeDir == null) {
            fail("Property 'jdepend.home' not defined");
        }
        homeDir = homeDir + File.separator;
        testDir = homeDir + File.separator + "test" + File.separator;
        testDataDir = testDir + "data" + File.separator;
        buildDir = homeDir + "target/classes" + File.separator;
        testBuildDir = homeDir + "target/test-classes" + File.separator;
        packageSubDir = "jdepend" + File.separator + "framework" + File.separator;
        originalUserHome = System.getProperty("user.home");
    }

    @After
    public void end() {
        System.setProperty("user.home", originalUserHome);
    }

    public String getHomeDir() {
        return homeDir;
    }

    public String getTestDataDir() {
        return testDataDir;
    }

    public String getTestDir() {
        return testDir;
    }

    public String getBuildDir() {
        return buildDir;
    }

    public String getTestBuildDir() {
        return testBuildDir;
    }

    public String getPackageSubDir() {
        return packageSubDir;
    }
}