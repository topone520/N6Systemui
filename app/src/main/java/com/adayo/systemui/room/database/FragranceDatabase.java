package com.adayo.systemui.room.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.adayo.systemui.room.bean.FragranceInfo;
import com.adayo.systemui.room.bean.VtpFragranceInfo;
import com.adayo.systemui.room.dao.FragranceDao;
import com.adayo.systemui.room.dao.VtpFragranceDao;
import com.adayo.systemui.room.migration.MigrationFrom;
import com.android.systemui.SystemUIApplication;

@Database(entities = {FragranceInfo.class, VtpFragranceInfo.class}, version = 2, exportSchema = false)
public abstract class FragranceDatabase extends RoomDatabase {

    public abstract FragranceDao fragranceDao();

    public abstract VtpFragranceDao vtpFragranceDao();

    private static volatile FragranceDatabase mFragranceDatabase;

    public static FragranceDatabase getInstance() {
        if (mFragranceDatabase == null) {
            synchronized (FragranceDatabase.class) {
                if (mFragranceDatabase == null) {
                    mFragranceDatabase = Room.databaseBuilder(SystemUIApplication.getSystemUIContext(), FragranceDatabase.class, "fragranceSlotTable")
                            .addMigrations(new MigrationFrom(1, 2))
                            // 默认不允许在主线程中连接数据库
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return mFragranceDatabase;
    }
}
