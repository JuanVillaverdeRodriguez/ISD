package es.udc.ws.app.model.eventservice;

import es.udc.ws.app.model.answer.Answer;
import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.eventservice.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    public Event addEvent(Event evento) throws InputValidationException;

    public List <Event> find (String keywords, LocalDateTime date_ini, LocalDateTime date_fin) throws InputValidationException;

    public Event findEvent(Long ID) throws InstanceNotFoundException;

    public Answer answerEvent(String email, Long eventoID, boolean answer) throws
            InputValidationException, InstanceNotFoundException,
            EventCelebrationException, AlreadyAnsweredException, CanceledEventException;

    public void cancelEvent(Long ID) throws InstanceNotFoundException,
            EventCelebrationException, CanceledEventException;
    public List <Answer> findAnswers(String email, boolean onlyAffirmative) throws InputValidationException;
}
