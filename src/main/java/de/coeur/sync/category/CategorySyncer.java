package de.coeur.sync.category;

import com.commercetools.sync.categories.CategorySync;
import com.commercetools.sync.categories.CategorySyncOptions;
import com.commercetools.sync.categories.CategorySyncOptionsBuilder;
import com.commercetools.sync.commons.helpers.BaseSyncStatistics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.coeur.sync.services.CategoryService;
import de.coeur.sync.services.impl.CategoryServiceImpl;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.categories.CategoryDraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;

import static de.coeur.sync.utils.SphereClientUtils.CTP_SOURCE_CLIENT;
import static de.coeur.sync.utils.SphereClientUtils.CTP_TARGET_CLIENT;
import static de.coeur.sync.utils.SphereClientUtils.closeCtpClients;
import static com.commercetools.sync.commons.utils.SyncUtils.replaceCategoriesReferenceIdsWithKeys;
import static java.lang.String.format;

public class CategorySyncer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategorySyncer.class);

    /**
     * Sync runner..
     *
     * @param args all args
     */
    public static void main(final String[] args) {
        final CategorySync sync = setupSync();
        final List<CategoryDraft> categoryDrafts = getSourceCategoryDraftsWithReferencesAsKeys();

        LOGGER.info("Starting sync..");
        sync.sync(categoryDrafts)
            .thenAccept(statistics -> {
                try {
                    final String statisticsAsJSONString = getStatisticsAsJSONString(statistics);
                    LOGGER.info(statisticsAsJSONString);
                } catch (final JsonProcessingException exception) {
                    LOGGER.error("Invalid statistics JSON string..", exception);
                } finally {
                    LOGGER.info(format("Categories have been synced from CTP project '%s' to project '%s'.",
                        CTP_SOURCE_CLIENT.getConfig().getProjectKey(),
                        CTP_TARGET_CLIENT.getConfig().getProjectKey()));
                    closeCtpClients();
                }
            })
            .toCompletableFuture().join();
    }

    /**
     * Sets up a sync instance for the {@code CTP_TARGET_CLIENT} with logging options.
     *
     * @return the setup {@link com.commercetools.sync.categories.CategorySync} instance.
     */
    private static CategorySync setupSync() {
        final CategorySyncOptions categorySyncOptions = CategorySyncOptionsBuilder.of(CTP_TARGET_CLIENT)
                                                                                  .setErrorCallBack(LOGGER::error)
                                                                                  .setWarningCallBack(LOGGER::warn)
                                                                                  .build();
        return new com.commercetools.sync.categories.CategorySync(categorySyncOptions);
    }

    /**
     * // TODO: Instead of reference expansion, we could cache all keys and replace references manually.
     * Fetches the {@code CTP_SOURCE_CLIENT} project categories with references expanded. Then it replaces all the
     * references with the keys and returns the reference replaced category drafts which are ready to sync.
     *
     * @return category drafts which are exactly the categories but references replaces with keys.
     */
    private static List<CategoryDraft> getSourceCategoryDraftsWithReferencesAsKeys() {
        final CategoryService categoryService = new CategoryServiceImpl(CTP_SOURCE_CLIENT);
        final List<Category> categories = categoryService.fetchAll();
        return replaceCategoriesReferenceIdsWithKeys(categories);
    }

    /**
     * Builds a JSON String that represents the fields of the supplied instance of {@link BaseSyncStatistics}.
     * Note: The order of the fields in the built JSON String depends on the order of the instance variables in this
     * class.
     *
     * @param statistics the instance of {@link BaseSyncStatistics} from which to create a JSON String.
     * @return a JSON String representation of the statistics object.
     */
    private static String getStatisticsAsJSONString(@Nonnull final BaseSyncStatistics statistics)
        throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(statistics);
    }
}
