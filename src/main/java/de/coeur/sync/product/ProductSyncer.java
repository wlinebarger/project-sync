package de.coeur.sync.product;

import com.commercetools.sync.products.ProductSync;
import com.commercetools.sync.products.ProductSyncOptions;
import com.commercetools.sync.products.ProductSyncOptionsBuilder;
import com.commercetools.sync.products.helpers.ProductSyncStatistics;
import de.coeur.sync.Syncer;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.ProductDraft;
import io.sphere.sdk.products.queries.ProductQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.commercetools.sync.products.utils.ProductReferenceReplacementUtils.buildProductQuery;
import static com.commercetools.sync.products.utils.ProductReferenceReplacementUtils.replaceProductsReferenceIdsWithKeys;
import static de.coeur.sync.utils.SphereClientUtils.CTP_TARGET_CLIENT;

public class ProductSyncer extends Syncer<Product, ProductDraft,
    ProductSyncStatistics, ProductSyncOptions, ProductQuery, ProductSync> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductSyncer.class);

    /**
     * Instantiates a {@link Syncer} instance.
     */
    public ProductSyncer() {
        final ProductSyncOptions productSyncOptions = ProductSyncOptionsBuilder.of(CTP_TARGET_CLIENT)
                                                                               .errorCallback(LOGGER::error)
                                                                               .warningCallback(LOGGER::warn)
                                                                               .build();
        this.sync = new ProductSync(productSyncOptions);
        this.query = buildProductQuery();
        // TODO: Instead of reference expansion, we could cache all keys and replace references manually.
    }

    @Override
    protected CompletableFuture<ProductSyncStatistics> syncPage(@Nonnull final List<Product> page) {
        final List<ProductDraft> draftsWithKeysInReferences = replaceProductsReferenceIdsWithKeys(page);
        return sync.sync(draftsWithKeysInReferences).toCompletableFuture();
    }
}
