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

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.micode.notes.R;
import net.micode.notes.data.Notes;
import net.micode.notes.tool.DataUtils;
import net.micode.notes.tool.ResourceParser.NoteItemBgResources;

/*
 * 功能描述：构建便签列表的项目的详细具体信息，继承线性列表，例如建立便签时间等
 */
public class NotesListItem extends LinearLayout {
    private ImageView mAlert;//提醒图标小闹钟
    private TextView mTitle;//题目
    private TextView mTime;//创建或修改时间
    private TextView mCallName;//联系人名字
    private NoteItemData mItemData;//便签数据
    private CheckBox mCheckBox;//复选框
/*
 * 功能描述：从R文件中获取相应的文本图片赋值给变量
 */
    public NotesListItem(Context context) {
        super(context);
        inflate(context, R.layout.note_item, this);
        //根据R.layout.note_item文件的布局来实现NotesListItem
        mAlert = (ImageView) findViewById(R.id.iv_alert_icon);
        //获取小闹钟的图标
        mTitle = (TextView) findViewById(R.id.tv_title);
        //获取题目（文件夹或便签项上的文本）
        mTime = (TextView) findViewById(R.id.tv_time);
        //获取创建或修改时间
        mCallName = (TextView) findViewById(R.id.tv_name);
        //获取联系人名字
        mCheckBox = (CheckBox) findViewById(android.R.id.checkbox);
        //获取复选框
    }

    public void bind(Context context, NoteItemData data, boolean choiceMode, boolean checked) {
        if (choiceMode && data.getType() == Notes.TYPE_NOTE) {
        	//如果当前处于选择模式下且数据类型为便签
            mCheckBox.setVisibility(View.VISIBLE);
            //设置复选框可见
            mCheckBox.setChecked(checked);
            //设置当前项目被选中
        } else { 
            mCheckBox.setVisibility(View.GONE);
            //设置复选框不可见
        }

        mItemData = data;
        if (data.getId() == Notes.ID_CALL_RECORD_FOLDER) {
        	//如果当前项目是ID_CALL_RECORD_FOLDER文件夹
            mCallName.setVisibility(View.GONE); 
            //设置联系人名字不可见
            mAlert.setVisibility(View.VISIBLE);
            //设置小图标（当前小图标为闹钟，会在下文替换）可见
            mTitle.setTextAppearance(context, R.style.TextAppearancePrimaryItem);
            //根据R.style.TextAppearancePrimaryItem设置title文本风格
            mTitle.setText(context.getString(R.string.call_record_folder_name)
                    + context.getString(R.string.format_folder_files_count, data.getNotesCount()));
            //设置title的内容（文件夹名字+数量）
            mAlert.setImageResource(R.drawable.call_record);
            //将提醒图标替换为”小电话“
        } else if (data.getParentId() == Notes.ID_CALL_RECORD_FOLDER) {
        	//如果当前项目是在ID_CALL_RECORD_FOLDER文件夹死下的便签
            mCallName.setVisibility(View.VISIBLE);
            //设置联系人姓名可见（因为是便签）
            mCallName.setText(data.getCallName());
            //设置联系人姓名的文本内容为data。callName
            mTitle.setTextAppearance(context,R.style.TextAppearanceSecondaryItem);
          //根据R.style.TextAppearanTextAppearanceSecondaryItem设置title文本风格
            mTitle.setText(DataUtils.getFormattedSnippet(data.getSnippet()));
            //设置title的文本内容为便签内容的前面片段
            if (data.hasAlert()) {
            	//如果当前便签存在提醒时间
                mAlert.setImageResource(R.drawable.clock);
                //将提箱小图标设置为“小闹钟”
                mAlert.setVisibility(View.VISIBLE);
                //将提醒小图标设置为可见
            } else {
                mAlert.setVisibility(View.GONE);
                //否则将提醒小图标设置为不可见
            }
        }
        /////////////////////上面的设置均在ID_CALL_RECORD_FOLDER文件下/////////////////////
        else {
        	//如果当前项目不在ID_CALL_RECORD_FOLDER下
            mCallName.setVisibility(View.GONE);
            //设置联系人姓名不可见
            mTitle.setTextAppearance(context, R.style.TextAppearancePrimaryItem);
            //设置title的文本格式为R.style.TextAppearancePrimaryItem
            if (data.getType() == Notes.TYPE_FOLDER) {
            	//如果当前先锋纲目的类型为文件夹
                mTitle.setText(data.getSnippet()
                        + context.getString(R.string.format_folder_files_count,
                                data.getNotesCount()));
                //设置文件夹的title为“名字+（count）”，例如 小米便签（3）
                mAlert.setVisibility(View.GONE);
                //设置提醒小图标不可见
            } else {
            	//当前项目的类型为便签
                mTitle.setText(DataUtils.getFormattedSnippet(data.getSnippet()));
                //设置便签的title为便签内容的前面片段
                if (data.hasAlert()) {
                	//如果当前便签存在提醒闹钟时间
                    mAlert.setImageResource(R.drawable.clock);
                    //将提醒小图标设置为“小闹钟”
                    mAlert.setVisibility(View.VISIBLE);
                    //设置提醒小闹钟可见
                } else {
                	//否则设置提醒小图标不可见
                    mAlert.setVisibility(View.GONE);
                }
            }
        }
        mTime.setText(DateUtils.getRelativeTimeSpanString(data.getModifiedDate()));
        //设置项目的修改时间
        setBackground(data);
        //根据data内容设置背景
    }
    /*
     * 功能描述：判断不同的类型及位置设置不同的背景格式
     */
    private void setBackground(NoteItemData data) {
        int id = data.getBgColorId();
        //获取一个int型的id，此id用来获取背景颜色
        if (data.getType() == Notes.TYPE_NOTE) {
        	//如果当前项目的类型是便签类型
            if (data.isSingle() || data.isOneFollowingFolder()) {
            	//如果此时项目为单个（非多选）或者在文件夹（列表中所有文件夹在便签之上）下只存在一个便签
                setBackgroundResource(NoteItemBgResources.getNoteBgSingleRes(id));
                //将背景设置为单个便签的背景
            } else if (data.isLast()) {
            	//如果当前便签为最后一个
                setBackgroundResource(NoteItemBgResources.getNoteBgLastRes(id));
                //将背景设置为最后便签的背景
            } else if (data.isFirst() || data.isMultiFollowingFolder()) {
            	//如果当前便签是第一个便签或者文件夹下有多个便签
                setBackgroundResource(NoteItemBgResources.getNoteBgFirstRes(id));
                //将背景设置为第一个便签的背景
            } else {
                setBackgroundResource(NoteItemBgResources.getNoteBgNormalRes(id));
                //将便签设置为普通类型便签的背景
            }
        } else {
        	//如果当前类型为文件夹类型
            setBackgroundResource(NoteItemBgResources.getFolderBgRes());
            //则将背景设置为文件夹的背景
        }
    }
    /*
     * 功能描述：返回当前便签的数据信息
     */
    public NoteItemData getItemData() {
        return mItemData;
    }
}
