package es.udc.ws.app.client.service.exceptions;

public class ClientEventCelebrationException extends Exception {

    private Long eventID;

    public ClientEventCelebrationException(Long eventID) {
        super("El evento con ID: " + eventID + " esta fuera de plazo (se celebra en menos de 24H o ya se ocurrio)");
        this.eventID= eventID;
    }

    public Long getEventID() {
        return eventID;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }


}
