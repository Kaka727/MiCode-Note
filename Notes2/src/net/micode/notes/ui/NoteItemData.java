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
import android.database.Cursor;
import android.text.TextUtils;

import net.micode.notes.data.Contact;
import net.micode.notes.data.Notes;
import net.micode.notes.data.Notes.NoteColumns;
import net.micode.notes.tool.DataUtils;

/*
 * 功能描述：定义一个便签项目（文件夹或者便签）的基本属性和信息，，可理解成数组
 */
public class NoteItemData {
    static final String [] PROJECTION = new String [] {
        NoteColumns.ID,//id号码，每个项都有唯一的id
        NoteColumns.ALERTED_DATE,//提醒日期
        NoteColumns.BG_COLOR_ID,//背景颜色的id，即不同id对应不同的背景颜色
        NoteColumns.CREATED_DATE,//便签的创建时间
        NoteColumns.HAS_ATTACHMENT,//布尔变量，是否存在匹配链接（联系人）？？
        NoteColumns.MODIFIED_DATE,//修改日期 
        NoteColumns.NOTES_COUNT,//便签的数量
        NoteColumns.PARENT_ID,//父文件夹的id
        NoteColumns.SNIPPET,//项的名字
        NoteColumns.TYPE,//项的类型（文件夹或者便签）
        NoteColumns.WIDGET_ID,//桌面小部件的id号
        NoteColumns.WIDGET_TYPE,//桌面小部件的大小类型（2x或4x）
    };
    /*
     * 以下几条语句将给每个字符串赋不同的整型值，对应上面的数组，可理解为数组的索引，使用时用cursor访问，
     */
    private static final int ID_COLUMN                    = 0;
    private static final int ALERTED_DATE_COLUMN          = 1;
    private static final int BG_COLOR_ID_COLUMN           = 2;
    private static final int CREATED_DATE_COLUMN          = 3;
    private static final int HAS_ATTACHMENT_COLUMN        = 4;
    private static final int MODIFIED_DATE_COLUMN         = 5;
    private static final int NOTES_COUNT_COLUMN           = 6;
    private static final int PARENT_ID_COLUMN             = 7;
    private static final int SNIPPET_COLUMN               = 8;
    private static final int TYPE_COLUMN                  = 9;
    private static final int WIDGET_ID_COLUMN             = 10;
    private static final int WIDGET_TYPE_COLUMN           = 11;
/*
 * 以下局部变量的定义与上面便签基本信息的定义相对应
 */
    private long mId;
    private long mAlertDate;
    private int mBgColorId;
    private long mCreatedDate;
    private boolean mHasAttachment;
    private long mModifiedDate;
    private int mNotesCount;
    private long mParentId;
    private String mSnippet;
    private int mType;
    private int mWidgetId;
    private int mWidgetType;
    private String mName;
    private String mPhoneNumber;

    private boolean mIsLastItem;//布尔变量，若真则表示为最后一个项目
    private boolean mIsFirstItem;//布尔变量，若真则表示为第一个项目
    private boolean mIsOnlyOneItem;//布尔变量，若真则表示当前只有一个项目
    private boolean mIsOneNoteFollowingFolder;//布尔变量，若真则表示文件夹下面只有一个便签
    private boolean mIsMultiNotesFollowingFolder;//布尔变量，若真则表示文件夹下有大于一个的便签
    
