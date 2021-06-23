package com.utn.UDEE.utils;

import com.utn.UDEE.exception.SinceUntilException;

import java.time.LocalDateTime;

public class Utils {

    public static void checkSinceUntil(LocalDateTime since, LocalDateTime until) throws SinceUntilException {
        if(since.isBefore(until)) {
            throw new SinceUntilException("'Since' cannot be higher than 'Until'");
        }
    }

}