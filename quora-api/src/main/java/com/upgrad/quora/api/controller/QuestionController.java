package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    /**
     * This method accepts QuestionRequest and authorization token in the form of POST method and create the question
     * in the DB, if authorization is successful.
     * @param questionRequest
     * @param authorization
     * @return QuestionResponse json
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.POST, path="/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(
            final QuestionRequest questionRequest, @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException{

        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());

        QuestionEntity createdQuestion = questionBusinessService.createQuestion(questionEntity, authorization);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid())
                .status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.OK);
    }


    /**
     * Method accepts the authorization token and returns the list of all questions
     * @param authorization
     * @return list of questiondetailresponse
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions
            (@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException{
        List<QuestionEntity> allQuestions = questionBusinessService.getAllQuestions(authorization);

        List<QuestionDetailsResponse> questionDetailsResponseArrayList = new ArrayList<>();
        if(allQuestions != null){
            for (QuestionEntity questionEntity :allQuestions ) {
                QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(questionEntity.getUuid())
                        .content(questionEntity.getContent());
                questionDetailsResponseArrayList.add(questionDetailsResponse);
            }
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponseArrayList, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.PUT, path="/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(
            final QuestionEditRequest questionEditRequest,
            @PathVariable("questionId") String uuid,
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException{

        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(uuid);
        questionEntity.setContent(questionEditRequest.getContent());
        QuestionEntity updatedQuestion = questionBusinessService.editQuestionContent(questionEntity, authorization);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(updatedQuestion.getUuid())
                .status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

}
