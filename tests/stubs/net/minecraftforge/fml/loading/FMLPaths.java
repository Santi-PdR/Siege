package net.minecraftforge.fml.loading;
import java.nio.file.Path;
/** Only the filesystem boundary is replaced when testing the real config implementation. */
public enum FMLPaths {
    CONFIGDIR;
    public Path get() { return Path.of(System.getProperty("siege.test.config")); }
}
