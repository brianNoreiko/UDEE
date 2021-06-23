package com.utn.UDEE.utils;

import com.utn.UDEE.exception.SinceUntilException;

import java.time.LocalDateTime;

public class Utils {

    public static void checkSinceUntil(LocalDateTime since, LocalDateTime until) throws SinceUntilException {
        if(since.isAfter(until)) {
            throw new SinceUntilException("'Since' cannot be higher than 'Until'");
        }
    }

    public static void checkSinceUntilTime(LocalDateTime from, LocalDateTime to) throws SinceUntilException {
        if(from.isAfter(to)) {
            throw new SinceUntilException("Time is wrong");
        }
    }

}