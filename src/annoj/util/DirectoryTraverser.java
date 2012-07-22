package annoj.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Utility class used for finding classes in a given package.
 * 
 * 
 * @author Akos Tajti
 */
public class DirectoryTraverser {

    /**
     * FInds the classes located in package <code>packageName</code> and returns
     * a list of strings of fully qualified names of the classes found. The method recursively
     * searches the subpackages of <code>packageName</code>. If <code>packageName</code>
     * is not a valid package name, then an empty list is returned.
     *
     * @param packageName 
     * @return The classes found
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static List<String> getClassNames(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<String> classes = new ArrayList<String>();
        for (File directory : dirs) {
            classes.addAll(findClassesInSubdirs(directory, packageName));
        }
        return classes;
    }

    private static List<String> findClassesInSubdirs(File directory, String packageName) throws ClassNotFoundException {
        List<String> classes = new ArrayList<String>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClassesInSubdirs(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
            }
        }
        return classes;
    }
}
