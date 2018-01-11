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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import net.micode.notes.data.Notes;
import net.micode.notes.data.Notes.NoteColumns;
/**
 * description：提醒初始化接收器，用广播接收器类实现提醒功能，实现初始化提醒闹钟功能   lyf
 */

public class AlarmInitReceiver extends BroadcastReceiver {
    private static final String [] PROJECTION = new String [] {
    	//projection是我们要查询数据库的列
        NoteColumns.ID,
        NoteColumns.ALERTED_DATE
    };

    private static final int COLUMN_ID                = 0;
    private static final int COLUMN_ALERTED_DATE      = 1;
    /**
     * 功能描述：接收广播事件时调用方法
     * 实现过程：获取当前时间，在数据库中查询提醒日期时间，接收广播
     * 参        数：context 应用程序环境的信息，即上下文， intent获取随广播而来的Intent中的数据
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        long currentDate = System.currentTimeMillis();//获取当前时间的毫秒数  lyf
        Cursor c = context.getContentResolver().query(Notes.CONTENT_NOTE_URI,
                PROJECTION,
                NoteColumns.ALERTED_DATE + ">? AND " + NoteColumns.TYPE + "=" + Notes.TYPE_NOTE,
                new String[] { String.valueOf(currentDate) },//long 变量 currentDate 转换成字符串
                null);
               //在数据库中查询
        if (c != null) {
            if (c.moveToFirst()) {
            	//判断查询结果是否为空   lyf
                do {
                    long alertDate = c.getLong(COLUMN_ALERTED_DATE);
                    Intent sender = new Intent(context, AlarmReceiver.class);
                    sender.setData(ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, c.getLong(COLUMN_ID)));
                    //把id和contentUri连接成一个新的Uri  lyf
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, sender, 0);
                    //可以理解为延迟执行的intent,获得广播  lyf
                    AlarmManager alermManager = (AlarmManager) context
                            .getSystemService(Context.ALARM_SERVICE);
                    alermManager.set(AlarmManager.RTC_WAKEUP, alertDate, pendingIntent);
                    //全局定时器在指定时长后执行某项操作,这里实现闹钟功能  lyf
                } while (c.moveToNext());//循环来逐条读取数据   lyf
            }
            c.close();
        }
    }
}
