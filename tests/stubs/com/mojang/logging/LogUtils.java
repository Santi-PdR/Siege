package com.mojang.logging;
/** Logging adapter for the isolated settings regression test. */
public final class LogUtils {
    private static final TestLogger LOGGER = new TestLogger();
    public static TestLogger getLogger() { return LOGGER; }
    public static final class TestLogger {
        public void warn(String message, Throwable error) { System.err.println(message); }
        public void debug(String message, Throwable error) { System.err.println(message); }
    }
}
