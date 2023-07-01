package es.udc.ws.app.model.event;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlEventDao implements SqlEventDao {

    protected AbstractSqlEventDao() {}

    //...Func-2.......................................................................................................//

    @Override
    public List<Event> find(Connection con, String keywords, LocalDateTime fecha_ini, LocalDateTime fecha_fin){

        String[] words = keywords != null ? keywords.split(" ") : null;
        String queryString = "SELECT eventId, eventName, duration, "
                + " description, eventDate, eventCreationDate, cancellation, assistant, absent" +
                " FROM Event WHERE eventDate BETWEEN ? AND ?";

        if (words != null && words.length > 0) {
            queryString += " AND";
            for (int i = 0; i < words.length; i++) {
                if (i > 0) {
                    queryString += " AND";
                }
                queryString += " LOWER(description) LIKE LOWER(?)";
            }
        }
        queryString += " ORDER BY eventName";

        try (PreparedStatement preparedStatement = con.prepareStatement(queryString)) {
            //System.out.println("DATA INICIIO" + Timestamp.valueOf(fecha_ini));
            preparedStatement.setString(1, "" + Timestamp.valueOf(fecha_ini) + "");
            preparedStatement.setString(2, "" + Timestamp.valueOf(fecha_fin) + "");


            if (words != null) {
                /* Fill "preparedStatement". */
                for (int i = 2; i-2 < words.length; i++) {
                    preparedStatement.setString(i + 1, "%" + words[i-2] + "%");
                }
            }

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            /* Read events. */
            List<Event> eventos = new ArrayList<Event>();

            while (resultSet.next()) {

                int i = 1;
                Long eventId = Long.valueOf(resultSet.getLong(i++));
                String eventName = resultSet.getString(i++);
                short duration = resultSet.getShort(i++);
                String description = resultSet.getString(i++);
                Timestamp eventDateAsTimestamp = resultSet.getTimestamp(i++);
                Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
                boolean cancelation = resultSet.getBoolean(i++);
                LocalDateTime eventDate = eventDateAsTimestamp.toLocalDateTime();
                LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();
                short assistant = resultSet.getShort(i++);
                short absent = resultSet.getShort(i++);

                eventos.add(new Event(eventId, eventName, description, duration, eventDate,
                        creationDate, cancelation, assistant, absent));

            }

            /* Return eventos. */
            return eventos;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //...Func-3.......................................................................................................//

    @Override
    public Event findById(Connection connection, Long eventId) throws InstanceNotFoundException {

        String queryString = "SELECT eventName, description, "
                + " duration, eventDate, eventCreationDate, cancellation, assistant, " +
                "absent FROM Event WHERE eventId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            int i = 1;
            preparedStatement.setLong(i++, eventId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(eventId,
                        Event.class.getName());
            }


            i = 1;
            String eventName = resultSet.getString(i++);
            String description = resultSet.getString(i++);
            short duration = resultSet.getShort(i++);
            Timestamp eventDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime eventDate = eventDateAsTimestamp.toLocalDateTime();
            Timestamp eventCreationDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime eventCreationDate = eventCreationDateAsTimestamp.toLocalDateTime();
            boolean cancelation = resultSet.getBoolean(i++);
            short assistant = resultSet.getShort(i++);
            short absent = resultSet.getShort(i++);

            return new Event(eventId, eventName, description, duration, eventDate, eventCreationDate,
                    cancelation, assistant, absent);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //...Func-4-5...................................................................................................//

    @Override
    public void update(Connection con, Event event) throws InstanceNotFoundException {

        /* Create "queryString". */
        String queryString = "UPDATE Event"
                + " SET eventName = ?, description = ?, duration = ?, "
                + "eventDate = ?, eventCreationDate = ?, cancellation = ?, assistant = ?, absent = ? WHERE eventId = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setString(i++, event.getEventName());
            preparedStatement.setString(i++, event.getDescription());
            preparedStatement.setShort(i++, event.getDuration());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getEventDate()));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getEventCreationDate()));
            preparedStatement.setBoolean(i++, event.isCancelation());
            preparedStatement.setShort(i++, event.getAssistant());
            preparedStatement.setShort(i++, event.getAbsent());
            preparedStatement.setLong(i++, event.getEventId());

            /* Execute query. */
            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new InstanceNotFoundException(event.getEventId(),
                        Event.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //...Extra........................................................................................................//

    @Override
    public void remove(Connection con, Long eventId) throws InstanceNotFoundException {

        /* Create "queryString". */
        String queryString = "DELETE FROM Event WHERE" + " eventId = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, eventId);

            /* Execute query. */
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(eventId,
                        Event.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

