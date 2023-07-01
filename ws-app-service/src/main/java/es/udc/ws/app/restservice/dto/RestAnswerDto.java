package es.udc.ws.app.restservice.dto;

public class RestAnswerDto {
    private Long answerId;
    private Long eventId;
    private String email;
    private boolean assistance;

    public RestAnswerDto(Long answerId, Long eventId, String email, boolean assist){
        this.answerId = answerId;
        this.eventId = eventId;
        this.email = email;
        this.assistance = assist;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAssistance() {
        return assistance;
    }

    public void setAssistance(Boolean assistance) {
        this.assistance = assistance;
    }
}
