package es.udc.ws.app.model.eventservice;

import es.udc.ws.app.model.answer.Answer;
import es.udc.ws.app.model.answer.SqlAnswerDao;
import es.udc.ws.app.model.answer.SqlAnswerDaoFactory;
import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.event.SqlEventDao;
import es.udc.ws.app.model.event.SqlEventDaoFactory;
import es.udc.ws.app.model.eventservice.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.validation.PropertyValidator;


import javax.sql.DataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import static es.udc.ws.app.model.util.ModelConstants.MAX_DURATION;

public class EventServiceImpl implements EventService {
    private final DataSource dataSource;
    private SqlAnswerDao answerDao = null;
    private SqlEventDao eventDao = null;

    public EventServiceImpl() {
        dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        eventDao = SqlEventDaoFactory.getDao();
        answerDao = SqlAnswerDaoFactory.getDao();
    }


    //...Funciones....................................................................................................//

    /*
    En el enunciado no se contempla una maxima duración asi que no hace falta la constante MAX_DURATION,
    sin embargo nos sirve para comprobar que el evento no tenga duración 0.
    * */

    private void validateEvent(Event evento) throws InputValidationException {

        PropertyValidator.validateMandatoryString("eventName", evento.getEventName());
        PropertyValidator.validateLong("duration", evento.getDuration(), 1, MAX_DURATION);
        PropertyValidator.validateNotNegativeLong("duration", evento.getDuration());
        PropertyValidator.validateMandatoryString("description", evento.getDescription());
        if (evento.getEventDate() == null){
            throw new InputValidationException("La fecha del evento no puede ser nula");
        }
        PropertyValidator.validateNotNegativeLong("eventDate",
                evento.getEventDate().getLong(ChronoField.EPOCH_DAY));
        PropertyValidator.validateNotNegativeLong("eventDate",
                evento.getEventDate().getLong(ChronoField.NANO_OF_DAY));

        evento.setEventDate(evento.getEventDate().withNano(0));
        if (evento.getEventDate().isBefore(LocalDateTime.now().withNano(0).minusHours(24))){
            throw new InputValidationException("La fecha del evento no puede darse en el pasado o en menos de 24 horas");
        }
    }

    private void validateEmail(String email) throws InputValidationException{
        PropertyValidator.validateMandatoryString("email", email);
    }

    private void validateInput(LocalDateTime date_ini, LocalDateTime date_fin) throws InputValidationException {

        if (date_fin != null && date_ini != null) {
            if (date_fin.isBefore(date_ini.withNano(0)))
                throw new InputValidationException("El orden de las fechas es incorrecto");
            else if (date_ini.isBefore(LocalDateTime.now().withNano(0)))
                throw new InputValidationException("Introduce un rango de fechas superior a la fecha actual");
        }
        PropertyValidator.validateNotNegativeLong("date_ini", date_ini.getLong(ChronoField.EPOCH_DAY));
        PropertyValidator.validateNotNegativeLong("date_ini", date_ini.getLong(ChronoField.NANO_OF_DAY));
        PropertyValidator.validateNotNegativeLong("date_fin", date_ini.getLong(ChronoField.EPOCH_DAY));
        PropertyValidator.validateNotNegativeLong("date_fin", date_ini.getLong(ChronoField.NANO_OF_DAY));
    }


    //...Func-1.......................................................................................................//

    @Override
    public Event addEvent(Event evento) throws InputValidationException {

        validateEvent(evento);
        LocalDateTime eventCreationDate = LocalDateTime.now().withNano(0);

        evento.setEventCreationDate(eventCreationDate);
        evento.setCancelation(false);
        evento.setAssistant((short) 0);
        evento.setAbsent((short) 0);

        try (Connection connection = dataSource.getConnection()) {

            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                Event createdEvent = eventDao.create(connection, evento);

                connection.commit();

                return createdEvent;

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e){
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //...Func-2.......................................................................................................//

    @Override
    public List<Event> find(String keywords, LocalDateTime date_ini, LocalDateTime date_fin)
            throws InputValidationException {

        validateInput(date_ini, date_fin);
        try(Connection connection = dataSource.getConnection()){
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);

            List<Event> events = eventDao.find(connection, keywords, date_ini, date_fin);
            connection.commit();

            return events;

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    //...Func-3.......................................................................................................//

    @Override
    public Event findEvent(Long ID) throws InstanceNotFoundException {

        try (Connection connection = dataSource.getConnection()) {
            try{
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                Event event = eventDao.findById(connection, ID);

                connection.commit();
                return event;

            }catch (InstanceNotFoundException e){
                connection.commit();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //...Func-4.......................................................................................................//

    @Override
    public Answer answerEvent(String email, Long eventId, boolean assistance) throws InputValidationException,
            InstanceNotFoundException, EventCelebrationException, AlreadyAnsweredException, CanceledEventException {

        LocalDateTime answerDate = LocalDateTime.now().withNano(0);
        Answer ans = new Answer(eventId, email, assistance, answerDate);
        validateEmail(ans.getEmail());

        try(Connection connection = dataSource.getConnection()){

            try{
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                Event evento = eventDao.findById(connection, eventId);
                boolean alreadyAnswered = answerDao.alreadyAnswered(connection, email, eventId);

                if (alreadyAnswered){
                    throw new AlreadyAnsweredException(email, eventId);
                }

                if (answerDate.plusHours(24).isAfter(evento.getEventDate())){
                    throw new EventCelebrationException(eventId);
                }
                if (evento.isCancelation()){
                    throw new CanceledEventException(eventId);
                }
                ans.setAnswerDate(answerDate);
                Answer createdAnswer = answerDao.create(connection, ans);

                // Update event con el numero nuevo de asistentes
                int count;
                if (assistance) {
                    count = evento.getAssistant() + 1;
                    evento.setAssistant((short)count);
                }
                else {
                    count = evento.getAbsent() + 1;
                    evento.setAbsent((short)count);
                }

                eventDao.update(connection, evento);

                connection.commit();

                return createdAnswer;

            }catch (InstanceNotFoundException | AlreadyAnsweredException | EventCelebrationException | CanceledEventException e){
                connection.commit();
                throw e;
            } catch (SQLException e){
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e){
                connection.rollback();
                throw e;
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    //...Func-5.......................................................................................................//

    @Override
    public void cancelEvent(Long ID) throws InstanceNotFoundException,
            EventCelebrationException, CanceledEventException {
        LocalDateTime cancelDate = LocalDateTime.now().withNano(0);

        try(Connection connection = dataSource.getConnection()){
            try{
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                Event evento = eventDao.findById(connection, ID);

                if (evento.isCancelation()){
                    throw new CanceledEventException(evento.getEventId());
                }
                if (cancelDate.isAfter(evento.getEventDate())){
                    throw new EventCelebrationException(evento.getEventId());
                }
                evento.setCancelation(true);
                eventDao.update(connection, evento);

                connection.commit();

            }catch (InstanceNotFoundException | EventCelebrationException | CanceledEventException e){
                connection.commit();
                throw e;
            }catch (SQLException e){
                connection.rollback();
                throw new RuntimeException(e);
            }catch (RuntimeException  | Error e){
                connection.rollback();
                throw e;
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    //...Func-6.......................................................................................................//

    @Override
    public List<Answer> findAnswers(String email, boolean onlyAffirmative) throws InputValidationException {

        validateEmail(email);

        try(Connection connection = dataSource.getConnection()){
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);

            List<Answer> ans = answerDao.findByEmail(connection, email, onlyAffirmative);

            connection.commit();

            return  ans;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

}
