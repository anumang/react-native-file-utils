import React, { Component } from 'react';
import {NativeModules} from 'react-native';

const UtilManager = NativeModules.FileUtilsModule;

export default class RNFU {

  static getPathFromURI(imageUri) {
    return UtilManager ? UtilManager.getPathFromURI(imageUri) : new Promise.resolve(() => imageUri);
  }
}