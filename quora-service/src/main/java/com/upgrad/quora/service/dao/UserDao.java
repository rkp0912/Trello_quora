package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
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
     * Creates a new entry in the user table
     * @param userEntity
     * @return
     */
    public UserEntity createUser(final UserEntity userEntity){
        entityManager.persist(userEntity);
        return userEntity;
    }

    /**
     * Gets the user from the user table using uuid
     * @param uuid
     * @return
     */
    public UserEntity getUserByUuid(final String uuid){
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", uuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


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
