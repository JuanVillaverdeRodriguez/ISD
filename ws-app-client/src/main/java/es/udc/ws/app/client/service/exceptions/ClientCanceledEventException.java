package es.udc.ws.app.client.service.exceptions;

public class ClientCanceledEventException extends Exception {

    private Long eventID;
    public ClientCanceledEventException(Long eventID) {
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
