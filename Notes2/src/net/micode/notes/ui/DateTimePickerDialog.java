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

import java.util.Calendar;
 
import net.micode.notes.R;
import net.micode.notes.ui.DateTimePicker;
import net.micode.notes.ui.DateTimePicker.OnDateTimeChangedListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

/* 
 * 功能描述：实现时间选择对话框，继承AlertDialog
 */
public class DateTimePickerDialog extends AlertDialog implements OnClickListener {

    private Calendar mDate = Calendar.getInstance();
    // 使用目前的时区和语言环境的方法得到一个日历
    private boolean mIs24HourView;//布尔变量，是否为24小时制
    private OnDateTimeSetListener mOnDateTimeSetListener;
    private DateTimePicker mDateTimePicker;
    //声明一个时间选择控件
    /*
     * 功能描述：给时间设置事件监听器
     */
    public interface OnDateTimeSetListener {
    	/*
    	 * 参数1：对话框
    	 * 参数2：日期
    	 */
        void  OnDateTimeSet(AlertDialog dialog, long date);
    }
    /*
     * 功能描述：实现对时间选择对话框的构建
     * 参数：1上下文传递数据
     * 		2日期
     */
    public DateTimePickerDialog(Context context, long date) {
        super(context);//调用上下文获取数据
        mDateTimePicker = new DateTimePicker(context);
        //实例化mDateTimePicker
        setView(mDateTimePicker);
        //添加子视图，将时间选择对话框
        
        /*
         * 功能描述：设置时间改变的事件监听器
         */
        mDateTimePicker.setOnDateTimeChangedListener(new OnDateTimeChangedListener() {
            public void onDateTimeChanged(DateTimePicker view, int year, int month,
                    int dayOfMonth, int hourOfDay, int minute) {
                mDate.set(Calendar.YEAR, year);
                //将参数year赋值给日历的年
                mDate.set(Calendar.MONTH, month);
               //将参数month值给日历的月
                mDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
              //将参数dayofmonth赋值给日历的日
                mDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
              //将参数hourofday的小时赋值给日历的小时
                mDate.set(Calendar.MINUTE, minute);
              //将参数minute日期的分钟赋值给日历的分钟
                updateTitle(mDate.getTimeInMillis());
                //把改变的时间更新到对话框的title
            }
        });
        mDate.setTimeInMillis(date);
        //  用给定的 data 值设置此 Calendar 的当前时间值。
        mDate.set(Calendar.SECOND, 0);
        //设置对话框的秒为0
        mDateTimePicker.setCurrentDate(mDate.getTimeInMillis());
        //设置当前对话框的时间为mDate日历的值
        setButton(context.getString(R.string.datetime_dialog_ok), this);
        //设置对话框弟一个button为“ok”
        setButton2(context.getString(R.string.datetime_dialog_cancel), (OnClickListener)null);
        //设置对话框的第二个button为“cancel”
        set24HourView(DateFormat.is24HourFormat(this.getContext()));
        //设置日期为24小时格式
        updateTitle(mDate.getTimeInMillis());
        //把改变的日期更新到title
    }
    /*
     * 功能描述：根据参数is24HourView来为mis24HourView赋值
     */
    public void set24HourView(boolean is24HourView) {
        mIs24HourView = is24HourView;
    }

    public void setOnDateTimeSetListener(OnDateTimeSetListener callBack) {
        mOnDateTimeSetListener = callBack;
    }
/*
 * 功能描述：实现更新对话框title的操作
 * 参数：长整形的date
 */
    private void updateTitle(long date) {
        int flag =
            DateUtils.FORMAT_SHOW_YEAR |
            DateUtils.FORMAT_SHOW_DATE |
            DateUtils.FORMAT_SHOW_TIME;
        flag |= mIs24HourView ? DateUtils.FORMAT_24HOUR : DateUtils.FORMAT_24HOUR;
        //设置一个标记位来判断显示时间格式
        setTitle(DateUtils.formatDateTime(this.getContext(), date, flag));
        //设置title来更新时间
    }
    /*
     * 功能描述：实现点击事件的触发(non-Javadoc)
     * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
     */
    public void onClick(DialogInterface arg0, int arg1) {
        if (mOnDateTimeSetListener != null) {
        	//如果mOnDateTimeSetListener不为空，即触发了监听事件，则将时间改变
            mOnDateTimeSetListener.OnDateTimeSet(this, mDate.getTimeInMillis());
        }
    }

}