package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

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

}
