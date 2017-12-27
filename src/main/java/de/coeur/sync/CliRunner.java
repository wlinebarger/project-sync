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
    private static final String SYNC_MODULE_OPTION_DESCRIPTION =
        format("Choose which sync module to run. \"%s\" runs product sync. \"%s\" runs category sync.",
            SYNC_MODULE_OPTION_PRODUCT_SYNC, SYNC_MODULE_OPTION_CATEGORY_SYNC);
    private static final String ILLEGAL_ARGUMENT_MESSAGE =
        format("Please choose either \"%s\" or \"%s\".", SYNC_MODULE_OPTION_CATEGORY_SYNC,
            SYNC_MODULE_OPTION_PRODUCT_SYNC);


    private static final String APPLICATION_DEFAULT_NAME = "COEUR-SYNC";
    private static final String APPLICATION_DEFAULT_VERSION = "1.0-dev";


    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private Options options;
    private CommandLine commandLine;
    private Syncer syncer;


    public static CliRunner of(@Nonnull final String[] arguments) {
        return new CliRunner(arguments);
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
                                        //.required(SYNC_MODULE_OPTION_IS_REQUIRED)
                                        .desc(SYNC_MODULE_OPTION_DESCRIPTION)
                                        .hasArg()
                                        .build();

        final Option helpOption = Option.builder("h")
                                        .longOpt("help")
                                        .desc("Print help information to System.out.")
                                        .build();

        final Option versionOption = Option.builder("v")
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
                case "s" :
                    processSyncOption();
                    break;
                case "h" :
                    printHelpToStdOut();
                    break;
                case "v" :
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
            format("Unknown argument \"%s\" supplied to \"-%s\" or \"--%s\"! %s", arg,
                SYNC_MODULE_OPTION_SHORT, SYNC_MODULE_OPTION_LONG, ILLEGAL_ARGUMENT_MESSAGE));
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
