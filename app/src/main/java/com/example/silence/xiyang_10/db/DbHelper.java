/*
 * Copyright (C) 2015 Federico Iosue (federico.iosue@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.example.silence.xiyang_10.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
//
//import it.feio.android.omniHandEdits.OmniHandEdits;
//import it.feio.android.omniHandEdits.async.upgrade.UpgradeProcessor;

import com.example.silence.xiyang_10.MainActivity;
import com.example.silence.xiyang_10.models.*;
//import it.feio.android.omniHandEdits.utils.*;


public class DbHelper extends SQLiteOpenHelper {

    // Database name
    private static final String DATABASE_NAME = Constants.DATABASE_NAME;
    // Database version aligned if possible to software version
    private static final int DATABASE_VERSION = 502;
    // Sql query file directory
    private static final String SQL_DIR = "sql";

    // HandEdits table name
    public static final String TABLE_HandEditS = "handedits";
    // HandEdits table columns
    public static final String KEY_ID = "creation";
    public static final String KEY_CREATION = "creation";
    public static final String KEY_LAST_MODIFICATION = "last_modification";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_ARCHIVED = "archived";
    public static final String KEY_TRASHED = "trashed";
    public static final String KEY_RECURRENCE_RULE = "recurrence_rule";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_CATEGORY = "category_id";
    public static final String KEY_CHECKLIST = "checklist";

    public static final String KEY_ZAN_NUMBER = "zan_number";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_JSONPATH = "json_path";
    public static final String KEY_COVER = "cover_path";



    // Categories table name
    public static final String TABLE_CATEGORY = "categories";
    // Categories table columns
    public static final String KEY_CATEGORY_ID = "category_id";
    public static final String KEY_CATEGORY_NAME = "name";
    public static final String KEY_CATEGORY_DESCRIPTION = "description";
    public static final String KEY_CATEGORY_COLOR = "color";

    // Queries
    private static final String CREATE_QUERY = "create.sql";
    private static final String UPGRADE_QUERY_PREFIX = "upgrade-";
    private static final String UPGRADE_QUERY_SUFFIX = ".sql";


    private final Context mContext;

    private static DbHelper instance = null;
	private SQLiteDatabase db;


	// 获取activity实例
	public static synchronized DbHelper getInstance() {
		return getInstance(MainActivity.getAppContext());
	}

	// 创建DbHelper对象
	public static synchronized DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelper(context);
        }
        return instance;
    }


    private DbHelper(Context mContext) {
        super(mContext, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = mContext;
    }

    // 获取数据库名
    public String getDatabaseName() {
        return DATABASE_NAME;
    }

	public SQLiteDatabase getDatabase() {
		return getDatabase(false);
	}

    // 获取可读写的数据库
	public SQLiteDatabase getDatabase(boolean forceWritable) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			if (db.isReadOnly() && forceWritable) {
				db = getWritableDatabase();
			}
			return db;
		} catch (IllegalStateException e) {
			return this.db;
		}
	}


    // 通过执行assets中的.sql文件创建表格
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.i(Constants.TAG, "Database creation");
            execSqlFile(CREATE_QUERY, db);
        } catch (IOException exception) {
            throw new RuntimeException("Database creation failed", exception);
        }
    }


    // 更新数据库版本
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		this.db = db;
        Log.i(Constants.TAG, "Upgrading database version from " + oldVersion + " to " + newVersion);

//        UpgradeProcessor.process(oldVersion, newVersion);

        try {
            for (String sqlFile : AssetUtils.list(SQL_DIR, mContext.getAssets())) {
                if (sqlFile.startsWith(UPGRADE_QUERY_PREFIX)) {
                    int fileVersion = Integer.parseInt(sqlFile.substring(UPGRADE_QUERY_PREFIX.length(),
                            sqlFile.length() - UPGRADE_QUERY_SUFFIX.length()));
                    if (fileVersion > oldVersion && fileVersion <= newVersion) {
                        execSqlFile(sqlFile, db);
                    }
                }
            }
            Log.i(Constants.TAG, "Database upgrade successful");

        } catch (IOException e) {
            throw new RuntimeException("Database upgrade failed", e);
        }
    }


    // 插入或更新一条手账内容
    public HandEdit updateHandEdit(HandEdit handedit, boolean updateLastModification) {
        SQLiteDatabase db = getDatabase(true);

        String content = null;

        // To ensure handedit and attachments insertions are atomical and boost performances transaction are used
        db.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, handedit.getTitle());
        values.put(KEY_CONTENT, content);
        values.put(KEY_JSONPATH,handedit.getJson_path());
        values.put(KEY_AUTHOR,handedit.getAuthor());
        values.put(KEY_COVER,handedit.getCover_path());
        values.put(KEY_CREATION, handedit.getCreation() != 0L ? handedit.getCreation() : Calendar.getInstance().getTimeInMillis());
        values.put(KEY_LAST_MODIFICATION, updateLastModification ? Calendar
                .getInstance().getTimeInMillis() : (handedit.getLastModification() != null ? handedit.getLastModification() :
                Calendar.getInstance().getTimeInMillis()));
        values.put(KEY_ARCHIVED, handedit.isArchived());
        values.put(KEY_TRASHED, handedit.isTrashed());
//        values.put(KEY_CATEGORY, handedit.getCategory() != null ? handedit.getCategory().getId() : null);

		db.insertWithOnConflict(TABLE_HandEditS, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
		Log.d(Constants.TAG, "Updated handedit titled '" + handedit.getTitle() + "'");


        db.setTransactionSuccessful();
        db.endTransaction();

        // Fill the handedit with correct data before returning it
        handedit.setCreation(handedit.getCreation() != null ? handedit.getCreation() : values.getAsLong(KEY_CREATION));
        handedit.setLastModification(values.getAsLong(KEY_LAST_MODIFICATION));

        return handedit;
    }


    // 执行.sql文件中的语句
    protected void execSqlFile(String sqlFile, SQLiteDatabase db) throws SQLException, IOException {
        Log.i(Constants.TAG, "  exec sql file: {}" + sqlFile);
        for (String sqlInstruction : SqlParser.parseSqlFile(SQL_DIR + "/" + sqlFile, mContext.getAssets())) {
            Log.v(Constants.TAG, "    sql: {}" + sqlInstruction);
            try {
                db.execSQL(sqlInstruction);
            } catch (Exception e) {
                Log.e(Constants.TAG, "Error executing command: " + sqlInstruction, e);
            }
        }
    }


    /**
     * 通过创建时间的tag获取一个手账内容
     */
    public HandEdit getHandEdit(long id) {

        String whereCondition = " WHERE "
                + KEY_ID + " = " + id;

        List<HandEdit> handedits = getHandEdits(whereCondition, true);
        HandEdit handedit;
        if (handedits.size() > 0) {
            handedit = handedits.get(0);
            Log.d("size","handedit is full");
        } else {
            Log.d("size","handedit is null");
            handedit = null;
        }
        return handedit;
    }


    /**
     * Getting All handedits
     *
     * @param checkNavigation Tells if navigation status (handedits, archived) must be kept in
     *                        consideration or if all handedits have to be retrieved
     * @return HandEdits list
     */
    public List<HandEdit> getAllHandEdits(String username,Boolean checkNavigation) {
        String whereCondition = " WHERE " + KEY_AUTHOR + " = \'"+username+"\' ";
        if (checkNavigation) {
            int navigation = Navigation.getNavigation();
            switch (navigation) {
                case Navigation.HANDEDITS:
                    return getHandEditsActive();
                case Navigation.ARCHIVE:
                    return getHandEditsArchived();
                case Navigation.TRASH:
                    return getHandEditsTrashed();
                case Navigation.UNCATEGORIZED:
                    return getHandEditsUncategorized();
                default:
                    return getHandEdits(whereCondition, true);
            }
        } else {
            return getHandEdits(whereCondition, true);
        }

    }


    public List<HandEdit> getHandEditsActive() {
        String whereCondition = " WHERE " + KEY_ARCHIVED + " IS NOT 1 AND " + KEY_TRASHED + " IS NOT 1 ";
        return getHandEdits(whereCondition, true);
    }


    public List<HandEdit> getHandEditsArchived() {
        String whereCondition = " WHERE " + KEY_ARCHIVED + " = 1 AND " + KEY_TRASHED + " IS NOT 1 ";
        return getHandEdits(whereCondition, true);
    }


    public List<HandEdit> getHandEditsTrashed() {
        String whereCondition = " WHERE " + KEY_TRASHED + " = 1 ";
        return getHandEdits(whereCondition, true);
    }


    public List<HandEdit> getHandEditsUncategorized() {
        String whereCondition = " WHERE "
                + "(" + KEY_CATEGORY_ID + " IS NULL OR " + KEY_CATEGORY_ID + " == 0) "
                + "AND " + KEY_TRASHED + " IS NOT 1";
        return getHandEdits(whereCondition, true);
    }


    public List<HandEdit> getHandEditsWithLocation() {
        String whereCondition = " WHERE " + KEY_LONGITUDE + " IS NOT NULL "
                + "AND " + KEY_LONGITUDE + " != 0 ";
        return getHandEdits(whereCondition, true);
    }


