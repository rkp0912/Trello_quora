package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class AnswerBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AnswerDao answerDao;

    /**
     * Method accepts the question uuid, answer entity and authorization token.
     * If the question and authorization token are valid, the answer is persisted in the DB
     * @param questionUuid
     * @param answerEntity
     * @param authorizationToken
     * @return AnswerEntity
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(
                final String questionUuid, AnswerEntity answerEntity, final String authorizationToken)
            throws AuthorizationFailedException, InvalidQuestionException{
        //Check if question exists
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionUuid);
        if(questionEntity == null){
            throw new InvalidQuestionException("QUES-001","The question entered is invalid");
        } else {
            //Is authorization token valid
            UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);
            if(userAuthEntity == null){
                throw new AuthorizationFailedException("ATHR-001","User has not signed in");
            } else {
                if(userAuthEntity.getLogoutAt() != null){
                    throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post an answer");
                } else {
                    //Add user and question to the answer
                    answerEntity.setQuestion(questionEntity);
                    answerEntity.setUser(userAuthEntity.getUser());
                    //Persist in the DB
                    return  answerDao.createAnswer(answerEntity);
                }
            }
        }

    }

    /**
     * Method updates the answer if the answer exists and the authorization token provided belongs to answer owner
     * @param answerId
     * @param answerEntity
     * @param authorizationToken
     * @return Answer Entity
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswerContent(
            final String answerId, AnswerEntity answerEntity, final String authorizationToken)
        throws AuthorizationFailedException, AnswerNotFoundException{
        //Check if the authorization token is valid
        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else {
            if(userAuthEntity.getLogoutAt() != null){
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to edit an answer");
            } else {
                //Get answer by UUID
                AnswerEntity answerTobeUpdated = answerDao.getAnswerByUuid(answerId);
                if(answerTobeUpdated == null){
                    throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
                } else {
                    //Only answer owner can update the answer.
                    if(answerTobeUpdated.getUser().getId() == userAuthEntity.getUser().getId()){
                        answerTobeUpdated.setAnswer(answerEntity.getAnswer());
                        answerTobeUpdated.setDate(ZonedDateTime.now());
                        answerDao.updateAnswer(answerTobeUpdated);
                        return answerTobeUpdated;
                    } else{
                        throw new AuthorizationFailedException("ATHR-003","Only the answer owner can edit the answer");
                    }
                }
            }
        }
    }


}