package Engine.Input;

import java.awt.KeyboardFocusManager;
import java.util.Locale;

public class KeyboardLayoutDetector {
    
    public enum LayoutType {
        QWERTY,
        AZERTY
    }
    
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
    
    private static LayoutType detectFromLocale() {
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        
        if ("FR".equals(country)) {
            return LayoutType.AZERTY;
        }
        
        return LayoutType.QWERTY;
    }
    
    public static boolean isAzerty() {
        return getCurrentLayout() == LayoutType.AZERTY;
    }
}
