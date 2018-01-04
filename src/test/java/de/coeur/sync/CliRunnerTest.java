package de.coeur.sync;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

import static de.coeur.sync.CliRunner.APPLICATION_DEFAULT_NAME;
import static de.coeur.sync.CliRunner.APPLICATION_DEFAULT_VERSION;
import static de.coeur.sync.CliRunner.HELP_OPTION_DESCRIPTION;
import static de.coeur.sync.CliRunner.HELP_OPTION_LONG;
import static de.coeur.sync.CliRunner.HELP_OPTION_SHORT;
import static de.coeur.sync.CliRunner.SYNC_MODULE_OPTION_DESCRIPTION;
import static de.coeur.sync.CliRunner.SYNC_MODULE_OPTION_LONG;
import static de.coeur.sync.CliRunner.SYNC_MODULE_OPTION_SHORT;
import static de.coeur.sync.CliRunner.VERSION_OPTION_DESCRIPTION;
import static de.coeur.sync.CliRunner.VERSION_OPTION_LONG;
import static de.coeur.sync.CliRunner.VERSION_OPTION_SHORT;
import static de.coeur.sync.CliRunner.buildCliOptions;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class CliRunnerTest {
    private static final TestLogger testLogger = TestLoggerFactory.getTestLogger(CliRunner.class);
    private static ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @BeforeClass
    public static void setupClass() throws UnsupportedEncodingException {
        final PrintStream printStream = new PrintStream(outputStream, false, "UTF-8");
        System.setOut(printStream);
    }

    @After
    public void afterTest() {
        testLogger.clearAll();
    }

    @Test
    public void run_WithEmptyArgumentList_ShouldLogErrorAndPrintHelp() throws UnsupportedEncodingException {
        CliRunner.of()
                 .run(new String[]{});
        // Assert error log
        assertSingleLoggingEvent(Level.ERROR, "Please pass at least 1 option to the CLI.", null);
        assertOutputStreamContainsHelpUsageWithSpecifiedCliOptions();
    }

    private void assertOutputStreamContainsHelpUsageWithSpecifiedCliOptions() throws UnsupportedEncodingException {
        assertThat(outputStream.toString("UTF-8"))
            .contains(format("usage: %s", APPLICATION_DEFAULT_NAME))
            .contains(format("-%s,--%s", HELP_OPTION_SHORT, HELP_OPTION_LONG))
            .contains(format("-%s,--%s", SYNC_MODULE_OPTION_SHORT, SYNC_MODULE_OPTION_LONG))
            .contains(format("-%s,--%s", VERSION_OPTION_SHORT, VERSION_OPTION_LONG));
    }

    @Test
    public void run_WithHelpAsLongArgument_ShouldPrintUsageHelpToSystemOut() throws UnsupportedEncodingException {
        CliRunner.of()
                 .run(new String[]{"-help"});

        assertThat(testLogger.getAllLoggingEvents()).isEmpty();
        assertOutputStreamContainsHelpUsageWithSpecifiedCliOptions();
    }

    @Test
    public void run_WithHelpAsShortArgument_ShouldPrintUsageHelpToSystemOut() throws UnsupportedEncodingException {
        CliRunner.of()
                 .run(new String[]{"-h"});

        assertThat(testLogger.getAllLoggingEvents()).isEmpty();
        assertOutputStreamContainsHelpUsageWithSpecifiedCliOptions();
    }

    @Test
    public void run_WithVersionAsShortArgument_ShouldLogApplicationVersionAsInfo() {
        CliRunner.of()
                 .run(new String[]{"-v"});
        assertSingleLoggingEvent(Level.INFO, APPLICATION_DEFAULT_VERSION, null);
    }

    @Test
    public void run_WithVersionAsLongArgument_ShouldLogApplicationVersionAsInfo() {
        CliRunner.of()
                 .run(new String[]{"--version"});
        assertSingleLoggingEvent(Level.INFO, APPLICATION_DEFAULT_VERSION, null);
    }

    private void assertSingleLoggingEvent(@Nonnull final Level logLevel,
                                          @Nonnull final String logMessage,
                                          @Nullable final Throwable logThrowable) {
        assertThat(testLogger.getAllLoggingEvents()).hasSize(1);
        final LoggingEvent loggingEvent = testLogger.getAllLoggingEvents().get(0);
        assertThat(loggingEvent).isExactlyInstanceOf(LoggingEvent.class);
        assertThat(loggingEvent.getLevel()).isEqualTo(logLevel);
        assertThat(loggingEvent.getMessage()).contains(logMessage);
        assertThat(loggingEvent.getThrowable().orNull()).isEqualTo(logThrowable);
    }

    @Test
    public void run_WithSyncAsArgumentWithNoArgs_ShouldLogErrorAndPrintHelpUsageToSystemOut()
        throws UnsupportedEncodingException {
        CliRunner.of()
                 .run(new String[]{"-s"});
        assertSingleLoggingEvent(Level.ERROR, "Parse error:\nMissing argument for option: s", null);
        assertOutputStreamContainsHelpUsageWithSpecifiedCliOptions();
    }

    @Test
    public void run_WithSyncAsArgumentWithIllegalArgs_ShouldLogErrorAndPrintHelpUsageToSystemOut()
        throws UnsupportedEncodingException {
        final String illegalArg = "illegal";
        CliRunner.of()
                 .run(new String[]{"-s", illegalArg});
        assertSingleLoggingEvent(Level.ERROR, format("Parse error:%nUnknown argument \"%s\" supplied to \"-%s\" or"
                + " \"--%s\" option!", illegalArg, SYNC_MODULE_OPTION_SHORT, SYNC_MODULE_OPTION_LONG), null);
        assertOutputStreamContainsHelpUsageWithSpecifiedCliOptions();
    }

    @Test
    public void run_WithSyncAsLongArgument_ShouldProcessSyncOption() {
        final CliRunner cliRunner = mock(CliRunner.class);

        final String mockLog = "sync done.";
        final CompletableFuture<Void> syncFutureResult =
            CompletableFuture.completedFuture("").thenAccept(ignoredResult -> testLogger.info(mockLog));


        when(cliRunner.getOptions()).thenReturn(buildCliOptions());
        when(cliRunner.getCommandLine()).thenCallRealMethod();
        doCallRealMethod().when(cliRunner).run(any());
        when(cliRunner.processSyncOption()).thenReturn(syncFutureResult);

        cliRunner.run(new String[]{"--sync", "arg"});

        assertSingleLoggingEvent(Level.INFO, mockLog, null);
    }

    @Test
    public void run_WithSyncAsShortArgument_ShouldProcessSyncOption() {
        final CliRunner cliRunner = mock(CliRunner.class);

        final String mockLog = "sync done.";
        final CompletableFuture<Void> syncFutureResult =
            CompletableFuture.completedFuture("").thenAccept(ignoredResult -> testLogger.info(mockLog));


        when(cliRunner.getOptions()).thenReturn(buildCliOptions());
        when(cliRunner.getCommandLine()).thenCallRealMethod();
        doCallRealMethod().when(cliRunner).run(any());
        when(cliRunner.processSyncOption()).thenReturn(syncFutureResult);

        cliRunner.run(new String[]{"-s", "arg"});

        assertSingleLoggingEvent(Level.INFO, mockLog, null);
    }

    @Test
    public void run_WithUnknownArgument_ShouldPrintErrorLogAndHelpUsage() throws UnsupportedEncodingException {
        CliRunner.of()
                 .run(new String[]{"-u"});

        // Assert error log
        assertSingleLoggingEvent(Level.ERROR, "Parse error:\nUnrecognized option: -u", null);
        assertOutputStreamContainsHelpUsageWithSpecifiedCliOptions();
    }

    @Test
    public void run_WithHelpAsArgument_ShouldPrintThreeOptionsWithDescriptionsToSystemOut()
        throws UnsupportedEncodingException {
        CliRunner.of()
                 .run(new String[]{"-h"});

        assertThat(testLogger.getAllLoggingEvents()).isEmpty();

        // Remove line breaks from output stream string.
        final String outputStreamWithoutLineBreaks = outputStream.toString("UTF-8")
                                                                 .replace("\n", "");

        // Replace multiple spaces with single space in output stream string.
        final String outputStreamWithSingleSpaces = outputStreamWithoutLineBreaks.trim().replaceAll(" +", " ");

        assertThat(outputStreamWithSingleSpaces).contains(format("-%s,--%s %s", HELP_OPTION_SHORT,
                                               HELP_OPTION_LONG, HELP_OPTION_DESCRIPTION))
                                           .contains(format("-%s,--%s <arg> %s", SYNC_MODULE_OPTION_SHORT,
                                               SYNC_MODULE_OPTION_LONG, SYNC_MODULE_OPTION_DESCRIPTION))
                                           .contains(format("-%s,--%s %s", VERSION_OPTION_SHORT,
                                               VERSION_OPTION_LONG, VERSION_OPTION_DESCRIPTION));
    }
}
