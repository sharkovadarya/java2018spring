package ru.spbau.group202.sharkova.hw5.xunit;

import org.junit.Test;
import ru.spbau.group202.sharkova.hw5.xunit.exceptions.ClassAfterMethodFailedException;
import ru.spbau.group202.sharkova.hw5.xunit.exceptions.ClassBeforeMethodFailedException;
import ru.spbau.group202.sharkova.hw5.xunit.exceptions.IncorrectTestException;
import ru.spbau.group202.sharkova.hw5.xunit.results.*;
import ru.spbau.group202.sharkova.hw5.xunit.util.*;

import java.util.List;

import static org.junit.Assert.*;

/**
 * This class checks correctness of TestHandler class methods
 * using utility classes in the 'util' package.
 */
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

    @Test
    public void testUnexpectedException()
            throws IncorrectTestException, ClassBeforeMethodFailedException, ClassAfterMethodFailedException {
        TestHandler handler = new TestHandler(UnexpectedExceptionClass.class);
        List<TestResult> results = handler.runTests();
        assertEquals(1, results.size());
        TestResult res = results.get(0);
        assertTrue(res instanceof TestResultUnexpectedException);
        TestResultUnexpectedException unexpectedExceptionRes = (TestResultUnexpectedException) res;
        assertEquals(unexpectedExceptionRes.getException().getClass(), NullPointerException.class);
        assertEquals(UnexpectedExceptionClass.class.getName(), res.getClassName());
        assertEquals("unexpectedExceptionTestMethod", res.getTestName());
        assertFalse(res.passed());
    }

    @Test
    public void testExpectedException()
            throws IncorrectTestException, ClassBeforeMethodFailedException, ClassAfterMethodFailedException {
        TestHandler handler = new TestHandler(ExpectedExceptionClass.class);
        List<TestResult> results = handler.runTests();
        assertEquals(1, results.size());
        TestResult res = results.get(0);
        assertTrue(res instanceof TestResultPassed);
        assertEquals(ExpectedExceptionClass.class.getName(), res.getClassName());
        assertEquals("expectedExceptionTestMethod", res.getTestName());
        assertTrue(res.passed());
    }

    @Test
    public void testExceptionNotThrown()
            throws IncorrectTestException, ClassBeforeMethodFailedException, ClassAfterMethodFailedException {
        TestHandler handler = new TestHandler(ExceptionNotThrownClass.class);
        List<TestResult> results = handler.runTests();
        assertEquals(1, results.size());
        TestResult res = results.get(0);
        assertTrue(res instanceof TestResultExceptionNotThrown);
        assertEquals(ExceptionNotThrownClass.class.getName(), res.getClassName());
        assertEquals("exceptionNotThrownTestMethod", res.getTestName());
        assertFalse(res.passed());
    }

    @Test(expected = IncorrectTestException.class)
    public void testConstructorWithArguments()
            throws IncorrectTestException, ClassBeforeMethodFailedException, ClassAfterMethodFailedException{
        TestHandler handler = new TestHandler(ConstructorWithArgumentsClass.class);
        handler.runTests();
    }

    @Test
    public void testBeforeAndAfterMethods()
            throws IncorrectTestException, ClassBeforeMethodFailedException, ClassAfterMethodFailedException {
        TestHandler handler = new TestHandler(BeforeAfterMethodsClass.class);
        assertEquals(0, BeforeAfterMethodsClass.getA());
        List<TestResult> results = handler.runTests();
        assertEquals(1, results.size());
        TestResult res = results.get(0);
        assertTrue(res instanceof TestResultPassed);
        assertEquals(BeforeAfterMethodsClass.class.getName(), res.getClassName());
        assertEquals("testMethod", res.getTestName());
        assertEquals(19, BeforeAfterMethodsClass.getA());
    }

    @Test
    public void testClassBeforeAndAfterMethods()
            throws IncorrectTestException, ClassBeforeMethodFailedException, ClassAfterMethodFailedException {
        TestHandler handler = new TestHandler(ClassBeforeAfterMethodsClass.class);
        assertEquals(0, ClassBeforeAfterMethodsClass.getList().size());
        List<TestResult> results = handler.runTests();
        assertEquals(2, results.size());
        TestResult res1;
        TestResult res2;
        if (results.get(0).getTestName().equals("testMethod1") && results.get(1).getTestName().equals("testMethod2")) {
            res1 = results.get(0);
            res2 = results.get(1);
        } else if (results.get(1).getTestName().equals("testMethod1") && results.get(0).getTestName().equals("testMethod2")) {
            res1 = results.get(1);
            res2 = results.get(0);
        } else {
            fail();
            return;
        }
        assertEquals(ClassBeforeAfterMethodsClass.class.getName(), res1.getClassName());
        assertEquals(ClassBeforeAfterMethodsClass.class.getName(), res2.getClassName());
        assertTrue(res1.passed());
        assertTrue(res2.passed());

        assertEquals(4, ClassBeforeAfterMethodsClass.getList().size());
        assertEquals(new Integer(30), ClassBeforeAfterMethodsClass.getList().get(0));
        assertEquals(new Integer(15), ClassBeforeAfterMethodsClass.getList().get(1));
        assertEquals(new Integer(17), ClassBeforeAfterMethodsClass.getList().get(2));
        assertEquals(new Integer(11), ClassBeforeAfterMethodsClass.getList().get(3));
    }

}