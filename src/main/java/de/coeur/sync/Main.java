package de.coeur.sync;


import de.coeur.sync.category.CategorySyncer;
import de.coeur.sync.product.ProductSyncer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class Main {

    private static final String SYNC_MODULE_OPTION_SHORT = "s";
    private static final String SYNC_MODULE_OPTION_LONG = "sync";
    private static final boolean SYNC_MODULE_OPTION_IS_REQUIRED = true;
    private static final String SYNC_MODULE_OPTION_PRODUCT_SYNC = "products";
    private static final String SYNC_MODULE_OPTION_CATEGORY_SYNC = "categories";
    private static final String SYNC_MODULE_OPTION_DESCRIPTION =
        format("Choose which sync module to run. \"%s\" runs product sync. \"%s\" runs category sync.",
            SYNC_MODULE_OPTION_PRODUCT_SYNC, SYNC_MODULE_OPTION_CATEGORY_SYNC);
    private static final String ILLEGAL_ARGUMENT_MESSAGE =
        format("Please choose either \"%s\" or \"%s\".", SYNC_MODULE_OPTION_CATEGORY_SYNC,
            SYNC_MODULE_OPTION_PRODUCT_SYNC);
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * Application entry point.
     *
     * @param args all args
     */
    public static void main(final String[] args) {
        final CommandLineParser parser = new DefaultParser();
        final Options options = getCliOptions();
        try {
            final CommandLine commandLine = parser.parse(options, args);
            processSyncOption(commandLine);
        } catch (final ParseException | IllegalArgumentException exception) {
            LOGGER.error(format("Parse error:%n%s", exception.getMessage()), exception);
        }
    }

    private static Options getCliOptions() {
        final Options options = new Options();
        final Option syncOption = Option.builder(SYNC_MODULE_OPTION_SHORT)
                                        .required(SYNC_MODULE_OPTION_IS_REQUIRED)
                                        .desc(SYNC_MODULE_OPTION_DESCRIPTION)
                                        .longOpt(SYNC_MODULE_OPTION_LONG)
                                        .hasArg()
                                        .build();
        options.addOption(syncOption);
        return options;
    }

    private static void processSyncOption(@Nonnull final CommandLine commandLine) {
        final String syncOptionValue = commandLine.getOptionValue(SYNC_MODULE_OPTION_SHORT);
        if (isBlank(syncOptionValue)) {
            throwIllegalArgExceptionForSyncOption(syncOptionValue);
        } else {
            final String trimmedLowerCasedValue = syncOptionValue.trim().toLowerCase();
            switch (trimmedLowerCasedValue) {
                case SYNC_MODULE_OPTION_CATEGORY_SYNC:
                    CategorySyncer.sync().toCompletableFuture().join();
                    break;
                case SYNC_MODULE_OPTION_PRODUCT_SYNC:
                    ProductSyncer.sync().toCompletableFuture().join();
                    break;
                default:
                    throwIllegalArgExceptionForSyncOption(syncOptionValue);
            }
        }
    }


    private static void throwIllegalArgExceptionForSyncOption(@Nullable final String arg) {
        throw new IllegalArgumentException(
            format("Unknown argument \"%s\" supplied to \"-%s\" or \"--%s\"! %s", arg,
                SYNC_MODULE_OPTION_SHORT, SYNC_MODULE_OPTION_LONG, ILLEGAL_ARGUMENT_MESSAGE));
    }
}
