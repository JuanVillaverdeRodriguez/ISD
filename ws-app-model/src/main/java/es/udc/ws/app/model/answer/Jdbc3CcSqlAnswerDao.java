package es.udc.ws.app.model.answer;

import java.sql.*;


public class Jdbc3CcSqlAnswerDao extends AbstractSqlAnswerDao{

    //...Func-4.......................................................................................................//

    @Override
    public Answer create(Connection con, Answer answer) {

        String queryString = "INSERT INTO Answer"
                + " (eventID, email, assistance, answerDate)"
                + " VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = con.prepareStatement(
                queryString, Statement.RETURN_GENERATED_KEYS)) {

            int i = 1;
            preparedStatement.setLong(i++, answer.getEventId());
            preparedStatement.setString(i++, answer.getEmail());
            preparedStatement.setBoolean(i++, answer.getAssistance());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(answer.getAnswerDate()));

            /* Execute query. */
            preparedStatement.executeUpdate();

            /* Get generated identifier. */
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()) {
                throw new SQLException(
                        "JDBC driver did not return generated key.");
            }
            Long answerID = resultSet.getLong(1);

            return new Answer(answerID, answer.getEventId(), answer.getEmail(), answer.getAssistance(), answer.getAnswerDate());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
