package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminController {

    @Autowired
    private AdminBusinessService adminBusinessService;

    /**
     * This method accepts the user uuid and the authorization token as Http DELETE method
     * and deletes the user if the authorization token  belongs to user having admin role.
     * @param userId
     * @param authorization
     * @return UserDeleteResponse JSON and HttpStatus.Ok
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    @RequestMapping(method = RequestMethod.DELETE, path="/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse>userDelete(
            @PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, UserNotFoundException{

        UserEntity userEntity = adminBusinessService.userDelete(userId, authorization);
        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(userEntity.getUuid())
                .status("USER SUCCESSFULLY DELETED");

        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);
    }

}
