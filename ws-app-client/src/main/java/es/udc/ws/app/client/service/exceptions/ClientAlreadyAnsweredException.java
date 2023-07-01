package es.udc.ws.app.client.service.exceptions;

public class ClientAlreadyAnsweredException extends Exception {
    private String email;
    private Long eventID;

    public ClientAlreadyAnsweredException(String email, Long eventID) {
        super("El usuario " + email + " ya ha respondido al evento con ID: " + eventID + ".");
        this.email = email;
        this.eventID = eventID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getEventID() {
        return eventID;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }
}
