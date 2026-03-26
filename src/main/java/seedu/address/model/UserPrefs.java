package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import seedu.address.commons.core.GuiSettings;

/**
 * Represents User's preferences.
 */
public class UserPrefs implements ReadOnlyUserPrefs {

    private GuiSettings guiSettings = new GuiSettings();
    private Path addressBookFilePath = Paths.get("data" , "rosterbolt.json");
    private Map<String, String> commandAliases = new LinkedHashMap<>();

    /**
     * Creates a {@code UserPrefs} with default values.
     */
    public UserPrefs() {}

    /**
     * Creates a {@code UserPrefs} with the prefs in {@code userPrefs}.
     */
    public UserPrefs(ReadOnlyUserPrefs userPrefs) {
        this();
        resetData(userPrefs);
    }

    /**
     * Resets the existing data of this {@code UserPrefs} with {@code newUserPrefs}.
     */
    public void resetData(ReadOnlyUserPrefs newUserPrefs) {
        requireNonNull(newUserPrefs);
        setGuiSettings(newUserPrefs.getGuiSettings());
        setAddressBookFilePath(newUserPrefs.getAddressBookFilePath());
        setCommandAliases(newUserPrefs.getCommandAliases());
    }

    public GuiSettings getGuiSettings() {
        return guiSettings;
    }

    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        this.guiSettings = guiSettings;
    }

    public Path getAddressBookFilePath() {
        return addressBookFilePath;
    }

    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        this.addressBookFilePath = addressBookFilePath;
    }

    /**
     * Returns an unmodifiable view of the command aliases.
     */
    public Map<String, String> getCommandAliases() {
        return Collections.unmodifiableMap(commandAliases);
    }

    /**
     * Replaces the contents of the command alias registry with {@code commandAliases}.
     */
    public void setCommandAliases(Map<String, String> commandAliases) {
        requireNonNull(commandAliases);
        this.commandAliases = new LinkedHashMap<>(commandAliases);
    }

    /**
      * Adds a command alias to the registry.
      */
    public void setCommandAlias(String shortName, String template) {
        requireNonNull(shortName);
        requireNonNull(template);
        commandAliases.put(shortName, template);
    }

    /**
      * Removes a command alias from the registry.
      */
    public void removeCommandAlias(String shortName) {
        requireNonNull(shortName);
        commandAliases.remove(shortName);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UserPrefs)) {
            return false;
        }

        UserPrefs otherUserPrefs = (UserPrefs) other;
        return guiSettings.equals(otherUserPrefs.guiSettings)
                && addressBookFilePath.equals(otherUserPrefs.addressBookFilePath)
                && commandAliases.equals(otherUserPrefs.commandAliases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guiSettings, addressBookFilePath, commandAliases);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Gui Settings : " + guiSettings);
        sb.append("\nLocal data file location : " + addressBookFilePath);
        sb.append("\nCommand aliases : " + commandAliases);
        return sb.toString();
    }

}
