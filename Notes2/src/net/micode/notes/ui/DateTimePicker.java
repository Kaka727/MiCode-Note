/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.micode.notes.ui;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import net.micode.notes.R;


import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
/**
 * description：设置日期和时间的控件，完成对日期和时间的设置  lyf
 */
public class DateTimePicker extends FrameLayout {
    //继承页面布局控件类
    private static final boolean DEFAULT_ENABLE_STATE = true;
    //判断是否默认
    private static final int HOURS_IN_HALF_DAY = 12;
    private static final int HOURS_IN_ALL_DAY = 24;
    private static final int DAYS_IN_ALL_WEEK = 7;
    private static final int DATE_SPINNER_MIN_VAL = 0;
    private static final int DATE_SPINNER_MAX_VAL = DAYS_IN_ALL_WEEK - 1;
    private static final int HOUR_SPINNER_MIN_VAL_24_HOUR_VIEW = 0;
    private static final int HOUR_SPINNER_MAX_VAL_24_HOUR_VIEW = 23;
    private static final int HOUR_SPINNER_MIN_VAL_12_HOUR_VIEW = 1;
    private static final int HOUR_SPINNER_MAX_VAL_12_HOUR_VIEW = 12;
    private static final int MINUT_SPINNER_MIN_VAL = 0;
    private static final int MINUT_SPINNER_MAX_VAL = 59;
    private static final int AMPM_SPINNER_MIN_VAL = 0;
    private static final int AMPM_SPINNER_MAX_VAL = 1;
   //各种时间值的设定（半天小时数，全天小时数，一周天数，小时、分钟上下限，上午下午等） lyf
    private final NumberPicker mDateSpinner;
    private final NumberPicker mHourSpinner;
    private final NumberPicker mMinuteSpinner;
    private final NumberPicker mAmPmSpinner;
    //定义日期时间数组选择器下拉列表
    private Calendar mDate;
    //定义可获取日期时间的类
    private String[] mDateDisplayValues = new String[DAYS_IN_ALL_WEEK];
    //日期显示字符串值
    private boolean mIsAm;
    //是否上午
    private boolean mIs24HourView;
    //是否24小时制
    private boolean mIsEnabled = DEFAULT_ENABLE_STATE;
    //是否默认
    private boolean mInitialising;
    
