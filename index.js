import React, { Component } from 'react';

const UtilManager = NativeModules.FileUtilsModule;


export default class RNFU extends Component {

  static constants = {
  };

  static propTypes = {
  };

  static defaultProps = {
  };

  constructor() {
    super();
  }

  componentWillMount() {
  }

  componentWillUnmount() {
  }

  componentWillReceiveProps() {
  }

  getPathFromURI(imageUri) {
    return UtilManager ? UtilManager.getPathFromURI(imageUri) : new Promise.resolve(() => imageUri);
  }
}