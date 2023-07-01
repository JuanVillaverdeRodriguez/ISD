package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.thrift.ThriftAppService;
import es.udc.ws.app.thrift.ThriftEventDto;
import es.udc.ws.app.thrift.ThriftInputValidationException;
import es.udc.ws.util.exceptions.InputValidationException;


import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ThriftAppServiceImpl implements ThriftAppService.Iface {


    @Override
    public long addEvent(ThriftEventDto eventDto) throws ThriftInputValidationException{
        Event event = EventToThriftEventDtoConversor.toEvent(eventDto);

        try {
            Event addedEvent = EventServiceFactory.getService().addEvent(event);
            return EventToThriftEventDtoConversor.toThriftEventDto(addedEvent).eventId;
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }
    }

    @Override
    public List<ThriftEventDto> find(String keywords, String endDate) throws ThriftInputValidationException{

        LocalDateTime fechaIni = LocalDateTime.now().withNano(0);

        try{
            List<Event> foundEvents = EventServiceFactory.getService().find(keywords, fechaIni, LocalDateTime.parse(endDate));
            return EventToThriftEventDtoConversor.toEventDtos(foundEvents);
        } catch (InputValidationException | DateTimeParseException e){
            throw new ThriftInputValidationException(e.getMessage());
        }
    }
}
