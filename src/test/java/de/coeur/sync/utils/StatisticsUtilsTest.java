package de.coeur.sync.utils;

import com.commercetools.sync.categories.helpers.CategorySyncStatistics;
import com.commercetools.sync.commons.helpers.BaseSyncStatistics;
import com.commercetools.sync.products.helpers.ProductSyncStatistics;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StatisticsUtilsTest {
    private BaseSyncStatistics syncStatistics;

    // TODO: NEED TO TEST #logStatistics


    @Test
    public void getStatisticsAsJSONString_WithProductSyncStatistics_ShouldBuildJsonString()
        throws JsonProcessingException {
        syncStatistics = new ProductSyncStatistics();
        syncStatistics.incrementCreated(10);
        syncStatistics.incrementFailed(10);
        syncStatistics.incrementUpdated(10);
        syncStatistics.incrementProcessed(30);

        final String statisticsAsJSONString = StatisticsUtils.getStatisticsAsJSONString(syncStatistics);
        assertThat(statisticsAsJSONString).contains("\"created\":10");
        assertThat(statisticsAsJSONString).contains("\"failed\":10");
        assertThat(statisticsAsJSONString).contains("\"updated\":10");
        assertThat(statisticsAsJSONString).contains("\"processed\":30");
    }

    @Test
    public void getStatisticsAsJSONString_WithCategorySyncStatistics_ShouldBuildJsonString()
        throws JsonProcessingException {
        syncStatistics = new CategorySyncStatistics();
        syncStatistics.incrementCreated(10);
        syncStatistics.incrementProcessed(30);

        final String statisticsAsJSONString = StatisticsUtils.getStatisticsAsJSONString(syncStatistics);
        assertThat(statisticsAsJSONString).contains("\"created\":10");
        assertThat(statisticsAsJSONString).contains("\"processed\":30");
        assertThat(statisticsAsJSONString).contains("\"categoryKeysWithMissingParents\":{}");
    }
}
