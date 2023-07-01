package es.udc.ws.app.model.answer;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlAnswerDao implements SqlAnswerDao{

    //...Func-4.......................................................................................................//

    public boolean alreadyAnswered(Connection con, String email, Long eventId) {

        String queryString = "SELECT answerId "
                + "FROM Answer WHERE email = ? AND eventId = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(queryString)) {

            int i = 1;
            preparedStatement.setString(i++, email);
            preparedStatement.setLong(i++, eventId);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //...Func-6.......................................................................................................//

    public List<Answer> findByEmail(Connection con, String email, boolean onlyAffirmative) {

        String queryString;
        if (!onlyAffirmative)
            queryString = "SELECT * "
                + " FROM Answer WHERE email = ?";
        else
            queryString = "SELECT * "
                    + " FROM Answer WHERE email = ? AND assistance = 1";

        try (PreparedStatement preparedStatement = con.prepareStatement(queryString)) {

            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Answer> answers = new ArrayList<>();
            ResultSetMetaData rsdm = resultSet.getMetaData();

            while(resultSet.next()){
                int z = 1;
                Long answerId = Long.valueOf(resultSet.getLong(z++));
                Long eventId = Long.valueOf(resultSet.getLong(z++));
                String foundEmail = resultSet.getString(z++);
                boolean assistance = resultSet.getBoolean(z++);
                Timestamp answerDateToTime = resultSet.getTimestamp(z++);
                LocalDateTime answerDate = answerDateToTime.toLocalDateTime();

                answers.add(new Answer(answerId, eventId, foundEmail, assistance, answerDate));
            }

            return answers;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //...Extra........................................................................................................//

    public Answer find(Connection con, Long answerId) throws InstanceNotFoundException {

        String queryString = "SELECT eventId, email, assistance, answerDate "
                + "FROM Answer WHERE answerId = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(queryString)) {

            int i = 1;
            preparedStatement.setLong(i++, answerId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(answerId,
                        Event.class.getName());
            }


            i = 1;
            Long eventId = resultSet.getLong(i++);
            String email = resultSet.getString(i++);
            boolean assistance = resultSet.getBoolean(i++);
            Timestamp answerDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime answerDate = answerDateAsTimestamp.toLocalDateTime();

            return new Answer(answerId, eventId, email, assistance, answerDate);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(Connection con, Long answerId) throws InstanceNotFoundException {
        /* Create "queryString". */
        String queryString = "DELETE FROM Answer WHERE" + " answerId = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, answerId);

            /* Execute query. */
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(answerId,
                        Event.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
