package ru.spbau.group202.sharkova.hw5;

import ru.spbau.group202.sharkova.hw5.xunit.TestHandler;
import ru.spbau.group202.sharkova.hw5.xunit.exceptions.*;
import ru.spbau.group202.sharkova.hw5.xunit.results.TestResult;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Console application to handle command line arguments and output results
 * such as info on every test (class and method name, execution time, status).
 */
public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Incorrect usage.");
            System.out.println("Input path to root directory of test classes and class name (with package).");
            System.out.println("Example: /home/username/hw5/target/test-classes/" +
                    " ru.spbau.group202.sharkova.hw5.xunit.util.OneTestMethodClass");
            return;
        }

        try {
            Class<?> testClass = loadClass(args[0], args[1]);
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
                System.out.println("test status: " + (res.passed() ? "passed" : "failed"));
                System.out.println(res.getDescription());
                System.out.println("execution time: " + res.getTime() + " milliseconds");
                totalTime += res.getTime();
                if (res.passed()) {
                    passed++;
                }
            }

            System.out.println(passed + " tests out of " + results.size() + " passed in " + totalTime + " milliseconds.");
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found.");
        } catch (IOException e) {
            System.out.println("Unable to process class.");
        } catch (ClassBeforeMethodFailedException e) {
            System.out.println("Method executed before class failed: " + e.getMessage());
        } catch (ClassAfterMethodFailedException e) {
            System.out.println("Method executed after class failed: " + e.getMessage());
        } catch (IncorrectTestException e) {
            System.out.println("Incorrect test: " + e.getMessage());
        } catch (ExtraAnnotatedMethodsException|ExtraAnnotationsException e) {
            System.out.println(e.getMessage());
        }
    }

    private static Class<?> loadClass(String pathToClass, String className)
            throws IOException, ClassNotFoundException {
        Path path = Paths.get(pathToClass);
        URLClassLoader classLoader = new URLClassLoader(new URL[]{path.toUri().toURL()});
        return classLoader.loadClass(className);
    }
}
