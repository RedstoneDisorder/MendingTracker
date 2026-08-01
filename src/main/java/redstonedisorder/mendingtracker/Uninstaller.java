package redstonedisorder.mendingtracker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class Uninstaller {
    public static void main(String[] args) {
        int result = JOptionPane.showConfirmDialog(
                null,
                "You just ran this mod outside of Minecraft! This will undo\nchanges to configs, to make the mod easier to uninstall.\nWould you like to proceed?",
                "MendingTracker Cleanup",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            Path path;
            Path backup;

            Path configFolder;
            try {
                configFolder = Path.of(Uninstaller.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().getParent().resolve("config");
            } catch (URISyntaxException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Could not get config path!\n" + e,
                        "MendingTracker Cleanup - Error",
                        JOptionPane.ERROR_MESSAGE
                );
                throw new RuntimeException(e);
            }

            path = configFolder.resolve("minihud.json");
            backup = configFolder.resolve("backup.json");

            if (Files.exists(path)) {
                try {
                    Files.copy(path, backup);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Could not create backup of config!\n" + e,
                            "MendingTracker Cleanup - Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    throw new RuntimeException(e);
                }

                ObjectMapper mapper = new ObjectMapper();
                File file = new File(path.toUri());

                try {
                    ObjectNode root = (ObjectNode) mapper.readTree(file);

                    Iterator<String> sections = root.fieldNames();
                    while (sections.hasNext()) {
                        String section = sections.next();

                        JsonNode node = root.get(section);
                        if (node instanceof ObjectNode obj) {
                            obj.remove("infoMending");
                        }
                    }

                    mapper.writerWithDefaultPrettyPrinter().writeValue(file, root);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Could not modify/save config!\n" + e,
                            "MendingTracker Cleanup - Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    try {
                        Files.copy(path, backup);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Could not restore backup of config!\n" + ex,
                                "MendingTracker Cleanup - Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                    throw new RuntimeException(e);
                }
                try {
                    Files.delete(backup);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Could not delete config backup!\n" + e,
                            "MendingTracker Cleanup - Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                JOptionPane.showMessageDialog(
                        null,
                        "MiniHUD config file successfully modified!",
                        "MendingTracker Cleanup - Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "MiniHUD config file not found, skipping...",
                        "MendingTracker Cleanup",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Exiting uninstallation",
                    "MendingTracker Cleanup - Exiting...",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}