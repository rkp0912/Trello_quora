package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class AnswerDao {

    @Autowired
    private EntityManager entityManager;

    /**
     * Method saves the answer entity record in the DB
     * @param answerEntity
     * @return
     */
    public AnswerEntity createAnswer(final AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
        return answerEntity;
    }

}
