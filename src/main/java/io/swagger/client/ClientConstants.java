package io.swagger.client;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;

public class ClientConstants {

    private static final String DEFAULT_BASE_URL = "http://msl.kenzanlabs.com:9003";

    public static String BASE_URL;

    public static final String TEST_TOKEN = "2883607a-176d-4729-a20b-ec441c285afb";
    public static final String TEST_ARTIST_ID = "3f213d36-0e22-45e1-a688-e14fda3bace3";
    public static final String TEST_SONG_ID = "a71d214c-ee76-45c5-a9f3-9b57fc15ef36";
    public static final String TEST_ALBUM_ID = "afa688c7-894d-4537-98f0-bdbccc184cbd";
    public static final String PAGE_SIZE = "12";
    public static final String TIMESTAMP = "";

    private static ClientConstants instance = null;

    private ClientConstants() {
        String configUrl = "file://" + System.getProperty("user.dir");
        configUrl += "/../msl-catalog-edge-config/edge-config.properties";
        String additionalUrlsProperty = "archaius.configurationSource.additionalUrls";
        System.setProperty(additionalUrlsProperty, configUrl);

        DynamicPropertyFactory propertyFactory = DynamicPropertyFactory.getInstance();
        DynamicStringProperty baseURL = propertyFactory.getStringProperty("base_url", DEFAULT_BASE_URL);
        BASE_URL = baseURL.getValue();
    }

    public static ClientConstants getInstance() {
        if ( instance == null ) {
            instance = new ClientConstants();
        }
        return instance;
    }
}
