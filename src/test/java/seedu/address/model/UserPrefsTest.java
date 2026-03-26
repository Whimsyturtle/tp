package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Reused from Codex suggestions upon providing specifications
 */

public class UserPrefsTest {

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        UserPrefs userPref = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPref.setGuiSettings(null));
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        UserPrefs userPrefs = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPrefs.setAddressBookFilePath(null));
    }

    @Test
    public void setCommandAliases_nullAliases_throwsNullPointerException() {
        UserPrefs userPrefs = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPrefs.setCommandAliases(null));
    }

    @Test
    public void commandAliases_modifyAliasRegistry_success() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setCommandAlias("ls", "list");
        assertEquals(Map.of("ls", "list"), userPrefs.getCommandAliases());

        userPrefs.removeCommandAlias("ls");
        assertEquals(Map.of(), userPrefs.getCommandAliases());
    }

    @Test
    public void resetData_copiesCommandAliases_success() {
        UserPrefs source = new UserPrefs();
        source.setCommandAlias("ls", "list");

        UserPrefs target = new UserPrefs();
        target.resetData(source);
        source.setCommandAlias("rm", "delete");

        assertEquals(Map.of("ls", "list"), target.getCommandAliases());
    }

    @Test
    public void toString_containsCommandAliases() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setCommandAlias("ls", "list");

        assertTrue(userPrefs.toString().contains("ls=list"));
    }

}
