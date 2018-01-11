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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * description：提醒接收器，当接收到广播时激活AlarmAlertActivity界面
 */
public class AlarmReceiver extends BroadcastReceiver {
	/**
     * 功能描述：接收广播事件时调用方法
     * 实现过程：打开AlarmAlertActivity类,创建新的Activit实例,接收广播时调用
     * 参        数：context 应用程序环境的信息，即上下文， intent获取随广播而来的Intent中的数据
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setClass(context, AlarmAlertActivity.class);
        //打开AlarmAlertActivity类
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //根据Activity Affinity判断是否需要创建新的Task，然后再创建新的Activit实例放进去。
        context.startActivity(intent);
    }
}
