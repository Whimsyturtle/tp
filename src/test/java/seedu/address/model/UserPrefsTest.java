package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

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

}