    private OnDateTimeChangedListener mOnDateTimeChangedListener;
    //时间监听器接口
    // 事件监听器：当它们的值发生改变时，将会激发相应的事件处理方法
    private NumberPicker.OnValueChangeListener mOnDateChangedListener = new NumberPicker.OnValueChangeListener() {
    	//日期监听器接口
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            mDate.add(Calendar.DAY_OF_YEAR, newVal - oldVal);
            //重新计算时间
            updateDateControl();//更新日期操作
            onDateTimeChanged();
        }
    };

    private NumberPicker.OnValueChangeListener mOnHourChangedListener = new NumberPicker.OnValueChangeListener() {
    	//小时监听器接口
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            boolean isDateChanged = false;//日期变动标志
            Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历
            if (!mIs24HourView) {
            	//是否24小时制
                if (!mIsAm && oldVal == HOURS_IN_HALF_DAY - 1 && newVal == HOURS_IN_HALF_DAY) {
                	//条件：时段为下午且旧值为11，新值为12
                    cal.setTimeInMillis(mDate.getTimeInMillis());
                    //返回此 Calendar 的时间值，以毫秒为单位，用该值设置此 Calendar 的当前时间值
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                    //根据日历的规则，为给定的日历字段添加或减去指定的时间量,这里给天数加一
                    isDateChanged = true;
                } else if (mIsAm && oldVal == HOURS_IN_HALF_DAY && newVal == HOURS_IN_HALF_DAY - 1) {
                	//条件：时段为上午且旧值为12，新值为11
                    cal.setTimeInMillis(mDate.getTimeInMillis());
                    cal.add(Calendar.DAY_OF_YEAR, -1);//天数减一
                    isDateChanged = true;
                }
                if (oldVal == HOURS_IN_HALF_DAY - 1 && newVal == HOURS_IN_HALF_DAY ||
                        oldVal == HOURS_IN_HALF_DAY && newVal == HOURS_IN_HALF_DAY - 1) {
                	//条件：若新旧值相差一
                    mIsAm = !mIsAm;//设定上下午互换
                    updateAmPmControl();//更新时段操作
                }
            } else {
                if (oldVal == HOURS_IN_ALL_DAY - 1 && newVal == 0) {
                	//条件：旧值为11，新值为0
                    cal.setTimeInMillis(mDate.getTimeInMillis());
                    cal.add(Calendar.DAY_OF_YEAR, 1);//天数加一
                    isDateChanged = true;
                } else if (oldVal == 0 && newVal == HOURS_IN_ALL_DAY - 1) {
                	//条件：旧值为0，新值为11
                    cal.setTimeInMillis(mDate.getTimeInMillis());
                    cal.add(Calendar.DAY_OF_YEAR, -1);//天数减一
                    isDateChanged = true;
                }
            }
            int newHour = mHourSpinner.getValue() % HOURS_IN_HALF_DAY + (mIsAm ? 0 : HOURS_IN_HALF_DAY);
            //定义24小时制小时数
            mDate.set(Calendar.HOUR_OF_DAY, newHour);
            //将给定的日历字段设置为新小时数
            onDateTimeChanged();
            if (isDateChanged) {//若日期改变
                setCurrentYear(cal.get(Calendar.YEAR));
                setCurrentMonth(cal.get(Calendar.MONTH));
                setCurrentDay(cal.get(Calendar.DAY_OF_MONTH));
                //将当前日历字段设置为返回的给定日历字段的值
            }
        }
    };

    private NumberPicker.OnValueChangeListener mOnMinuteChangedListener = new NumberPicker.OnValueChangeListener() {
    	//分钟监听器接口
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            int minValue = mMinuteSpinner.getMinValue();
            int maxValue = mMinuteSpinner.getMaxValue();
            //分钟下拉列表最大最小值
            int offset = 0;
            if (oldVal == maxValue && newVal == minValue) {
                offset += 1;
            } else if (oldVal == minValue && newVal == maxValue) {
                offset -= 1;
            }//根据新旧值更新offset
            if (offset != 0) {
                mDate.add(Calendar.HOUR_OF_DAY, offset);//小时加offset
                mHourSpinner.setValue(getCurrentHour());
                updateDateControl();
                int newHour = getCurrentHourOfDay();
                if (newHour >= HOURS_IN_HALF_DAY) {
                    mIsAm = false;
                    updateAmPmControl();
                } else {
                    mIsAm = true;
                    updateAmPmControl();
                }
            }//以小时数更新上下午
            mDate.set(Calendar.MINUTE, newVal);
            //设置当前分钟数为新值
            onDateTimeChanged();
        }
    };

    private NumberPicker.OnValueChangeListener mOnAmPmChangedListener = new NumberPicker.OnValueChangeListener() {
    	//时段监听器接口
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            mIsAm = !mIsAm;
            if (mIsAm) {
                mDate.add(Calendar.HOUR_OF_DAY, -HOURS_IN_HALF_DAY);
            } else {
                mDate.add(Calendar.HOUR_OF_DAY, HOURS_IN_HALF_DAY);
            }//上午小时减12，下午小时加12
            updateAmPmControl();
            onDateTimeChanged();
        }
    };

    public interface OnDateTimeChangedListener {
        void onDateTimeChanged(DateTimePicker view, int year, int month,
                int dayOfMonth, int hourOfDay, int minute);
    }//年月日界面交接接口
    /**
     * 功能描述：设置日期和时间的控件，完成对日期和时间的设置 lyf
     * 参        数：context 应用程序环境的信息，即上下文
     */
    public DateTimePicker(Context context) {
        this(context, System.currentTimeMillis());
    }
    /**
     * 功能描述：设置日期和时间的控件，完成对日期和时间的设置（24小时制） lyf
     * 参        数：context 应用程序环境的信息，即上下文 date 日期
     */
    public DateTimePicker(Context context, long date) {
        this(context, date, DateFormat.is24HourFormat(context));
    }
    /**
     * 功能描述：设置日期和时间的控件，完成对日期和时间的设置 lyf
     * 参        数：context 应用程序环境的信息，即上下文 date 日期 is24HourView 是否24小时制
     */
    public DateTimePicker(Context context, long date, boolean is24HourView) {
        super(context);//调用父类的构造函数
        mDate = Calendar.getInstance();
        mInitialising = true;
        mIsAm = getCurrentHourOfDay() >= HOURS_IN_HALF_DAY;//获取上下午
        inflate(context, R.layout.datetime_picker, this);
        //将一个xml中定义的布局datetime_picker找出来

        mDateSpinner = (NumberPicker) findViewById(R.id.date);//找到日期数字选择器
        mDateSpinner.setMinValue(DATE_SPINNER_MIN_VAL);
        mDateSpinner.setMaxValue(DATE_SPINNER_MAX_VAL);
        mDateSpinner.setOnValueChangedListener(mOnDateChangedListener);
        //初始化日期列表各值
        mHourSpinner = (NumberPicker) findViewById(R.id.hour);
        mHourSpinner.setOnValueChangedListener(mOnHourChangedListener);
        //初始化小时列表各值
        mMinuteSpinner =  (NumberPicker) findViewById(R.id.minute);
        mMinuteSpinner.setMinValue(MINUT_SPINNER_MIN_VAL);
        mMinuteSpinner.setMaxValue(MINUT_SPINNER_MAX_VAL);
        mMinuteSpinner.setOnLongPressUpdateInterval(100);
        mMinuteSpinner.setOnValueChangedListener(mOnMinuteChangedListener);
        //初始化分钟列表各值

        String[] stringsForAmPm = new DateFormatSymbols().getAmPmStrings();
        mAmPmSpinner = (NumberPicker) findViewById(R.id.amPm);
        mAmPmSpinner.setMinValue(AMPM_SPINNER_MIN_VAL);
        mAmPmSpinner.setMaxValue(AMPM_SPINNER_MAX_VAL);
        mAmPmSpinner.setDisplayedValues(stringsForAmPm);
        mAmPmSpinner.setOnValueChangedListener(mOnAmPmChangedListener);
        //初始化上下午时段列表各值

        // update controls to initial state 更新操作到初始状态       
        updateDateControl();
        updateHourControl();
        updateAmPmControl();
        
        set24HourView(is24HourView);

        // set to current time
        setCurrentDate(date);

        setEnabled(isEnabled());

        // set the content descriptions
        mInitialising = false;
    }
    /**
     * 功能描述：设置各可操作列表为可用
     * 实现过程：将日期、分钟、小时、上下午等列表置为可用 lyf
     * 参        数：enabled 是否可用标记
     */
    @Override
    public void setEnabled(boolean enabled) {
    	//判断是否已标记
        if (mIsEnabled == enabled) {
            return;
        }
        super.setEnabled(enabled);
        mDateSpinner.setEnabled(enabled);
        mMinuteSpinner.setEnabled(enabled);
        mHourSpinner.setEnabled(enabled);
        mAmPmSpinner.setEnabled(enabled);
        mIsEnabled = enabled;
    }
    /**
     * 功能描述：设置可操作列表为可用
     */
    @Override
    public boolean isEnabled() {
        return mIsEnabled;
    }
    /**
     * Get the current date in millis
     *功能描述：获取当前时间毫秒数
     * @return the current date in millis
     */
    public long getCurrentDateInTimeMillis() {
        return mDate.getTimeInMillis();
    }

    /**
     * Set the current date
     *功能描述：设定当前日期时间
     *参数：date 
     * @param date The current date in millis
     */
    public void setCurrentDate(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);// 用给定的 long 值设置此 Calendar 的当前时间值
        setCurrentDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        //设定给定日历字段的值（年，月，日，小时，分钟）
    }

    /**
     * Set the current date
     *功能描述：按年，月，日，小时，分钟设定当前日期时间
     * @param year The current year
     * @param month The current month
     * @param dayOfMonth The current dayOfMonth
     * @param hourOfDay The current hourOfDay
     * @param minute The current minute
     */
    public void setCurrentDate(int year, int month,
            int dayOfMonth, int hourOfDay, int minute) {
        setCurrentYear(year);
        setCurrentMonth(month);
        setCurrentDay(dayOfMonth);
        setCurrentHour(hourOfDay);
        setCurrentMinute(minute);
    }

    /**
     * Get current year
     *功能描述：获取当前年份
     * @return The current year
     */
    public int getCurrentYear() {
        return mDate.get(Calendar.YEAR);
    }

    /**
     * Set current year
     *功能描述：设定当前年份
     * @param year The current year
     */
    public void setCurrentYear(int year) {
        if (!mInitialising && year == getCurrentYear()) {
            return;
        }//非默认及年份正确则返回
        mDate.set(Calendar.YEAR, year);
        updateDateControl();//更新日期操作
        onDateTimeChanged();
    }

    /**
     * Get current month in the year
     *功能描述：获取当前月份
     * @return The current month in the year
     */
    public int getCurrentMonth() {
        return mDate.get(Calendar.MONTH);
    }

    /**
     * Set current month in the year
     *功能描述：设定当前月份
     *参数：month 要设定的月份
     * @param month The month in the year
     */
    public void setCurrentMonth(int month) {
        if (!mInitialising && month == getCurrentMonth()) {
            return;
        }//非默认及月份正确则返回
        mDate.set(Calendar.MONTH, month);
        updateDateControl();
        onDateTimeChanged();
    }

    /**
     * Get current day of the month
     *功能描述：获得当前日期
     * @return The day of the month
     */
    public int getCurrentDay() {
        return mDate.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Set current day of the month
     *功能描述：设定当前日期
     *参数：dayOfMonth 要设定的日期
     * @param dayOfMonth The day of the month
     */
    public void setCurrentDay(int dayOfMonth) {
        if (!mInitialising && dayOfMonth == getCurrentDay()) {
            return;
        }//非默认及天数正确则返回
        mDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDateControl();
        onDateTimeChanged();
    }

    /**
     * Get current hour in 24 hour mode, in the range (0~23)
     * 功能描述：以24小时制获取当前时间
     * @return The current hour in 24 hour mode
     */
    public int getCurrentHourOfDay() {
        return mDate.get(Calendar.HOUR_OF_DAY);
    }
    /**
     * 功能描述：获取当前小时数    
     */
    private int getCurrentHour() {
        if (mIs24HourView){
            return getCurrentHourOfDay();
        } else {
            int hour = getCurrentHourOfDay();
            if (hour > HOURS_IN_HALF_DAY) {
                return hour - HOURS_IN_HALF_DAY;
            } else {
                return hour == 0 ? HOURS_IN_HALF_DAY : hour;
            }//如果24小时制返回当前小时数，如果12小时制转换为12小时制小时数返回
        }
    }

    /**
     * Set current hour in 24 hour mode, in the range (0~23)
     * 功能描述：设置当前小时数
     * @param hourOfDay
     */
    public void setCurrentHour(int hourOfDay) {
        if (!mInitialising && hourOfDay == getCurrentHourOfDay()) {
            return;
        }//非默认及小时数正确则返回
        mDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
      //如果24小时制设置当前小时数，如果12小时制转换为12小时制小时数设置
        if (!mIs24HourView) {
            if (hourOfDay >= HOURS_IN_HALF_DAY) {
                mIsAm = false;
                if (hourOfDay > HOURS_IN_HALF_DAY) {
                    hourOfDay -= HOURS_IN_HALF_DAY;
                }
            } else {
                mIsAm = true;
                if (hourOfDay == 0) {
                    hourOfDay = HOURS_IN_HALF_DAY;
                }
            }
            updateAmPmControl();
        }
        mHourSpinner.setValue(hourOfDay);
        onDateTimeChanged();
    }

    /**
     * Get currentMinute
     * 功能描述：获取当前分钟 数
     * @return The Current Minute
     */
    public int getCurrentMinute() {
        return mDate.get(Calendar.MINUTE);
    }

    /**
     * Set current minute
     * 功能描述：设置当前分钟数
     */
    public void setCurrentMinute(int minute) {
        if (!mInitialising && minute == getCurrentMinute()) {
            return;
        }//非默认及分钟数正确则返回
        mMinuteSpinner.setValue(minute);
        mDate.set(Calendar.MINUTE, minute);
        onDateTimeChanged();
    }

    /**
     * @return true if this is in 24 hour view else false.
     * 功能描述：判断是否24小时制
     */
    public boolean is24HourView () {
        return mIs24HourView;
    }

    /**
     * Set whether in 24 hour or AM/PM mode.
     * 功能描述：设置24小时显示方式或上午下午显示方式
     * @param is24HourView True for 24 hour mode. False for AM/PM mode.
     */
    public void set24HourView(boolean is24HourView) {
        if (mIs24HourView == is24HourView) {
            return;
        }
        mIs24HourView = is24HourView;
        //若为24小时制则隐藏上下午显示，若不是则显示上下午显示
        mAmPmSpinner.setVisibility(is24HourView ? View.GONE : View.VISIBLE);
        int hour = getCurrentHourOfDay();
        updateHourControl();
        setCurrentHour(hour);
        updateAmPmControl();
    }
    /**
     * 功能描述：更新日期操作到初始状态
     */
    private void updateDateControl() {
        Calendar cal = Calendar.getInstance();
        //获取当前时间
        cal.setTimeInMillis(mDate.getTimeInMillis());
        //获取当前毫秒数设置日历的当前时间
        cal.add(Calendar.DAY_OF_YEAR, -DAYS_IN_ALL_WEEK / 2 - 1); 
        mDateSpinner.setDisplayedValues(null);
        //设置显示格式为月-日-星期几
        for (int i = 0; i < DAYS_IN_ALL_WEEK; ++i) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            mDateDisplayValues[i] = (String) DateFormat.format("MM.dd EEEE", cal);
        }  
        //显示可选自定义的值列表
        mDateSpinner.setDisplayedValues(mDateDisplayValues);
        mDateSpinner.setValue(DAYS_IN_ALL_WEEK / 2);
        mDateSpinner.invalidate();
    }
    /**
     * 功能描述：更新上下午操作到初始状态
     */
    private void updateAmPmControl() {
    	//24小时制则隐藏上下午显示，不是则设置上下午操作
        if (mIs24HourView) {
            mAmPmSpinner.setVisibility(View.GONE);
        } else {
            int index = mIsAm ? Calendar.AM : Calendar.PM;
            mAmPmSpinner.setValue(index);
            mAmPmSpinner.setVisibility(View.VISIBLE);
        }
    }
    /**
     * 功能描述：更新小时操作到初始状态
     */
    private void updateHourControl() {
    	//24小时制及12小时制设置
        if (mIs24HourView) {
            mHourSpinner.setMinValue(HOUR_SPINNER_MIN_VAL_24_HOUR_VIEW);
            mHourSpinner.setMaxValue(HOUR_SPINNER_MAX_VAL_24_HOUR_VIEW);
        } else {
            mHourSpinner.setMinValue(HOUR_SPINNER_MIN_VAL_12_HOUR_VIEW);
            mHourSpinner.setMaxValue(HOUR_SPINNER_MAX_VAL_12_HOUR_VIEW);
        }
    }

    /**
     * Set the callback that indicates the 'Set' button has been pressed.
     * 功能描述：设置时间调整事件的回调函数。
     * 参数 ：onTimeChangedListener   回调函数，不能为空
     * @param callback the callback, if null will do nothing
     */
    public void setOnDateTimeChangedListener(OnDateTimeChangedListener callback) {
        mOnDateTimeChangedListener = callback;
    }
    /**
     * 功能描述：回调函数
     */
    private void onDateTimeChanged() {
        if (mOnDateTimeChangedListener != null) {
            mOnDateTimeChangedListener.onDateTimeChanged(this, getCurrentYear(),
                    getCurrentMonth(), getCurrentDay(), getCurrentHourOfDay(), getCurrentMinute());
        }
    }//采用相关的监听器对其状态进行监听
}