    /*
     * 功能描述：为每一个便签属性赋值
     * 实现方式：采用context的上下文和cursor
     */
    public NoteItemData(Context context, Cursor cursor) {
        mId = cursor.getLong(ID_COLUMN);
        //获取id
        mAlertDate = cursor.getLong(ALERTED_DATE_COLUMN);
        //获取提醒日期
        mBgColorId = cursor.getInt(BG_COLOR_ID_COLUMN);
        //获取背景颜色
        mCreatedDate = cursor.getLong(CREATED_DATE_COLUMN);
        //获取创建日期
        mHasAttachment = (cursor.getInt(HAS_ATTACHMENT_COLUMN) > 0) ? true : false;
        //获取是否存在匹配值
        mModifiedDate = cursor.getLong(MODIFIED_DATE_COLUMN);
        //获取修改日期
        mNotesCount = cursor.getInt(NOTES_COUNT_COLUMN);
        //获取便签数量
        mParentId = cursor.getLong(PARENT_ID_COLUMN);
        //获取父文件夹id
        mSnippet = cursor.getString(SNIPPET_COLUMN);
        //获取项的名字
        mSnippet = mSnippet.replace(NoteEditActivity.TAG_CHECKED, "").replace(
                NoteEditActivity.TAG_UNCHECKED, "");
        mType = cursor.getInt(TYPE_COLUMN);
        //获取项的类型
        mWidgetId = cursor.getInt(WIDGET_ID_COLUMN);
        //获取小部件id
        mWidgetType = cursor.getInt(WIDGET_TYPE_COLUMN);
        //获取小部件类型	
        
        mPhoneNumber = "";
        //电话号码初值为空
        if (mParentId == Notes.ID_CALL_RECORD_FOLDER) {
        	//如果当前处于ID_CALL_RECORD_FOLDER转改下
            mPhoneNumber = DataUtils.getCallNumberByNoteId(context.getContentResolver(), mId);
            //获取电话号码
            if (!TextUtils.isEmpty(mPhoneNumber)) {
            	//如果电话号码不为空
                mName = Contact.getContact(context, mPhoneNumber);
                //将电话号码与名字联系人姓名匹配
                if (mName == null) {
                	//如果联系人姓名为空
                    mName = mPhoneNumber;
                    //设置联系人姓名为电话号码
                }
            }
        }

        if (mName == null) {
            mName = "";//如果联系人姓名为空则赋空值
        }
        checkPostion(cursor);
    }
    /*
     * 功能描述：检查当前便签所在的位置 
     */
    private void checkPostion(Cursor cursor) { 
        mIsLastItem = cursor.isLast() ? true : false;
        //判断是否为最后一行
        mIsFirstItem = cursor.isFirst() ? true : false;
        //判断是否为第一行
        mIsOnlyOneItem = (cursor.getCount() == 1);
        //判断是否是当前文件夹只有一个便签
        mIsMultiNotesFollowingFolder = false;
        //初始化“文件夹下有多个便签”为假
        mIsOneNoteFollowingFolder = false;
       //初始化“文件夹下有一个便签”为假
        if (mType == Notes.TYPE_NOTE && !mIsFirstItem) {
        	//如果当前便签的类型是便签类型且不是第一个项目
            int position = cursor.getPosition();
            //获取当前便签的位置
            if (cursor.moveToPrevious()) 
            {
            	//如果前一个不为空
                if (cursor.getInt(TYPE_COLUMN) == Notes.TYPE_FOLDER
                        || cursor.getInt(TYPE_COLUMN) == Notes.TYPE_SYSTEM) {
                	//如果当前cursor指向的类型是ＦＯＬＤＥＲ或ＳＹＳＴＥＭ
                    if (cursor.getCount() > (position + 1)) {
                    	//如果当前cursor的行大于position + 1
                        mIsMultiNotesFollowingFolder = true;
                        //将文件夹下有多个便签置为真
                    } else {
                        mIsOneNoteFollowingFolder = true;
                        //将文件夹下只有一个便签置为真
                    }
                }
                if (!cursor.moveToNext()) {
                	//将cursor移动到下一个项目，如果为空则报错
                    throw new IllegalStateException("cursor move to previous but can't move back");
                }
            }
        }
    }
    /*
     * 功能描述：判断文件夹下是否只有一个便签，返回结果
     */
    public boolean isOneFollowingFolder() {
        return mIsOneNoteFollowingFolder;
    }
    /*
     * 功能描述：判断文件夹下是否有多个便签，返回结果
     */
    public boolean isMultiFollowingFolder() {
        return mIsMultiNotesFollowingFolder;
    }
    /*
     * 功能描述：判断当前项目是否为最后一个项目，返回结果
     */
    public boolean isLast() {
        return mIsLastItem;
    }
    /*
     * 功能描述：返回联系人姓名
     */
    public String getCallName() {
        return mName;
    }
    /*
     * 功能描述：判断当前项目是否为最后第一个项目，返回结果
     */
    public boolean isFirst() {
        return mIsFirstItem;
    }
    /*
     * 功能描述：判断当前是否只有一个项目，返回结果
     */

    public boolean isSingle() {
        return mIsOnlyOneItem;
    }
    /*
     * 功能描述：返回项目id
     */
    public long getId() {
        return mId;
    }
    /*
     * 功能描述：返回提醒日期
     */
    public long getAlertDate() {
        return mAlertDate;
    }
    /*
     * 功能描述：返回创建便签的日期
     */

    public long getCreatedDate() {
        return mCreatedDate;
    }

    /*
     * 功能描述：返回是否存在联系人匹配
     */
    public boolean hasAttachment() {
        return mHasAttachment;
    }

    /*
     * 功能描述：返回修改日期
     */
    public long getModifiedDate() {
        return mModifiedDate;
    }

    /*
     * 功能描述：返回北京颜色的id
     */
    public int getBgColorId() {
        return mBgColorId;
    }

    /*
     * 功能描述：返回父文件夹的id
     */
    public long getParentId() {
        return mParentId;
    }

    /*
     * 功能描述：返回当前文件夹的便签数量
     */
    public int getNotesCount() {
        return mNotesCount;
    }
    /*
     * 功能描述：返回当前文件夹的id，与父文件夹效果相同
     */

    public long getFolderId () {
        return mParentId;
    }

    /*
     * 功能描述：返回当前项目类型
     */
    public int getType() {
        return mType;
    }

    /*
     * 功能描述：返回当前便签生成的桌面小部件类型
     */
    public int getWidgetType() {
        return mWidgetType;
    }
    /*
     * 功能描述：返回当前便签生成的桌面小部件的id
     */
    public int getWidgetId() {
        return mWidgetId;
    }
    /*
     * 功能描述：返回当前项目的名字
     */
    public String getSnippet() {
        return mSnippet;
    }
    /*
     * 功能描述：判断是否存在闹钟（提醒日期），若存在返回真
     */
    public boolean hasAlert() {
        return (mAlertDate > 0);
    }
    /*
     * 功能描述：判断是否存在联系人电话，若为真说明存在
     */
    public boolean isCallRecord() {
        return (mParentId == Notes.ID_CALL_RECORD_FOLDER && !TextUtils.isEmpty(mPhoneNumber));
        //存在的条件是满足当前在ID_CALL_RECORD_FOLDER文件夹下且电话号码不为空
    }
    /*
     * 功能描述：返回便签的类型
     */
    public static int getNoteType(Cursor cursor) {
        return cursor.getInt(TYPE_COLUMN);
    }
}