//    /**
//     * Counts words in a HandEdit
//     */
//    public int getWords(HandEdit HandEdit) {
//        int count = 0;
//        String[] fields = {HandEdit.getTitle(), HandEdit.getContent()};
//        for (String field : fields) {
//            boolean word = false;
//            int endOfLine = field.length() - 1;
//            for (int i = 0; i < field.length(); i++) {
//                // if the char is a letter, word = true.
//                if (Character.isLetter(field.charAt(i)) && i != endOfLine) {
//                    word = true;
//                    // if char isn't a letter and there have been letters before,
//                    // counter goes up.
//                } else if (!Character.isLetter(field.charAt(i)) && word) {
//                    count++;
//                    word = false;
//                    // last word of String; if it doesn't end with a non letter, it
//                    // wouldn't count without this.
//                } else if (Character.isLetter(field.charAt(i)) && i == endOfLine) {
//                    count++;
//                }
//            }
//        }
//        return count;
//    }


//    /**
//     * Counts chars in a HandEdit
//     */
//    public int getChars(HandEdit HandEdit) {
//        int count = 0;
//        count += HandEdit.getTitle().length();
//        count += HandEdit.getContent().length();
//        return count;
//    }


    /**
     * 通过where条件获取表中符合条件的手账条目
     */
    public List<HandEdit> getHandEdits(String whereCondition, boolean order) {
        List<HandEdit> HandEditList = new ArrayList<>();

        String sort_column = KEY_CREATION, sort_order = " DESC";


        // Generic query to be specialized with conditions passed as parameter
        String query = "SELECT "
                + KEY_CREATION + ","
                + KEY_LAST_MODIFICATION + ","
                + KEY_ZAN_NUMBER+","
                + KEY_AUTHOR+","
                + KEY_JSONPATH+","
                + KEY_COVER+","
                + KEY_TITLE + ","
                + KEY_CONTENT + ","
                + KEY_ARCHIVED + ","
                + KEY_TRASHED
                + " FROM " + TABLE_HandEditS
                + whereCondition
                + (order ? " ORDER BY " + sort_column + sort_order : "");

        Log.v(Constants.TAG, "Query: " + query);

        Cursor cursor = null;
        try {
            cursor = getDatabase().rawQuery(query, null);

            // Looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    int i = 0;
                    HandEdit HandEdit = new HandEdit();
                    HandEdit.setCreation(cursor.getLong(i++));
                    HandEdit.setLastModification(cursor.getLong(i++));
                    HandEdit.setZan_number(cursor.getLong(i++));
                    HandEdit.setAuthor(cursor.getString(i++));
                    HandEdit.setJson_path(cursor.getString(i++));
                    HandEdit.setCover_path(cursor.getString(i++));
                    HandEdit.setTitle(cursor.getString(i++));
                    HandEdit.setContent(cursor.getString(i++));
                    HandEdit.setArchived("1".equals(cursor.getString(i++)));
                    HandEdit.setTrashed("1".equals(cursor.getString(i++)));


                    // Eventual decryption of content


                    // Adding HandEdit to list
                    if(!HandEdit.isTrashed())
                        HandEditList.add(HandEdit);

                } while (cursor.moveToNext());
            }

        } finally {
            if (cursor != null)
                cursor.close();
        }

        Log.v(Constants.TAG, "Query: Retrieval finished!");
        return HandEditList;
    }


    /**
     * Archives/restore single HandEdit
     */
    public void archiveHandEdit(HandEdit HandEdit, boolean archive) {
        HandEdit.setArchived(archive);
        updateHandEdit(HandEdit, false);
    }


    /**
     * Trashes/restore single HandEdit
     */
    public void trashHandEdit(HandEdit HandEdit, boolean trash) {
        HandEdit.setTrashed(trash);
        updateHandEdit(HandEdit, false);
    }


    /**
     * Deleting single HandEdit
     */
    public boolean deleteHandEdit(HandEdit HandEdit) {
        return deleteHandEdit(HandEdit, false);
    }


    /**
     * Deleting single HandEdit but keeping attachments
     */
    public boolean deleteHandEdit(HandEdit HandEdit, boolean keepAttachments) {
        int deletedHandEdits;
        boolean result = true;
        SQLiteDatabase db = getDatabase(true);
        // Delete HandEdits
        deletedHandEdits = db.delete(TABLE_HandEditS, KEY_ID + " = ?", new String[]{String.valueOf(HandEdit.get_id())});

        // Check on correct and complete deletion
        result = result && deletedHandEdits == 1;
        return result;
    }


    /**
     * Empties trash deleting all trashed HandEdits
     */
    public void emptyTrash() {
        for (HandEdit HandEdit : getHandEditsTrashed()) {
            deleteHandEdit(HandEdit);
        }
    }


    /**
     * Gets HandEdits matching pattern with title or content text
     *
     * @param pattern String to match with
     * @return HandEdits list
     */
    // 搜索算法？
    public List<HandEdit> getHandEditsByPattern(String username,String pattern) {
        int navigation = Navigation.getNavigation();
        String whereCondition = " WHERE "
                + KEY_TRASHED + " IS NOT 1 AND "
                + KEY_AUTHOR + " = \'" + username+"\'"
                + " AND (" + KEY_TITLE + " LIKE '%" + pattern + "%' )";
        return getHandEdits(whereCondition, true);
    }





    /**
     * Retrieves all tags
     */
    public List<Tag> getTags() {
        return getTags(null);
    }


    /**
     * Retrieves all tags of a specified HandEdit
     */
    public List<Tag> getTags(HandEdit HandEdit) {
        List<Tag> tags = new ArrayList<>();
        HashMap<String, Integer> tagsMap = new HashMap<>();

        String whereCondition = " WHERE "
                + (HandEdit != null ? KEY_ID + " = " + HandEdit.get_id() + " AND " : "")
                + "(" + KEY_CONTENT + " LIKE '%#%' OR " + KEY_TITLE + " LIKE '%#%' " + ")"
                + " AND " + KEY_TRASHED + " IS " + (Navigation.checkNavigation(Navigation.TRASH) ? "" : " NOT ") + " 1";
        List<HandEdit> HandEditsRetrieved = getHandEdits(whereCondition, true);

        for (HandEdit HandEditRetrieved : HandEditsRetrieved) {
            HashMap<String, Integer> tagsRetrieved = TagsHelper.retrieveTags(HandEditRetrieved);
            for (String s : tagsRetrieved.keySet()) {
                int count = tagsMap.get(s) == null ? 0 : tagsMap.get(s);
                tagsMap.put(s, ++count);
            }
        }

        for (String s : tagsMap.keySet()) {
            Tag tag = new Tag(s, tagsMap.get(s));
            tags.add(tag);
        }

        Collections.sort(tags, (tag1, tag2) -> tag1.getText().compareToIgnoreCase(tag2.getText()));
        return tags;
    }


    /**
     * Retrieves all HandEdits related to category it passed as parameter
     */
    public List<HandEdit> getHandEditsByTag(String tag) {
        if (tag.contains(",")) {
            return getHandEditsByTag(tag.split(","));
        } else {
            return getHandEditsByTag(new String[]{tag});
        }
    }


    /**
     * Retrieves all HandEdits with specified tags
     */
    public List<HandEdit> getHandEditsByTag(String[] tags) {
        StringBuilder whereCondition = new StringBuilder();
        whereCondition.append(" WHERE ");
        for (int i = 0; i < tags.length; i++) {
            if (i != 0) {
                whereCondition.append(" AND ");
            }
			whereCondition.append("(" + KEY_CONTENT + " LIKE '%").append(tags[i]).append("%' OR ").append(KEY_TITLE)
					.append(" LIKE '%").append(tags[i]).append("%')");
        }
        // Trashed HandEdits must be included in search results only if search if performed from trash
        whereCondition.append(" AND " + KEY_TRASHED + " IS ").append(Navigation.checkNavigation(Navigation.TRASH) ?
                "" : "" +
                " NOT ").append(" 1");

		return rx.Observable.from(getHandEdits(whereCondition.toString(), true))
				.map(handedit -> {
					boolean matches = rx.Observable.from(tags)
							.all(tag -> {
								Pattern p = Pattern.compile(".*(\\s|^)" + tag + "(\\s|$).*", Pattern.MULTILINE);
								return p.matcher((handedit.getTitle() + " " + handedit.getContent())).find();
							}).toBlocking().single();
					return matches ? handedit : null;
				})
				.filter(HandEdit -> HandEdit != null)
				.toList().toBlocking().single();
	}



}
