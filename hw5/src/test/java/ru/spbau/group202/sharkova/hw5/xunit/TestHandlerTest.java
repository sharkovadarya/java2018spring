package ru.spbau.group202.sharkova.hw5.xunit;

import org.junit.Test;
import ru.spbau.group202.sharkova.hw5.xunit.exceptions.ClassAfterMethodFailedException;
import ru.spbau.group202.sharkova.hw5.xunit.exceptions.ClassBeforeMethodFailedException;
import ru.spbau.group202.sharkova.hw5.xunit.exceptions.IncorrectTestException;
import ru.spbau.group202.sharkova.hw5.xunit.results.TestResult;
import ru.spbau.group202.sharkova.hw5.xunit.results.TestResultIgnored;
import ru.spbau.group202.sharkova.hw5.xunit.results.TestResultPassed;
import ru.spbau.group202.sharkova.hw5.xunit.util.IgnoreMethodClass;
import ru.spbau.group202.sharkova.hw5.xunit.util.OneTestMethodClass;

import java.util.List;

import static org.junit.Assert.*;

public class TestHandlerTest {

    @Test
    public void testClassWithOneTestMethod()
            throws IncorrectTestException, ClassBeforeMethodFailedException, ClassAfterMethodFailedException {
        TestHandler handler = new TestHandler(OneTestMethodClass.class);
        List<TestResult> results = handler.runTests();
        assertEquals(1, results.size());
        TestResult res = results.get(0);
        assertEquals(OneTestMethodClass.class.getName(), res.getClassName());
        assertEquals("testMethod", res.getTestName());
        assertTrue(res.passed());
    }

    @Test
    public void testIgnore()
            throws IncorrectTestException, ClassBeforeMethodFailedException, ClassAfterMethodFailedException {
        TestHandler handler = new TestHandler(IgnoreMethodClass.class);
        List<TestResult> results = handler.runTests();
        assertEquals(2, results.size());
        TestResult ignore;
        TestResult noIgnore;
        if (results.get(0) instanceof TestResultIgnored && results.get(1) instanceof TestResultPassed) {
            ignore = results.get(0);
            noIgnore = results.get(1);
        } else if (results.get(0) instanceof TestResultPassed && results.get(1) instanceof TestResultIgnored) {
            ignore = results.get(1);
            noIgnore = results.get(0);
        } else {
            fail();
            return;
        }

        assertEquals(IgnoreMethodClass.class.getName(), noIgnore.getClassName());
        assertEquals("noIgnoreTestMethod", noIgnore.getTestName());
        assertTrue(noIgnore.passed());
        assertEquals(IgnoreMethodClass.class.getName(), ignore.getClassName());
        assertEquals("ignoreTestMethod", ignore.getTestName());
        assertTrue(ignore.passed());
    }

}