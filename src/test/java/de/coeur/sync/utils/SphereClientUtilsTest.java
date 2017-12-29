package de.coeur.sync.utils;

import io.sphere.sdk.client.SphereClient;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SphereClientUtilsTest {
    @Test
    public void getCtpSourceClientConfig_WithCredentialsInPropertiesFile_ShoulCreateSourceClient() {
        final SphereClient ctpSourceClient = SphereClientUtils.CTP_SOURCE_CLIENT;
        assertThat(ctpSourceClient).isNotNull();
        assertThat(ctpSourceClient.getConfig().getProjectKey()).isEqualTo("testSourceProjectKey");
    }
}
