package redstonedisorder.mendingtracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigHelper {
    static Path configPath = FabricLoader.getInstance().getConfigDir().resolve("mending-tracker.json");
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static ConfigOptions configOptions = new ConfigOptions();

    public static void getConfigOptions() {
        if (Files.exists(configPath)) try (Reader reader = Files.newBufferedReader(configPath)) {
            configOptions = gson.fromJson(reader, ConfigOptions.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean saveConfig(ConfigOptions options) {
        try (Writer writer = Files.newBufferedWriter(configPath)) {
            gson.toJson(options, writer);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
