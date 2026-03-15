package Engine.Input;

import java.awt.KeyboardFocusManager;
import java.util.Locale;

public class KeyboardLayoutDetector {
	//Détecte la disposition du clavier actuelle (QWERTY ou AZERTY)
    
    public enum LayoutType {
        QWERTY,
        AZERTY
    }
    //Renvoie la disposition de clavier courante si echou se baser  -> locale du système.
    public static LayoutType getCurrentLayout() {
        String layout = getKeyboardLayoutIdentifier();
        
        if (layout == null) {
            return detectFromLocale();
        }
        
        if (layout.contains("FR") || layout.contains("fr")) {
            return LayoutType.AZERTY;
        }
        
        return LayoutType.QWERTY;
    }
    //Tenter de récupérer l'identifiant du layout clavier
    private static String getKeyboardLayoutIdentifier() {
        try {
            KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            java.lang.reflect.Method method = kfm.getClass().getMethod("getCurrentKeyboardLayout");
            Object result = method.invoke(kfm);
            
            if (result instanceof Long) {
                long layoutId = (Long) result;
                return Long.toHexString(layoutId).toUpperCase();
            }
            
            if (result instanceof String) {
                return ((String) result).toUpperCase();
            }
        } catch (Exception e) {
        }
        
        return null;
    }
    //Détecte la disposition du clavier
    private static LayoutType detectFromLocale() {
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        
        if ("FR".equals(country)) {
            return LayoutType.AZERTY;
        }
        
        return LayoutType.QWERTY;
    }
    //Renvoie vrai si le clavier est AZERTY.
    public static boolean isAzerty() {
        return getCurrentLayout() == LayoutType.AZERTY;
    }
}
