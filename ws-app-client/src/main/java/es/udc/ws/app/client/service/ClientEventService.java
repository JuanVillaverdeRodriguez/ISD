package es.udc.ws.app.client.service;

import es.udc.ws.app.client.service.dto.ClientAnswerDto;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.exceptions.ClientAlreadyAnsweredException;
import es.udc.ws.app.client.service.exceptions.ClientCanceledEventException;
import es.udc.ws.app.client.service.exceptions.ClientEventCelebrationException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface ClientEventService {

    public Long addEvent(ClientEventDto evento) throws InputValidationException;

    public List<ClientEventDto> find(String keywords, LocalDateTime endDate) throws InputValidationException;

    public ClientEventDto findByID(Long eventID) throws InputValidationException, InstanceNotFoundException;

    public ClientAnswerDto answerEvent(String email, Long eventId, boolean assistance) throws InputValidationException,
            ClientAlreadyAnsweredException, ClientEventCelebrationException, ClientCanceledEventException, InstanceNotFoundException;

    public void cancelEvent(Long eventId) throws InstanceNotFoundException, ClientEventCelebrationException,
            ClientCanceledEventException;

    public List<ClientAnswerDto> findAnswers(String email, boolean affirmative) throws InputValidationException;

}
