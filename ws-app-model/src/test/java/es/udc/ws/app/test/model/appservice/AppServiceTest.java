package es.udc.ws.app.test.model.appservice;

import es.udc.ws.app.model.answer.Answer;
import es.udc.ws.app.model.answer.SqlAnswerDao;
import es.udc.ws.app.model.answer.SqlAnswerDaoFactory;
import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.event.SqlEventDao;
import es.udc.ws.app.model.event.SqlEventDaoFactory;
import es.udc.ws.app.model.eventservice.EventService;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import static org.junit.jupiter.api.Assertions.*;

public class AppServiceTest {
    private final long NON_EXISTING_ID = -1;
    private final String USER_EMAIL = "email@email.com";
    private static EventService eventService = null;
    private static SqlAnswerDao answerDao = null;
    private static SqlEventDao eventDao = null;

    //private final static double horas = 12.5;


    @BeforeAll
    public static void init() {

        DataSource dataSource = new SimpleDataSource();

        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);

        eventService = EventServiceFactory.getService();

        answerDao = SqlAnswerDaoFactory.getDao();

        eventDao = SqlEventDaoFactory.getDao();

    }

    //...Funciones....................................................................................................//

    private Event getValidEvent(String eventName, String description, LocalDateTime eventDate) {
        short duration = 4000;
        return new Event(eventName, description, duration, eventDate);
    }

    private Event getValidEvent(String eventName) {
        short duration = 4000;
        LocalDateTime eventDate = LocalDateTime.now().withNano(0).plusYears(1);

        return new Event(eventName, "Event description", duration, eventDate);
    }

    private Event getValidEvent() {
        return getValidEvent("Event title");
    }

    private Event createEvent(Event event) {

        Event addedEvent;

        try {
            addedEvent = eventService.addEvent(event);
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
        return addedEvent;
    }
    private Answer createValidAnswer(){
        Answer addedAns;
        Event evento = createEvent(getValidEvent());

        try{
            addedAns = eventService.answerEvent(USER_EMAIL, evento.getEventId(), false);
        }catch (InputValidationException | InstanceNotFoundException | EventCelebrationException
                | CanceledEventException | AlreadyAnsweredException e){
            throw new RuntimeException(e);
        }
        return addedAns;
    }
    private Answer findAnswer(Long answerId){
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try(Connection connection = dataSource.getConnection()){
            try{

                return answerDao.find(connection, answerId);

            }catch (InstanceNotFoundException e){
                connection.commit();
                throw new RuntimeException(e);
            }catch (RuntimeException | Error e){
                connection.rollback();
                throw e;
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    public void update(Event evento){
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try(Connection con = dataSource.getConnection()){
            try{
                con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                con.setAutoCommit(false);

                eventDao.update(con, evento);

                con.commit();
            }catch (InstanceNotFoundException e){
                con.commit();
                throw new RuntimeException(e);
            }catch (RuntimeException | Error e){
                con.rollback();
                throw e;
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    private void removeEvent(Long Id){
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try(Connection connection = dataSource.getConnection()){
            try{
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                eventDao.remove(connection, Id);
                connection.commit();
            }catch (InstanceNotFoundException e){
                connection.commit();
                throw new RuntimeException(e);
            }catch (RuntimeException | Error e){
                connection.rollback();
                throw e;
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
    private void removeAnswer(Long Id){
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try(Connection connection = dataSource.getConnection()){
            try{
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                answerDao.remove(connection, Id);
                connection.commit();

            }catch (InstanceNotFoundException e){
                connection.commit();
                throw new RuntimeException(e);
            }catch (RuntimeException | Error e){
                connection.rollback();
                throw e;
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    //...Func-1.......................................................................................................//

    @Test
    public void testAddEventAndFindEvent() throws InputValidationException, InstanceNotFoundException {
        Event event = getValidEvent();
        Event addedEvent = null;

        try {
            LocalDateTime beforeCreationDate = LocalDateTime.now().withNano(0);

            addedEvent = eventService.addEvent(event);

            LocalDateTime afterCreationDate = LocalDateTime.now().withNano(0);
            Event foundEvent = eventService.findEvent(addedEvent.getEventId());

            assertEquals(addedEvent, foundEvent);
            assertEquals(event.getEventName(), foundEvent.getEventName());
            assertEquals(event.getEventDate(), foundEvent.getEventDate());
            assertTrue((foundEvent.getEventCreationDate().compareTo(beforeCreationDate) >= 0)
                    && (foundEvent.getEventCreationDate().compareTo(afterCreationDate)) <= 0);
        } finally {
            if (addedEvent != null) {
                removeEvent(addedEvent.getEventId());
            }
        }
    }

    @Test
    public void testAddEventWithNullName() {
        assertThrows(InputValidationException.class, () ->{
            Event evento =  getValidEvent();
            evento.setEventName(null);
            Event addedEvent = eventService.addEvent(evento);
            removeEvent(addedEvent.getEventId());
        });
    }

    @Test
    public void testAddEventWithBlankName(){
        assertThrows(InputValidationException.class, () -> {
            Event evento = getValidEvent();
            evento.setEventName("");
            Event addedEvent = eventService.addEvent(evento);
            removeEvent(addedEvent.getEventId());
        });
    }

    @Test
    public void testAddEventWithPastDate(){
        assertThrows(InputValidationException.class, () -> {
            Event evento = getValidEvent();
            evento.setEventDate(LocalDateTime.now().withNano(0).withYear(2021));
            Event addedEvent = eventService.addEvent(evento);
            removeEvent(addedEvent.getEventId());
        });
    }

    @Test
    public void testAddEventWithNullDate(){
        assertThrows(InputValidationException.class, () -> {
            Event evento = getValidEvent();
            evento.setEventDate(null);
            Event addedEvent = eventService.addEvent(evento);
            removeEvent(addedEvent.getEventId());
        });
    }

    @Test
    public void testAddEventWithBlankDescription(){
        assertThrows(InputValidationException.class, () -> {
            Event evento = getValidEvent();
            evento.setDescription("");
            Event addedEvent = eventService.addEvent(evento);
            removeEvent(addedEvent.getEventId());
        });
    }

    @Test
    public void testAddEventWithNullDescription(){
        assertThrows(InputValidationException.class, () -> {
            Event evento = getValidEvent();
            evento.setDescription(null);
            Event addedEvent = eventService.addEvent(evento);
            removeEvent(addedEvent.getEventId());
        });
    }

    @Test
    public void testAddEventWithNoDuration(){
        assertThrows(InputValidationException.class, () -> {
            Event evento = getValidEvent();
            evento.setDuration((short)0);
            Event addedEvent = eventService.addEvent(evento);
            removeEvent(addedEvent.getEventId());
        });
    }

    @Test
    public void testAddEventWithNegativeDuration(){
        assertThrows(InputValidationException.class, () -> {
            Event evento = getValidEvent();
            evento.setDuration((short)-1);
            Event addedEvent = eventService.addEvent(evento);
            removeEvent(addedEvent.getEventId());
        });
    }

    //...Func-2.......................................................................................................//

    @Test
    public void testFindEvents() {
        //Create event dates
        LocalDateTime eventDate1 = LocalDateTime.now().withNano(0).plusYears(3);
        LocalDateTime eventDate2 = LocalDateTime.now().withNano(0).plusYears(5);
        LocalDateTime eventDate3 = LocalDateTime.now().withNano(0).plusYears(1);
        LocalDateTime eventDate4 = LocalDateTime.now().withNano(0).plusYears(3);
        LocalDateTime eventDate5 = LocalDateTime.now().withNano(0).plusYears(1);

        //Add events to database
        Event event1 = createEvent(getValidEvent("Event1","Comida 1", eventDate1));
        Event event2 = createEvent(getValidEvent("Event2","Cena de empresa", eventDate2));
        Event event3 = createEvent(getValidEvent("Event3","Comida 2", eventDate3));
        Event event4 = createEvent(getValidEvent("Event4","Cena de empresa 2", eventDate4));
        Event event5 = createEvent(getValidEvent("Event5","Merendola", eventDate5));

        //Add events to list
        List <Event> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);
        events.add(event3);
        events.add(event4);
        events.add(event5);

        //Create search dates
        LocalDateTime date1 = LocalDateTime.now().withNano(0).plusYears(2);
        LocalDateTime date2 = LocalDateTime.now().withNano(0).plusYears(8);

        try {
            //Check if could find every event given a correct input
            List <Event> foundEvents = eventService.find("comIda", date1, date2);
            assertEquals(1, foundEvents.size());

            foundEvents = eventService.find("emp", date1, date2);
            assertEquals(2, foundEvents.size());

            foundEvents = eventService.find("Launch", date1, date2);
            assertEquals(0, foundEvents.size());

            foundEvents = eventService.find("Merendola", date1, date2);
            assertEquals(0, foundEvents.size());

            foundEvents = eventService.find("", date1, date2);
            assertEquals(3, foundEvents.size());

            //Check if it gives the desired output
            assertEquals(event2.getEventId(),foundEvents.get(1).getEventId());
            assertEquals(event2.getEventDate(),foundEvents.get(1).getEventDate());
            assertEquals(event2.getEventName(),foundEvents.get(1).getEventName());
            assertEquals(event2.getEventCreationDate(),foundEvents.get(1).getEventCreationDate());
            assertEquals(event2.getAssistant(),foundEvents.get(1).getAssistant());
            assertEquals(event2.getAbsent(),foundEvents.get(1).getAbsent());
            assertEquals(event2.getDuration(),foundEvents.get(1).getDuration());
            assertEquals(event2.getDescription(),foundEvents.get(1).getDescription());
            assertFalse(foundEvents.get(1).isCancelation());

            //Check if InputValidationException is thrown if date2 < date1
            assertThrows(InputValidationException.class, () -> eventService.find("Event", date2, date1));

            //Check if InputValidationException is thrown if date1 < localdate
            assertThrows(InputValidationException.class, () -> eventService.find("Event", date1,
                    LocalDateTime.now().withNano(0)));

        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        } finally { //Delete all events
            for (Event event : events) {
                removeEvent(event.getEventId());
            }
        }
    }


    //...Func-3.......................................................................................................//

    @Test
    public void testFindNonExistentEvent(){
        assertThrows(InstanceNotFoundException.class, () -> eventService.findEvent(NON_EXISTING_ID));
    }

    //...Func-4.......................................................................................................//

    @Test
    public void testAnswerEvent() throws InputValidationException, InstanceNotFoundException,
            EventCelebrationException, AlreadyAnsweredException, CanceledEventException{
        Event evento = createEvent(getValidEvent());
        Answer answeredEventt, answeredEventf;
        Answer foundAnswer = null;

        try {
            //Respuesta afirmativa....................................................................................//
            LocalDateTime beforeCreationDate = LocalDateTime.now().withNano(0);

            answeredEventt = eventService.answerEvent(USER_EMAIL, evento.getEventId(), true);

            LocalDateTime afterCreationDate = LocalDateTime.now().withNano(0);

            foundAnswer = findAnswer(answeredEventt.getAnswerId());

            Event foundEvent = eventService.findEvent(evento.getEventId());

            assertEquals(answeredEventt, foundAnswer);
            assertEquals(evento.getEventId(), foundAnswer.getEventId());
            assertEquals(USER_EMAIL, foundAnswer.getEmail());
            assertTrue(foundAnswer.getAssistance());
            assertEquals(1, foundEvent.getAssistant());
            assertEquals(0, foundEvent.getAbsent());
            assertTrue((foundAnswer.getAnswerDate().compareTo(beforeCreationDate) >= 0)
                    && (foundAnswer.getAnswerDate().compareTo(afterCreationDate)) <= 0);

            //Respuesta negativa......................................................................................//

            beforeCreationDate = LocalDateTime.now().withNano(0);

            answeredEventf = eventService.answerEvent("usuario@email.com", evento.getEventId(), false);

            afterCreationDate = LocalDateTime.now().withNano(0);

            foundAnswer = findAnswer(answeredEventf.getAnswerId());

            foundEvent = eventService.findEvent(evento.getEventId());

            assertEquals(answeredEventf, foundAnswer);
            assertEquals(evento.getEventId(), foundAnswer.getEventId());
            assertEquals("usuario@email.com", foundAnswer.getEmail());
            assertFalse(foundAnswer.getAssistance());
            assertEquals(1, foundEvent.getAssistant());
            assertEquals(1, foundEvent.getAbsent());
            assertTrue((foundAnswer.getAnswerDate().compareTo(beforeCreationDate) >= 0)
                    && (foundAnswer.getAnswerDate().compareTo(afterCreationDate)) <= 0);

        }finally {
            if (foundAnswer != null)
                removeAnswer(foundAnswer.getAnswerId());
            removeEvent(evento.getEventId());
        }

    }

    @Test
    public void testAnswerWithNullEmail(){
        assertThrows(InputValidationException.class, () -> {
            Event evento = createEvent(getValidEvent());
            Answer ans = eventService.answerEvent(null, evento.getEventId(), false);
            removeEvent(evento.getEventId());
            removeAnswer(ans.getAnswerId());
        });
    }

    @Test
    public void testAnswerWithBlankEmail(){
        assertThrows(InputValidationException.class, () -> {
            Event evento =createEvent(getValidEvent());
            Answer ans = eventService.answerEvent("", evento.getEventId(), false);
            removeEvent(evento.getEventId());
            removeAnswer(ans.getAnswerId());
        });
    }

    @Test
    public void testAnswerNonExistantEvent(){
        assertThrows(InstanceNotFoundException.class, () -> {
            Answer ans = eventService.answerEvent(USER_EMAIL, NON_EXISTING_ID, false);
            removeAnswer(ans.getAnswerId());
        });
    }

    @Test
    public void testAnswerAlreadyAnsweredEvent(){
        assertThrows(AlreadyAnsweredException.class, () -> {
            Answer ans = createValidAnswer();
            Answer secondAns = eventService.answerEvent(ans.getEmail(), ans.getEventId(), true);
            removeEvent(ans.getEventId());
            removeAnswer(ans.getAnswerId());
            removeAnswer(secondAns.getAnswerId());
        });
    }

    @Test
    public void testAnswerCanceledEvent(){
        Event evento = createEvent(getValidEvent());
        evento.setCancelation(true);
        update(evento);

        assertThrows(CanceledEventException.class, () -> {
            Answer ans = eventService.answerEvent(USER_EMAIL, evento.getEventId(), false);
            removeAnswer(ans.getEventId());
        });
        removeEvent(evento.getEventId());
    }

    @Test
    public void testAnswerPassDeadline(){
        Event event = createEvent(getValidEvent());
        event.setEventDate(LocalDateTime.now().withNano(0).plusHours(3));
        update(event);

        // Too late to answer
        assertThrows(EventCelebrationException.class, () -> {
            Answer ans = eventService.answerEvent(USER_EMAIL, event.getEventId(), false);
            removeAnswer(ans.getAnswerId());
        });

        event.setEventDate(LocalDateTime.now().minusDays(1));
        update(event);

        //Already Celebrated
        assertThrows(EventCelebrationException.class, () -> {
            Answer ans = eventService.answerEvent(USER_EMAIL, event.getEventId(), false);
            removeAnswer(ans.getAnswerId());
        });

        removeEvent(event.getEventId());
    }


    //...Func-5.......................................................................................................//

    @Test
    public void testInvalidCancel(){
        Event evento = createEvent(getValidEvent());
        evento.setEventDate(LocalDateTime.now().withNano(0).withYear(2021));
        update(evento);

        // Already celebrated
        assertThrows(EventCelebrationException.class, () -> {eventService.cancelEvent(evento.getEventId());});

        evento.setEventDate(LocalDateTime.now().withNano(0).withYear(2024));
        evento.setCancelation(true);
        update(evento);

        // Already canceled:
        assertThrows(CanceledEventException.class, () -> eventService.cancelEvent(evento.getEventId()));

    }

    @Test
    public void testCancelEvent(){
        Event evento = createEvent(getValidEvent());
        evento.setEventDate(LocalDateTime.now().plusDays(7));

        try{
            Event foundEvent = eventService.findEvent(evento.getEventId());

            assertEquals(foundEvent.isCancelation(), evento.isCancelation());
            assertEquals(evento.getEventId(), foundEvent.getEventId());

        }catch (InstanceNotFoundException e){
            throw new RuntimeException(e);
        }finally {
            removeEvent(evento.getEventId());
        }
    }

    //...Func-6.......................................................................................................//

    @Test
    public void testFindAnswers() throws InputValidationException, InstanceNotFoundException, EventCelebrationException,
            AlreadyAnsweredException, CanceledEventException{
        Event evento = createEvent(getValidEvent());
        Event evento2 = createEvent(getValidEvent());
        Event evento3 = createEvent(getValidEvent());
        Event evento4 = createEvent(getValidEvent());
        Event evento5 = createEvent(getValidEvent());
        String person1 = USER_EMAIL;
        List<Answer> answeredEvent = new ArrayList<>();
        List<Answer> foundAnswers = new ArrayList<>();
        List<Answer> affirmativeAnswers;

        try{
            // Persona 1
            answeredEvent.add(eventService.answerEvent(person1, evento.getEventId(), true));
            answeredEvent.add(eventService.answerEvent(person1, evento2.getEventId(), false));
            answeredEvent.add(eventService.answerEvent(person1, evento3.getEventId(), true));
            answeredEvent.add(eventService.answerEvent(person1, evento4.getEventId(), false));
            answeredEvent.add(eventService.answerEvent(person1, evento5.getEventId(), true));

            foundAnswers = eventService.findAnswers(person1, false);
            affirmativeAnswers = eventService.findAnswers(person1, true);

            assertEquals(answeredEvent.size(), foundAnswers.size());
            assertEquals(affirmativeAnswers.size(), 3);
        }finally {
            for(Answer ans : foundAnswers){
                removeAnswer(ans.getAnswerId());
            }
            removeEvent(evento.getEventId());
            removeEvent(evento2.getEventId());
            removeEvent(evento3.getEventId());
            removeEvent(evento4.getEventId());
            removeEvent(evento5.getEventId());
        }
    }
}
