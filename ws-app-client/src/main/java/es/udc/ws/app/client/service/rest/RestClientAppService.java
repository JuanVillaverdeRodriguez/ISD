package es.udc.ws.app.client.service.rest;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.dto.ClientAnswerDto;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.exceptions.ClientAlreadyAnsweredException;
import es.udc.ws.app.client.service.exceptions.ClientCanceledEventException;
import es.udc.ws.app.client.service.exceptions.ClientEventCelebrationException;
import es.udc.ws.app.client.service.rest.json.JsonToClientAnswerDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientEventDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientExceptionConversor;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RestClientAppService implements ClientEventService {
    private final static String ENDPOINT_ADDRESS_PARAMETER = "RestClientEventService.endpointAddress";

    private String endpointAddress;

    @Override
    public Long addEvent(ClientEventDto event) throws InputValidationException {

        try{
            HttpResponse response = Request.Post(getEndpointAddress() + "events").
                    bodyStream(toInputStream(event), ContentType.create("application/json")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientEventDtoConversor.toClientEventDto(response.getEntity().getContent()).getEventId();

        }catch(InputValidationException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientEventDto> find(String keywords, LocalDateTime endDate) throws InputValidationException {

        try {
            String dateString = endDate.format(DateTimeFormatter.ISO_DATE_TIME);
            HttpResponse response = Request.Get(getEndpointAddress() + "events?date="
                            + URLEncoder.encode(dateString, "UTF-8") +
                            (keywords != null ? "&keywords="+ URLEncoder.encode(keywords, "UTF-8"):"")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientEventDtoConversor.toClientEventDtos(response.getEntity().getContent());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClientEventDto findByID(Long ID) throws InputValidationException, InstanceNotFoundException{

        try {

            HttpResponse response = Request.Get(getEndpointAddress() + "events/" + ID).execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientEventDtoConversor.toClientEventDto(response.getEntity().getContent());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClientAnswerDto answerEvent(String email, Long eventId, boolean assistance) throws InputValidationException,
            ClientAlreadyAnsweredException, ClientEventCelebrationException, ClientCanceledEventException, InstanceNotFoundException {
        try {
            HttpResponse response = Request.Post(getEndpointAddress() + "answers?email="
                    + URLEncoder.encode(email, "UTF-8") + "&eventId="
                    + eventId + "&assistance="
                    + URLEncoder.encode(String.valueOf(assistance), "UTF-8")).execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientAnswerDtoConversor.toClientAnswerDto(response.getEntity().getContent());

        } catch (InputValidationException | ClientEventCelebrationException | ClientCanceledEventException |
                 ClientAlreadyAnsweredException | InstanceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cancelEvent(Long id) throws InstanceNotFoundException, ClientEventCelebrationException, ClientCanceledEventException {
        try{
            HttpResponse response = Request.Post(getEndpointAddress() + "events/" + id + "/cancel").
                    execute().returnResponse();
            validateStatusCode(HttpStatus.SC_NO_CONTENT, response);

        }catch (ClientCanceledEventException | ClientEventCelebrationException | InstanceNotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientAnswerDto> findAnswers(String email, boolean affirmative) throws InputValidationException {

        try {
            HttpResponse response = Request.Get(getEndpointAddress() + "answers?email="
                    + URLEncoder.encode(email, "UTF-8") + "&assistance="
                    + URLEncoder.encode(String.valueOf(affirmative), "UTF-8")).execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientAnswerDtoConversor.toClientAnswerDtos(response.getEntity().getContent());


        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized String getEndpointAddress() {
        if (endpointAddress == null) {
            endpointAddress = ConfigurationParametersManager
                    .getParameter(ENDPOINT_ADDRESS_PARAMETER);
        }
        return endpointAddress;
    }

    private InputStream toInputStream(ClientEventDto event) {

        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream,
                    JsonToClientEventDtoConversor.toObjectNode(event));

            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void validateStatusCode(int successCode, HttpResponse response) throws Exception {
        try {

            int statusCode = response.getStatusLine().getStatusCode();

            /* Success? */
            if (statusCode == successCode) {
                return;
            }

            /* Handler error. */
            switch (statusCode) {

                case HttpStatus.SC_NOT_FOUND:
                    throw JsonToClientExceptionConversor.fromNotFoundErrorCode(
                            response.getEntity().getContent());

                case HttpStatus.SC_BAD_REQUEST:
                    throw JsonToClientExceptionConversor.fromBadRequestErrorCode(
                            response.getEntity().getContent());

                case HttpStatus.SC_FORBIDDEN:
                    throw JsonToClientExceptionConversor.fromForbiddenErrorCode(
                            response.getEntity().getContent());

                default:
                    throw new RuntimeException("HTTP error; status code = "
                            + statusCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
