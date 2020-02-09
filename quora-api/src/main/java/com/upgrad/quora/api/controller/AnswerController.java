package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;

    /**
     * This method accepts question id, answer request  and authorization token
     * On successful validation of question and authorization token, answer will saved and appropriate UUID and
     * success message will be returned with Httpstatus as ok
     * @param answerRequest
     * @param questionId
     * @param authorization
     * @return answerresponse json and httpstatus.ok
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @RequestMapping(method = RequestMethod.POST, path="/question/{questionId}/answer/create",
    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(
            final AnswerRequest answerRequest,
            @PathVariable("questionId") final String questionId,
            @RequestHeader("authorization") final String authorization)
        throws AuthorizationFailedException, InvalidQuestionException{

        final AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity.setDate(ZonedDateTime.now());
        final AnswerEntity createdAnswer = answerBusinessService.createAnswer(questionId, answerEntity, authorization);
        AnswerResponse answerResponse = new AnswerResponse()
                .id(createdAnswer.getUuid())
                .status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);
    }

}
