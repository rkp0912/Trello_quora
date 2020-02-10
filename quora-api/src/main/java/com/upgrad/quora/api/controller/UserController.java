package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserBusinessService userBusinessService;


    /**
     * This method is used for registering a user. The information is passed as Http POST method
     * @param signupUserRequest
     * @return SignupUserResponse JSON and HttpStatus.OK
     * @throws SignUpRestrictedException
     */
    @RequestMapping(method = RequestMethod.POST, path="/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(
            final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {

        final UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");
        userEntity.setContactNumber(signupUserRequest.getContactNumber());

        final UserEntity createdUserEntity = userBusinessService.signup(userEntity);
        SignupUserResponse signupUserResponse = new SignupUserResponse().id(createdUserEntity.getUuid())
                .status("USER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupUserResponse>(signupUserResponse, HttpStatus.OK);
    }


    /**
     * This method accepts username and password as "Basic [<username:password> encoded as base64 ]" as Http POST method
     * return access-token on successful validation otherwise throws Authenticationfailed exception
     * @param authentication
     * @return SigninResponse and  HttpStatus.OK
     * @throws AuthenticationFailedException
     */
    @RequestMapping(method=RequestMethod.POST, path="/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authentication)
            throws AuthenticationFailedException {
        byte[] decode = Base64.getDecoder().decode(authentication.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        UserAuthEntity userAuthEntity = userBusinessService.authenticate(decodedArray[0], decodedArray[1]);
        UserEntity user = userAuthEntity.getUser();

        SigninResponse signinResponse = new SigninResponse();
        signinResponse.setId(user.getUuid());
        signinResponse.setMessage("SIGNED IN SUCCESSFULLY");


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("access-token", userAuthEntity.getAccessToken());

        return  new ResponseEntity<SigninResponse>(signinResponse, httpHeaders, HttpStatus.OK);
    }

    /**
     * Method accepts the access_token as http POST method and logout the user, if token is valid
     * @param authorizationToken
     * @return SignoutResponse JSON and HttpStatus.OK
     * @throws SignOutRestrictedException
     */
    @RequestMapping(method = RequestMethod.POST, path="/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String authorizationToken) throws SignOutRestrictedException{
        final UserEntity userEntity = userBusinessService.logoutUser(authorizationToken);
        SignoutResponse signoutResponse = new SignoutResponse().id(userEntity.getUuid())
                .message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }
}
