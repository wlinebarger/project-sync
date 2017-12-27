package de.coeur.sync.category;

import com.commercetools.sync.categories.CategorySync;
import com.commercetools.sync.categories.CategorySyncOptions;
import com.commercetools.sync.categories.CategorySyncOptionsBuilder;
import com.commercetools.sync.categories.helpers.CategorySyncStatistics;
import de.coeur.sync.Syncer;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.categories.CategoryDraft;
import io.sphere.sdk.categories.queries.CategoryQuery;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.commercetools.sync.categories.utils.CategoryReferenceReplacementUtils.buildCategoryQuery;
import static com.commercetools.sync.categories.utils.CategoryReferenceReplacementUtils.replaceCategoriesReferenceIdsWithKeys;
import static de.coeur.sync.utils.SphereClientUtils.CTP_TARGET_CLIENT;

public class CategorySyncer extends Syncer<Category, CategoryDraft,
    CategorySyncStatistics, CategorySyncOptions, CategoryQuery, CategorySync> {

    /**
     * Instantiates a {@link Syncer} instance.
     */
    public CategorySyncer() {
        final CategorySyncOptions categorySyncOptions = CategorySyncOptionsBuilder.of(CTP_TARGET_CLIENT)
                                                                                  .errorCallback(LOGGER::error)
                                                                                  .warningCallback(LOGGER::warn)
                                                                                  .build();
        this.sync = new CategorySync(categorySyncOptions);
        this.query = buildCategoryQuery();
        // TODO: Instead of reference expansion, we could cache all keys and replace references manually.
    }

    @Override
    public CompletableFuture<CategorySyncStatistics> syncPage(@Nonnull final List<Category> page) {
        final List<CategoryDraft> draftsWithKeysInReferences = replaceCategoriesReferenceIdsWithKeys(page);
        return sync.sync(draftsWithKeysInReferences)
            .toCompletableFuture();
    }
}
