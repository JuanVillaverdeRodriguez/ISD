package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestEventDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class JsonToRestEventDtoConversor {
    public static ObjectNode toObjectNode(RestEventDto event) {

        ObjectNode eventObject = JsonNodeFactory.instance.objectNode();

        if (event.getEventId() != null) {
            eventObject.put("eventId", event.getEventId());
        }

        eventObject.put("eventName", event.getEventName()).
                put("description", event.getDescription()).
                put("duration", event.getDuration()).
                put("date", event.getFechaCelebracion().format(DateTimeFormatter.ISO_DATE_TIME));

        if (event.getEventId() != null) {
            eventObject.put("answers", event.getAnswers()).
                    put("assistants", event.getAssistants()).
                    put("cancellation", event.isCancelation());
        }

        return eventObject;
    }

    public static ArrayNode toArrayNode(List<RestEventDto> events) {

        ArrayNode eventsNode = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < events.size(); i++) {
            RestEventDto eventDto = events.get(i);
            ObjectNode eventObject = toObjectNode(eventDto);
            eventsNode.add(eventObject);
        }

        return eventsNode;
    }

    public static RestEventDto toRestEventDto(InputStream jsonEvent) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEvent);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode eventObject = (ObjectNode) rootNode;

                String name = eventObject.get("eventName").textValue().trim();
                String description = eventObject.get("description").textValue().trim();
                short duration =  eventObject.get("duration").shortValue();
                LocalDateTime fecha = LocalDateTime.parse(eventObject.get("date").textValue().trim());


                return new RestEventDto(name,duration,description,fecha);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}
