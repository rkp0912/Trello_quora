package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonBusinessService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthDao userAuthDao;

    /**
     * Check the access_token exists and it is valid. If gets the user based on the UUID and return the user.
     * @param userId
     * @param authorizationToken
     * @return Returns the UserEntity
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    public UserEntity getUserByUserId(final String userId, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException{
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuth(authorizationToken);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else{
            if(userAuthEntity.getLogoutAt() != null){
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
            } else{
                UserEntity userEntity = userDao.getUserByUuid(userId);
                if(userEntity == null)
                    throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
                else
                    return userEntity;
            }
        }
    }

}
