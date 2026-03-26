package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AliasCommand;
import seedu.address.logic.commands.AliasesCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.UnaliasCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.predicates.PersonContainsSubstringsPredicate;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Some test cases in this class were adapted from Codex-generated test specifications.
 */
public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        resetLogic();
    }

    private void resetLogic() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("rosterbolt.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_aliasExpandedCommand_success() throws Exception {
        model.setCommandAlias("ls", ListCommand.COMMAND_WORD);
        assertCommandSuccess("ls", ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_aliasExpandedCommandWithTrailingWhitespace_success() throws Exception {
        model.setCommandAlias("ls", ListCommand.COMMAND_WORD);
        assertCommandSuccess("ls   ", ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_aliasExpandedCommandWithArguments_success() throws Exception {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.setCommandAlias("rm", DeleteCommand.COMMAND_WORD);
        resetLogic();

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setCommandAlias("rm", DeleteCommand.COMMAND_WORD);
        expectedModel.deletePerson(ALICE);

        assertCommandSuccess("rm 1", DeleteCommand.buildSuccessMessage(List.of(ALICE)), expectedModel);
    }

    @Test
    public void execute_aliasExpandedCommandWithArgumentsAndTrailingWhitespace_success() throws Exception {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.setCommandAlias("rm", DeleteCommand.COMMAND_WORD);
        resetLogic();

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setCommandAlias("rm", DeleteCommand.COMMAND_WORD);
        expectedModel.deletePerson(ALICE);

        assertCommandSuccess("rm 1   ", DeleteCommand.buildSuccessMessage(List.of(ALICE)), expectedModel);
    }

    @Test
    public void execute_aliasExpandedCommandTemplateWithDefaultArguments_success() throws Exception {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.setCommandAlias("ss", "find m/ss meie");
        resetLogic();

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setCommandAlias("ss", "find m/ss meie");
        expectedModel.updateFilteredPersonList(
                new PersonContainsSubstringsPredicate(Collections.singletonList("meie")));

        assertCommandSuccess("ss", String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2), expectedModel);
    }

    @Test
    public void execute_aliasExpandedMetaCommand_success() throws Exception {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.setCommandAlias("wipe", ClearCommand.COMMAND_WORD);
        resetLogic();

        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs());
        expectedModel.setCommandAlias("wipe", ClearCommand.COMMAND_WORD);

        assertCommandSuccess("wipe", ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_validCommand_savesUserPrefs() throws Exception {
        model.setCommandAlias("ls", ListCommand.COMMAND_WORD);

        assertCommandSuccess(ListCommand.COMMAND_WORD, ListCommand.MESSAGE_SUCCESS, model);

        UserPrefs readBack = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")).readUserPrefs().get();
        assertEquals(Map.of("ls", ListCommand.COMMAND_WORD), readBack.getCommandAliases());
    }

    @Test
    public void execute_aliasCommandAndUseAlias_success() throws Exception {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setCommandAlias("ls", ListCommand.COMMAND_WORD);

        assertCommandSuccess("alias ls list",
                String.format(AliasCommand.MESSAGE_SUCCESS, "ls", ListCommand.COMMAND_WORD),
                expectedModel);
        assertCommandSuccess("ls", ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_aliasesCommand_success() throws Exception {
        model.setCommandAlias("rm", DeleteCommand.COMMAND_WORD);
        model.setCommandAlias("ls", ListCommand.COMMAND_WORD);

        String expectedMessage = AliasesCommand.MESSAGE_ALIASES_HEADER
                + "\nls -> " + ListCommand.COMMAND_WORD
                + "\nrm -> " + DeleteCommand.COMMAND_WORD;
        assertCommandSuccess(AliasesCommand.COMMAND_WORD, expectedMessage, model);
    }

    @Test
    public void execute_unaliasCommand_success() throws Exception {
        model.setCommandAlias("ls", ListCommand.COMMAND_WORD);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandSuccess("unalias ls", String.format(UnaliasCommand.MESSAGE_SUCCESS, "ls"), expectedModel);
    }

    @Test
    public void execute_duplicateAliasCommand_throwsCommandException() {
        model.setCommandAlias("ls", ListCommand.COMMAND_WORD);
        assertCommandException("alias ls list", AliasCommand.MESSAGE_DUPLICATE_ALIAS);
    }

    @Test
    public void execute_reservedAliasName_throwsParseException() {
        assertParseException("alias list help", AliasCommand.MESSAGE_RESERVED_ALIAS_NAME);
    }

    @Test
    public void execute_invalidAliasTemplate_throwsParseException() {
        assertParseException("alias l ls", AliasCommand.MESSAGE_INVALID_ALIAS_TEMPLATE);
    }

    @Test
    public void execute_editPreviousWithoutPreviousCommand_throwsCommandException() {
        assertCommandException(LogicManager.EDIT_PREVIOUS_COMMAND_WORD,
                LogicManager.EDIT_PREVIOUS_MESSAGE_NO_PREVIOUS_COMMAND);
    }

    @Test
    public void execute_editPreviousListCommand_success() throws Exception {
        assertCommandSuccess(ListCommand.COMMAND_WORD, ListCommand.MESSAGE_SUCCESS, model);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        CommandResult result = logic.execute(LogicManager.EDIT_PREVIOUS_COMMAND_WORD);

        assertEquals(String.format(LogicManager.EDIT_PREVIOUS_MESSAGE_SUCCESS, ListCommand.COMMAND_WORD),
                result.getFeedbackToUser());
        assertEquals(Optional.of(ListCommand.COMMAND_WORD), result.getCommandTextToPopulate());
        assertEquals(expectedModel, model);
    }

    @Test
    public void execute_editPreviousDeleteCommand_success() throws Exception {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        resetLogic();

        String deleteCommand = DeleteCommand.COMMAND_WORD + " 1";
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.deletePerson(ALICE);
        assertCommandSuccess(deleteCommand,
                DeleteCommand.buildSuccessMessage(List.of(ALICE)),
                expectedModel);

        CommandResult result = logic.execute(LogicManager.EDIT_PREVIOUS_COMMAND_WORD);
        assertEquals(String.format(LogicManager.EDIT_PREVIOUS_MESSAGE_SUCCESS, deleteCommand),
                result.getFeedbackToUser());
        assertEquals(Optional.of(deleteCommand), result.getCommandTextToPopulate());
        assertEquals(expectedModel, model);
    }

    @Test
    public void execute_repeatedEditPrevious_success() throws Exception {
        assertCommandSuccess(ListCommand.COMMAND_WORD, ListCommand.MESSAGE_SUCCESS, model);

        CommandResult firstResult = logic.execute(LogicManager.EDIT_PREVIOUS_COMMAND_WORD);
        assertEquals(String.format(LogicManager.EDIT_PREVIOUS_MESSAGE_SUCCESS, ListCommand.COMMAND_WORD),
                firstResult.getFeedbackToUser());
        assertEquals(Optional.of(ListCommand.COMMAND_WORD), firstResult.getCommandTextToPopulate());

        CommandResult secondResult = logic.execute(LogicManager.EDIT_PREVIOUS_COMMAND_WORD);
        assertEquals(String.format(LogicManager.EDIT_PREVIOUS_MESSAGE_SUCCESS, ListCommand.COMMAND_WORD),
                secondResult.getFeedbackToUser());
        assertEquals(Optional.of(ListCommand.COMMAND_WORD), secondResult.getCommandTextToPopulate());
    }

    @Test
    public void execute_editPreviousWithArguments_throwsParseException() {
        assertParseException(LogicManager.EDIT_PREVIOUS_COMMAND_WORD + " extra",
                String.format(seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                        LogicManager.EDIT_PREVIOUS_MESSAGE_USAGE));
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), model.getUserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with an AddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath)
                    throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
