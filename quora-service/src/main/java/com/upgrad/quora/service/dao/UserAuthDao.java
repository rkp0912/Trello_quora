package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserAuthDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Creates a new entry in the user_auth table
     * @param userAuthEntity
     * @return
     */
    public UserAuthEntity createUserAuth(final UserAuthEntity userAuthEntity){
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    /**
     * Fetches the record from UserAuthEntity using access token
     * @param accessToken
     * @return
     */
    public UserAuthEntity getUserAuth(final String accessToken){
        try {
            return entityManager.createNamedQuery("userAuthByAccessToken", UserAuthEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Updates an existing UserAuthEntity record.
     * @param userAuthEntity
     */
    public void updateUserAuth(final UserAuthEntity userAuthEntity){
        entityManager.merge(userAuthEntity);
    }
}
