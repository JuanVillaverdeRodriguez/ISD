package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.answer.Answer;

import java.util.ArrayList;
import java.util.List;

public class AnswerToRestAnswerDtoConversor {
    public static List<RestAnswerDto> toRestAnswersDtos(List<Answer> answers){
        List<RestAnswerDto> answersDtos = new ArrayList<>(answers.size());
        for(int i=0; i < answers.size(); i++){
            Answer answer = answers.get(i);
            answersDtos.add(toRestAnswerDto(answer));
        }
        return answersDtos;
    }

    public static RestAnswerDto toRestAnswerDto(Answer answer){
        return new RestAnswerDto(answer.getAnswerId(), answer.getEventId(), answer.getEmail(), answer.getAssistance());
    }

    public static Answer toAnswer(RestAnswerDto answer){
        return new Answer(answer.getAnswerId(), answer.getEventId(), answer.getEmail(), answer.getAssistance());
    }
}
