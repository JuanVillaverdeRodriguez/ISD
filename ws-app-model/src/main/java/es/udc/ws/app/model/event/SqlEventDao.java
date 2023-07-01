package es.udc.ws.app.model.event;


import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public interface SqlEventDao {

    Event create(Connection connection, Event event);
    List<Event> find(Connection connection, String keywords, LocalDateTime fecha_ini, LocalDateTime fecha_fin);
    Event findById(Connection connection, Long eventId) throws InstanceNotFoundException;
    void update(Connection connection, Event event) throws InstanceNotFoundException;
    void remove(Connection connection, Long eventId)throws InstanceNotFoundException;
}
