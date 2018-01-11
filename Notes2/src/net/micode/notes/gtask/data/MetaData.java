/*
 * Copyright ()2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenss/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.micode.notes.gtask.data;

import android.database.Cursor;
import android.util.Log;

import net.micode.notes.tool.GTaskStringUtils;

import org.json.JSONException;
import org.json.JSONObject;

//在这里metadata就是一个对于各种活动的注释
public class MetaData extends Task {
    private final static String TAG = MetaData.class.getSimpleName();//基层类的名称
       private String mRelatedGid = null;

    public void setMeta(String gid, JSONObject metaInfo) {
        try {
            metaInfo.put(GTaskStringUtils.META_HEAD_GTASK_ID, gid);//相关gtask的id
        } catch (JSONException e) {
            Log.e(TAG, "failed to put related gid");//显示错误信息
        }
        setNotes(metaInfo.toString());
        setName(GTaskStringUtils.META_NOTE_NAME);//返回名称，使用字符串表示
    }
//返回相关gtask的id
    public String getRelatedGid() {
        return mRelatedGid;
    }
//是否值得保存，仅当mnotes不为null，也就是有内容的时候，才能进行保存
    @Override
    public boolean isWorthSaving() {
        return getNotes() != null;
    }
//通过远程的json格式的文本来设置内容，当mnotes有内容
    @Override
    public void setContentByRemoteJSON(JSONObject js) {
        super.setContentByRemoteJSON(js);
        if (getNotes() != null) {
            try {
                JSONObject metaInfo = new JSONObject(getNotes().trim());//获取mnotes的一个副本
                mRelatedGid = metaInfo.getString(GTaskStringUtils.META_HEAD_GTASK_ID);
            } catch (JSONException e) {
                Log.w(TAG, "failed to get related gid");
                mRelatedGid = null;
            }
        }
    }
//抛出异常，“通过本地的json格式文本文件来设置内容”不能被调用
    @Override
    public void setContentByLocalJSON(JSONObject js) {
        // this function should not be called
        throw new IllegalAccessError("MetaData:setContentByLocalJSON should not be called");
    }
//抛出异常，“通过内容生成本地的json格式文本文件”不能被调用
    @Override
    public JSONObject getLocalJSONFromContent() {
        throw new IllegalAccessError("MetaData:getLocalJSONFromContent should not be called");
    }
//抛出异常，“获取同步活动”不能被调用
    @Override
    public int getSyncAction(Cursor c) {
        throw new IllegalAccessError("MetaData:getSyncAction should not be called");
    }

}