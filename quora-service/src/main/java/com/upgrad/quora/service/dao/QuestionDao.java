package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
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

}
