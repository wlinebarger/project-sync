package de.coeur.sync.utils;


import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientConfig;

public final class SphereClientUtils {
    public static final SphereClientConfig CTP_SOURCE_CLIENT_CONFIG = SphereClientConfig.of(
        System.getenv("COEUR_SOURCE_PROJECT_KEY"),
        System.getenv("COEUR_SOURCE_CLIENT_ID"),
        System.getenv("COEUR_SOURCE_CLIENT_SECRET"));
    public static final SphereClientConfig CTP_TARGET_CLIENT_CONFIG = SphereClientConfig.of(
        System.getenv("COEUR_TARGET_PROJECT_KEY"),
        System.getenv("COEUR_TARGET_CLIENT_ID"),
        System.getenv("COEUR_TARGET_CLIENT_SECRET"));
    public static final SphereClient CTP_SOURCE_CLIENT =
        ClientConfigurationUtils.createClient(CTP_SOURCE_CLIENT_CONFIG);
    public static final SphereClient CTP_TARGET_CLIENT =
        ClientConfigurationUtils.createClient(CTP_TARGET_CLIENT_CONFIG);

    /**
     * Max limit that can be applied to a query in CTP.
     */
    public static final Long QUERY_MAX_LIMIT = 500L;

    public static void closeCtpClients() {
        CTP_SOURCE_CLIENT.close();
        CTP_TARGET_CLIENT.close();
    }
}
