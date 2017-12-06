package de.coeur.sync.product;

import com.commercetools.sync.products.ProductSync;
import com.commercetools.sync.products.ProductSyncOptions;
import com.commercetools.sync.products.ProductSyncOptionsBuilder;
import com.commercetools.sync.products.helpers.ProductSyncStatistics;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.ProductDraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static com.commercetools.sync.commons.utils.CtpQueryUtils.queryAll;
import static com.commercetools.sync.products.utils.ProductReferenceReplacementUtils.buildProductQuery;
import static com.commercetools.sync.products.utils.ProductReferenceReplacementUtils.replaceProductsReferenceIdsWithKeys;
import static de.coeur.sync.utils.SphereClientUtils.CTP_SOURCE_CLIENT;
import static de.coeur.sync.utils.SphereClientUtils.CTP_TARGET_CLIENT;
import static de.coeur.sync.utils.SphereClientUtils.closeCtpClients;
import static de.coeur.sync.utils.StatisticsUtils.logStatistics;
import static java.lang.String.format;

public class ProductSyncer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductSyncer.class);
    private static ProductSync productSync;

    /**
     * Fetches the {@code CTP_SOURCE_CLIENT} project products with all needed references expanded and treats each
     * product page as a batch to the sync process. Then returns a completion stage containing the sync process
     * statistics as a result.
     *
     * @return completion stage containing the sync process statistics as a result.
     */
    public static CompletionStage<Void> sync() {
        productSync = setupSync();
        LOGGER.info("Starting sync..");
        return queryAll(CTP_SOURCE_CLIENT, buildProductQuery(), ProductSyncer::syncProductPage)
            .thenAccept(voidResult -> {
                final ProductSyncStatistics statistics = productSync.getStatistics();
                logStatistics(statistics, LOGGER);
                LOGGER.info(format("Product Syncing from CTP project '%s' to project '%s' is done.",
                    CTP_SOURCE_CLIENT.getConfig().getProjectKey(),
                    CTP_TARGET_CLIENT.getConfig().getProjectKey()));
                closeCtpClients();
            });
    }

    /**
     * // TODO: Instead of reference expansion, we could cache all keys and replace references manually.
     * Given a {@link List} representing a page of {@link Product}, this method replaces all the
     * references with the keys and returns the reference replaced product drafts which are ready to sync. Then calls
     * (in a blocking fashion) the sync process on these reference replaced product drafts.
     *
     */
    private static void syncProductPage(@Nonnull final List<Product> productPage) {
        final List<ProductDraft> draftsWithKeysInReferences = replaceProductsReferenceIdsWithKeys(productPage);
        productSync.sync(draftsWithKeysInReferences)
                    .toCompletableFuture()
                    .join();
    }

    /**
     * Sets up a sync instance for the {@code CTP_TARGET_CLIENT} with logging options.
     *
     * @return the setup {@link com.commercetools.sync.products.ProductSync} instance.
     */
    private static ProductSync setupSync() {
        final ProductSyncOptions syncOptions = ProductSyncOptionsBuilder.of(CTP_TARGET_CLIENT)
                                                                        .errorCallback(LOGGER::error)
                                                                        .warningCallback(LOGGER::warn)
                                                                        .build();
        return new ProductSync(syncOptions);
    }
}
