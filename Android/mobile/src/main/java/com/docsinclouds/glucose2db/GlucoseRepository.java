package com.docsinclouds.glucose2db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import com.docsinclouds.glucose2db.GlucoseDataBase.GlucoDao;
import com.docsinclouds.glucose2db.GlucoseDataBase.GlucoDatabase;
import com.docsinclouds.glucose2db.GlucoseDataBase.GlucoEntity;
import java.util.List;

public class GlucoseRepository {

    private static GlucoseRepository mGlucoseRepository;

    private GlucoDao mGlucoDao;
    private LiveData<List<GlucoEntity>> glucoEntityList;

    public GlucoseRepository(Application application) {
        GlucoDatabase appDatabase = GlucoDatabase.getDatabase(application);
        mGlucoDao = appDatabase.glucoDao();
        glucoEntityList = mGlucoDao.loadLatestValuesFromDB(51);
    }

    public LiveData<List<GlucoEntity>> getLatestEntities() {
        return glucoEntityList;
    }

    public void insert(GlucoEntity glucoEntity) {
        new insertAsyncTask(mGlucoDao).execute(glucoEntity);
    }

    public void trimDb(int amountToKeep) {
        new trimAsyncTask(mGlucoDao).execute(amountToKeep);
    }

    public void deleteAll(){ new deleteAllEntriesAsyncTask(mGlucoDao).execute();}

    private static class insertAsyncTask extends AsyncTask<GlucoEntity, Void, Void> {

        private GlucoDao mGlucoDao;

        insertAsyncTask(GlucoDao dao) {
            mGlucoDao = dao;
        }

        @Override
        protected Void doInBackground(final GlucoEntity... glucoEntities) {
            mGlucoDao.insertGlucovalue(glucoEntities[0]);
            return null;
        }
    }

    private static class trimAsyncTask extends AsyncTask<Integer, Void, Void> {

        private GlucoDao mGlucoDao;

        trimAsyncTask(GlucoDao dao) {
            mGlucoDao = dao;
        }

        @Override
        protected Void doInBackground(final Integer... amount) {
            mGlucoDao.trimDB(amount[0]);
            return null;
        }
    }

    private static class deleteAllEntriesAsyncTask extends AsyncTask<Void, Void, Void>{
        private GlucoDao mAsyncGlucoDao;

        deleteAllEntriesAsyncTask(GlucoDao dao) {
            mAsyncGlucoDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncGlucoDao.deleteAll();
            return null;
        }
    }
}
