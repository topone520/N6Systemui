package com.adayo.systemui.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.adayo.systemui.room.bean.VtpFragranceInfo;

import java.util.List;

@Dao
public interface VtpFragranceDao {

    @Query("select * from VtpFragranceSlotTable")
    List<VtpFragranceInfo> getFragranceInfoList();

    @Query("select * from VtpFragranceSlotTable where position in (:position)")
    VtpFragranceInfo getFragranceInfoByPosition(int position);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFragranceInfo(VtpFragranceInfo fragranceInfo);

    @Update()
    void updateFragranceInfo(VtpFragranceInfo fragranceInfo);

//    @Delete()
//    void deleteFragranceSlotInfo();
}
