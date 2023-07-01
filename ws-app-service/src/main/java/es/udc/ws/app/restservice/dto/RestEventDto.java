package es.udc.ws.app.restservice.dto;

import java.time.LocalDateTime;

public class RestEventDto {
    private Long eventId;
    private String eventName;
    private String description;
    private short duration;
    private LocalDateTime fechaCelebracion;
    private short answers;
    private short assistants;
    private boolean cancelation;


    /*
    request.getPathInfo --> null | "/" (GET /movies?)
    ServletUtils.getIdFromPath --> extrae la id (GET /movies/123)
     */
    public RestEventDto(String eventName, short duration, String description, LocalDateTime fechaCel){
        this.eventName = eventName;
        this.duration = duration;
        this.description = description;
        this.fechaCelebracion = fechaCel;
        this.answers = 0;
        this.assistants = 0;
        this.cancelation = false;
    }

    public RestEventDto(Long eventId, String eventName, short duration, String description, LocalDateTime fechaCel){
        this(eventName,duration,description,fechaCel);
        this.eventId = eventId;
    }

    public RestEventDto(Long eventId, String eventName, short duration, String description, LocalDateTime fechaCel,
                        short answers, short assistants, boolean cancelation ){
        this(eventId,eventName,duration,description,fechaCel);
        this.answers = answers;
        this.assistants = assistants;
        this.cancelation = cancelation;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
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

    public boolean isCancelation() {
        return cancelation;
    }

    public void setCancelation(boolean cancelation) {
        this.cancelation = cancelation;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public short getDuration() {
        return duration;
    }

    public void setDuration(short duration) {
        this.duration = duration;
    }

    public LocalDateTime getFechaCelebracion() {
        return fechaCelebracion;
    }

    public void setFechaCelebracion(LocalDateTime fechaCelebracion) {
        this.fechaCelebracion = fechaCelebracion;
    }

    public short getAssistants() {
        return assistants;
    }

    public void setAssistants(short assistants) {
        this.assistants = assistants;
    }
}
