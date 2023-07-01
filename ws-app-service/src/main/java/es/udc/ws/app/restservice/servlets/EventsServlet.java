package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.CanceledEventException;
import es.udc.ws.app.model.eventservice.exceptions.EventCelebrationException;
import es.udc.ws.app.restservice.dto.EventToRestEventDtoConversor;
import es.udc.ws.app.restservice.dto.RestEventDto;
import es.udc.ws.app.restservice.json.EventExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestEventDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.exceptions.ParsingException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsServlet extends RestHttpServletTemplate {

    //ServletsUtils.normalizePath le quita la barra al final de la url si es que lo tiene
    //Por si el string de la fecha no es valida hay que poner un catch y decir que echa un InputValidationException

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {

        if(ServletUtils.normalizePath(req.getPathInfo()) == null){
            RestEventDto eventDto = JsonToRestEventDtoConversor.toRestEventDto(req.getInputStream());
            Event event = EventToRestEventDtoConversor.toEvent(eventDto);

            event = EventServiceFactory.getService().addEvent(event);

            eventDto = EventToRestEventDtoConversor.toRestEventDto(event);
            String eventUrl = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + event.getEventId();
            Map<String, String> headers = new HashMap<>(1);
            headers.put("Location", eventUrl);
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                    JsonToRestEventDtoConversor.toObjectNode(eventDto), headers);
        }else{

            String[] path = ServletUtils.normalizePath(req.getPathInfo()).split("/");

            if (path.length == 3 && path[2].equals("cancel") ){
                try{
                    Long eventId = Long.parseLong(path[1]);

                    EventServiceFactory.getService().cancelEvent(eventId);
                    ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NO_CONTENT, null, null);

                }catch (CanceledEventException e) {
                    ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                            EventExceptionToJsonConversor.toCanceledEventException(e), null);
                }catch (EventCelebrationException e) {
                    ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                            EventExceptionToJsonConversor.toEventCelebrationException(e), null);
                }catch (NumberFormatException e){
                    throw new InputValidationException(e.getMessage());
                }
            }
        }
    }
    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {

        if (ServletUtils.normalizePath(req.getPathInfo()) == null) {

            try {
                LocalDateTime fechaFin = LocalDateTime.parse(ServletUtils.getMandatoryParameter(req, "date"));
                String keywords = req.getParameter("keywords");
                LocalDateTime fechaIni = LocalDateTime.now().withNano(0);

                List<Event> events = EventServiceFactory.getService().find(keywords, fechaIni, fechaFin);

                List<RestEventDto> eventDtos = EventToRestEventDtoConversor.toRestEventDtos(events);

                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                        JsonToRestEventDtoConversor.toArrayNode(eventDtos), null);
            } catch (DateTimeParseException e){
                throw new InputValidationException(e.getMessage());
            }
        } else {
            Long ID = ServletUtils.getIdFromPath(req,"events");

            Event event = EventServiceFactory.getService().findEvent(ID);

            RestEventDto eventDto = EventToRestEventDtoConversor.toRestEventDto(event);

            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                    JsonToRestEventDtoConversor.toObjectNode(eventDto), null);
        }
    }
}
