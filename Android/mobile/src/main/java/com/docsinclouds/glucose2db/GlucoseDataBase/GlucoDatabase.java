package com.docsinclouds.glucose2db.GlucoseDataBase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {GlucoEntity.class}, version = 2, exportSchema = false)
public abstract class GlucoDatabase extends RoomDatabase {
    public abstract GlucoDao glucoDao();
    private static GlucoDatabase INSTANCE;

    public static GlucoDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GlucoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GlucoDatabase.class, "glucose_database")
                        .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}
