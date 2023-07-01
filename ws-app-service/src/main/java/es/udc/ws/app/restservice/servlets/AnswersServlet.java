package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.model.answer.Answer;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.AlreadyAnsweredException;
import es.udc.ws.app.model.eventservice.exceptions.CanceledEventException;
import es.udc.ws.app.model.eventservice.exceptions.EventCelebrationException;
import es.udc.ws.app.restservice.dto.AnswerToRestAnswerDtoConversor;
import es.udc.ws.app.restservice.dto.RestAnswerDto;
import es.udc.ws.app.restservice.json.EventExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestAnswerDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswersServlet extends RestHttpServletTemplate {

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, InputValidationException, InstanceNotFoundException {

        ServletUtils.checkEmptyPath(req);
        Long eventId = ServletUtils.getMandatoryParameterAsLong(req,"eventId");
        String email = ServletUtils.getMandatoryParameter(req,"email");
        boolean assistance = Boolean.parseBoolean(ServletUtils.getMandatoryParameter(req,"assistance"));

        try {
            Answer answer = EventServiceFactory.getService().answerEvent(email, eventId, assistance);
            RestAnswerDto answerDto = AnswerToRestAnswerDtoConversor.toRestAnswerDto(answer);
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                    JsonToRestAnswerDtoConversor.toObjectNode(answerDto), null);

        } catch (EventCelebrationException e) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                    EventExceptionToJsonConversor.toEventCelebrationException(e), null);
        } catch (AlreadyAnsweredException e) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                    EventExceptionToJsonConversor.toAlreadyAnsweredException(e), null);
        } catch (CanceledEventException e) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                    EventExceptionToJsonConversor.toCanceledEventException(e), null);
        }
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, InputValidationException {

        ServletUtils.checkEmptyPath(req);
        String email = ServletUtils.getMandatoryParameter(req,"email");
        boolean assistance = Boolean.parseBoolean(ServletUtils.getMandatoryParameter(req,"assistance"));

        List<Answer> answers = EventServiceFactory.getService().findAnswers(email, assistance);

        List<RestAnswerDto> answerDtos = AnswerToRestAnswerDtoConversor.toRestAnswersDtos(answers);
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                JsonToRestAnswerDtoConversor.toArrayNode(answerDtos), null);
    }
}
