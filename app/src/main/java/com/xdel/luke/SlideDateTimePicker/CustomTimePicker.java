package com.xdel.luke.SlideDateTimePicker;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.xdel.luke.R;

/**
 * Created by kean.ong on 11/07/2017.
 */
public class CustomTimePicker extends TimePicker {

    public CustomTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        Class<?> idClass = null;
        Class<?> numberPickerClass = null;
        Field selectionDividerField = null;
        Field hourField = null;
        Field minuteField = null;
        Field amPmField = null;
        NumberPicker hourNumberPicker = null;
        NumberPicker minuteNumberPicker = null;
        NumberPicker amPmNumberPicker = null;

        try
        {
            // Create an instance of the id class
            idClass = Class.forName("com.android.internal.R$id");

            // Get the fields that store the resource IDs for the hour, minute and amPm NumberPickers
            hourField = idClass.getField("hour");
            minuteField = idClass.getField("minute");
            amPmField = idClass.getField("amPm");

            // Use the resource IDs to get references to the hour, minute and amPm NumberPickers
            hourNumberPicker = (NumberPicker) findViewById(hourField.getInt(null));
            minuteNumberPicker = (NumberPicker) findViewById(minuteField.getInt(null));
            amPmNumberPicker = (NumberPicker) findViewById(amPmField.getInt(null));

            numberPickerClass = Class.forName("android.widget.NumberPicker");

            // Set the value of the mSelectionDivider field in the hour, minute and amPm NumberPickers
            // to refer to our custom drawables
            selectionDividerField = numberPickerClass.getDeclaredField("mSelectionDivider");
            selectionDividerField.setAccessible(true);
            selectionDividerField.set(hourNumberPicker, getResources().getDrawable(R.drawable.selection_divider_9));
            selectionDividerField.set(minuteNumberPicker, getResources().getDrawable(R.drawable.selection_divider_9));
            selectionDividerField.set(amPmNumberPicker, getResources().getDrawable(R.drawable.selection_divider_9));
        }
        catch (ClassNotFoundException e)
        {

        }
        catch (NoSuchFieldException e)
        {

        }
        catch (IllegalAccessException e)
        {

        }
        catch (IllegalArgumentException e)
        {

        }
    }

}
