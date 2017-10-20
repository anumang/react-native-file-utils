package com.rngrp;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.database.Cursor;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

public class RNFileUtilsModule extends ReactContextBaseJavaModule {
	private static final String NAME = "FileUtilsModule";

	private static final String ERROR_UNEXPECTED_EXCEPTION = "UNEXPECTED_EXCEPTION";
	private static final String ERROR_UNHANDLED_FILE_TYPE = "UNHANDLED_FILE_TYPE";

  private static ReactApplicationContext _reactContext;

  public RNFileUtilsModule(ReactApplicationContext reactContext) {
      super(reactContext);
      _reactContext = reactContext;
  }

  @Override
  public String getName() {
    return NAME;
  }

  private WritableMap makeErrorPayload(Exception ex) {
    WritableMap error = Arguments.createMap();
    error.putString("message", ex.getMessage());
    return error;
  }

  @ReactMethod
  public void getPathFromURI(String uriString, Promise promise) {
    Uri uri = Uri.parse(uriString);
    String resultPath = null;
    try {
      boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
      if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
          final String docId = DocumentsContract.getDocumentId(uri);
          final String[] split = docId.split(":");
          final String type = split[0];
          Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
          final String selection = "_id=?";
          final String[] selectionArgs = new String[] {split[1]};
          resultPath = getDataColumn(context, contentUri, selection, selectionArgs);
      } else if ("content".equalsIgnoreCase(uri.getScheme())){
          resultPath = getDataColumn(context, uri, null, null);
      } else if ("file".equalsIgnoreCase(uri.getScheme())) {
          resultPath = uri.getPath();
      }
      if(resultPath == null) {
        promise.reject(ERROR_UNHANDLED_FILE_TYPE, "File location could not detected.");
      } else {
        promise.resolve(resultPath);
      }
    } catch (Exception ex) {
			ex.printStackTrace();
      promise.reject(ERROR_UNEXPECTED_EXCEPTION, ex.getMessage());
    }
  }

  /**
  * Get the value of the data column for this Uri. This is useful for
  * MediaStore Uris, and other file-based ContentProviders.
  *
  * @param context The context.
  * @param uri The Uri to query.
  * @param selection (Optional) Filter used in the query.
  * @param selectionArgs (Optional) Selection arguments used in the query.
  * @return The value of the _data column, which is typically a file path.
  */
  public static String getDataColumn(Context context, Uri uri, String selection,
          String[] selectionArgs) {

      Cursor cursor = null;
      final String column = "_data";
      final String[] projection = {
              column
      };

      try {
          cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                  null);
          if (cursor != null && cursor.moveToFirst()) {
              final int column_index = cursor.getColumnIndexOrThrow(column);
              return cursor.getString(column_index);
          }
      } finally {
          if (cursor != null)
              cursor.close();
      }
      return null;
  }
}
