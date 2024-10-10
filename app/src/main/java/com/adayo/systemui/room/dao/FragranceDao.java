package com.adayo.systemui.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.adayo.systemui.room.bean.FragranceInfo;

import java.util.List;

@Dao
public interface FragranceDao {

    @Query("select * from FragranceSlotTable")
    List<FragranceInfo> getFragranceInfoList();

    @Query("select * from FragranceSlotTable where position in (:position)")
    FragranceInfo getFragranceInfoByPosition(int position);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFragranceInfo(FragranceInfo fragranceInfo);

    @Update()
    void updateFragranceInfo(FragranceInfo fragranceInfo);

//    @Delete()
//    void deleteFragranceSlotInfo();
}
