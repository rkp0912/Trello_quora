package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

}
