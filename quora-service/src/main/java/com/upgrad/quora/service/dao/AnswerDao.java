package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Repository
public class AnswerDao {

    @Autowired
    private EntityManager entityManager;

    /**
     * Method saves the answer entity record in the DB
     * @param answerEntity
     * @return Answer Entity
     */
    public AnswerEntity createAnswer(final AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    /**
     * Method get the answer entity record from DB using UUID
     * @param uuid
     * @return Answer entity
     */
    public AnswerEntity getAnswerByUuid(final String uuid){
        try {
            return entityManager.createNamedQuery("getAnswerByUuid", AnswerEntity.class).setParameter("uuid", uuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method updates the existing answer entity record in the DB
     * @param updatedAnswerEntity
     */
    public void updateAnswer(final AnswerEntity updatedAnswerEntity){
        entityManager.merge(updatedAnswerEntity);
    }

    /**
     * Method removes the answer entity record from the DB
     * @param deleteAnswer
     */
    public void deleteAnswer(final AnswerEntity deleteAnswer){
        entityManager.remove(deleteAnswer);
    }

    /**
     * Fetches the list of answers for a given question entity
     * @param questionEntity
     * @return List of AnswerEntity
     */
    public List<AnswerEntity> getAllAnswersToQuestion(final QuestionEntity questionEntity){
        try {
            return entityManager.createNamedQuery("getAllAnswerByQuestionUuid", AnswerEntity.class)
                    .setParameter("question", questionEntity)
                    .getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
