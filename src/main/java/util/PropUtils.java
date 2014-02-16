package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jcechace on 15/02/14.
 */
public class PropUtils {
    public static final String RUNTIME = "runtime";
    public static final String CONFIG = "config";
    public static final String ADMIN = "admin";

    private static final Properties props = new Properties();

    static {
        try (InputStream in = PropUtils.class.getResourceAsStream("/label.properties")) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load label.properties");
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    // Convenience methods

    public static String getHeaderNavLabel(String itemKey) {
        return get("navigation.header." + itemKey + ".label");
    }

    public static String getHeaderNavId(String itemKey) {
        return get("navigation.header." + itemKey + ".id");
    }


    public static String getNavSectionLabel(String pageKey, String sectionKey) {
        return get("navigation." + pageKey + "." + sectionKey + ".label");
    }

    public static String getNavSectionId(String pageKey, String sectionKey) {
        return get("navigation." + pageKey + "." + sectionKey + ".id");
    }
}
