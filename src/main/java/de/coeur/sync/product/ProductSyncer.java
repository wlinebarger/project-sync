package de.coeur.sync.product;

import com.commercetools.sync.products.ProductSync;
import com.commercetools.sync.products.ProductSyncOptions;
import com.commercetools.sync.products.ProductSyncOptionsBuilder;
import com.commercetools.sync.products.helpers.ProductSyncStatistics;
import de.coeur.sync.Syncer;
import io.sphere.sdk.commands.UpdateAction;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.ProductDraft;
import io.sphere.sdk.products.commands.updateactions.Publish;
import io.sphere.sdk.products.queries.ProductQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;

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
        super(
            new ProductSync(ProductSyncOptionsBuilder.of(CTP_TARGET_CLIENT)
                                                       .errorCallback(LOGGER::error)
                                                       .warningCallback(LOGGER::warn)
                                                       .beforeUpdateCallback(ProductSyncer::appendPublishIfPublished)
                                                       .build()),
            buildProductQuery());
            // TODO: Instead of reference expansion, we could cache all keys and replace references manually.
    }

    @Override
    @Nonnull
    protected List<ProductDraft> getDraftsFromPage(@Nonnull final List<Product> page) {
        return replaceProductsReferenceIdsWithKeys(page);
    }

    /**
     * Used for the beforeUpdateCallback of the sync. When an {@code oldProduct} is updated, this method will add
     * a {@link Publish} update action to the list of update actions, only if the {@code oldProduct} has the published
     * field set to true and has new update actions. Which means that it will publish the staged changes caused by the
     * {@code updateActions} if it was already published.
     *
     * @param updateActions update actions needed to sync {@code newProductDraft} to {@code oldProduct}.
     * @param newProductDraft the product draft with the changes.
     * @param oldProduct the old product to be updated.
     * @return the same list of update actions with a publish update action added, if there are staged changes that
     *         should be published.
     */
    static List<UpdateAction<Product>> appendPublishIfPublished(@Nonnull final List<UpdateAction<Product>>
                                                                                       updateActions,
                                                                        @Nonnull final ProductDraft newProductDraft,
                                                                        @Nonnull final Product oldProduct) {
        if (!updateActions.isEmpty() && oldProduct.getMasterData().isPublished()) {
            updateActions.add(Publish.of());
        }
        return updateActions;
    }
}
