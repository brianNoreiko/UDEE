package com.utn.UDEE.utils;

import com.utn.UDEE.exception.AccessUnauthorizedException;
import com.utn.UDEE.exception.SinceUntilException;
import com.utn.UDEE.exception.doesNotExist.UserDoesNotExist;
import com.utn.UDEE.models.User;
import com.utn.UDEE.models.UserType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Utils {

    public static void checkSinceUntil(LocalDate since, LocalDate until) throws SinceUntilException {
        if(since.isAfter(until)) {
            throw new SinceUntilException("'Since' cannot be higher than 'Until'");
        }
    }

    public static void checkSinceUntilTime(LocalDateTime from, LocalDateTime to) throws SinceUntilException {
        if(from.isAfter(to)) {
            throw new SinceUntilException("Time is wrong");
        }
    }

    public static void userPermissionCheck(User queryUser, User clientUser) throws UserDoesNotExist, AccessUnauthorizedException {
        if(queryUser.getIdUser().equals(clientUser.getIdUser()) || queryUser.getUserType().equals(UserType.EMPLOYEE)) {
            if(!clientUser.getUserType().equals(UserType.CLIENT)) {
                throw new UserDoesNotExist(String.format("The client with id %s ",clientUser.getIdUser()," do not exists"));
            }
        } else {
            throw new AccessUnauthorizedException("You have not access to this resource");
        }
    }

}