
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
 * distributed under th  e License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.micode.notes.gtask.remote;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import net.micode.notes.R;
import net.micode.notes.ui.NotesListActivity;
import net.micode.notes.ui.NotesPreferenceActivity;

/**
 * 类的功能：GTask的异步任务，继承自安卓的异步任务      
 * */
public class GTaskASyncTask extends AsyncTask<Void, String, Integer> 
{
	//静态常量，标识通知信息的ID
    private static int GTASK_SYNC_NOTIFICATION_ID = 5234235;
    //声明了接口      完成的监听器
    public interface OnCompleteListener {
        void onComplete();
    }
    //私有成员  上下文   抽象类  允许获取以应用为特征的资源和类型。
    //同时启动应用级的操作，如启动Activity，broadcasting和接收intents
    private Context mContext;
    //私有成员  通知管理器
    private NotificationManager mNotifiManager;
    //GTask管理器
    private GTaskManager mTaskManager;
    //接口的声明   
    private OnCompleteListener mOnCompleteListener;
    /*
     * 函数功能描述：构造函数，初始化私有成员
     * 参数：context--当前运行的特征；  listener：接口监听器*/
    public GTaskASyncTask(Context context, OnCompleteListener listener) 
    {
    	//初始化mContext
        mContext = context;
        //初始化  接口
        mOnCompleteListener = listener;
        //初始化  涉及类型的强制转化   创建Notification通知
        mNotifiManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mTaskManager = GTaskManager.getInstance();
    }
    /*
     * 函数功能：取消同步
     * 参数：无*/
    public void cancelSync() 
    {
        mTaskManager.cancelSync();
    }
    /*
     * 函数功能：发布正在进行的状态信息
     * 参数：message   正在进行的信息*/
    public void publishProgess(String message) 
    {
    	//新的字符串message
        publishProgress(new String[] {
            message
        });
    }
    /*
     * 函数功能：展示通知信息
     * 参数： tickerId 一个ID      content   通知的信息内容 */
    private void showNotification(int tickerId, String content) 
    {
    	//
        Notification notification = new Notification(R.drawable.notification, mContext
                .getString(tickerId), System.currentTimeMillis());
        //设置默认亮度
        notification.defaults = Notification.DEFAULT_LIGHTS;
        //设置一个标志  自动删除的flag
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        PendingIntent pendingIntent;
        //若时间断续的id不等于R中success的id，则pendingIntent获取PreferenceActivity
        if (tickerId != R.string.ticker_success) {
            pendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext,
                    NotesPreferenceActivity.class), 0);

        } 
        //否则pendingIntent获取NotesListActivity
        else {
            pendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext,
                    NotesListActivity.class), 0);
        } 
        //notification.setLatestEventInfo(mContext, mContext.getString(R.string.app_name), content,
          //      pendingIntent);
        mNotifiManager.notify(GTASK_SYNC_NOTIFICATION_ID, notification);
    }

    @Override
    protected Integer doInBackground(Void... unused) {
        publishProgess(mContext.getString(R.string.sync_progress_login, NotesPreferenceActivity
                .getSyncAccountName(mContext)));
        return mTaskManager.sync(mContext, this);
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        showNotification(R.string.ticker_syncing, progress[0]);
        if (mContext instanceof GTaskSyncService) {
            ((GTaskSyncService) mContext).sendBroadcast(progress[0]);
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result == GTaskManager.STATE_SUCCESS) {
            showNotification(R.string.ticker_success, mContext.getString(
                    R.string.success_sync_account, mTaskManager.getSyncAccount()));
            NotesPreferenceActivity.setLastSyncTime(mContext, System.currentTimeMillis());
        } else if (result == GTaskManager.STATE_NETWORK_ERROR) {
            showNotification(R.string.ticker_fail, mContext.getString(R.string.error_sync_network));
        } else if (result == GTaskManager.STATE_INTERNAL_ERROR) {
            showNotification(R.string.ticker_fail, mContext.getString(R.string.error_sync_internal));
        } else if (result == GTaskManager.STATE_SYNC_CANCELLED) {
            showNotification(R.string.ticker_cancel, mContext
                    .getString(R.string.error_sync_cancelled));
        }
        if (mOnCompleteListener != null) {
            new Thread(new Runnable() {

                public void run() {
                    mOnCompleteListener.onComplete();
                }
            }).start();
        }
    }
}
