# Change Log

## 3.0.1 - 2016-01-26
- Fixed issue with API21+ code running on earlier API versions and causing the plugin to crash.

## 3.0.0 - 2016-01-25
- Removed connecting / disconnecting statuses since they didn't provide much value and complicated the callback logic.
- Shortened serviceUuid, characteristicUuid, descriptorUuid to uuid or service/characteristic/descriptor depending on the context. See the readme for more info.
- Removed error callback from initialize. The success callback will be used to provide enabled or disabled status updates. [\#227](https://github.com/randdusing/BluetoothLE/issues/227)
- Read/Write/Subscribe/Unsubscribe now return the error callback on unexpected disconnects. For example, iOS 9 wouldn't automatically call the error callback for a read operation when unexpectedly disconnected. On iOS 8 and Android, it would. Future versions will add this to other operations like rssi, readDescriptor, writeDescriptor, etc.
- More advertisement data provided on iOS. [\#110](https://github.com/randdusing/BluetoothLE/issues/110)
- Support for new and improved Android scanning. [\#232](https://github.com/randdusing/BluetoothLE/issues/232) Thanks [pscholl](https://github.com/pscholl)
- Fixed bug with scan race condition [\#223](https://github.com/randdusing/BluetoothLE/issues/223)
- Fixed bug where unsubscribe wouldn't throw an error when already unsubscribed and subscribe wouldn't throw an error when already subscribed.
- Fixed bug where error callback was used instead of success callback when the device unexpectedly disconnected. [\#209](https://github.com/randdusing/BluetoothLE/issues/209)
- Fixed bug where non-BLE devices were returned with retrieveConnected on Android
- Added permissions information to Android discovery.
- Updated and simplified Angular wrapper. Timeouts can now specified for almost any type of operation. Improved its example.
- Removed example. Use the Angular wrapper's example instead. Keeping both examples up to date was too time consuming.
- Changed license to MIT.

## 2.7.1 - 2016-01-12
### Changes
- Forgot to increment version

## 2.7.0 - 2015-12-30
### Changes
- Close can now be called if the device isn't already disconnected.

## 2.6.0 - 2015-12-30
### Changes
- Instead of automatically requesting permissions on scan. It can now be done via the hasPermission and requestPermission functions available for Android. This only needs to be done on Android 6.0 / SDK 23.

## 2.5.0 - 2015-11-10
### Fixed
- Scan now works on Android 6.0, but requires ACCESS_COARSE_LOCATION permission. The permission will be requested when startScan is called. If the permission isn't granted, an error of "permissions" will be returned. [\#204](https://github.com/randdusing/BluetoothLE/issues/204)

### Documentation
- Added information about permissions and targetting SDK 23

## 2.4.0 - 2015-10-20
### Added
- iOS supports Android style discovery [\#63](https://github.com/randdusing/BluetoothLE/issues/63)

### Fixed
- isConnected and isDiscovered now have separate error callbacks

## 2.3.0 - 2015-09-27
### Other
- Added support for NPM

## 2.2.0 - 2015-09-27
### Added
- Allow duplicate advertisements packets in iOS [\#184](https://github.com/randdusing/BluetoothLE/issues/184)
- Change MTU on Android [\#183](https://github.com/randdusing/BluetoothLE/pull/183) - Needs additional testing

### Fixed
- Issue with RSSI callback missing device information
- Issue when device name is null

### Documentation
- Many updates pertaining to the changes above
- Cleaned up the example app's code, removed jQuery Mobile

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
