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
 * Discription：Node类为抽象类，由其他类继承，定义一些基础的变量和常量
 *                                                        薛江涛   11.10
 */

package net.micode.notes.gtask.data;

import android.database.Cursor;

import org.json.JSONObject;

public abstract class Node {
	//无同步动作
    public static final int SYNC_ACTION_NONE = 0;
    //远程添加动作
    public static final int SYNC_ACTION_ADD_REMOTE = 1;
    //本地添加动作
    public static final int SYNC_ACTION_ADD_LOCAL = 2;
    //远程删除动作
    public static final int SYNC_ACTION_DEL_REMOTE = 3;
    //本地删除动作
    public static final int SYNC_ACTION_DEL_LOCAL = 4;
    //远程更新动作
    public static final int SYNC_ACTION_UPDATE_REMOTE = 5;
    //本地更新动作
    public static final int SYNC_ACTION_UPDATE_LOCAL = 6;
    //更新冲突的动作
    public static final int SYNC_ACTION_UPDATE_CONFLICT = 7;
    //同步错误
    public static final int SYNC_ACTION_ERROR = 8;
    //id信息
    private String mGid;
    //名字
    private String mName;
    //最后一次Modified的时间
    private long mLastModified;
    //是否删除
    private boolean mDeleted;
    /*
     * 构造函数
     * 将变量进行初始化
     * */
    public Node() {
        mGid = null;
        mName = "";
        mLastModified = 0;
        mDeleted = false;
    }
    
    /*
     * 抽象方法getCreateAction
     * 参数：int actionId 进行处理的actionId
     * 返回值：返回一个JSONObject类记录了按传输格式的数据*/
    public abstract JSONObject getCreateAction(int actionId);
    /*
     * 抽象方法getUpdateAction
     * 参数：int actionId 进行处理的actionId
     * 返回值：返回一个JSONObject类记录了按传输格式的数据*/
    public abstract JSONObject getUpdateAction(int actionId);
    /*
     * 抽象方法setContentByRemoteJSON
     * 参数：JSONObject js 要进行数据传输的JSONObject     * 
     * */
    public abstract void setContentByRemoteJSON(JSONObject js);
    /*
     * 抽象方法setContentByLocalJSON
     * 参数：JSONObject js 要进行数据传输的JSONObject     * 
     * */
    public abstract void setContentByLocalJSON(JSONObject js);

    public abstract JSONObject getLocalJSONFromContent();

    public abstract int getSyncAction(Cursor c);

    public void setGid(String gid) 
    {
        this.mGid = gid;
    }
    //
    public void setName(String name) {
        this.mName = name;
    }

    public void setLastModified(long lastModified) {
        this.mLastModified = lastModified;
    }

    public void setDeleted(boolean deleted) {
        this.mDeleted = deleted;
    }
    //便于获取私有类型的值
    public String getGid() {
        return this.mGid;
    }
    //便于获取私有类型的值
    public String getName() {
        return this.mName;
    }
    //便于获取私有类型的值
    public long getLastModified() {
        return this.mLastModified;
    }
    //便于获取私有类型的值
    public boolean getDeleted() {
        return this.mDeleted;
    }

}
