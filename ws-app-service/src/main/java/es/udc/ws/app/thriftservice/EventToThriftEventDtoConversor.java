package es.udc.ws.app.thriftservice;

import es.udc.ws.app.thrift.ThriftEventDto;
import es.udc.ws.app.model.event.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventToThriftEventDtoConversor {
    public static Event toEvent(ThriftEventDto eventDto){
        return new Event(eventDto.eventName, eventDto.description, eventDto.duration, LocalDateTime.parse(eventDto.fechaCelebracion));
    }

    public static ThriftEventDto toThriftEventDto(Event event){
        return new ThriftEventDto(event.getEventId(), event.getEventName(), event.getDescription(), event.getDuration(),
                event.getEventDate().format(DateTimeFormatter.ISO_DATE_TIME), (short) (event.getAssistant() + event.getAbsent()),
                event.getAssistant(), event.isCancelation());
    }

    public static List<ThriftEventDto> toEventDtos (List<Event> events){
        List<ThriftEventDto> eventDtos = new ArrayList<>(events.size());
        for (Event event : events){
            eventDtos.add(toThriftEventDto(event));
        }
        return eventDtos;
    }
}