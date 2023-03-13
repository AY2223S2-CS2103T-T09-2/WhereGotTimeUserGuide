package seedu.address.testutil;

import seedu.address.logic.commands.EditIsolatedEventCommand;

import java.time.LocalDateTime;

public class EditEventDescriptorBuilder {
    private EditIsolatedEventCommand.EditEventDescriptor descriptor;

    public EditEventDescriptorBuilder() {
        descriptor = new EditIsolatedEventCommand.EditEventDescriptor();
    }

    public EditEventDescriptorBuilder(EditIsolatedEventCommand.EditEventDescriptor descriptor) {
        this.descriptor = new EditIsolatedEventCommand.EditEventDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditPersonDescriptor} with fields containing {@code person}'s details
     */
    public EditEventDescriptorBuilder(String eventName, LocalDateTime startDate, LocalDateTime endDate) {
        descriptor = new EditIsolatedEventCommand.EditEventDescriptor();
        descriptor.setEventName(eventName);
        descriptor.setEndDate(endDate);
        descriptor.setStartDate(startDate);
    }

    public EditIsolatedEventCommand.EditEventDescriptor build() {
        return descriptor;
    }
}

