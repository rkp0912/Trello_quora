package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessService {
    @Autowired
    private UserDao userDao;

    /**
     * Checks if the autherization token is valid and role of the user is admin.
     * Find the user with the provided userid, if user exists, delete the user from DB
     * @param userId
     * @param authorizationToken
     * @return Returns UserEntity Object on success.
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity userDelete(final String userId, final String authorizationToken)
            throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else{
            if(userAuthEntity.getLogoutAt() != null){
                throw new AuthorizationFailedException("ATHR-002","User is signed out.");
            } else{

                if(userAuthEntity.getUser().getRole().equals("nonadmin")){
                    throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
                }

                UserEntity userEntity = userDao.getUserByUuid(userId);
                if(userEntity == null)
                    throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
                else
                    userDao.deleteUser(userEntity);
                    return userEntity;
            }
        }

    }
}
