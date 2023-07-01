package es.udc.ws.app.client.service.dto;

import java.time.LocalDateTime;

public class ClientEventDto {
    private Long eventId;
    private String eventName;
    private String description;
    private LocalDateTime endDate;
    private LocalDateTime celDate;
    private short answers;
    private short affirmativeAnswers;
    private boolean cancellation;

    public ClientEventDto(String eventName, String description, LocalDateTime celDate, LocalDateTime endDate){
        this.eventName = eventName;
        this.celDate = celDate;
        this.endDate = endDate;
        this.description = description;
    }

    public ClientEventDto(Long eventId, String eventName, String description, LocalDateTime celDate,
                          LocalDateTime endDate, short answers, short affirmativeAnswers, boolean cancellation) {
        this(eventName,description, celDate, endDate);
        this.eventId = eventId;
        this.answers = answers;
        this.affirmativeAnswers = affirmativeAnswers;
        this.cancellation = cancellation;
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

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getCelDate() {
        return celDate;
    }

    public void setCelDate(LocalDateTime celDate) {
        this.celDate = celDate;
    }

    public short getAffirmativeAnswers() {
        return affirmativeAnswers;
    }

    public void setAffirmativeAnswers(short affirmativeAnswers) {
        this.affirmativeAnswers = affirmativeAnswers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public short getAnswers() {
        return answers;
    }

    public void setAnswers(short answers) {
        this.answers = answers;
    }

    public boolean isCancellation() {
        return cancellation;
    }

    public void setCancellation(boolean cancellation) {
        this.cancellation = cancellation;
    }
}
