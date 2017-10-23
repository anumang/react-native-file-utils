import React, { Component } from 'react';
import {NativeModules} from 'react-native';

const UtilManager = NativeModules.FileUtilsModule;

export default class RNFU {

  static DocumentDirectoryPath = UtilManager.RNFUDocumentDirectoryPath;
  static PicturesDirectoryPath = UtilManager.RNFUPicturesDirectoryPath;
  static DownloadsDirectoryPath = UtilManager.RNFUDownloadsDirectoryPath;
  static DCIMDirectoryPath = UtilManager.RNFUDCIMDirectoryPath;
  static CachesDirectoryPath = UtilManager.RNFUCachesDirectoryPath;
  static FilesDirectoryPath = UtilManager.RNFUFilesDirectoryPath;

  static getPathFromURI(imageUri) {
    return UtilManager ? UtilManager.getPathFromURI(imageUri) : new Promise.resolve(() => imageUri);
  }
}