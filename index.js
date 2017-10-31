import React, { Component } from 'react';
import {NativeModules} from 'react-native';

const UtilManager = NativeModules.FileUtilsModule;

export default class RNFU {

  static DocumentDirectoryPath = UtilManager ? UtilManager.RNFUDocumentDirectoryPath : null;
  static PicturesDirectoryPath = UtilManager ? UtilManager.RNFUPicturesDirectoryPath : null;
  static DownloadsDirectoryPath = UtilManager ? UtilManager.RNFUDownloadsDirectoryPath : null;
  static DCIMDirectoryPath = UtilManager ? UtilManager.RNFUDCIMDirectoryPath : null;
  static CachesDirectoryPath = UtilManager ? UtilManager.RNFUCachesDirectoryPath : null;
  static FilesDirectoryPath = UtilManager ? UtilManager.RNFUFilesDirectoryPath : null;

  static getPathFromURI(imageUri) {
    return UtilManager ? UtilManager.getPathFromURI(imageUri) : new Promise.resolve(() => imageUri);
  }
}