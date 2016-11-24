# React Native Google Login

Support google login without google-services.json file config.

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
    include ':rngooglelogin'
    project(':rngooglelogin').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-google-login/android')
    ```

2. In `android/app/build.gradle`

    ```
    ...
    dependencies {
        ...
        compile project(':rngooglelogin')
    }
    ```

3. Register module (in MainActivity.java)

    ```
    import com.aotasoft.rngooglelogin.GoogleLoginPackage;  // <--- import

    public class MainActivity extends ReactActivity {
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

