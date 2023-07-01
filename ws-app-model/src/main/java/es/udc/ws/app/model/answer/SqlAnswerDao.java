package es.udc.ws.app.model.answer;


import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.util.List;

public interface SqlAnswerDao {

    public Answer create(Connection con, Answer answer);
    public boolean alreadyAnswered(Connection con, String email, Long eventId);
    public List<Answer> findByEmail(Connection con, String email, boolean onlyAffirmative);
    public Answer find(Connection con, Long answerId) throws InstanceNotFoundException;
    public void remove(Connection con, Long ID) throws InstanceNotFoundException;

}
