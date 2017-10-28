package odometer.hfad.com.joggingtracker.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static odometer.hfad.com.joggingtracker.data.JogContract.AUTHORITY;

public class JogsContentProvider extends ContentProvider {

    private static final int ALL_JOGS = 100;
    private static final int JOG_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, JogContract.JogEntry.TABLE_NAME, ALL_JOGS);
        uriMatcher.addURI(AUTHORITY, JogContract.JogEntry.TABLE_NAME + "/#", JOG_WITH_ID);
        return uriMatcher;
    }

    private JogsDbHelper mJogDb;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mJogDb = new JogsDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mJogDb.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor returnCursor;

        switch (match) {
            case ALL_JOGS:
                returnCursor = db.query(JogContract.JogEntry.TABLE_NAME,
                        null, null, null, null, null, sortOrder);
                break;
            case JOG_WITH_ID:
                String jogId = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{jogId};
                returnCursor = db.query(JogContract.JogEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = mJogDb.getWritableDatabase();
        long rowsChanged;

        rowsChanged = db.insert(JogContract.JogEntry.TABLE_NAME, null, contentValues);
        if (rowsChanged != 0) {
            Log.d("blahblahrowschanged", "insert: success " + rowsChanged);
        } else {
            throw new UnsupportedOperationException("Row not inserted " + uri);
        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String whereClause, @Nullable String[] whereArgs) {
        SQLiteDatabase db = mJogDb.getWritableDatabase();
        int rowsDeleted;
        rowsDeleted = db.delete(JogContract.JogEntry.TABLE_NAME, whereClause, whereArgs);
        if (rowsDeleted != 0) {
            Log.d("blahblahrowschanged", "delete: success " + rowsDeleted);
        } else {
            throw new UnsupportedOperationException("Row not deleted " + uri);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
