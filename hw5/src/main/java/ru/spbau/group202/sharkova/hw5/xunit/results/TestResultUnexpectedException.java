package ru.spbau.group202.sharkova.hw5.xunit.results;

/**
 * Result of a test that threw an expection without it being specified in 'expected' attribute.
 */
public class TestResultUnexpectedException extends TestResult {
    private Exception exception;

    public TestResultUnexpectedException(String className, String testName, Exception exception,
                                    long time) {
        super(className, testName, "Unexpected " + exception.getClass().getName() +
                        (exception.getMessage() == null ||  exception.getMessage().isEmpty()
                                ? ""
                                : " (" + exception.getMessage() + ")"),
                time,
                false);
        this.exception = exception;
    }

    /**
     * Returns the unexpected exception that was thrown.
     * @return the unexpected exception
     */
    public Exception getException() {
        return exception;
    }
}
