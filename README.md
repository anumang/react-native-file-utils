# react-native-file-utils

Helper utils for common problems.

Utils:

 * `getPathFromURI` : Convert Uri to real device path for Android
 * Known paths for Android:
   * `PicturesDirectoryPath` : Internal pictures directory 
   * `DownloadsDirectoryPath` : Internal downloads directory
   * `DCIMDirectoryPath` : Internal DCIM directory 
   * `CachesDirectoryPath` : Application cache directory 
   * `FilesDirectoryPath` : Application files directory 

## Installation (Android)

`npm i react-native-file-utils --save`

### Linking

`react-native link react-native-file-utils`

OR

* `android/settings.gradle`

```gradle
...
include ':react-native-file-utils'
project(':react-native-file-utils').projectDir = new File(settingsDir, '../node_modules/react-native-file-utils/android')
```

* `android/app/build.gradle`

```gradle
...
dependencies {
    ...
    compile project(':react-native-file-utils')
}
```

* register module (in MainActivity.java)

```java
import com.anumang.rnfileutils.RNFUPackage; // <------- add package

public class MainActivity extends ReactActivity {
   // ...
    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
        new MainReactPackage(), // <---- add comma
        new RNFUPackage() // <---------- add package
      );
    }
```

## Example usage (Android only)

```javascript
// require the module
var RNFU = require('react-native-file-utils');

RNFU.getPathFromURI(uriString).then(filePath =>
  console.log(filePath)
)
```

## Use Case - get images from CameraRoll as base64

  * Required: react-native-fs
    https://github.com/johanneslumpe/react-native-fs

```javascript
RNFU.getPathFromURI(imageUri).then(path =>
  RNFS.readFile(path, 'base64').then(imageBase64 =>
    console.log(imageBase64)
  )
)
```

## Use Case - create images on DCIM directory as base64

  * Required: react-native-fs
    https://github.com/johanneslumpe/react-native-fs

```javascript
let filePath = RNFU.DCIMDirectoryPath + fileName;
RNFU.writeFile(filePath, base64Content, 'base64').then(path =>
  RNFS.readFile(path, 'base64').then(imageBase64 =>
    console.log(imageBase64)
  )
)
```

