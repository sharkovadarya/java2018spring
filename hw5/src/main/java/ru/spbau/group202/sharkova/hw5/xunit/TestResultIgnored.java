package ru.spbau.group202.sharkova.hw5.xunit;

public class TestResultIgnored extends TestResult {
    public TestResultIgnored(String className, String testName, String cause, long time) {
        super(className, testName, "Ignored: " + cause, time, true);
    }
}
