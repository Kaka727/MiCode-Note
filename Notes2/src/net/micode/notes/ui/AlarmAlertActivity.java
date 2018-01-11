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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import net.micode.notes.R;
import net.micode.notes.data.Notes;
import net.micode.notes.tool.DataUtils;

import java.io.IOException;
/**
 * description：提醒到时界面，此界面可以唤醒休眠的屏幕并弹出窗口   lyf
 */
public class AlarmAlertActivity extends Activity implements OnClickListener, OnDismissListener {
	private long mNoteId;
    private String mSnippet;
    private static final int SNIPPET_PREW_MAX_LEN = 60;
    MediaPlayer mPlayer;//声明播放器         lyf
    /**
     * 功能描述：主要做这个activity启动时一些必要的初始化工作
     * 实现过程：调用父类的onCreate构造函数，窗体显示状态设为无标题，对Window的flag进行设置
     *       当锁屏的时候，显示该window,即覆盖在屏幕锁之上，并保证屏幕亮起
     * 参        数：savedInstanceState 保存当前Activity的状态信息
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    //一个activity启动调用的第一个函数就是onCreate    lyf
    	super.onCreate(savedInstanceState);
    	//调用父类的onCreate构造函数,savedInstanceState是保存当前Activity的状态信息     lyf
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //窗体显示状态操作，此处设为无标题    lyf

        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
     //对Window的flag进行设置:当锁屏的时候，显示该window,即覆盖在屏幕锁之上.    lyf
        if (!isScreenOn()) {//屏幕处于锁闭状态时     lyf
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            		//当该window对用户可见时，让设备屏幕处于高亮（bright）状态    lyf
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    //当window被显示的时候，系统将把它当做一个用户活动事件，以点亮手机屏幕    lyf
                    | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                    //当该window对用户可见的时候，允许锁屏    lyf
                    | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
                     //当请求布局时，你的窗口可能出现在状态栏的上面或下面，从而造成遮挡。   lyf
                     //当设置这一选项后，窗口管理器将确保窗口内容不会被装饰条（状态栏）盖住    lyf
        }

        Intent intent = getIntent();
        //获取由上一个Activity传递过来的Intent对象    lyf

        try {
            mNoteId = Long.valueOf(intent.getData().getPathSegments().get(1));
            //获取mNoteId，依次提取出Path的各个部分的字符串，以字符串数组的形式输出    lyf
            mSnippet = DataUtils.getSnippetById(this.getContentResolver(), mNoteId);
            //从DataUtils类调用getSnippetById获取mSnippet串    lyf
            mSnippet = mSnippet.length() > SNIPPET_PREW_MAX_LEN ? mSnippet.substring(0,
                    SNIPPET_PREW_MAX_LEN) + getResources().getString(R.string.notelist_string_info)
                    : mSnippet;
                    //对长度大于60的mSnippet串的处理    lyf
        } catch (IllegalArgumentException e) {
        	//当try语句中出现异常是时，会执行catch中的语句    lyf
            e.printStackTrace();
            //实例化Exception类型的对象e，在命令行打印异常信息在程序中出错的位置及原因    lyf
            return;

        }

        mPlayer = new MediaPlayer();
        if (DataUtils.visibleInNoteDatabase(getContentResolver(), mNoteId, Notes.TYPE_NOTE)) {
        	//播放器触发条件，从DataUtils类中visibleInNoteDatabase    lyf
            showActionDialog();
            //显示对话框   lyf
            playAlarmSound();
            //播放闹钟响铃     lyf
        } else {
            finish();
        }
    }
    /**
     * 功能描述：通过获取电源服务判断屏幕是否锁闭
     */
    private boolean isScreenOn() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }
    /**
     * 功能描述：播放闹钟响铃   
     * 实现过程：获取当前系统的铃声uri，设置静音模式下仍然闹铃，播放闹铃
     */
    private void playAlarmSound() {
        Uri url = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);
         //获取当前系统的铃声uri    lyf
        int silentModeStreams = Settings.System.getInt(getContentResolver(),
                Settings.System.MODE_RINGER_STREAMS_AFFECTED, 0);

        if ((silentModeStreams & (1 << AudioManager.STREAM_ALARM)) != 0) {
            mPlayer.setAudioStreamType(silentModeStreams);
        } else {
            mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        }// 静音模式下仍然闹铃，也就是Alarm不受RingerMode设置的影响     lyf
        try {
            mPlayer.setDataSource(this, url);//设置需要加载的音频文件
            mPlayer.prepare();//设置播放器进入prepare状态
            mPlayer.setLooping(true);//设置循环
            mPlayer.start();//开始播放
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }//对各种异常的处理    lyf
    }
    /**
     * 功能描述：弹出消息框
     */
    private void showActionDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.app_name);//标题为app_name   lyf
        dialog.setMessage(mSnippet);//设置消息框显示内容   lyf
        dialog.setPositiveButton(R.string.notealert_ok, this);
        //确定按钮显示内容Got it   lyf
        if (isScreenOn()) {
            dialog.setNegativeButton(R.string.notealert_enter, this);
        }
        //取消按钮显示内容Take a look    lyf
        dialog.show().setOnDismissListener(this);
        //显示可解除对话框     lyf
    }
    /**
     * 功能描述：点击时事件发生
     */
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE://取消按钮按下
                Intent intent = new Intent(this, NoteEditActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra(Intent.EXTRA_UID, mNoteId);
                startActivity(intent);
                //显示出便签编辑界面     lyf
                break;
            default://确定按钮按下
                break;//无动作
        }
    }
    /**
     * 功能描述：解除对话框     
     */
    public void onDismiss(DialogInterface dialog) {
        stopAlarmSound();//停止播放闹铃
        finish();//完成结束对话框
    }
    /**
     * 功能描述：停止播放闹铃   
     */
    private void stopAlarmSound() {
        if (mPlayer != null) {
            mPlayer.stop();//停止播放
            mPlayer.release();//释放播放器占用的资源，一旦确定不再使用播放器时应当尽早调用它释放资源
            mPlayer = null;
        }
    }
}
