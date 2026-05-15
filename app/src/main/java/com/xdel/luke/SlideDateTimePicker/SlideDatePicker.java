package com.xdel.luke.SlideDateTimePicker;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Date;

/**
 * Created by kean.ong on 11/07/2017.
 */
public class SlideDatePicker {

    public static final int HOLO_DARK = 1;
    public static final int HOLO_LIGHT = 2;

    private FragmentManager mFragmentManager;
    private SlideDateListener mListener;
    private Date mInitialDate;
    private Date mMinDate;
    private Date mMaxDate;
    private boolean mIsClientSpecified24HourTime;
    private boolean mIs24HourTime;
    private int mTheme;
    private int mIndicatorColor;

    /**
     * Creates a new instance of {@code SlideDatePicker}.
     *
     * @param fm  The {@code FragmentManager} from the calling activity that is used
     *            internally to show the {@code DialogFragment}.
     */
    public SlideDatePicker(FragmentManager fm)
    {
        // See if there are any DialogFragments from the FragmentManager
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(SlideDateDialogFragment.TAG_SLIDE_DATE_DIALOG_FRAGMENT);

        // Remove if found
        if (prev != null)
        {
            ft.remove(prev);
            ft.commit();
        }

        mFragmentManager = fm;
    }

    /**
     * <p>Sets the listener that is used to inform the client when
     * the user selects a new date and time.</p>
     *
     * <p>This must be called before {@link ##show()}.</p>
     *
     * @param listener
     */
    public void setListener(SlideDateListener listener)
    {
        mListener = listener;
    }

    /**
     * <p>Sets the initial date and time to display in the date
     * and time pickers.</p>
     *
     * <p>If this method is not called, the current date and time
     * will be displayed.</p>
     *
     * @param initialDate  the {@code Date} object used to determine the
     *                     initial date and time to display
     */
    public void setInitialDate(Date initialDate)
    {
        mInitialDate = initialDate;
    }

    /**
     * <p>Sets the minimum date that the DatePicker should show.</p>
     *
     * <p>This must be called before {@link #show()}.</p>
     *
     * @param minDate  the minimum selectable date for the DatePicker
     */
    public void setMinDate(Date minDate)
    {
        mMinDate = minDate;
    }

    /**
     * <p>Sets the maximum date that the DatePicker should show.</p>
     *
     * <p>This must be called before {@link #show()}.</p>
     *
     * @param maxDate  the maximum selectable date for the DatePicker
     */
    public void setMaxDate(Date maxDate)
    {
        mMaxDate = maxDate;
    }

    /**
     * Sets the theme of the dialog. If no theme is specified, it
     * defaults to holo light.
     *
     * @param theme  {@code SlideDatePicker.HOLO_DARK} for a dark theme, or
     *               {@code SlideDatePicker.HOLO_LIGHT} for a light theme
     */
    public void setTheme(int theme)
    {
        mTheme = theme;
    }

    /**
     * Sets the color of the underline for the currently selected tab.
     *
     * @param indicatorColor  the color of the selected tab's underline
     */
    public void setIndicatorColor(int indicatorColor)
    {
        mIndicatorColor = indicatorColor;
    }

    /**
     * Shows the dialog to the user. Make sure to call
     * {@link ##setListener()} before calling this.
     */
    public void show()
    {
        if (mListener == null)
        {
            throw new NullPointerException(
                    "Attempting to bind null listener to SlideDatePicker");
        }

        if (mInitialDate == null)
        {
            setInitialDate(new Date());
        }

        SlideDateDialogFragment dialogFragment =
                SlideDateDialogFragment.newInstance(
                        mListener,
                        mInitialDate,
                        mMinDate,
                        mMaxDate,
                        mTheme,
                        mIndicatorColor);

        dialogFragment.show(mFragmentManager,
                SlideDateDialogFragment.TAG_SLIDE_DATE_DIALOG_FRAGMENT);
    }

    /*
     * The following implements the builder API to simplify
     * creation and display of the dialog.
     */
    public static class Builder
    {
        // Required
        private FragmentManager fm;
        private SlideDateListener listener;

        // Optional
        private Date initialDate;
        private Date minDate;
        private Date maxDate;
        private boolean isClientSpecified24HourTime;
        private boolean is24HourTime;
        private int theme;
        private int indicatorColor;

        public Builder(FragmentManager fm)
        {
            this.fm = fm;
        }

        /**
         * @see SlideDatePicker##setListener(SlideDateListener)
         */
        public Builder setListener(SlideDateListener listener)
        {
            this.listener = listener;
            return this;
        }

        /**
         * @see SlideDatePicker#setInitialDate(Date)
         */
        public Builder setInitialDate(Date initialDate)
        {
            this.initialDate = initialDate;
            return this;
        }

        /**
         * @see SlideDatePicker#setMinDate(Date)
         */
        public Builder setMinDate(Date minDate)
        {
            this.minDate = minDate;
            return this;
        }

        /**
         * @see SlideDatePicker#setMaxDate(Date)
         */
        public Builder setMaxDate(Date maxDate)
        {
            this.maxDate = maxDate;
            return this;
        }

        /**
         * @see SlideDatePicker#setTheme(int)
         */
        public Builder setTheme(int theme)
        {
            this.theme = theme;
            return this;
        }

        /**
         * @see SlideDatePicker#setIndicatorColor(int)
         */
        public Builder setIndicatorColor(int indicatorColor)
        {
            this.indicatorColor = indicatorColor;
            return this;
        }

        /**
         * <p>Build and return a {@code SlideDatePicker} object based on the previously
         * supplied parameters.</p>
         *
         * <p>You should call {@link #show()} immediately after this.</p>
         *
         * @return
         */
        public SlideDatePicker build()
        {
            SlideDatePicker picker = new SlideDatePicker(fm);
            picker.setListener(listener);
            picker.setInitialDate(initialDate);
            picker.setMinDate(minDate);
            picker.setMaxDate(maxDate);
            picker.setTheme(theme);
            picker.setIndicatorColor(indicatorColor);

            return picker;
        }
    }


}
