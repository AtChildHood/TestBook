package com.example.hanjun.testbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hanjun on 2016/4/30.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // 数据库版本号
    private static final int DATABASE_VERSION = 1;
    // 数据库名
    private static final String DATABASE_NAME = "TalkDB.db";

    // 数据表名，一个数据库中可以有多个表（虽然本例中只建立了一个表）
    public static final String TABLE_NAME = "PersonTable";
    public static final String TABLE_NAME_NOtShowMessage = "NOtShowMessage";

    // 构造函数，调用父类SQLiteOpenHelper的构造函数
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version, DatabaseErrorHandler errorHandler)
    {
        super(context, name, factory, version, errorHandler);

    }
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version)
    {
        super(context, name, factory, version);
        // SQLiteOpenHelper的构造函数参数：
        // context：上下文环境
        // name：数据库名字
        // factory：游标工厂（可选）
        // version：数据库模型版本号
    }

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // 数据库实际被创建是在getWritableDatabase()或getReadableDatabase()方法调用时
        Log.d("DatabaseHelper", "DatabaseHelper Constructor");
        // CursorFactory设置为null,使用系统默认的工厂类
    }

    public DatabaseHelper(Context context,String database_name)
    {
        super(context, database_name, null, DATABASE_VERSION);
        // 数据库实际被创建是在getWritableDatabase()或getReadableDatabase()方法调用时
        Log.d("DatabaseHelper", "DatabaseHelper Constructor");
        // CursorFactory设置为null,使用系统默认的工厂类
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // 调用时间：数据库第一次创建时onCreate()方法会被调用
        // onCreate方法有一个 SQLiteDatabase对象作为参数，根据需要对这个对象填充表和初始化数据
        // 这个方法中主要完成创建数据库后对数据库的操作

        Log.d("DatabaseHelper", "DatabaseHelper onCreate");
        // 构建创建表的SQL语句（可以从SQLite Expert工具的DDL粘贴过来加进StringBuffer中）
        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append("CREATE TABLE " + TABLE_NAME + " (");
        sBuffer.append("_id INTEGER NOT NULL PRIMARY KEY, ");
        sBuffer.append("message TEXT);");

        // 执行创建表的SQL语句
        db.execSQL(sBuffer.toString());
        // 即便程序修改重新运行，只要数据库已经创建过，就不会再进入这个onCreate方法

        StringBuffer sBuffercopy = new StringBuffer();
        sBuffercopy.append("CREATE TABLE " + TABLE_NAME_NOtShowMessage + " (");
        sBuffercopy.append("_id INTEGER NOT NULL PRIMARY KEY, ");
        sBuffercopy.append("message TEXT);");
        // 执行创建表的SQL语句
        db.execSQL(sBuffercopy.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // 调用时间：如果DATABASE_VERSION值被改为别的数,系统发现现有数据库版本不同,即会调用onUpgrade
        // onUpgrade方法的三个参数，一个 SQLiteDatabase对象，一个旧的版本号和一个新的版本号
        // 这样就可以把一个数据库从旧的模型转变到新的模型
        // 这个方法中主要完成更改数据库版本的操作
        Log.d("DatabaseHelper", "DatabaseHelper onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        // 上述做法简单来说就是，通过检查常量值来决定如何，升级时删除旧表，然后调用onCreate来创建新表
        // 一般在实际项目中是不能这么做的，正确的做法是在更新数据表结构时，还要考虑用户存放于数据库中的数据不丢失
    }

    public long insert(String message) {
        Log.d("DatabaseHelper", "插入了一条数据");
        SQLiteDatabase db= getWritableDatabase();
        db.beginTransaction();
        ContentValues cv=new ContentValues();
        cv.put("message", message);
        long insertNumber = db.insert(TABLE_NAME, "message", cv);
        db.setTransactionSuccessful();
        db.endTransaction();
        return insertNumber;

    }


    public long insertNotReceiveMessageTable(String message) {
        Log.d("DatabaseHelper", "插入了一条数据到未收到消息数据库中");
        SQLiteDatabase db= getWritableDatabase();
        db.beginTransaction();
        ContentValues cv=new ContentValues();
        cv.put("message", message);
        long insertNumber = db.insert(TABLE_NAME_NOtShowMessage, "message", cv);
        db.setTransactionSuccessful();
        db.endTransaction();
        return insertNumber;

    }
    public void clearNotReceiveMessageTable() {
        Log.d("DatabaseHelper", "清空未收到消息数据库");
        SQLiteDatabase db= getWritableDatabase();
        db.beginTransaction();
        String sql = "DELETE  FROM " + TABLE_NAME_NOtShowMessage + ";";
        db.execSQL(sql);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public Cursor queryNotReceiveMessageTable() {
        SQLiteDatabase db=getReadableDatabase();
        // 调用查找书库代码并返回数据源
        Cursor cursor = db.rawQuery("select count(*)from "+TABLE_NAME_NOtShowMessage,null);
        //游标移到第一条记录准备获取数据
        cursor.moveToFirst();
        // 获取数据中的LONG类型数据
        Long count = cursor.getLong(0);
        return db.query(TABLE_NAME_NOtShowMessage, new String[]{"_id","message"}, "_id >= ? and _id <=?", new String[]{"0",count+""},
                null, null,null);
    }


    /**
     * 删除数据库
     * @param context
     * @return
     */
    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(TABLE_NAME);
    }

    public Cursor query() {

        SQLiteDatabase db=getReadableDatabase();
        // 调用查找书库代码并返回数据源
        Cursor cursor = db.rawQuery("select count(*)from "+TABLE_NAME,null);
        //游标移到第一条记录准备获取数据
        cursor.moveToFirst();
        // 获取数据中的LONG类型数据
        Long count = cursor.getLong(0);
         return db.query(TABLE_NAME, new String[]{"_id","message"}, "_id > ? and _id <=?", new String[]{(count-7)+"",count+""},
                null, null,null);

    }



    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
        // 每次打开数据库之后首先被执行
        Log.d("DatabaseHelper", "DatabaseHelper onOpen");
    }


}
