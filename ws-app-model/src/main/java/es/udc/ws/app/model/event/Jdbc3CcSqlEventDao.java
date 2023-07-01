package es.udc.ws.app.model.event;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class Jdbc3CcSqlEventDao extends AbstractSqlEventDao{

    //...Func-1.......................................................................................................//

    @Override
    public Event create(Connection connection, Event event) {

        String queryString = "INSERT INTO Event"
                + " (eventName, description, duration, eventDate, eventCreationDate, cancellation, assistant, absent)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                queryString, Statement.RETURN_GENERATED_KEYS)) {
            
            int i = 1;
            preparedStatement.setString(i++, event.getEventName());
            preparedStatement.setString(i++, event.getDescription());
            preparedStatement.setShort(i++, event.getDuration());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getEventDate()));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getEventCreationDate()));
            preparedStatement.setBoolean(i++, event.isCancelation());
            preparedStatement.setShort(i++, event.getAssistant());
            preparedStatement.setShort(i++, event.getAbsent());

            /* Execute query. */
            preparedStatement.executeUpdate();

            /* Get generated identifier. */
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()) {
                throw new SQLException(
                        "JDBC driver did not return generated key.");
            }
            Long eventId = resultSet.getLong(1);

            return new Event(eventId, event.getEventName(),
                    event.getDescription(), event.getDuration(),
                    event.getEventDate(), event.getEventCreationDate(),
                    event.isCancelation(), event.getAssistant(), event.getAbsent());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
