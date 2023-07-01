package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.event.Event;

import java.util.ArrayList;
import java.util.List;

public class EventToRestEventDtoConversor {

    public static List<RestEventDto> toRestEventDtos(List<Event> eventos){
        List<RestEventDto> eventDtos = new ArrayList<>(eventos.size());
        for(int i = 0; i < eventos.size(); i++){
            Event evento = eventos.get(i);
            eventDtos.add(toRestEventDto(evento));
        }
        return eventDtos;
    }

    public static RestEventDto toRestEventDto(Event evento){
        return new RestEventDto(evento.getEventId(), evento.getEventName(), evento.getDuration(),
                evento.getDescription(), evento.getEventDate(), (short) (evento.getAssistant()+ evento.getAbsent()),
                evento.getAssistant(), evento.isCancelation());
    }

    public static Event toEvent(RestEventDto evento){
        return new Event(evento.getEventName(), evento.getDescription(), evento.getDuration(),
                evento.getFechaCelebracion());
    }
}