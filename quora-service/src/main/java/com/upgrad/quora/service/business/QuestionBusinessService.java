package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao  questionDao;


    /**
     * Method validates if the token is valid and saves the question to the DB otherwise throws the exception
     * @param newQuestion
     * @param authorizationToken
     * @return
     * @throws AuthorizationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity newQuestion, final String authorizationToken)
            throws AuthorizationFailedException{

       UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else{
            if(userAuthEntity.getLogoutAt() != null){
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a question");
            } else{
                //Gets the logged-in user object
                UserEntity userEntity = userDao.getUserByPk(userAuthEntity.getUser().getId());
                //Set the user to the question and save
                newQuestion.setUser(userEntity);
                questionDao.createQuestion(newQuestion);
                return newQuestion;
            }
        }

    }



}
