package com.docsinclouds.glucose2db.GlucoseDataBase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import java.util.List;

@Dao
public interface GlucoDao {
    @Query("SELECT * FROM GlucoEntity")
    List<GlucoEntity> getAll();

    @Query("SELECT * FROM GlucoEntity WHERE mId IN (:userIds)")
    List<GlucoEntity> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM GlucoEntity ORDER BY timestamps DESC LIMIT :amount")
    LiveData<List<GlucoEntity>> loadLatestValuesFromDB(int amount);

    @Query("SELECT * FROM GlucoEntity ORDER BY timestamps DESC LIMIT :amount")
    List<GlucoEntity> getLatestValueList(int amount);

    @Query("SELECT * FROM GlucoEntity WHERE timestamps <= :timestamp + :range AND " +
        "timestamps >= :timestamp - :range ORDER BY timestamps DESC LIMIT 1")
    GlucoEntity getOneValueInTimeRange(long timestamp, long range);

    @Query("DELETE FROM GlucoEntity WHERE mId NOT IN(SELECT mId FROM GlucoEntity " +
        "ORDER BY timestamps DESC LIMIT :amountToKeep)")
    void trimDB(int amountToKeep);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGlucovalue(GlucoEntity glucoEntity);

    @Insert
    void insertAll(GlucoEntity... glucoEntities);

    @Delete
    void delete(GlucoEntity glucoEntity);

    @Query("DELETE FROM GlucoEntity")
    void deleteAll();


}
