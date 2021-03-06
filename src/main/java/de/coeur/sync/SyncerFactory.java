package de.coeur.sync;

import de.coeur.sync.category.CategorySyncer;
import de.coeur.sync.product.ProductSyncer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static de.coeur.sync.CliRunner.SYNC_MODULE_OPTION_CATEGORY_SYNC;
import static de.coeur.sync.CliRunner.SYNC_MODULE_OPTION_LONG;
import static de.coeur.sync.CliRunner.SYNC_MODULE_OPTION_PRODUCT_SYNC;
import static de.coeur.sync.CliRunner.SYNC_MODULE_OPTION_SHORT;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

public final class SyncerFactory {

    private SyncerFactory() {
    }

    /**
     * Builds an instance of {@link Syncer} corresponding to the passed option value. Acceptable values are either
     * "products" or "categories". Other cases, would cause an {@link IllegalArgumentException} to be thrown.
     *
     * @param syncOptionValue the string value passed to the sync option. Acceptable values are either "products" or
     *                        "categories". Other cases, would cause an {@link IllegalArgumentException} to be thrown.
     * @return The instance of the syncer corresponding to the passed option value.
     * @throws IllegalArgumentException if a wrong option value is passed to the sync option. (Wrong values are anything
     *                                  other than "products" or "categories".
     */
    @Nonnull
    static Syncer getSyncer(@Nullable final String syncOptionValue) {
        if (isBlank(syncOptionValue)) {
            final String errorMessage = format(
                "Blank argument supplied to \"-%s\" or \"--%s\" option! Please choose either \"%s\" or \"%s\".",
                SYNC_MODULE_OPTION_SHORT, SYNC_MODULE_OPTION_LONG, SYNC_MODULE_OPTION_PRODUCT_SYNC,
                SYNC_MODULE_OPTION_CATEGORY_SYNC);
            throw new IllegalArgumentException(errorMessage);
        }

        final String valueTrimmedAndLowerCased = syncOptionValue.trim().toLowerCase();
        switch (valueTrimmedAndLowerCased) {
            case SYNC_MODULE_OPTION_CATEGORY_SYNC:
                return new CategorySyncer();
            case SYNC_MODULE_OPTION_PRODUCT_SYNC:
                return new ProductSyncer();
            default:
                final String errorMessage = format("Unknown argument \"%s\" supplied to \"-%s\" or \"--%s\" option! "
                        + "Please choose either \"%s\" or \"%s\".", syncOptionValue, SYNC_MODULE_OPTION_SHORT,
                    SYNC_MODULE_OPTION_LONG, SYNC_MODULE_OPTION_PRODUCT_SYNC, SYNC_MODULE_OPTION_CATEGORY_SYNC);
                throw new IllegalArgumentException(errorMessage);
        }
    }
}
