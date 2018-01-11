/*
 * 
Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
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

package net.micode.notes.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.micode.notes.data.Notes.DataColumns;
import net.micode.notes.data.Notes.DataConstants;
import net.micode.notes.data.Notes.NoteColumns;


public class NotesDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "note.db";

    private static final int DB_VERSION = 4;
// Interface Is Type: interfaces should describe a type and hence have methods.
    public interface TABLE {
    	//Redundant Modifier:�����public
        public static final String NOTE = "note";

        public static final String DATA = "data";
    }

    private static final String TAG = "NotesDatabaseHelper";

    private static NotesDatabaseHelper mInstance;

    private static final String CREATE_NOTE_TABLE_SQL =
    		//Operator Wrap: '+' should be on a new line.  ����
        "CREATE TABLE " + TABLE.NOTE + "(" +
            NoteColumns.ID + " INTEGER PRIMARY KEY," +
            NoteColumns.PARENT_ID + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.ALERTED_DATE + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.BG_COLOR_ID + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.CREATED_DATE + " INTEGER NOT NULL DEFAULT (strftime('%s','now') * 1000)," +
            NoteColumns.HAS_ATTACHMENT + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.MODIFIED_DATE + " INTEGER NOT NULL DEFAULT (strftime('%s','now') * 1000)," +
            NoteColumns.NOTES_COUNT + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.SNIPPET + " TEXT NOT NULL DEFAULT ''," +
            NoteColumns.TYPE + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.WIDGET_ID + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.WIDGET_TYPE + " INTEGER NOT NULL DEFAULT -1," +
            NoteColumns.SYNC_ID + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.LOCAL_MODIFIED + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.ORIGIN_PARENT_ID + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.GTASK_ID + " TEXT NOT NULL DEFAULT ''," +
            NoteColumns.VERSION + " INTEGER NOT NULL DEFAULT 0" +
        ")";

    private static final String CREATE_DATA_TABLE_SQL =
        "CREATE TABLE " + TABLE.DATA + "(" +
            DataColumns.ID + " INTEGER PRIMARY KEY," +
            DataColumns.MIME_TYPE + " TEXT NOT NULL," +
            DataColumns.NOTE_ID + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.CREATED_DATE + " INTEGER NOT NULL DEFAULT (strftime('%s','now') * 1000)," +
            NoteColumns.MODIFIED_DATE + " INTEGER NOT NULL DEFAULT (strftime('%s','now') * 1000)," +
            DataColumns.CONTENT + " TEXT NOT NULL DEFAULT ''," +
            DataColumns.DATA1 + " INTEGER," +
            DataColumns.DATA2 + " INTEGER," +
            DataColumns.DATA3 + " TEXT NOT NULL DEFAULT ''," +
            DataColumns.DATA4 + " TEXT NOT NULL DEFAULT ''," +
            DataColumns.DATA5 + " TEXT NOT NULL DEFAULT ''" +
        ")";

    private static final String CREATE_DATA_NOTE_ID_INDEX_SQL =
        "CREATE INDEX IF NOT EXISTS note_id_index ON " +
        TABLE.DATA + "(" + DataColumns.NOTE_ID + ");";

    /**
     * Increase folder's note count when move note to the folder
     * 在将便签移至文件夹时增加便签数量，通过Notes_count+1实现
     */
    private static final String NOTE_INCREASE_FOLDER_COUNT_ON_UPDATE_TRIGGER =
        "CREATE TRIGGER increase_folder_count_on_update "+
        " AFTER UPDATE OF " + NoteColumns.PARENT_ID + " ON " + TABLE.NOTE +
        " BEGIN " +
        "  UPDATE " + TABLE.NOTE +
        //Whitespace Around: '+' is not preceded with whitespace.
        "   SET " + NoteColumns.NOTES_COUNT + "=" + NoteColumns.NOTES_COUNT + " + 1" +
        "  WHERE " + NoteColumns.ID + "=new." + NoteColumns.PARENT_ID + ";" +
        " END";

    /**
     * Decrease folder's note count when move note from folder
     * 移除便签时使便签数量减少
     */
    private static final String NOTE_DECREASE_FOLDER_COUNT_ON_UPDATE_TRIGGER =
        "CREATE TRIGGER decrease_folder_count_on_update " +
        " AFTER UPDATE OF " + NoteColumns.PARENT_ID + " ON " + TABLE.NOTE +
        " BEGIN " +
        "  UPDATE " + TABLE.NOTE +
        "   SET " + NoteColumns.NOTES_COUNT + "=" + NoteColumns.NOTES_COUNT + "-1" +
        "  WHERE " + NoteColumns.ID + "=old." + NoteColumns.PARENT_ID +
        "  AND " + NoteColumns.NOTES_COUNT + ">0" + ";" +
        " END";

    /**
     * Increase folder's note count when insert new note to the folder
     * 在文件夹中新建便签时使便签数量增加
     */
    private static final String NOTE_INCREASE_FOLDER_COUNT_ON_INSERT_TRIGGER =
        "CREATE TRIGGER increase_folder_count_on_insert " +
        " AFTER INSERT ON " + TABLE.NOTE +
        " BEGIN " +
        "  UPDATE " + TABLE.NOTE +
        "   SET " + NoteColumns.NOTES_COUNT + "=" + NoteColumns.NOTES_COUNT + " + 1" +
        "  WHERE " + NoteColumns.ID + "=new." + NoteColumns.PARENT_ID + ";" +
        " END";

    /**
     * Decrease folder's note count when delete note from the folder.
     * 从文件夹中删除便签时使便签数量减少
     */
    private static final String NOTE_DECREASE_FOLDER_COUNT_ON_DELETE_TRIGGER =
        "CREATE TRIGGER decrease_folder_count_on_delete " +
        " AFTER DELETE ON " + TABLE.NOTE +
        " BEGIN " +
        "  UPDATE " + TABLE.NOTE +
        "   SET " + NoteColumns.NOTES_COUNT + "=" + NoteColumns.NOTES_COUNT + "-1" +
        "  WHERE " + NoteColumns.ID + "=old." + NoteColumns.PARENT_ID +
        "  AND " + NoteColumns.NOTES_COUNT + ">0;" +
        " END";

    /**
     * Update note's content when insert data with type. {@link DataConstants#NOTE}
     * 当便签插入新内容时进行更新
     */
    private static final String DATA_UPDATE_NOTE_CONTENT_ON_INSERT_TRIGGER =
        "CREATE TRIGGER update_note_content_on_insert " +
        " AFTER INSERT ON " + TABLE.DATA +
        " WHEN new." + DataColumns.MIME_TYPE + "='" + DataConstants.NOTE + "'" +
        " BEGIN" +
        "  UPDATE " + TABLE.NOTE +
        "   SET " + NoteColumns.SNIPPET + "=new." + DataColumns.CONTENT +
        "  WHERE " + NoteColumns.ID + "=new." + DataColumns.NOTE_ID + ";" +
        " END";

    /**
     * Update note's content when data with {@link DataConstants#NOTE} type has changed
     * 当便签数据有所更改时进行更新
     */
    private static final String DATA_UPDATE_NOTE_CONTENT_ON_UPDATE_TRIGGER =
        "CREATE TRIGGER update_note_content_on_update " +
        " AFTER UPDATE ON " + TABLE.DATA +
        " WHEN old." + DataColumns.MIME_TYPE + "='" + DataConstants.NOTE + "'" +
        " BEGIN" +
        "  UPDATE " + TABLE.NOTE +
        "   SET " + NoteColumns.SNIPPET + "=new." + DataColumns.CONTENT +
        "  WHERE " + NoteColumns.ID + "=new." + DataColumns.NOTE_ID + ";" +
        " END";

    /**
     * Update note's content when data with {@link DataConstants#NOTE} type has deleted
     * 当便签内容被删除时进行更新
     */
    private static final String DATA_UPDATE_NOTE_CONTENT_ON_DELETE_TRIGGER =
        "CREATE TRIGGER update_note_content_on_delete " +
        " AFTER delete ON " + TABLE.DATA +
        " WHEN old." + DataColumns.MIME_TYPE + "='" + DataConstants.NOTE + "'" +
        " BEGIN" +
        "  UPDATE " + TABLE.NOTE +
        "   SET " + NoteColumns.SNIPPET + "=''" +
        "  WHERE " + NoteColumns.ID + "=old." + DataColumns.NOTE_ID + ";" +
        " END";

    /**
     * Delete datas belong to note which has been deleted
     * 删除已经被删除的便签中的内容
     */
    private static final String NOTE_DELETE_DATA_ON_DELETE_TRIGGER =
        "CREATE TRIGGER delete_data_on_delete " +
        " AFTER DELETE ON " + TABLE.NOTE +
        " BEGIN" +
        "  DELETE FROM " + TABLE.DATA +
        "   WHERE " + DataColumns.NOTE_ID + "=old." + NoteColumns.ID + ";" +
        " END";

    /**
     * Delete notes belong to folder which has been deleted
     * 删除已经被删除的文件夹中的便签
     */
    private static final String FOLDER_DELETE_NOTES_ON_DELETE_TRIGGER =
        "CREATE TRIGGER folder_delete_notes_on_delete " +
        " AFTER DELETE ON " + TABLE.NOTE +
        " BEGIN" +
        "  DELETE FROM " + TABLE.NOTE +
        "   WHERE " + NoteColumns.PARENT_ID + "=old." + NoteColumns.ID + ";" +
        " END";

    /**
     * Move notes belong to folder which has been moved to trash folder
     * 将被移至垃圾箱中的文件夹中的便签移出
     */
    private static final String FOLDER_MOVE_NOTES_ON_TRASH_TRIGGER =
        "CREATE TRIGGER folder_move_notes_on_trash " +
        " AFTER UPDATE ON " + TABLE.NOTE +
        " WHEN new." + NoteColumns.PARENT_ID + "=" + Notes.ID_TRASH_FOLER +
        " BEGIN" +
        "  UPDATE " + TABLE.NOTE +
        "   SET " + NoteColumns.PARENT_ID + "=" + Notes.ID_TRASH_FOLER +
        "  WHERE " + NoteColumns.PARENT_ID + "=old." + NoteColumns.ID + ";" +
        " END";

    public NotesDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void createNoteTable(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTE_TABLE_SQL);
        reCreateNoteTableTriggers(db);
        createSystemFolder(db);
        Log.d(TAG, "note table has been created");
    }
    //创建Note表的触发器
    //db.execSQL()对数据库进行了多种情况下的处理
    private void reCreateNoteTableTriggers(SQLiteDatabase db) {
        db.execSQL("DROP TRIGGER IF EXISTS increase_folder_count_on_update");
        db.execSQL("DROP TRIGGER IF EXISTS decrease_folder_count_on_update");
        db.execSQL("DROP TRIGGER IF EXISTS decrease_folder_count_on_delete");
        db.execSQL("DROP TRIGGER IF EXISTS delete_data_on_delete");
        db.execSQL("DROP TRIGGER IF EXISTS increase_folder_count_on_insert");
        db.execSQL("DROP TRIGGER IF EXISTS folder_delete_notes_on_delete");
        db.execSQL("DROP TRIGGER IF EXISTS folder_move_notes_on_trash");

        db.execSQL(NOTE_INCREASE_FOLDER_COUNT_ON_UPDATE_TRIGGER);
        db.execSQL(NOTE_DECREASE_FOLDER_COUNT_ON_UPDATE_TRIGGER);
        db.execSQL(NOTE_DECREASE_FOLDER_COUNT_ON_DELETE_TRIGGER);
        db.execSQL(NOTE_DELETE_DATA_ON_DELETE_TRIGGER);
        db.execSQL(NOTE_INCREASE_FOLDER_COUNT_ON_INSERT_TRIGGER);
        db.execSQL(FOLDER_DELETE_NOTES_ON_DELETE_TRIGGER);
        db.execSQL(FOLDER_MOVE_NOTES_ON_TRASH_TRIGGER);
    }

    private void createSystemFolder(SQLiteDatabase db) {    //建立系统文件夹
    	ContentValues values = new ContentValues();

        /**
         * call record foler for call notes
         * 调用记录文件夹来获取电话记录
         * values.put()通过对数据库的操作存入相应信息，put(key,value),key是字段名称，value是字段值
         */
        values.put(NoteColumns.ID, Notes.ID_CALL_RECORD_FOLDER);
        values.put(NoteColumns.TYPE, Notes.TYPE_SYSTEM);
        db.insert(TABLE.NOTE, null, values);

        /**
         * root folder which is default folder
         * 根文件夹错误时
         */
        values.clear();
        values.put(NoteColumns.ID, Notes.ID_ROOT_FOLDER);
        values.put(NoteColumns.TYPE, Notes.TYPE_SYSTEM);
        db.insert(TABLE.NOTE, null, values);

        /**
         * temporary folder which is used for moving note
         * 临时文件夹用来移动便签
         */
        values.clear();
        values.put(NoteColumns.ID, Notes.ID_TEMPARAY_FOLDER);
        values.put(NoteColumns.TYPE, Notes.TYPE_SYSTEM);
        db.insert(TABLE.NOTE, null, values);

        /**
         * create trash folder
         * 创建垃圾箱
         */
        values.clear();
        values.put(NoteColumns.ID, Notes.ID_TRASH_FOLER);
        values.put(NoteColumns.TYPE, Notes.TYPE_SYSTEM);
        db.insert(TABLE.NOTE, null, values);
    }

    public void createDataTable(SQLiteDatabase db) {
        db.execSQL(CREATE_DATA_TABLE_SQL);
        reCreateDataTableTriggers(db);
        db.execSQL(CREATE_DATA_NOTE_ID_INDEX_SQL);
        Log.d(TAG, "data table has been created");
    }

    private void reCreateDataTableTriggers(SQLiteDatabase db) {
        db.execSQL("DROP TRIGGER IF EXISTS update_note_content_on_insert");
        db.execSQL("DROP TRIGGER IF EXISTS update_note_content_on_update");
        db.execSQL("DROP TRIGGER IF EXISTS update_note_content_on_delete");

        db.execSQL(DATA_UPDATE_NOTE_CONTENT_ON_INSERT_TRIGGER);
        db.execSQL(DATA_UPDATE_NOTE_CONTENT_ON_UPDATE_TRIGGER);
        db.execSQL(DATA_UPDATE_NOTE_CONTENT_ON_DELETE_TRIGGER);
    }

    static synchronized NotesDatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NotesDatabaseHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createNoteTable(db);
        createDataTable(db);
    }

    @Override
    //版本更新函数，每种版本对应不同数据库
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        boolean reCreateTriggers = false;
        boolean skipV2 = false;

        if (oldVersion == 1) {
            upgradeToV2(db);
            skipV2 = true; // this upgrade including the upgrade from v2 to v3
            oldVersion++;
        }

        if (oldVersion == 2 && !skipV2) {
            upgradeToV3(db);
            reCreateTriggers = true;
            oldVersion++;
        }

        if (oldVersion == 3) {
            upgradeToV4(db);
            oldVersion++;
        }

        if (reCreateTriggers) {
            reCreateNoteTableTriggers(db);
            reCreateDataTableTriggers(db);
        }

        if (oldVersion != newVersion) {
            throw new IllegalStateException("Upgrade notes database to version " + newVersion
                    + "fails");
        }
    }
    //版本2增添文件表、数据表两种结构
    private void upgradeToV2(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE.NOTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE.DATA);
        createNoteTable(db);
        createDataTable(db);
    }

    private void upgradeToV3(SQLiteDatabase db) {
        // drop unused triggers
    	//褪去不需要的触发器
        db.execSQL("DROP TRIGGER IF EXISTS update_note_modified_date_on_insert");
        db.execSQL("DROP TRIGGER IF EXISTS update_note_modified_date_on_delete");
        db.execSQL("DROP TRIGGER IF EXISTS update_note_modified_date_on_update");
        // add a column for gtask id
        //为GTask创造专栏
        db.execSQL("ALTER TABLE " + TABLE.NOTE + " ADD COLUMN " + NoteColumns.GTASK_ID
                + " TEXT NOT NULL DEFAULT ''");
        // add a trash system folder
        //加入系统垃圾箱
        ContentValues values = new ContentValues();
        values.put(NoteColumns.ID, Notes.ID_TRASH_FOLER);
        values.put(NoteColumns.TYPE, Notes.TYPE_SYSTEM);
        db.insert(TABLE.NOTE, null, values);
    }
    //版本4为VERSION创造专栏
    private void upgradeToV4(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + TABLE.NOTE + " ADD COLUMN " + NoteColumns.VERSION
                + " INTEGER NOT NULL DEFAULT 0");
    }
}
