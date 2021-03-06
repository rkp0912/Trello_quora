package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Creates a new question entity record.
     * @param questionEntity
     * @return questionentity object.
     */
    public QuestionEntity createQuestion(final QuestionEntity questionEntity){
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    /**
     * Method returns the list of all records in the question entity
     * @return list of Question entity
     */
    public List<QuestionEntity> getAllQuestions(){
        try {
            return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Fetches the Question Entity record from the DB for a uuid
     * @param uuid
     * @return
     */
    public QuestionEntity getQuestionByUuid(final String uuid){
        try {
            return entityManager.createNamedQuery("getQuestionByUuid", QuestionEntity.class).setParameter("uuid", uuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method updates the existing QuestionEntity record in the DB
     * @param updatedQuestion
     */
    public void updateQuestion(final QuestionEntity updatedQuestion){
        entityManager.merge(updatedQuestion);
    }

    /**
     * Method deletes the Question Entity record from the DB
     * @param deleteQuestion
     */
    public void deleteQuestion(final QuestionEntity deleteQuestion){
        entityManager.remove(deleteQuestion);
    }

    /**
     * Method returns the list of Question Entity records for a given user uuid
     * @param user
     * @return List of QuestionEntity
     */
    public List<QuestionEntity> getQuestionsByUser(final UserEntity user){
        try {
            return entityManager.createNamedQuery("getQuestionsByUserId", QuestionEntity.class)
                    .setParameter("user", user)
                    .getResultList();
        } catch (NoResultException nre) {
            return null;
        }

    }

}
