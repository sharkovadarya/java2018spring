package ru.spbau.group202.sharkova.hw5;

import ru.spbau.group202.sharkova.hw5.xunit.TestHandler;
import ru.spbau.group202.sharkova.hw5.xunit.TestResult;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Incorrect usage.");
            System.out.println("Input .class file name.");
            return;
        }

        try {
            Class<?> testClass = loadClass(args[0]);
            TestHandler handler = new TestHandler(testClass);
            List<TestResult> results = handler.runTests();
            if (results.isEmpty()) {
                System.out.println("No test results.");
                return;
            }

            System.out.println("Tests for class " + testClass.getName());
            long totalTime = 0;
            int passed = 0;
            for (int i = 0; i < results.size(); i++) {
                TestResult res = results.get(i);
                System.out.println("test " + i + " " + res.getClassName() + "." + res.getTestName());
                System.out.println("test " + (res.passed() ? "passed" : "failed"));
                System.out.println(res.getDescription());
                System.out.println("execution time: " + res.getTime());
                totalTime += res.getTime();
                if (res.passed()) {
                    passed++;
                }
            }

            System.out.println(passed + " tests out of " + results.size() + " passed in " + totalTime);
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found.");
        } catch (IOException e) {
            System.out.println("Unable to process class.");
        }
    }

    private static Class<?> loadClass(String pathToClass)
            throws IOException, ClassNotFoundException {
        Path path = Paths.get(pathToClass);
        URLClassLoader classLoader = new URLClassLoader(new URL[]{path.toUri().toURL()});
        String className = path.toString().replace(File.separatorChar, '.');
        className = className.substring(0, className.lastIndexOf('.'));
        return classLoader.loadClass(className);
    }
}
