package Core.Database;

import java.io.IOException;

/**
 * Factory gérant l'instanciation et le cycle de vie du link JsonDataProvider.
 * Cette classe permet d'accéder à une instance partagée (cache) des données 
 * pour optimiser les performances et la consommation mémoire, tout en offrant 
 * la possibilité de forcer un rechargement si nécessaire
 * @author RAHARIMANANA Tianantenaina BOUKIRAT Thafat ZEGHBIB Sonia
 */
public class JsonDataProviderFactory {
    
    /** Instance unique mise en cache (Pattern Singleton). */
    private static JsonDataProvider cachedProvider;
    
    /**
     * Récupère l'instance partagée du fournisseur de données. 
     * Si l'instance n'existe pas, elle est créée et mise en cache.
     * @return L'instance unique du  JsonDataProvider.
     * @throws IOException Si le chargement initial du fichier JSON échoue.
     */
    public static JsonDataProvider create() throws IOException {
        if (cachedProvider == null) {
            cachedProvider = new JsonDataProvider();
        }
        return cachedProvider;
    }
    
    /**
     * Crée une nouvelle instance du fournisseur de données sans utiliser le cache.
     * @return Une nouvelle instance JsonDataProvider.
     * @throws IOException Si la lecture du fichier échoue.
     */
    public static JsonDataProvider createNew() throws IOException {
        return new JsonDataProvider();
    }
    
    /**
     * Vide le cache actuel
     * */
    public static void reset() {
        cachedProvider = null;
    }
}