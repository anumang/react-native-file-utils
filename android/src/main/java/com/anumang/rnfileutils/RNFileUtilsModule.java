package com.anumang.rnfileutils;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.database.Cursor;
import android.content.ContentUris;

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

  private  static final String PRIMARY_TYPE = "primary";
  private  static final String IMAGE_TYPE = "image";
  private  static final String VIDEO_TYPE = "video";
  private  static final String AUDIO_TYPE = "audio";

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
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
      }

      private String getDownloadsDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
      }

      private String getPicturesDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
      }

      private String getDCIMDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
      }

      private String getCachesDirectory() {
        return _reactContext.getCacheDir().getAbsolutePath();
      }

      private String getFilesDirectory() {
        return _reactContext.getFilesDir().getAbsolutePath();
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
  try{
      Uri uri = Uri.parse(uriString);
      final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

      // DocumentProvider
      if (isKitKat && DocumentsContract.isDocumentUri(_reactContext, uri)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
          final String docId = DocumentsContract.getDocumentId(uri);
          final String[] split = docId.split(":");
          final String type = split[0];

          if (PRIMARY_TYPE.equalsIgnoreCase(type)) {
            promise.resolve(Environment.getExternalStorageDirectory() + "/" + split[1]);
          }

          // TODO handle non-primary volumes
        }
        // DownloadsProvider
        else if (isDownloadsDocument(uri)) {

          final String id = DocumentsContract.getDocumentId(uri);
          final Uri contentUri = ContentUris.withAppendedId(
                  Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

          promise.resolve(getDataColumn(contentUri, null, null));
        }
        // MediaProvider
        else if (isMediaDocument(uri)) {
          final String docId = DocumentsContract.getDocumentId(uri);
          final String[] split = docId.split(":");
          final String type = split[0];

          Uri contentUri = null;
          if (IMAGE_TYPE.equals(type)) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
          } else if (VIDEO_TYPE.equals(type)) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
          } else if (AUDIO_TYPE.equals(type)) {
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
          }

          final String selection = ID_SELECTOR;
          final String[] selectionArgs = new String[] {
                  split[1]
          };

          promise.resolve(getDataColumn(contentUri, selection, selectionArgs));
        }
      }
      // MediaStore (and general)
      else if (CONTENT_SCHEME.equalsIgnoreCase(uri.getScheme())) {

        // Return the remote address
        if (isGooglePhotosUri(uri))
          promise.resolve(uri.getLastPathSegment());

        promise.resolve(getDataColumn( uri, null, null));
      }
      // File
      else if (FILE_SCHEME.equalsIgnoreCase(uri.getScheme())) {
        promise.resolve(uri.getPath());
      }

      promise.reject(ERROR_UNHANDLED_FILE_TYPE, "File location could not detected.");
  } catch (Exception ex) {
    ex.printStackTrace();
    promise.reject(ERROR_UNEXPECTED_EXCEPTION, ex.getMessage());
  }
  }

  public static String getDataColumn(Uri uri, String selection,
                                     String[] selectionArgs) {

    Cursor cursor = null;
    final String column = DATA_COLUMN;
    final String[] projection = {
            column
    };

    try {
      cursor = _reactContext.getContentResolver().query(uri, projection, selection, selectionArgs,
              null);
      if (cursor != null && cursor.moveToFirst()) {
        final int index = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(index);
      }
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return null;
  }


  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is ExternalStorageProvider.
   */
  public static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is DownloadsProvider.
   */
  public static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is MediaProvider.
   */
  public static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is Google Photos.
   */
  public static boolean isGooglePhotosUri(Uri uri) {
    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
  }
}
