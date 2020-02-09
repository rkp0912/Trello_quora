package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * Method returns all questions in the DB, if the authorization token valid.
     * @param authorizationToken
     * @return List of Question Entity
     * @throws AuthorizationFailedException
     */
    public List<QuestionEntity> getAllQuestions(final String authorizationToken) throws AuthorizationFailedException{
        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else{
            if(userAuthEntity.getLogoutAt() != null){
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions");
            } else{
                return questionDao.getAllQuestions();
            }
        }
    }


    /**
     * Method checks if the authorization token is valid, checks if the question with uuid exits.
     * If success then update the question details in the DB
     * @param updatedQuestion
     * @param authorizationToken
     * @return
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(
            QuestionEntity updatedQuestion, final String authorizationToken)
            throws AuthorizationFailedException, InvalidQuestionException{

        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else{
            if(userAuthEntity.getLogoutAt() != null){
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to edit the question");
            } else{
                    QuestionEntity questionEntity = questionDao.getQuestionByUuid(updatedQuestion.getUuid());
                    if(questionEntity == null){
                        throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
                    }
                    if(questionEntity.getUser().getId() == userAuthEntity.getUser().getId()){
                        questionEntity.setContent(updatedQuestion.getContent());
                        questionDao.updateQuestion(questionEntity);
                        return questionEntity;
                    } else {
                        throw new AuthorizationFailedException("ATHR-003","Only the question owner can edit the question");
                    }
            }
        }
    }

    /**
     * Method deletes the question from the DB, if the authorization token belongs to the question owner or role as admin
     * and question exists in the DB
     * @param questionUuid
     * @param authorizationToken
     * @return
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion (
            final String questionUuid, final String authorizationToken)
            throws AuthorizationFailedException, InvalidQuestionException{

        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else{
            if(userAuthEntity.getLogoutAt() != null){
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to delete a question");
            } else{
                QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionUuid);
                if(questionEntity == null){
                    throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
                }
                //if the authorized user is the question owner or his role is administrator, then delete the question
                if(questionEntity.getUser().getId() == userAuthEntity.getUser().getId() ||
                        userAuthEntity.getUser().getRole().equals("admin")){
                    questionDao.deleteQuestion(questionEntity);
                    return questionEntity;
                } else {
                    throw new AuthorizationFailedException("ATHR-003","Only the question owner can edit the question");
                }
            }
        }
    }


    /**
     * Method returns list of all questions of a specific user if user id and  authorization token are valid
     * @param uuidOfUser
     * @param authorizationToken
     * @return List of questionEntity
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    public List<QuestionEntity> getAllQuestionsByUser(final String uuidOfUser, final String authorizationToken)
        throws AuthorizationFailedException, UserNotFoundException{
        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else{
            if(userAuthEntity.getLogoutAt() != null){
                throw new AuthorizationFailedException(
                        "ATHR-002","User is signed out.Sign in first to get all questions posted by a specific user");
            } else{
                    UserEntity userEntity = userDao.getUserByUuid(uuidOfUser);
                    if(userEntity == null){
                        throw new UserNotFoundException("USR-001",
                                "User with entered uuid whose question details are to be seen does not exist");
                    } else {
                        return questionDao.getQuestionsByUser(userEntity);
                    }
            }
        }
    }
}
