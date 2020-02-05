package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    /**
     * Gets the database entry based on username
     * @param userName
     * @return
     * @throws SignUpRestrictedException
     */
    public UserEntity getUserByUserName(final String userName) throws SignUpRestrictedException{
        return userDao.getUserByUserName(userName);
    }


    /**
     * Gets the database entry based on email
     * @param email
     * @return
     * @throws SignUpRestrictedException
     */
    public UserEntity getUserByEmail(final String email) throws SignUpRestrictedException {
         return userDao.getUserByEmail(email);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException{
        //Step1: validate username exits in the database. If exists throw SignUpRestrictedException with username already exists
        if(userDao.getUserByUserName(userEntity.getUserName()) != null){
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }
        // validate email exists in the database, if exists throw SignUpRestrictedException with email already exists
        if(userDao.getUserByEmail(userEntity.getEmail()) != null){
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        //if username and email are not taken, persist the user in the database.

        //Encrypt the password
        String password = userEntity.getPassword();
        if(password == null){
            password = "password";
        }
        String[] encryptedPassword = passwordCryptographyProvider.encrypt(password);
        userEntity.setSalt(encryptedPassword[0]);
        userEntity.setPassword(encryptedPassword[1]);

        //persist in the database
        return  userDao.createUser(userEntity);
    }

}
