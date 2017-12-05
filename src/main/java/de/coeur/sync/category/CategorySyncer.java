package de.coeur.sync.category;

import com.commercetools.sync.categories.CategorySync;
import com.commercetools.sync.categories.CategorySyncOptions;
import com.commercetools.sync.categories.CategorySyncOptionsBuilder;
import com.commercetools.sync.categories.helpers.CategorySyncStatistics;
import com.commercetools.sync.commons.helpers.BaseSyncStatistics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.categories.CategoryDraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static com.commercetools.sync.categories.utils.CategoryReferenceReplacementUtils.buildCategoryQuery;
import static com.commercetools.sync.categories.utils.CategoryReferenceReplacementUtils.replaceCategoriesReferenceIdsWithKeys;
import static com.commercetools.sync.commons.utils.CtpQueryUtils.queryAll;
import static de.coeur.sync.utils.SphereClientUtils.CTP_SOURCE_CLIENT;
import static de.coeur.sync.utils.SphereClientUtils.CTP_TARGET_CLIENT;
import static de.coeur.sync.utils.SphereClientUtils.closeCtpClients;
import static java.lang.String.format;

public class CategorySyncer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategorySyncer.class);
    private static CategorySync categorySync;

    /**
     * Fetches the {@code CTP_SOURCE_CLIENT} project categories with all needed references expanded and treats category
     * page as a batch to the sync process. Then returns a completion stage containing the sync process statistics as a
     * result.
     *
     * @return completion stage containing the sync process statistics as a result.
     */
    public static CompletionStage<Void> syncCategories() {
        categorySync = setupSync();
        LOGGER.info("Starting sync..");
        return queryAll(CTP_SOURCE_CLIENT, buildCategoryQuery(), CategorySyncer::syncCategoryPage)
            .thenAccept(voidResult -> {
                final CategorySyncStatistics statistics = categorySync.getStatistics();
                logStatistics(statistics);
                LOGGER.info(format("Category Syncing from CTP project '%s' to project '%s' is done.",
                    CTP_SOURCE_CLIENT.getConfig().getProjectKey(),
                    CTP_TARGET_CLIENT.getConfig().getProjectKey()));
                closeCtpClients();
            });
    }

    /**
     * // TODO: Instead of reference expansion, we could cache all keys and replace references manually.
     * Given a {@link List} representing a page of {@link Category}, this method replaces all the
     * references with the keys and returns the reference replaced category drafts which are ready to sync. Then calls
     * (in a blocking fashion) the sync process on these reference replaced category drafts.
     *
     */
    private static void syncCategoryPage(@Nonnull final List<Category> categoryPage) {
        final List<CategoryDraft> draftsWithKeysInReferences = replaceCategoriesReferenceIdsWithKeys(categoryPage);
        categorySync.sync(draftsWithKeysInReferences)
                    .toCompletableFuture()
                    .join();
    }

    /**
     * Sets up a sync instance for the {@code CTP_TARGET_CLIENT} with logging options.
     *
     * @return the setup {@link com.commercetools.sync.categories.CategorySync} instance.
     */
    private static CategorySync setupSync() {
        final CategorySyncOptions categorySyncOptions = CategorySyncOptionsBuilder.of(CTP_TARGET_CLIENT)
                                                                                  .errorCallback(LOGGER::error)
                                                                                  .warningCallback(LOGGER::warn)
                                                                                  .build();
        return new CategorySync(categorySyncOptions);
    }

    private static void logStatistics(@Nonnull final CategorySyncStatistics statistics) {
        try {
            final String statisticsAsJSONString = getStatisticsAsJSONString(statistics);
            LOGGER.info(statisticsAsJSONString);
        } catch (final JsonProcessingException exception) {
            LOGGER.error("Invalid statistics JSON string..", exception);
        }
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
