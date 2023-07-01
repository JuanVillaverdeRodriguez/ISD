package es.udc.ws.app.client.service.dto;

public class ClientAnswerDto {
    private Long answerId;
    private Long eventId;
    private String email;
    private boolean assistance;


    public ClientAnswerDto(Long answerId, Long eventId, String email, boolean assistance) {
        this.answerId = answerId;
        this.eventId = eventId;
        this.email = email;
        this.assistance = assistance;
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

    public boolean isAssistance() {
        return assistance;
    }

    public void setAssistance(boolean assistance) {
        this.assistance = assistance;
    }
}
