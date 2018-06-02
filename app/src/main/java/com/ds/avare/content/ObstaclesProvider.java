package com.ds.avare.content;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ds.avare.storage.Preferences;

/**
 * Created by zkhan on 3/10/17.
 */

public class ObstaclesProvider extends MainProvider {


    public static final int OBSTACLES = 300;
    public static final int OBSTACLES_ID = 301;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/rv-obs";

    private static final UriMatcher mURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        mURIMatcher.addURI(ObstaclesContract.AUTHORITY, ObstaclesContract.BASE, OBSTACLES);
        mURIMatcher.addURI(ObstaclesContract.AUTHORITY, ObstaclesContract.BASE + "/#", OBSTACLES_ID);
    }


    @Override
    public String getType(Uri uri) {
        int uriType = mURIMatcher.match(uri);
        switch (uriType) {
            case OBSTACLES:
                return CONTENT_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ObstaclesContract.TABLE);

        int uriType = mURIMatcher.match(uri);
        switch (uriType) {
            case OBSTACLES:
                // no filter
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }


        try {
            Cursor cursor = queryBuilder.query(mDatabaseHelper.getReadableDatabase(),
                    projection, selection, selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        }
        catch (Exception e) {
            // Something wrong, missing or deleted database from download
            resetDatabase();
        }
        return null;
    }


    @Override
    public boolean onCreate() {
        super.onCreate();
        mDatabaseHelper = new ObstaclesDatabaseHelper(getContext(), mPref.mapsFolder());
        return true;
    }

    /**
     * Sync database on folder change, deleted database, new database, and other conditions
     */
    public void resetDatabase() {
        if(mDatabaseHelper != null) {
            mDatabaseHelper.close();
        }
        onCreate();
    }

}
