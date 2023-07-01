package es.udc.ws.app.model.event;

import java.time.LocalDateTime;
import java.util.Objects;

public class Event {
    private Long eventId;
    private String eventName;
    private String description;
    private short duration;
    private LocalDateTime eventDate;
    private LocalDateTime eventCreationDate;
    private short assistant;
    private short absent;
    private boolean cancelation;

    public Event(String name, String description, short duration, LocalDateTime eventDate){
        this.eventName = name;
        this.description = description;
        this.duration = duration;
        this.eventDate = eventDate;
        this.cancelation = false;
    }

    public Event(Long id, String name, String description, short duration, LocalDateTime eventDate){
        this(name, description, duration, eventDate);
        this.eventId = id;
    }

    public Event(Long id, String name, String description, short duration, LocalDateTime eventDate, LocalDateTime eventCreationDate){
        this(id, name, description, duration, eventDate);
        this.eventCreationDate = eventCreationDate;
    }

    public Event(Long id, String name, String description, short duration, LocalDateTime eventDate, LocalDateTime eventCreationDate, boolean cancelation){
        this(id, name, description, duration, eventDate, eventCreationDate);
        this.cancelation = cancelation;
    }

    public Event(Long id, String name, String description, short duration, LocalDateTime eventDate, LocalDateTime eventCreationDate, boolean cancelation, short assistant, short absent){
        this(id, name, description, duration, eventDate, eventCreationDate, cancelation);
        this.assistant = assistant;
        this.absent = absent;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public short getDuration() {
        return duration;
    }

    public void setDuration(short duration) {
        this.duration = duration;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public LocalDateTime getEventCreationDate() {
        return eventCreationDate;
    }

    public void setEventCreationDate(LocalDateTime eventCreationDate) {
        this.eventCreationDate = eventCreationDate;
    }

    public boolean isCancelation() {
        return cancelation;
    }

    public void setCancelation(boolean cancelation) {
        this.cancelation = cancelation;
    }

    public short getAssistant(){
        return assistant;
    }

    public void setAssistant(short n_asist){
        this.assistant = n_asist;
    }

    public short getAbsent(){
        return absent;
    }

    public void setAbsent(short n_auses){
        this.absent = n_auses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return duration == event.duration && assistant == event.assistant && absent == event.absent && cancelation == event.cancelation && eventId.equals(event.eventId) && eventName.equals(event.eventName) && description.equals(event.description) && eventDate.equals(event.eventDate) && eventCreationDate.equals(event.eventCreationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventName, description, duration, eventDate, eventCreationDate, assistant, absent, cancelation);
    }
}
