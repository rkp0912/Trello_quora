package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommonController {

    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     *This method accepts the User UUID and authorization token as Http GET method and returns the user details
     * @param userId
     * @param authorization
     * @return UserDetailsResponse JSON and HttpStatus.OK
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET, path="/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse>userProfile(@PathVariable("userId") final String userId,
                                                      @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        final UserEntity userEntity = commonBusinessService.getUserByUserId(userId, authorization);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse()
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .userName(userEntity.getUserName())
                .emailAddress(userEntity.getEmail())
                .country(userEntity.getCountry())
                .aboutMe(userEntity.getAboutMe())
                .dob(userEntity.getDob())
                .contactNumber(userEntity.getContactNumber());

        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);

    }

}
