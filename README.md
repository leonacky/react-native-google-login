# React Native Google Login

Support google login without google-services.json file config.

### Usage
```
var {GoogleLoginManager} = require('react-native-google-login');

GoogleLoginManager.login(function(error, data){
  if (!error) {
    console.log("Login data: ", data);
  } else {
    console.log("Error: ", error);
  }
})
```


### Install

- Run in your project:
```sh
$ npm i -S https://github.com/leonacky/react-native-google-login.git
```

#### iOS
Comming soon

#### Android

1. In `android/setting.gradle`

    ```
    ...
    include ':react-native-google-login'
    project(':react-native-google-login').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-google-login/android')
    ```

2. In `android/app/build.gradle`

    ```
    ...
    dependencies {
        ...
        compile project(':react-native-google-login')
    }
    ```

3. Register module (in MainApplication.java)

    ```
    import com.aotasoft.rngooglelogin.GoogleLoginPackage;  // <--- import

    public class MainApplication extends Application implements ReactApplication {
      ......

      @Override
      protected List<ReactPackage> getPackages() {
        return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
          new VectorIconsPackage(),
          new OrientationPackage(this),
          new GoogleLoginPackage()   // <--- Add here!
      );
    }

      ......

    }
    ```
4. Add Server Api Key to get token (optional)

    in strings.xml add: 
    
    ```
    <string name="rn_google_server_key">xxxxxxxxx.apps.googleusercontent.com</string>
    ```
    
    in manifest.xml add: 
    
    ```
    <meta-data android:name="rn_google_server_key" android:value="@string/rn_google_server_key" />

    ```

