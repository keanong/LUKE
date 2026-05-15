package com.xdel.luke.SlideDateTimePicker;

import java.util.Date;

/**
 * Created by kean.ong on 11/07/2017.
 */
public abstract class SlideDateListener {
    /**
     * Informs the client when the user presses "OK"
     * and selects a date and time.
     *
     * @param date  The {@code Date} object that contains the date
     *              and time that the user has selected.
     */
    public abstract void onDateSet(Date date);

    /**
     * Informs the client when the user cancels the
     * dialog by pressing Cancel, touching outside
     * the dialog or pressing the Back button.
     * This override is optional.
     */
    public void onDateCancel()
    {

    }
}
