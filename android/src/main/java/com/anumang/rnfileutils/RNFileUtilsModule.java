package com.anumang.rnfileutils;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.database.Cursor;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class RNFileUtilsModule extends ReactContextBaseJavaModule {
  private static final String NAME = "FileUtilsModule";

  private static final String ERROR_UNEXPECTED_EXCEPTION = "UNEXPECTED_EXCEPTION";
  private static final String ERROR_UNHANDLED_FILE_TYPE = "UNHANDLED_FILE_TYPE";

  private  static final String ID_SELECTOR = "_id=?";
  private  static final String DATA_COLUMN = "_data";

  private  static final String CONTENT_SCHEME = "content";
  private  static final String FILE_SCHEME = "file";

  private static final String RNFUDocumentDirectoryPath = "RNFUDocumentDirectoryPath";
  private static final String RNFUPicturesDirectoryPath = "RNFUPicturesDirectoryPath";
  private static final String RNFUDCIMDirectoryPath = "RNFUDCIMDirectoryPath";
  private static final String RNFUCachesDirectoryPath = "RNFUCachesDirectoryPath";
  private static final String RNFUDownloadsDirectoryPath = "RNFUDownloadsDirectoryPath";
  private static final String RNFUFilesDirectoryPath = "RNFUFilesDirectoryPath";

  private static ReactApplicationContext _reactContext;


  public RNFileUtilsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    _reactContext = reactContext;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Map<String, Object> getConstants() {
    return Collections.unmodifiableMap(new HashMap<String, Object>() {
      {
        put(RNFUDocumentDirectoryPath, getDocumentDirectory());
        put(RNFUDownloadsDirectoryPath, getDownloadsDirectory());
        put(RNFUPicturesDirectoryPath, getPicturesDirectory());
        put(RNFUDCIMDirectoryPath, getDCIMDirectory());
        put(RNFUCachesDirectoryPath, getCachesDirectory());
        put(RNFUFilesDirectoryPath, getFilesDirectory());
      }

      private String getDocumentDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
      }

      private String getDownloadsDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
      }

      private String getPicturesDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
      }

      private String getDCIMDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
      }

      private String getCachesDirectory() {
        return _reactContext.getCacheDir().getPath();
      }

      private String getFilesDirectory() {
        return _reactContext.getFilesDir().getPath();
      }

    });
  }


  /**
   * Get the device path from uri
   *
   * @param uriString string.
   * @param promise resul promise
   * @return void
   */
  @ReactMethod
  public void getPathFromURI(String uriString, Promise promise) {
    Uri uri = Uri.parse(uriString);
    String resultPath = null;
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(_reactContext, uri)) {
        final String[] id = getDocumentUriId(uri);
        resultPath = extractDataColumn(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ID_SELECTOR, id);
      } else if (CONTENT_SCHEME.equalsIgnoreCase(uri.getScheme())){
        resultPath = extractDataColumn(uri, null, null);
      } else if (FILE_SCHEME.equalsIgnoreCase(uri.getScheme())) {
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
   * @param uri The Uri to query.
   * @param selection (Optional) Filter used in the query.
   * @param selectionArgs (Optional) Selection arguments used in the query.
   * @return The value of the _data column, which is typically a file path.
   */
  public static String extractDataColumn(Uri uri, String selection,
                                     String[] selectionArgs) {

    Cursor cursor = null;
    try {
      cursor = _reactContext.getContentResolver().query(uri, new String[]{DATA_COLUMN}, selection, selectionArgs,
              null);
      if (cursor != null && cursor.moveToFirst()) {
        final int column_index = cursor.getColumnIndexOrThrow(DATA_COLUMN);
        return cursor.getString(column_index);
      }
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return null;
  }

  private String[] getDocumentUriId(Uri uri) {
    String idString =  DocumentsContract.getDocumentId(uri);
    return new String[] {idString.split(":")[1]};
  }
}
