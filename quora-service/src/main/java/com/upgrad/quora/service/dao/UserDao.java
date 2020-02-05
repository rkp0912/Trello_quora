package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Reads the user from DB based on username
     * @param userName
     * @return
     */
    public  UserEntity getUserByUserName(final String userName){
        try {
            return entityManager.createNamedQuery("userByUserName", UserEntity.class).setParameter("userName", userName)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    /**
     * Reads the user from DB based on email
     * @param email
     * @return
     */
    public UserEntity getUserByEmail(final String email){
        try{
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email)
                    .getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    /**
     * Creates a new entry in the DB
     * @param userEntity
     * @return
     */
    public UserEntity createUser(final UserEntity userEntity){
        entityManager.persist(userEntity);
        return userEntity;
    }


}