package es.udc.ws.app.model.eventservice.exceptions;

public class CanceledEventException extends Exception{
    private Long eventID;

    public CanceledEventException(Long eventID) {
        super("El evento con ID: " + eventID + " esta cancelado");
        this.eventID= eventID;
    }

    public Long getEventID() {
        return eventID;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }
}
