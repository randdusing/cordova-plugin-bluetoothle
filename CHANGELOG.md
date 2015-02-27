# Change Log
All notable changes to this project will be documented in this file from 2.1.0 onwards.


## 2.1.0 - 2015-02-26
### Added
- Request connection priority support added on Android although throughput not personally tested by me. Updated documentation and example as well [\#134](https://github.com/randdusing/BluetoothLE/issues/134) [\#136](https://github.com/randdusing/BluetoothLE/issues/136)
- Retrieve connected support for Android. Some older Android versions / older devices wouldn't include paired devices in a scan. Doesn't support UUID filtering like iOS

### Changes
- Android projects should target API 21 [\#134](https://github.com/randdusing/BluetoothLE/issues/134) [\#136](https://github.com/randdusing/BluetoothLE/issues/136)

### Fixed
- Issue involving status receiver if initialize was passed null (or invalid) parameters [\#114](https://github.com/randdusing/BluetoothLE/issues/114)
- Issue with discovery status after disconnecting and reconnecting without closing on Android [\#141](https://github.com/randdusing/BluetoothLE/issues/141)
- Issue with unexpected disconnect getting stuck on disconnect on Android 5.0+ [\#139](https://github.com/randdusing/BluetoothLE/issues/139)
- Issue with isConnected returning the negated value on IOS [\#130](https://github.com/randdusing/BluetoothLE/issues/130)
- Issue with retrieveConnected in example app

### Documentation
- Updated discovery documentation to include params [\#137](https://github.com/randdusing/BluetoothLE/issues/137)