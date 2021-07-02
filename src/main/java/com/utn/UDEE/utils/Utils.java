package com.utn.UDEE.utils;

import com.utn.UDEE.exception.AccessNotAllowedException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.exception.SinceUntilException;
import com.utn.UDEE.models.User;
import com.utn.UDEE.models.UserType;

import java.time.LocalDateTime;

public class Utils {

    public static void checkSinceUntil(LocalDateTime since, LocalDateTime until) throws SinceUntilException {
        if(since.isAfter(until)) {
            throw new SinceUntilException("'Since' cannot be higher than 'Until'");
        }
    }

    public static void userPermissionCheck(User queryUser, User user) throws ResourceDoesNotExistException, AccessNotAllowedException {
        if(queryUser.getId().equals(user.getId()) || queryUser.getUserType().equals(UserType.EMPLOYEE)) {
            if(!user.getUserType().equals(UserType.CLIENT)) {
                throw new ResourceDoesNotExistException (String.format("The client with id %s ",user.getId()," do not exists"));
            }
        } else {
            throw new AccessNotAllowedException("You have not access to this resource");
        }
    }



}

