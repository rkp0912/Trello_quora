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
     * Gets the user from user table based on PrimaryKey which is id.
     * @param id
     * @return user entity object
     */
    public UserEntity getUserByPk(final Integer id){
        try {
            return entityManager.createNamedQuery("userByPk", UserEntity.class).setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException nre) {
           return null;
        }
    }


    /**
     * Delete the userEntity
     * @param userEntity
     */
    public void deleteUser(final UserEntity userEntity){
        entityManager.remove(userEntity);
    }


}
