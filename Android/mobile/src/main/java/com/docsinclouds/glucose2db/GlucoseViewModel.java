package com.docsinclouds.glucose2db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.docsinclouds.glucose2db.GlucoseDataBase.GlucoEntity;
import java.util.List;

public class GlucoseViewModel extends AndroidViewModel {

    private GlucoseRepository mGlucoseRepository;

    private LiveData<List<GlucoEntity>> mGlucoEntities;

    public GlucoseViewModel(@NonNull Application application)
    {
        super(application);
        mGlucoseRepository = new GlucoseRepository(application);
        mGlucoEntities = mGlucoseRepository.getLatestEntities();

    }

    LiveData<List<GlucoEntity>> getAllEntities(){
        return mGlucoEntities;
    }

    public void insert(GlucoEntity glucoEntity){
        mGlucoseRepository.insert(glucoEntity);
    }

    public void deleteAll() { mGlucoseRepository.deleteAll();}



}
