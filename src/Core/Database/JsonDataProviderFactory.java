package Core.Database;

import java.io.IOException;

public class JsonDataProviderFactory {
    
    private static JsonDataProvider cachedProvider;
    
    public static JsonDataProvider create() throws IOException {
        if (cachedProvider == null) {
            cachedProvider = new JsonDataProvider();
        }
        return cachedProvider;
    }
    
    public static JsonDataProvider createNew() throws IOException {
        return new JsonDataProvider();
    }
    
    public static void reset() {
        cachedProvider = null;
    }
}