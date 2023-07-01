package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientAnswerDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientAnswerDtoConversor {

    public static List<ClientAnswerDto> toClientAnswerDtos(InputStream jsonAnswer) throws ParsingException {

        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonAnswer);
            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ArrayNode answersArray = (ArrayNode) rootNode;
                List<ClientAnswerDto> answersDtos = new ArrayList<>(answersArray.size());
                for (JsonNode answerNode : answersArray) {
                    answersDtos.add(toClientAnswerDto(answerNode));
                }

                return answersDtos;
            }
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    public static ClientAnswerDto toClientAnswerDto(JsonNode answerNode) throws ParsingException {
        if (answerNode.getNodeType() != JsonNodeType.OBJECT) {
            throw new ParsingException("Unrecognized JSON (object expected)");
        } else {
            ObjectNode answerObject = (ObjectNode) answerNode;
            JsonNode answerIdNode = answerObject.get("answerId");

            Long answerId = (answerIdNode != null) ? answerIdNode.longValue() : null;
            String email = answerObject.get("email").textValue().trim();
            Long eventId = (answerObject.get("eventId").longValue());
            boolean assistance = answerObject.get("assistance").booleanValue();

            return new ClientAnswerDto(answerId, eventId, email, assistance);
        }
    }

    public static ClientAnswerDto toClientAnswerDto(InputStream jsonAnswer) throws ParsingException {

        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonAnswer);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode eventObject = (ObjectNode) rootNode;

                JsonNode AnswerIdNode = eventObject.get("answerId");

                Long answerId = (AnswerIdNode != null) ? AnswerIdNode.longValue() : null;
                Long eventId = eventObject.get("eventId").longValue();
                String email = eventObject.get("email").textValue().trim();
                boolean assistance = eventObject.get("assistance").booleanValue();

                return new ClientAnswerDto(answerId, eventId, email, assistance);

            }
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}
