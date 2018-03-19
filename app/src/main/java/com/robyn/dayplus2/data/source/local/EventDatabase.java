package com.robyn.dayplus2.data.source.local;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.robyn.dayplus2.data.MyEvent;

/**
 * To update database table:
 * 1. Update version number
 * 2. call migrate methods
 * <p>
 * Created by yifei on 5/19/2017.
 */

@Database(entities = {MyEvent.class}, version = 9)
public abstract class EventDatabase extends RoomDatabase {
    private static EventDatabase INSTANCE;

    public abstract EventsDao eventsDao();

    @VisibleForTesting
    private static final Migration MIGRATION_2_6 = new Migration(2, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            // Create the new table
            database.execSQL(
                    "CREATE TABLE events_new (uuid TEXT NOT NULL, title TEXT, description TEXT, bgImagePath TEXT, datetime INTEGER NOT NULL, repeatMode INTEGER NOT NULL, categoryCode INTEGER NOT NULL, isStar INTEGER NOT NULL, PRIMARY KEY(uuid))");

            // Add additional column to old table

            database.execSQL("ALTER TABLE Event ADD COLUMN bgImagePath TEXT");

            // Copy the data
            database.execSQL("INSERT INTO events_new (uuid, title, description, bgImagePath, " +
                    "datetime, repeatMode, categoryCode, isStar)" +
                    "SELECT mUUID, mTitle, mDescription, bgImagePath, " +
                    "mDatetime, mRepeatMode, mCategory, mStar FROM Event");

            // Remove the old table
            database.execSQL("DROP TABLE Event");

            // Change the table name to the correct one
            database.execSQL("ALTER TABLE events_new RENAME TO MyEvent");
        }
    };

    @VisibleForTesting
    private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

    @VisibleForTesting
    private static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            // Create the new table
            database.execSQL(
                    "CREATE TABLE events_8 (uuid TEXT NOT NULL, title TEXT, description TEXT, bgImagePath TEXT, datetime INTEGER NOT NULL, " +
                            "repeatMode INTEGER NOT NULL, categoryCode INTEGER NOT NULL, isStar INTEGER NOT NULL, PRIMARY KEY(uuid))");

            // Copy the data
            database.execSQL(
                    "INSERT INTO events_8 (uuid, title, description, bgImagePath, " +
                            "datetime, repeatMode, categoryCode, isStar)" +
                            "SELECT uuid, title, description, bgImagePath, " +
                            "datetime, repeatMode, categoryCode, isStar FROM MyEvent");

            // Remove the old table
            database.execSQL("DROP TABLE MyEvent");

            // Change the table name to the correct one
            database.execSQL("ALTER TABLE events_8 RENAME TO MyEvent");
        }
    };

    @VisibleForTesting
    private static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

    @VisibleForTesting
    private static final Migration MIGRATION_2_9 = new Migration(2, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            // Create the new table
            database.execSQL(
                    "CREATE TABLE events_new (uuid TEXT NOT NULL, title TEXT, description TEXT, " +
                            "bgImagePath TEXT, datetime INTEGER NOT NULL, " +
                            "repeatMode INTEGER NOT NULL, categoryCode INTEGER NOT NULL, " +
                            "isStar INTEGER NOT NULL, PRIMARY KEY(uuid))");


//            // Create the new table
//            database.execSQL(
//                    "CREATE TABLE events_8 (uuid TEXT NOT NULL, title TEXT, description TEXT, " +
//                            "bgImagePath TEXT, datetime INTEGER NOT NULL, " +
//                            "repeatMode INTEGER NOT NULL, categoryCode INTEGER NOT NULL, " +
//                            "isStar INTEGER NOT NULL, PRIMARY KEY(uuid))");

            // Add additional column to old table

            database.execSQL("ALTER TABLE Event ADD COLUMN bgImagePath TEXT");

            // Copy the data
            database.execSQL(
                    "INSERT INTO events_new (uuid, title, description, bgImagePath, " +
                    "datetime, repeatMode, categoryCode, isStar)" +
                    "SELECT mUUID, mTitle, mDescription, bgImagePath, " +
                    "mDatetime, mRepeatMode, mCategory, mStar FROM Event");

//            // Copy the data
//            database.execSQL(
//                    "INSERT INTO events_8 (uuid, title, description, bgImagePath, " +
//                            "datetime, repeatMode, categoryCode, isStar)" +
//                            "SELECT uuid, title, description, bgImagePath, " +
//                            "datetime, repeatMode, categoryCode, isStar FROM MyEvent");

            // Remove the old table
            database.execSQL("DROP TABLE Event");

            // Change the table name to the correct one
            database.execSQL("ALTER TABLE events_new RENAME TO MyEvent");


//
//
//
//            // Remove the old table
//            database.execSQL("DROP TABLE MyEvent");
//
//            // Change the table name to the correct one
//            database.execSQL("ALTER TABLE events_8 RENAME TO MyEvent");
        }
    };

    public static EventDatabase getInMemoryDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room
                    .databaseBuilder(
                            context.getApplicationContext(),
                            EventDatabase.class,
                            "eventDatabase"
                    )
                    .addMigrations(
                            MIGRATION_2_6,
                            MIGRATION_6_7,
                            MIGRATION_7_8,
                            MIGRATION_8_9,
                            MIGRATION_2_9
                    )
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

//    @NonNull
//    @Override
//    public SupportSQLiteOpenHelper getOpenHelper() {
//        INSTANCE.getOpenHelper();
//        MyEvent welcomeItem = new MyEvent(
//                DateTime.now().getMillisOfDay(),
//                "You are with DayPlus!"
//        );
//        INSTANCE.eventsDao().insertEvent(welcomeItem);
//        return super.getOpenHelper();
//    }

}