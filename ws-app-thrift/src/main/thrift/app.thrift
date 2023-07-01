namespace java es.udc.ws.app.thrift

struct ThriftEventDto {
    1: i64 eventId;
    2: string eventName;
    3: string description;
    4: i16 duration;
    5: string fechaCelebracion;
    6: i16 answers;
    7: i16 assistants;
    8: bool cancelation;
}

exception ThriftInputValidationException {
    1: string message
}

service ThriftAppService {

    i64 addEvent(1: ThriftEventDto eventDto) throws (1: ThriftInputValidationException e)

    list<ThriftEventDto> find(1: string keywords, 2: string endDate) throws (1: ThriftInputValidationException e)
}