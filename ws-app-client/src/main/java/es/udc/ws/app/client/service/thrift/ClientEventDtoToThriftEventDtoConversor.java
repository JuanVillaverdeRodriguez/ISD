package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.thrift.ThriftEventDto;
import es.udc.ws.app.client.service.dto.ClientEventDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class ClientEventDtoToThriftEventDtoConversor {
    public static ThriftEventDto toThriftEventDto (ClientEventDto eventDto){

        Long ID = eventDto.getEventId();

        return new ThriftEventDto(
                ID == null ? -1 : ID,
                eventDto.getEventName(),
                eventDto.getDescription(),
                (short) Duration.between(eventDto.getCelDate(), eventDto.getEndDate()).toMinutes(),
                eventDto.getCelDate().format(DateTimeFormatter.ISO_DATE_TIME),
                eventDto.getAnswers(),
                eventDto.getAffirmativeAnswers(),
                eventDto.isCancellation()
        );
    }

    public static ClientEventDto toClientEventDto(ThriftEventDto event){
        return new ClientEventDto(event.getEventId(), event.getEventName(), event.getDescription(),
                LocalDateTime.parse(event.getFechaCelebracion()),
                LocalDateTime.parse(event.getFechaCelebracion()).plusMinutes(event.getDuration()),
                event.getAnswers(), event.getAssistants(), event.isCancelation());
    }

    public static List<ClientEventDto> toClientEventDtos (List<ThriftEventDto> events){
        List<ClientEventDto> eventDtos = new ArrayList<>(events.size());
        for (ThriftEventDto event : events){
            eventDtos.add(toClientEventDto(event));
        }
        return eventDtos;
    }
}
