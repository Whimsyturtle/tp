package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import seedu.address.MainApp;

/**
 * Reuse testing code from Codex.
 */
public class StaticCopyResourceTest {

    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";
    private static final String COMMAND_BOX_FXML = "/view/CommandBox.fxml";

    @Test
    public void mainWindowFxml_usesRosterBoltTitle() throws IOException {
        String mainWindowContent = readResourceContent(MAIN_WINDOW_FXML);

        assertTrue(mainWindowContent.contains("title=\"RosterBolt\""));
    }

    @Test
    public void commandBoxFxml_usesUpdatedPromptText() throws IOException {
        String commandBoxContent = readResourceContent(COMMAND_BOX_FXML);

        assertTrue(commandBoxContent.contains("promptText=\"Enter a command (e.g. list, find, add)\""));
    }

    @Test
    public void helpWindow_usesRosterBoltUserGuideCopy() {
        assertEquals("https://ay2526s2-cs2103t-t12-1.github.io/tp/UserGuide.html", HelpWindow.USERGUIDE_URL);
        assertEquals("Refer to the RosterBolt user guide: " + HelpWindow.USERGUIDE_URL, HelpWindow.HELP_MESSAGE);
    }

    private static String readResourceContent(String resourcePath) throws IOException {
        URL resourceUrl = MainApp.class.getResource(resourcePath);
        assertNotNull(resourceUrl, resourcePath + " should exist");
        return new String(resourceUrl.openStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
