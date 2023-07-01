package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.dto.ClientAnswerDto;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.exceptions.ClientAlreadyAnsweredException;
import es.udc.ws.app.client.service.exceptions.ClientCanceledEventException;
import es.udc.ws.app.client.service.exceptions.ClientEventCelebrationException;
import es.udc.ws.app.thrift.*;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ThriftClientAppService implements ClientEventService {

    private final static String ENDPOINT_ADDRESS_PARAMETER =
            "ThriftClientEventService.endpointAddress";

    private final static String endpointAddress =
            ConfigurationParametersManager.getParameter(ENDPOINT_ADDRESS_PARAMETER);

    @Override
    public Long addEvent(ClientEventDto evento) throws InputValidationException {
        ThriftAppService.Client client = getClient();
        try (TTransport transport = client.getInputProtocol().getTransport()) {
            transport.open();
            return client.addEvent(ClientEventDtoToThriftEventDtoConversor.toThriftEventDto(evento));
        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientEventDto> find(String keywords, LocalDateTime endDate) throws InputValidationException {
        ThriftAppService.Client client = getClient();
        try (TTransport transport = client.getInputProtocol().getTransport()) {
            transport.open();
            List<ThriftEventDto> foundEvents = client.find(keywords, endDate.format(DateTimeFormatter.ISO_DATE_TIME));
            return ClientEventDtoToThriftEventDtoConversor.toClientEventDtos(foundEvents);
        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClientEventDto findByID(Long eventID) throws InputValidationException, InstanceNotFoundException {
        return null;
    }

    @Override
    public ClientAnswerDto answerEvent(String email, Long eventId, boolean assistance) throws InputValidationException, ClientAlreadyAnsweredException, ClientEventCelebrationException, ClientCanceledEventException, InstanceNotFoundException {
        return null;
    }

    @Override
    public void cancelEvent(Long eventId) throws InstanceNotFoundException, ClientEventCelebrationException, ClientCanceledEventException {

    }

    @Override
    public List<ClientAnswerDto> findAnswers(String email, boolean affirmative) throws InputValidationException {
        return null;
    }

    private ThriftAppService.Client getClient() {
        try {

            TTransport transport = new THttpClient(endpointAddress);
            TProtocol protocol = new TBinaryProtocol(transport);

            return new ThriftAppService.Client(protocol);

        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }
    }
}
