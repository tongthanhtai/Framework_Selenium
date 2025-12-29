package utils.PathUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtil {

    public static String normalizePath(String path) {
        Path normalizedPath = Paths.get(path).normalize();
        return normalizedPath.toString();
    }
}
