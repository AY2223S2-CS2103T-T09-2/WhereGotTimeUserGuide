package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.event.IsolatedEvent;
import seedu.address.model.event.IsolatedEventList;
import seedu.address.model.event.RecurringEvent;
import seedu.address.model.event.RecurringEventList;
import seedu.address.model.event.exceptions.EventConflictException;
import seedu.address.model.group.Group;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final List<JsonAdaptedTag> tagged = new ArrayList<>();
    private final List<JsonAdaptedGroup> groups = new ArrayList<>();
    private final List<JsonAdaptedIsolatedEvent> isolatedEvents = new ArrayList<>();
    private final List<JsonAdaptedRecurringEvent> recurringEvents = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("tagged") List<JsonAdaptedTag> tagged,
            @JsonProperty("groups") List<JsonAdaptedGroup> groups,
            @JsonProperty("isolatedEvents") List<JsonAdaptedIsolatedEvent> isolatedEvents,
            @JsonProperty("recurringEvents") List<JsonAdaptedRecurringEvent> recurringEvents) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        if (tagged != null) {
            this.tagged.addAll(tagged);
        }
        if (groups != null) {
            this.groups.addAll(groups);
        }
        if (isolatedEvents != null) {
            this.isolatedEvents.addAll(isolatedEvents);
        }
        if (recurringEvents != null) {
            this.recurringEvents.addAll(recurringEvents);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        tagged.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        groups.addAll(source.getGroups().stream()
                .map(JsonAdaptedGroup::new)
                .collect(Collectors.toList()));
        isolatedEvents.addAll(source.getIsolatedEventList()
                .getList()
                .stream()
                .map(JsonAdaptedIsolatedEvent::new)
                .collect(Collectors.toList()));
        recurringEvents.addAll(source.getRecurringEventList()
                .getList()
                .stream()
                .map(JsonAdaptedRecurringEvent::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        final List<Group> personGroups = new ArrayList<>();

        final IsolatedEventList modelIsolatedEventList = new IsolatedEventList();
        final RecurringEventList modelRecurringEventList = new RecurringEventList();

        for (JsonAdaptedTag tag : tagged) {
            personTags.add(tag.toModelType());
        }

        for (JsonAdaptedGroup group : groups) {
            personGroups.add(group.toModelType());
        }
        for (JsonAdaptedIsolatedEvent isolatedEvent : isolatedEvents) {
            IsolatedEvent modelIsolatedEvent = isolatedEvent.toModelType();

            try {
                modelIsolatedEvent.checkValidStartEnd();
                modelIsolatedEventList.checkClashingIsolatedEvent(modelIsolatedEvent.getStartDate(),
                        modelIsolatedEvent.getEndDate());
                modelIsolatedEvent.checkConflictsRecurringEventList(modelRecurringEventList);
            } catch (EventConflictException e) {
                throw new IllegalValueException(e.getMessage());
            }

            try {
                modelIsolatedEvent.checkNotEnded();
            } catch (EventConflictException e) {
                continue;
            }

            modelIsolatedEventList.insert(isolatedEvent.toModelType());
        }
        for (JsonAdaptedRecurringEvent recurringEvent : recurringEvents) {
            RecurringEvent modelRecurringEvent = recurringEvent.toModelType();
            try {
                modelRecurringEvent.checkPeriod();
                modelRecurringEvent.listConflictedEventWithIsolated(modelIsolatedEventList);
            } catch (EventConflictException e) {
                throw new IllegalValueException(e.getMessage());
            }
            if (modelRecurringEventList.checkClashingRecurringEvent(modelRecurringEvent) != null) {
                throw new IllegalValueException(RecurringEvent.MESSAGE_CONSTRAINTS_CLASH);
            }
            modelRecurringEventList.insert(recurringEvent.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        final Set<Tag> modelTags = new HashSet<>(personTags);
        final Set<Group> modelGroups = new HashSet<>(personGroups);

        return new Person(modelName, modelPhone, modelEmail, modelAddress, modelTags, modelGroups,
                modelIsolatedEventList, modelRecurringEventList);
    }

}
