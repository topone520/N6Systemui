package com.adayo.systemui.room.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class MigrationFrom extends Migration {
    /**
     * Creates a new migration between {@code startVersion} and {@code endVersion}.
     *
     * @param startVersion The start version of the database.
     * @param endVersion   The end version of the database after this migration is applied.
     */
    public MigrationFrom(int startVersion, int endVersion) {
        super(startVersion, endVersion);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `VtpFragranceSlotTable` ("
                + "`position` INTEGER PRIMARY KEY NOT NULL,"
                + "`type` INTEGER NOT NULL,"
                + "`title` TEXT,"
                + "`slide` INTEGER NOT NULL,"
                + "`cover` INTEGER NOT NULL,"
                + "`background` INTEGER NOT NULL,"
                + "`write` INTEGER NOT NULL"
                + ")");

    }
}
