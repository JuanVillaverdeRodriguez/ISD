package es.udc.ws.app.client.ui;

import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.ClientEventServiceFactory;
import es.udc.ws.app.client.service.dto.ClientAnswerDto;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.exceptions.ClientAlreadyAnsweredException;
import es.udc.ws.app.client.service.exceptions.ClientCanceledEventException;
import es.udc.ws.app.client.service.exceptions.ClientEventCelebrationException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AppServiceClient {
    public static void main(String[] args) {

        if(args.length == 0) {
            ayudaysalir();
        }
        ClientEventService clientEventService = ClientEventServiceFactory.getService();
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if("-a".equalsIgnoreCase(args[0])) {

            validateArgs(args, 5, null);

            try {
                Long eventId = clientEventService.addEvent(new ClientEventDto(
                        args[1], args[2], LocalDateTime.parse(args[3]), LocalDateTime.parse(args[4])
                ));

                System.out.println("Event " + eventId + " created sucessfully");

            } catch (NumberFormatException | InputValidationException | DateTimeParseException ex) {
                ex.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if ("-d".equalsIgnoreCase(args[0])) {

            if(args.length == 2 || args.length == 3){
                boolean keyw = args.length == 3;
                try {
                    List<ClientEventDto> events;
                    if (args.length == 2){
                        events = clientEventService.find(null, LocalDateTime.parse(args[1]));
                    } else {
                        events = clientEventService.find(args[2], LocalDateTime.parse(args[1]));
                    }
                    System.out.println("Found " + events.size() +
                            " event(s) from today to '" + args[1] + "'" + (keyw? " with keywords '" + args[2] + "'":""));
                    for (int i = 0; i < events.size(); i++) {
                        ClientEventDto eventDto = events.get(i);
                        printEvent(eventDto);
                    }
                } catch (InputValidationException | DateTimeParseException e) {
                    e.printStackTrace(System.err);
                } catch (Exception e){
                    e.printStackTrace(System.err);
                }
            } else {
                ayudaysalir();
            }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if ("-i".equalsIgnoreCase(args[0])){

            validateArgs(args, 2, new int[] {1});

            try{
                ClientEventDto eventDto = clientEventService.findByID(Long.parseLong(args[1]));
                System.out.println("Found event with ID " + eventDto.getEventId());
                printEvent(eventDto);
            } catch (NumberFormatException | InputValidationException ex) {
                ex.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if ("-r".equalsIgnoreCase(args[0])){

            validateArgs(args, 4, new int[] {2});
            try{
                ClientAnswerDto answerDto = clientEventService.answerEvent(args[1], Long.parseLong(args[2]),
                        Boolean.parseBoolean(args[3]));
                System.out.println(args[1] + (Boolean.parseBoolean(args[3]) ? " will assist to event with ID ":
                        " will not assist to event with ID ") + args[2]);
                System.out.println("AnswerID: " + answerDto.getAnswerId() +
                        ", Email: " + answerDto.getEmail() +
                        ", EventID: " + answerDto.getEventId() +
                        ", Assistance: " + answerDto.isAssistance()
                );

            } catch (InputValidationException | ClientEventCelebrationException | NumberFormatException
                    | ClientCanceledEventException | ClientAlreadyAnsweredException | InstanceNotFoundException e) {
                e.printStackTrace(System.err);
            }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if ("-c".equalsIgnoreCase(args[0])){

            validateArgs(args, 2, new int[] {1});
            try{
                clientEventService.cancelEvent(Long.parseLong(args[1]));
                System.out.println("Event with ID " + args[1] + " canceled successfully");
            } catch (InstanceNotFoundException | ClientEventCelebrationException | ClientCanceledEventException e){
                e.printStackTrace(System.err);
            } catch (Exception e){
                e.printStackTrace(System.err);
            }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if ("-f".equalsIgnoreCase(args[0])) {

            validateArgs(args, 3, null);
            try {
                List<ClientAnswerDto> answers = clientEventService.findAnswers(args[1], Boolean.parseBoolean(args[2]));
                System.out.println("Found " + answers.size() +
                        (Boolean.parseBoolean(args[2])? " affirmative": "")+ " answer(s) from " + args[1]);
                for (int i = 0; i < answers.size(); i++){
                    ClientAnswerDto answerDto = answers.get(i);
                    System.out.println("AnswerID: " + answerDto.getAnswerId() +
                            ", Email: " + answerDto.getEmail() +
                            ", EventID: " + answerDto.getEventId() +
                            ", Assistance: " + answerDto.isAssistance());
                }
            } catch (InputValidationException e){
                e.printStackTrace(System.err);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }

    private static void printEvent(ClientEventDto eventDto){
        System.out.println("Id: " + eventDto.getEventId() +
                ", EventName: " + eventDto.getEventName() +
                ", Description: " + eventDto.getDescription() +
                ", CelebrationDate: " + eventDto.getCelDate().format(DateTimeFormatter.ISO_DATE_TIME) +
                ", EndDate: " + eventDto.getEndDate().format(DateTimeFormatter.ISO_DATE_TIME) +
                ", Answers: " + eventDto.getAnswers() +
                ", Assistants: " + eventDto.getAffirmativeAnswers() +
                ", Cancellation: " + eventDto.isCancellation());
    }

    private static void validateArgs(String[] args, int expectedArgs, int[] numericArguments) {
        if(expectedArgs != args.length) {
            ayudaysalir();
        } if (numericArguments != null){
            for(int i = 0 ; i< numericArguments.length ; i++) {
                int position = numericArguments[i];
                try {
                    Long.parseLong(args[position]);
                } catch(NumberFormatException n) {
                    ayudaysalir();
                }
            }
        }
    }

    public static void ayudaysalir() {
        ayuda();
        System.exit(-1);
    }

    public static void ayuda() {
        System.err.println("Uso:\n" +
                "    [add_event]           EventServiceClient -a <eventName> <description> <celDate> <endDate> \n" +
                "    [find_event_by_date]  EventServiceClient -d <date> [<keywords>]\n" +
                "    [find_event_by_ID]    EventServiceClient -i <ID>\n" +
                "    [answer_event]        EventServiceClient -r <email> <eventId> <assistance>\n" +
                "    [cancel_event]        EventServiceClient -c <ID>\n" +
                "    [find_answers]        EventServiceClient -f <email> <assistance>\n");
    }
}