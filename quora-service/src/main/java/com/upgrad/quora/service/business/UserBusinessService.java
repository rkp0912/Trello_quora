package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserBusinessService {

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

    /**
     * This method registers the user if username and password already present in the DB
     * @param userEntity
     * @return
     * @throws SignUpRestrictedException
     */
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

    /**
     * Method check if the user is present in the DB, if so a token will be generated. The token will be saved
     * as well as sent to user for further authentication. Otherwise throws an exception.
     * @param username
     * @param password
     * @return
     * @throws AuthenticationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate(final String username, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.getUserByUserName(username);
        if(userEntity == null){
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }
        final String encryptedPassword = passwordCryptographyProvider.encrypt(password, userEntity.getSalt());
        if(encryptedPassword.equals(userEntity.getPassword())){
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuthEntity = new UserAuthEntity();
            userAuthEntity.setUuid(UUID.randomUUID().toString());
            userAuthEntity.setUser(userEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(),now, expiresAt));
            userAuthEntity.setLoginAt(now);
            userAuthEntity.setExpiresAt(expiresAt);
            userDao.createUserAuth(userAuthEntity);

            return userAuthEntity;
        } else {
            throw new AuthenticationFailedException("ATH-002","Password failed");
        }

    }


}
