package de.coeur.sync;

import de.coeur.sync.category.CategorySyncer;
import de.coeur.sync.product.ProductSyncer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

public final class CliRunner {
    private static final String SYNC_MODULE_OPTION_SHORT = "s";
    private static final String HELP_OPTION_SHORT = "h";
    private static final String VERSION_OPTION_SHORT = "v";

    private static final String SYNC_MODULE_OPTION_LONG = "sync";
    private static final String SYNC_MODULE_OPTION_PRODUCT_SYNC = "products";
    private static final String SYNC_MODULE_OPTION_CATEGORY_SYNC = "categories";

    private static final String APPLICATION_DEFAULT_NAME = "COEUR-SYNC";
    private static final String APPLICATION_DEFAULT_VERSION = "1.0-dev";


    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private Options options;
    private CommandLine commandLine;
    private Syncer syncer;


    static void of(@Nonnull final String[] arguments) {
        new CliRunner(arguments);
    }

    private CliRunner(@Nonnull final String[] arguments) {
        final CommandLineParser parser = new DefaultParser();
        options = getCliOptions();
        try {
            commandLine = parser.parse(options, arguments);
            processCliArguments();
        } catch (final ParseException | IllegalArgumentException exception) {
            LOGGER.error(format("Parse error:%n%s", exception.getMessage()), exception);
            printHelpToStdOut();
        }
    }

    private static Options getCliOptions() {
        final Options options = new Options();
        final Option syncOption = Option.builder(SYNC_MODULE_OPTION_SHORT)
                                        .longOpt(SYNC_MODULE_OPTION_LONG)
                                        .desc(format("Choose which sync module to run. \"%s\" runs product sync. \"%s\""
                                                + " runs category sync.", SYNC_MODULE_OPTION_PRODUCT_SYNC,
                                            SYNC_MODULE_OPTION_CATEGORY_SYNC))
                                        .hasArg()
                                        .build();

        final Option helpOption = Option.builder(HELP_OPTION_SHORT)
                                        .longOpt("help")
                                        .desc("Print help information to System.out.")
                                        .build();

        final Option versionOption = Option.builder(VERSION_OPTION_SHORT)
                                           .longOpt("version")
                                           .desc("Print the version of the application.")
                                           .build();
        options.addOption(syncOption);
        options.addOption(helpOption);
        options.addOption(versionOption);
        return options;
    }



    private void processCliArguments() {
        final Option[] options = commandLine.getOptions();
        if (options.length == 0) {
            LOGGER.error("Please pass at least 1 option to the CLI.");
            printHelpToStdOut();
        } else {
            final Option option = options[0];
            final String optionName = option.getOpt();
            switch (optionName) {
                case SYNC_MODULE_OPTION_SHORT :
                    processSyncOption();
                    break;
                case HELP_OPTION_SHORT :
                    printHelpToStdOut();
                    break;
                case VERSION_OPTION_SHORT :
                    logApplicationVersion();
                    break;
                default:
                    LOGGER.error(format("Unrecognized option: -%s", optionName));
                    printHelpToStdOut();
            }
        }
    }

    private void processSyncOption() {
        final String syncOptionValue = commandLine.getOptionValue(SYNC_MODULE_OPTION_SHORT);

        if (isBlank(syncOptionValue)) {
            throwIllegalArgExceptionForSyncOption(syncOptionValue);
        }

        switch (syncOptionValue.trim().toLowerCase()) {
            case SYNC_MODULE_OPTION_CATEGORY_SYNC:
                syncer = new CategorySyncer();
                break;
            case SYNC_MODULE_OPTION_PRODUCT_SYNC:
                syncer = new ProductSyncer();
                break;
            default:
                throwIllegalArgExceptionForSyncOption(syncOptionValue);
        }

        syncer.sync().toCompletableFuture().join();
    }


    private static void throwIllegalArgExceptionForSyncOption(@Nullable final String arg) {
        throw new IllegalArgumentException(
            format("Unknown argument \"%s\" supplied to \"-%s\" or \"--%s\"! Please choose either \"%s\" or \"%s\".",
                arg, SYNC_MODULE_OPTION_SHORT, SYNC_MODULE_OPTION_LONG, SYNC_MODULE_OPTION_PRODUCT_SYNC,
                SYNC_MODULE_OPTION_CATEGORY_SYNC));
    }

    private void printHelpToStdOut() {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getApplicationName(), options);
    }

    private static String getApplicationName() {
        final String implementationTitle = Main.class.getPackage().getImplementationTitle();
        return isBlank(implementationTitle) ? APPLICATION_DEFAULT_NAME : implementationTitle;
    }

    private static void logApplicationVersion() {
        final String implementationVersion = getApplicationVersion();
        LOGGER.info(implementationVersion);
    }

    private static String getApplicationVersion() {
        final String implementationVersion = Main.class.getPackage().getImplementationVersion();
        return isBlank(implementationVersion) ? APPLICATION_DEFAULT_VERSION : implementationVersion;
    }
}
