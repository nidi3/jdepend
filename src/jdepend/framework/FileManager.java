package jdepend.framework;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * The <code>FileManager</code> class is responsible for extracting
 * Java class files (<code>.class</code> files) from a collection of
 * registered directories.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class FileManager {

    private List<File> directories;
    private boolean acceptInnerClasses;


    public FileManager() {
        directories = new ArrayList<File>();
        acceptInnerClasses = true;
    }

    /**
     * Determines whether inner classes should be collected.
     *
     * @param b <code>true</code> to collect inner classes;
     *          <code>false</code> otherwise.
     */
    public void acceptInnerClasses(boolean b) {
        acceptInnerClasses = b;
    }

    public void addDirectory(String name) throws IOException {

        File directory = new File(name);

        if (directory.isDirectory() || acceptJarFile(directory)) {
            directories.add(directory);
        } else {
            throw new IOException("Invalid directory or JAR file: " + name);
        }
    }

    public boolean acceptFile(File file) {
        return acceptClassFile(file) || acceptJarFile(file);
    }

    public boolean acceptClassFile(File file) {
        return file.isFile() && acceptClassFileName(file.getName());
    }

    public boolean acceptClassFileName(String name) {
        if (!acceptInnerClasses) {
            if (name.toLowerCase().indexOf("$") > 0) {
                return false;
            }
        }

        return name.toLowerCase().endsWith(".class");
    }

    public boolean acceptJarFile(File file) {
        return isJar(file) || isZip(file) || isWar(file);
    }

    public Collection<File> extractFiles() {
        Collection<File> files = new TreeSet<File>();

        for (File directory : directories) {
            collectFiles(directory, files);
        }

        return files;
    }

    private void collectFiles(File directory, Collection<File> files) {
        if (directory.isFile()) {
            addFile(directory, files);
        } else {

            String[] directoryFiles = directory.list();

            for (String directoryFile : directoryFiles) {

                File file = new File(directory, directoryFile);
                if (acceptFile(file)) {
                    addFile(file, files);
                } else if (file.isDirectory()) {
                    collectFiles(file, files);
                }
            }
        }
    }

    private void addFile(File f, Collection<File> files) {
        if (!files.contains(f)) {
            files.add(f);
        }
    }

    private boolean isWar(File file) {
        return existsWithExtension(file, ".war");
    }

    private boolean isZip(File file) {
        return existsWithExtension(file, ".zip");
    }

    private boolean isJar(File file) {
        return existsWithExtension(file, ".jar");
    }

    private boolean existsWithExtension(File file, String extension) {
        return file.isFile() && file.getName().toLowerCase().endsWith(extension);
    }

}