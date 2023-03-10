package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.event.IsolatedEvent;
import seedu.address.model.person.Person;


/**
 * Command class for AddIsolatedEventCommand.
 */
public class AddIsolatedEventCommand extends Command {
    public static final String COMMAND_WORD = "event_create";
    public static final String MESSAGE_SUCCESS = "New isolated event added: %1$s";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Add an isolated event into the isolated event list"
            + "by the index number used in the last person listing. "
            + "Existing remark will be overwritten by the input.\n"
            + "Parameters: start date and end date (must be in the format of ) "
            + "e/ [REMARK]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + "r/ Likes to swim.";

    public final IsolatedEvent eventToAdd;
    public final Index index;

    /**
     * Constructor for AddIsolatedEventCommand Object.
     * @param index
     * @param eventToAdd
     */
    public AddIsolatedEventCommand(Index index, IsolatedEvent eventToAdd) {
        this.eventToAdd = eventToAdd;
        this.index = index;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        model.addIsolatedEvent(personToEdit, eventToAdd);

        return new CommandResult(String.format(MESSAGE_SUCCESS, eventToAdd) + " to "
                + personToEdit.getName());
    }
}
