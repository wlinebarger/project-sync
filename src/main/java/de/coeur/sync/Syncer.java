package de.coeur.sync;

import com.commercetools.sync.commons.BaseSync;
import com.commercetools.sync.commons.BaseSyncOptions;
import com.commercetools.sync.commons.helpers.BaseSyncStatistics;
import io.sphere.sdk.models.Resource;
import io.sphere.sdk.queries.QueryDsl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static com.commercetools.sync.commons.utils.CtpQueryUtils.queryAll;
import static de.coeur.sync.utils.SphereClientUtils.CTP_SOURCE_CLIENT;
import static de.coeur.sync.utils.SphereClientUtils.CTP_TARGET_CLIENT;
import static de.coeur.sync.utils.SphereClientUtils.closeCtpClients;
import static de.coeur.sync.utils.StatisticsUtils.logStatistics;
import static java.lang.String.format;
import static java.util.concurrent.CompletableFuture.allOf;

/**
 * Base class of the syncer that handles syncing a resource from a source CTP project to a target CTP project.
 * @param <T> The type of the resource (e.g. {@link io.sphere.sdk.products.Product},
 *            {@link io.sphere.sdk.categories.Category}, etc..)
 * @param <S> The type of the resource draft (e.g. {@link io.sphere.sdk.products.ProductDraft},
 *            {@link io.sphere.sdk.categories.CategoryDraft}, etc..)
 * @param <U> The type of the sync statistics resulting from the sync process (e.g.
 *            {@link com.commercetools.sync.products.helpers.ProductSyncStatistics},
 *            {@link com.commercetools.sync.categories.helpers.CategorySyncStatistics}, etc..)
 * @param <V> The type of the sync options used for the sync (e.g.
 *            {@link com.commercetools.sync.products.ProductSyncOptions},
 *            {@link com.commercetools.sync.categories.CategorySyncOptions}, etc..)
 * @param <C> The type of the query used to query for the source resources (e.g.
 *            {@link io.sphere.sdk.products.queries.ProductQuery},
 *            {@link io.sphere.sdk.categories.queries.CategoryQuery}, etc..)
 * @param <B> The type of the sync instance used to execute the sync process (e.g.
 *            {@link com.commercetools.sync.products.ProductSync},
 *            {@link com.commercetools.sync.categories.CategorySync}, etc..)
 */
public abstract class Syncer<
    T extends Resource,
    S,
    U extends BaseSyncStatistics,
    V extends BaseSyncOptions<T, S>,
    C extends QueryDsl<T, C>,
    B extends BaseSync<S, U, V>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Syncer.class);
    protected B sync;
    protected C query;

    /**
     * Fetches the {@code CTP_SOURCE_CLIENT} project resources of type {@code T} with all needed references expanded and
     * treats each page as a batch to the sync process. Then executes the sync process of all pages in parallel. It then
     * returns a completion stage containing no result after the execution of the sync process and logging the result.
     *
     * @return completion stage containing no result after the execution of the sync process and logging the result.
     */
    public CompletionStage<Void> sync() {
        LOGGER.info("Starting sync..");
        return queryAll(CTP_SOURCE_CLIENT, query, this::syncPage)
            .thenCompose(syncStages -> allOf(syncStages.toArray(new CompletableFuture[syncStages.size()])))
            .thenAccept(ignoredResult -> processSyncResult());
    }

    private void processSyncResult() {
        final BaseSyncStatistics statistics = sync.getStatistics();
        logStatistics(statistics, LOGGER);
        LOGGER.info(format("Syncing from CTP project '%s' to project '%s' is done.",
            CTP_SOURCE_CLIENT.getConfig().getProjectKey(),
            CTP_TARGET_CLIENT.getConfig().getProjectKey()));
        closeCtpClients();
    }

    /**
     * Given a {@link List} representing a page of resources of type {@code T}, this method creates a
     * {@link CompletableFuture} of each sync process on the given page as a batch.
     */
    protected abstract CompletableFuture<U> syncPage(@Nonnull final List<T> page);
}
