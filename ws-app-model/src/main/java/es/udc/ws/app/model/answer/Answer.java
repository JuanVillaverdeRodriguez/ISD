package es.udc.ws.app.model.answer;

import java.time.LocalDateTime;
import java.util.Objects;

public class Answer {
    private Long answerId;
    private Long eventId;
    private String email;
    private Boolean assistance;
    private LocalDateTime answerDate;

    public Answer(Long eventId, String email, Boolean assistance) {
        this.eventId = eventId;
        this.email = email;
        this.assistance = assistance;
    }

    public Answer(Long eventId, String email, Boolean assistance, LocalDateTime answerDate) {
        this(eventId, email, assistance);
        this.answerDate = answerDate;
    }
    public Answer(Long answerId, Long eventId, String email, Boolean assistance) {
        this(eventId, email, assistance);
        this.answerId = answerId;
    }
    public Answer(Long answerId, Long eventId, String email, Boolean assistance, LocalDateTime answerDate) {
        this(eventId, email, assistance, answerDate);
        this.answerId = answerId;
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

    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = answerDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer = (Answer) o;
        return answerId.equals(answer.answerId) && eventId.equals(answer.eventId) && email.equals(answer.email) && assistance.equals(answer.assistance) && answerDate.equals(answer.answerDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(answerId, eventId, email, assistance, answerDate);
    }
}
