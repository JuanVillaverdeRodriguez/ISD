package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.model.eventservice.exceptions.AlreadyAnsweredException;
import es.udc.ws.app.model.eventservice.exceptions.CanceledEventException;
import es.udc.ws.app.model.eventservice.exceptions.EventCelebrationException;

public class EventExceptionToJsonConversor {

    public static ObjectNode toAlreadyAnsweredException(AlreadyAnsweredException ex) {
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "AlreadyAnswered");
        exceptionObject.put("eventId", (ex.getEventID() != null) ? ex.getEventID() : null);

        if (ex.getEmail() != null) {
            exceptionObject.put("email", ex.getEmail());
        }
        else {
            exceptionObject.set("email", null);
        }

        return exceptionObject;
    }

    public static ObjectNode toCanceledEventException(CanceledEventException ex) {
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "CanceledEvent");
        exceptionObject.put("eventId", (ex.getEventID() != null) ? ex.getEventID() : null);

        return exceptionObject;
    }

    public static ObjectNode toEventCelebrationException(EventCelebrationException ex) {
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "EventCelebration");
        exceptionObject.put("eventId", (ex.getEventID() != null) ? ex.getEventID() : null);

        return exceptionObject;

    }
}
