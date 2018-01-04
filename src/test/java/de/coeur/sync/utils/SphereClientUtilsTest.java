package de.coeur.sync.utils;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.commands.ProductCreateCommand;
import org.junit.Test;

import java.util.concurrent.RejectedExecutionException;

import static de.coeur.sync.utils.SphereClientUtils.CTP_SOURCE_CLIENT;
import static de.coeur.sync.utils.SphereClientUtils.CTP_TARGET_CLIENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class SphereClientUtilsTest {
    @Test
    public void getCtpSourceClientConfig_WithCredentialsInPropertiesFile_ShouldCreateSourceClient() {
        final SphereClient ctpSourceClient = CTP_SOURCE_CLIENT;
        assertThat(ctpSourceClient).isNotNull();
        assertThat(ctpSourceClient.getConfig().getProjectKey()).isEqualTo("testSourceProjectKey");
    }

    @Test
    public void getCtpTargetClientConfig_WithCredentialsInPropertiesFile_ShouldCreateTargetClient() {
        final SphereClient ctpTargetClient = CTP_TARGET_CLIENT;
        assertThat(ctpTargetClient).isNotNull();
        assertThat(ctpTargetClient.getConfig().getProjectKey()).isEqualTo("testTargetProjectKey");
    }

    @Test
    public void closeCtpClients_ShouldCloseClients() {
        SphereClientUtils.closeCtpClients();

        assertThatThrownBy(() -> CTP_SOURCE_CLIENT.execute(mock(ProductCreateCommand.class)))
            .isInstanceOf(RejectedExecutionException.class);


        assertThatThrownBy(() -> CTP_TARGET_CLIENT.execute(mock(ProductCreateCommand.class)))
            .isInstanceOf(RejectedExecutionException.class);
    }
}
