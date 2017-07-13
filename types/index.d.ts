declare namespace BluetoothlePlugin {
    interface Bluetoothle {
        /**
         * Initialize Bluetooth on the device
         * @param initializeResult  The callback that is passed initialize status (enabled/disabled)
         * @param params            Init params
         *
         */
        initialize(
            initializeResult:(result: { status: boolean }) => void,
            params?: InitParams): void;

        /**
         * Enable Bluetooth on the device. Android support only
         * @param enableSuccess The success callback isn't actually used. Listen to initialize callbacks for change in Bluetooth state.
         *                      A successful enable will return a status => enabled via initialize success callback.
         * @param enableError   The callback that will be triggered when the enable operation fails
         *
         */
        enable(
            enableSuccess:(result: { status: boolean }) => void,
            enableError:(error: Error) => void): void;

        /**
         * Disable Bluetooth on the device. Android support only
         * @param disableSuccess The success callback isn't actually used. Listen to initialize callbacks for change in Bluetooth state.
         *                       A successful disable will return an error => enable via initialize error callback.
         * @param disableError   The callback that will be triggered when the disable operation fails
         *
         */
        disable(
            disableSuccess: (result: Error) => void,
            disableError: (error: Error) => void): void;

        /**
         * Scan for Bluetooth LE devices. Since scanning is expensive, stop as soon as possible. The Cordova app should use a timer to limit the scan interval.
         * Android API >= 23 requires ACCESS_COARSE_LOCATION permissions to find unpaired devices.
         * Permissions can be requested by using the hasPermission and requestPermission functions.
         * Android API >= 23 also requires location services to be enabled. Use isLocationEnabled to determine whether location services are enabled.
         * If not enabled, use requestLocation to prompt the location services settings page.
         * @param startScanSuccess The success callback that is passed scan status object
         * @param startScanError   The callback that will be triggered when the start scan operation fails
         * @param params           Scan params
         *
         */
        startScan(
            startScanSuccess:(status: ScanStatus) => void,
            startScanError: (error: Error) => void,
            params?: ScanParams): void;

        /**
         * Stop scan for Bluetooth LE devices. Since scanning is expensive, stop as soon as possible
         * The app should use a timer to limit the scanning time.
         * @param stopScanSuccess   The success callback that is passed status string.
         * @param stopScanError     The callback that will be triggered when the stop scan operation fails
         *
         */
        stopScan(
            stopScanSuccess: (result: { status: string }) => void,
            stopScanError: (error: Error) => void): void;

        /**
         * Retrieved paired Bluetooth LE devices. In iOS, devices that are "paired" to will not return during a normal scan.
         * Callback is "instant" compared to a scan.
         * @param retrieveConnectedSuccess The success callback that is passed array of device objects
         * @param retrieveConnectedError   The callback that will be triggered when the retrieved devices operation fails
         * @param params                   An array of service IDs to filter the retrieval by. If no service IDs are specified, no devices will be returned.
         *
         */
        retrieveConnected(
            retrieveConnectedSuccess: (devices: DeviceInfo[]) => void,
            retrieveConnectedError: (error: Error) => void,
            params?: { services?: string[] }): void;

        /**
         * Bond with a device.
         * The device doesn't need to be connected to initiate bonding. Android support only.
         * @param bondSuccess  The first success callback should always return with status == bonding.
         *                     If the bond is created, the callback will return again with status == bonded.
         *                     If the bonding popup is canceled or the wrong code is entered, the callback will return again with status == unbonded.
         * @param bondError    The callback that will be triggered when the bond operation fails
         * @param params       The address/identifier provided by the scan's return object
         *
         */
        bond(
            bondSuccess: (status: DeviceInfo) => void,
            bondError: (error: Error) => void,
            params: { address: string }): void;

        /**
         * Unbond with a device. The device doesn't need to be connected to initiate bonding. Android support only.
         * @param unbondSuccess  The success callback should always return with status == unbonded, that is passed with device object
         * @param unbondError    The callback that will be triggered when the unbond operation fails
         * @param params         The address/identifier
         *
         */
        unbond(
            unbondSuccess: (status: DeviceInfo) => void,
            unbondError: (error: Error) => void,
            params: { address: string }): void;

        /**
         * Connect to a Bluetooth LE device
         * @param connectSuccess The success callback that is passed with device object
         * @param connectError   The callback that will be triggered when the connect operation fails
         * @param params         The address/identifier
         *
         */
        connect(
            connectSuccess: (status: DeviceInfo) => void,
            connectError: (error: Error) => void,
            params: { address: string, autoConnect?: boolean }): void;

        /**
         * Reconnect to a previously connected Bluetooth device
         * @param reconnectSuccess  The success callback that is passed with device object
         * @param reconnectError    The callback that will be triggered when the reconnect operation fails
         * @param params            The address/identifier
         *
         */
        reconnect(
            reconnectSuccess: (status: DeviceInfo) => void,
            reconnectError: (error: Error) => void,
            params: { address: string }): void;

        /**
         * Disconnect from a Bluetooth LE device.
         * It's simpler to just call close(). Starting with iOS 10, disconnecting before closing seems required!
         * @param disconnectSuccess The success callback that is passed with device object
         * @param disonnectError    The callback that will be triggered when the disconnect  operation fails
         * @param params            The address/identifier
         *
         */
        disconnect(
            disconnectSuccess: (status: DeviceInfo) => void,
            disonnectError: (error: Error) => void,
            params: { address: string }): void;

        /**
         * Close/dispose a Bluetooth LE device.
         * Prior to 2.7.0, you needed to disconnect to the device before closing, but this is no longer the case.
         * Starting with iOS 10, disconnecting before closing seems required!
         * @param closeSuccess The success callback that is passed with device object
         * @param closeError   The callback that will be triggered when the close  operation fails
         * @param params       The address/identifier
         *
         */
        close(
            closeSuccess: (status: DeviceInfo) => void,
            closeError: (error: Error) => void,
            params: { address: string }): void;

        /**
         * Discover all the devices services, characteristics and descriptors.
         * Doesn't need to be called again after disconnecting and then reconnecting.
         * If using iOS, you shouldn't use discover and services/characteristics/descriptors on the same device.
         * There seems to be an issue with calling discover on iOS8 devices, so use with caution.
         * On some Android versions, the discovered services may be cached for a device.
         * Subsequent discover events will make use of this cache.
         * If your device's services change, set the clearCache parameter to force Android to re-discover services.
         * @param discoverSuccess   The success callback that is passed with device object (contains array of service objects)
         * @param discoverError     The callback that will be triggered when the discover operation fails
         * @param params            The address/identifier
         *
         */
        discover(
            discoverSuccess: (device: Device) => void,
            discoverError: (error: Error) => void,
            params: { address: string, clearCache?: boolean }): void;

        /**
         * Discover the device's services.
         * Not providing an array of services will return all services and take longer to discover. iOS support only.
         * @param servicesSuccess The success callback that is passed with services
         * @param servicesError   The callback that will be triggered when the services operation fails
         * @param params          The address/identifier and an array of service IDs to filter the scan or empty array / null
         *
         */
        services(
            servicesSuccess: (services: Services) => void,
            servicesError: (error: Error) => void,
            params: { address: string, services?: string[] }): void;

        /**
         * Discover the service's characteristics.
         * Not providing an array of characteristics will return all characteristics and take longer to discover. iOS support only.
         * @param characteristicsSuccess The success callback that is passed with array of characteristics
         * @param characteristicsError   The callback that will be triggered when the characteristics operation fails
         * @param params                 Characteristic params
         *
         */
        characteristics(
            characteristicsSuccess: (characteristics: Characteristics) => void,
            characteristicsError: (error: Error) => void,
            params: CharacteristicParams): void;

        /**
         * Discover the characteristic's descriptors. iOS support only.
         * @param descriptorsSuccess    The success callback that is passed with array of descriptors
         * @param descriptorsError      The callback that will be triggered when the descriptors operation fails
         * @param params                Descriptor params
         *
         */
        descriptors(
            descriptorsSuccess: (descriptors: Descriptors) =>void,
            descriptorsError: (error: Error) => void,
            params: DescriptorParams): void;

        /**
         * Read a particular service's characteristic once
         * @param readSuccess   The success callback that is passed with operarion result
         * @param readError     The callback that will be triggered when the read operation fails
         * @param params        Descriptor params
         *
         */
        read(
            readSuccess: (result: OperationResult) => void,
            readError: (error: Error) => void,
            params: DescriptorParams): void;

        /**
         * Subscribe to a particular service's characteristic.
         * Once a subscription is no longer needed, execute unsubscribe in a similar fashion.
         * The Client Configuration descriptor will automatically be written to enable notification/indication based on the characteristic's properties.
         * @param subscribeSuccess  The success callback that is passed with operarion result
         * @param subscribeError    The callback that will be triggered when the subscribe operation fails
         * @param params            Descriptor params
         *
         */
        subscribe(
            subscribeSuccess: (result: OperationResult) => void,
            subscribeError: (error: Error) => void,
            params: DescriptorParams): void;

        /**
         * Unsubscribe to a particular service's characteristic.
         * @param unsubscribeSuccess    The success callback that is passed with unsubscribe result
         * @param unsubscribeError      The callback that will be triggered when the unsubscribe operation fails
         * @param params                Descriptor params
         */
        unsubscribe(
            unsubscribeSuccess: (result: UnsubscribeResult) => void,
            unsubscribeError: (error: Error) => void,
            params: DescriptorParams): void;

        /**
         * Write a particular service's characteristic
         * @param writeSuccess      The success callback that is passed with operation result
         * @param writeError        The callback that will be triggered when the write operation fails
         * @param params            Descriptor params
         *
         */
        write(
            writeSuccess: (result: OperationResult) => void,
            writeError: (error: Error) => void,
            params: WriteCharacteristicParams): void;

        /**
         * Write Quick / Queue, use this method to quickly execute write without response commands when writing more than 20 bytes at a time.
         * @param writeSuccess      The success callback that is passed with operation result
         * @param writeError        The callback that will be triggered when the write operation fails
         * @param params            Descriptor params
         *
         */
        writeQ(
            writeSuccess: (result: OperationResult) => void,
            writeError: (error: Error) => void,
            params: WriteCharacteristicParams): void;

        /**
         * Read a particular characterist's descriptor
         * @param readDescriptorSuccess  The success callback that is passed with description object
         * @param readDescriptorError    The callback that will be triggered when the read descriptor  operation fails
         * @param params                 Read descriptor's params
         *
         */
        readDescriptor(
            readDescriptorSuccess: (descriptor: Descriptor) => void,
            readDescriptorError: (error: Error) => void,
            params: OperationDescriptorParams): void;

        /**
         * Write a particular characteristic's descriptor. Unable to write characteristic configuration directly to keep in line with iOS implementation.
         * Instead use subscribe/unsubscribe, which will automatically enable/disable notification.
         * @param writeDescriptorSuccess    The success callback that is passed with description object
         * @param writeDescriptorError      The callback that will be triggered when the write descriptor operation fails
         * @param params                    Write descriptor params
         *
         */
        writeDescriptor(
            writeDescriptorSuccess: (descriptor: Descriptor) => void,
            writeDescriptorError: (error: Error) => void,
            params: WriteDescriptorParams): void;

        /**
         * Read RSSI of a connected device. RSSI is also returned with scanning.
         * @param rssiSuccess The success callback that is passed with RSSI object
         * @param rssiError   The callback that will be triggered when rssi operation fails
         * @param params      The address/identifier
         *
         */
        rssi(
            rssiSuccess: (rssi: RSSI) => void,
            rssiError: (error: Error) => void,
            params: { address: string }): void;

        /**
         * Set MTU of a connected device. Android only.
         * @param mtuSuccess The success callback that is passed with MTU object
         * @param mtuError   The callback that will be triggered when mtu operation fails
         * @param params     The address/identifier and mtu value
         *
         */
        mtu(
            mtuSuccess: (mtu: MTU) => void,
            mtuError: (error: Error) => void,
            params: { address: string, mtu?: number }): void;

        /**
         * Request a change in the connection priority to improve throughput when transfer large amounts of data via BLE.
         * Android support only. iOS will return error.
         * @param success   The success callback that is passed with device object
         * @param error     The callback that will be triggered when request connection priority operation fails
         * @param params    The address/identifier and connection priority
         *
         */
        requestConnectionPriority(
            success: (result: DeviceInfo) => void,
            error: (error: Error) => void,
            params: { address: string, connectionPriority: ConnectionPriority}): void;

        /**
         * Determine whether the adapter is initialized. No error callback. Returns true or false
         * @param success  The success callback that is passed with initizialization status
         */
        isInitialized(
            success: (result: { isInitialized: boolean }) => void): void;

        /**
         * Determine whether the adapter is enabled. No error callback
         * @param success The success callback that is passed with enabled status
         */
        isEnabled(
            success: (result: { isEnabled: boolean }) => void): void;

        /**
         * Determine whether the adapter is initialized. No error callback. Returns true or false
         * @param success The success callback that is passed with scan status
         */
        isScanning(
            success: (result: { isScanning: boolean }) => void): void;

        /**
         * Determine whether the device is bonded or not, or error if not initialized. Android support only.
         * @param isBondedSuccess The success callback that is passed with bonded status
         * @param isBondedError   The callback that will be triggered when scanning operation fails
         * @param params          The address/identifier
         *
         */
        isBonded(
            isBondedSuccess: (result: BondedStatus) => void,
            isBondedError: (error: Error) => void,
            params: { address: string }): void;

        /**
         * Determine whether the device was connected, or error if not initialized.
         * @param wasConnectedSuccess The success callback that is passed with previos connection status
         * @param wasConnectedError   The callback that will be triggered when connection operation fails
         * @param params              The address/identifier
         *
         */
        wasConnected(
            wasConnectedSuccess: (result: PrevConnectionStatus) => void,
            wasConnectedError: (error: Error) => void,
            params: { address: string }): void;

        /**
         * Determine whether the device is connected, or error if not initialized or never connected to device
         * @param isConnectedSuccess The success callback that is passed with current connection status
         * @param isConnectedError   The callback that will be triggered when connection operation fails
         * @param params             The address/identifier
         *
         */
        isConnected(
            isConnectedSuccess:(result: CurrConnectionStatus) => void,
            isConnectedError: (error: Error) => void,
            params: { address: string }): void;

        /**
         * Determine whether the device's characteristics and descriptors have been discovered, or error if not initialized or not connected to device.
         * @param isDiscoveredSuccess   The success callback that is passed with discover status
         * @param isDiscoveredError     The callback that will be triggered when discover operation fails
         * @param params                The address/identifier
         *
         */
        isDiscovered(
            isDiscoveredSuccess:(result: DiscoverStatus) => void,
            isDiscoveredError: (error: Error) => void,
            params: { address: string }): void;

        /**
         * Determine whether coarse location privileges are granted since scanning for unpaired devices requies it in Android API 23
         * @param success The success callback that is passed with has permission value
         */
        hasPermission(
            success: (result: { hasPermission: boolean }) => void): void;

        /**
         * Request coarse location privileges since scanning for unpaired devices requires it in Android API 23.
         * Will return an error if called on iOS or Android versions prior to 6.0.
         * @param success The success callback that is passed with request permission value
         */
        requestPermission(
            success: (result: { requestPermission: boolean }) => void): void;

        /**
         * Determine if location services are enabled or not. Location Services are required to find devices in Android API 23
         * @param isLocationEnabledSuccess The success callback that is passed with isLocationEnabled value
         * @param isLocationEnabledError   The callback that will be triggered when isLocationEnabled operation fails
         *
         */
        isLocationEnabled(
            isLocationEnabledSuccess: (result: { isLocationEnabled: boolean }) => void,
            isLocationEnabledError: (error: Error) => void): void;

        /**
         * Prompt location services settings pages. requestLocation property returns whether location services are enabled or disabled.
         * Location Services are required to find devices in Android API 23.
         * @param requestLocationSuccess The success callback that is passed with request location result value
         * @param requestLocationError   The callback that will be triggered when requestLocation operation fails
         *
         */
        requestLocation(
            requestLocationSuccess: (result: { requestLocation: boolean }) => void,
            requestLocationError: (error: Error) => void): void;

        /**
         * Initialize Bluetooth on the device. Must be called before anything else.
         * Callback will continuously be used whenever Bluetooth is enabled or disabled.
         * @param success   The success callback that is passed with InitializeResult object
         * @param error     The callback that will be triggered when initializePeripheral operation fails
         * @param params    Init peripheral params
         *
         */
        initializePeripheral(
            success: (result: InitializeResult) => void,
            error: (error: Error) => void,
            params?: InitPeripheralParams): void;

        /**
         * Add a service with characteristics and descriptors. If more than one service is added, add them sequentially
         * @param success   The success callback that is passed with service id and device's status
         * @param error     The callback that will be triggered when addService operation fails
         * @param params    Service's UUID and array of characteristics
         *
         */
        addService(
            success: (result: { service: string, status: Status }) => void,
            error: (error: Error) => void,
            params: { service: string, characteristics: Characteristic[] }): void;

        /**
         * Remove a service
         * @param success   The success callback that is passed with service id and device's status
         * @param error     The callback that will be triggered when removeService operation fails
         * @param params    Service's UUID
         *
         */
        removeService(
            success: (result: { service: string, status: Status }) => void,
            error: (error: Error) => void,
            params: { service: string }): void;

        /**
         * Remove all services
         * @param success The success callback that is passed with device's status
         * @param error   The callback that will be triggered when removeAllServices operation fails
         *
         */
        removeAllServices(
            success: (result: { status: Status }) => void,
            error: (error: Error) => void): void;

        /**
         * Start advertising as a BLE device. Note: This needs to be improved so services can be used for both Android and iOS.
         * On iOS, the advertising devices likes to rename itself back to the name of the device, i.e. Rand' iPhone
         * @param success The success callback that is passed with device's status
         * @param error   The callback that will be triggered when startAdvertising operation fails
         * @param params  Advertising params
         *
         */
        startAdvertising(
            success: (result: { status: Status}) => void,
            error: (error: Error) => void,
            params: AdvertisingParams): void;

        /**
         * Stop advertising
         * @param success The success callback that is passed with device's status
         * @param error   The callback that will be triggered when stopAdvertising operation fails
         *
         */
        stopAdvertising(
            success: (status: Status) => void,
            error: (error: Error) => void): void;

        /**
         * Determine if app is advertising or not.
         * @param success The success callback that is with passed with advertising status
         * @param error   The callback that will be triggered when isAdvertising operation fails
         *
         */
        isAdvertising(
            success: (result: { status: boolean }) => void,
            error: (error: Error) => void): void;

        /**
         * Respond to a read or write request
         * @param success   The success callback that is passed with device's status
         * @param error     The callback that will be triggered when respond operation fails
         * @param params    Respond params
         *
         */
        respond(
            success: (result: { status: Status }) => void,
            error: (error: Error) => void,
            params: RespondParams): void;

        /**
         * Update a value for a subscription. Currently all subscribed devices will receive update.
         * Device specific updates will be added in the future.
         * If sent equals false in the return value, you must wait for the peripheralManagerIsReadyToUpdateSubscribers event before sending more updates.
         * @param success   The success callback that is passed with device's status and sent value
         * @param error     The callback that will be triggered when notify operation fails
         * @param params    The notify params
         *
         */
        notify(
            success: (result: { status: Status, sent: boolean }) => void,
            error: (error: Error) => void,
            params: NotifyParams): void;

        /**
         * Helper function to convert a base64 encoded string from a characteristic or descriptor value into a uint8Array object
         * @param  value Encoded string which need t be encoded
         * @return       uint8Array object
         */
        encodedStringToBytes(value: string): Uint8Array;

        /**
         * Helper function to convert a unit8Array to a base64 encoded string for a characteric or descriptor write
         * @param  value uint8Array object
         * @return       encoded string
         */
        bytesToEncodedString(value: Uint8Array): string;

        /**
         * Helper function to convert a string to bytes
         * @param  value    Encoded string
         * @return          Array of bytes
         */
        stringToBytes(value: string): Uint8Array;

        /**
         * Helper function to convert bytes to a string.
         * @param  value    Array of bytes
         * @return          Encoded string
         */
        bytesToString(value: Uint8Array): string;
    }

    /* Available status of device */
    type Status = "scanStarted" | "scanStopped" | "scanResult" | "connected" | "disconnected"
                        | "bonding" | "bonded" | "unbonded" | "closed" | "services" | "discovered"
                        | "characteristics" | "descriptors" | "read" | "subscribed" | "unsubscribed"
                        | "subscribedResult" | "written" | "readDescriptor" | "writeDescriptor"
                        | "rssi" | "mtu" | "connectionPriorityRequested" |"enabled" | "disabled"
                        | "readRequested" | "writeRequested" | "mtuChanged" | "notifyReady" | "notifySent"
                        | "serviceAdded" | "serviceRemoved" | "allServicesRemoved" | "advertisingStarted"
                        | "advertisingStopped" | "responded" | "notified";

    /** Avaialable connection priorities */
    type ConnectionPriority = "low" | "balanced" | "high";

    interface Params {
        /** The address/identifier provided by the scan's return object */
        address: string,
        /** The service's ID */
        service: string
    }

    interface InitPeripheralParams {
        /** Should user be prompted to enable Bluetooth */
        request?: boolean,
        /* A unique string to identify your app. Bluetooth Central background mode is required to use this, but background mode doesn't seem to require specifying the restoreKey */
        restoreKey?: string

    }

    interface InitParams extends InitPeripheralParams {
        /** Should change in Bluetooth status notifications be sent */
        statusReceiver?: boolean,
    }

    interface ScanParams {
        /* An array of service IDs to filter the scan or empty array / null. This parameter is not supported on Windows platform yet */
        services?: string[],
        /** True/false to allow duplicate advertisement packets, defaults to false (iOS)*/
        allowDuplicates?: boolean,
        /** Defaults to Low Power. Available from API21 / API 23 (Android) */
        scanMode?: BluetoothScanMode,
        /** Defaults to Aggressive. Available from API23 (Android) */
        matchMode?: BluetoothMatchMode,
        /** Defaults to One Advertisement. Available from API23 (Android) */
        matchNum?: BluetoothMatchNum,
        /** Defaults to All Matches. Available from API21 / API 23. (Android) */
        callbackType?: BluetoothCallbackType
    }

    interface NotifyParams {
        /** Service's UUID */
        service: string,
        /** Characteristic's UUID */
        characteristic: string,
        /** Base64 encoded string, number or string */
        value: string
    }

    interface RespondParams {
        /** This integer value will be incremented every read/writeRequested */
        requestId: number,
        /** base64 string */
        value: string,
        /** not documented */
        offset?: number
    }

    interface CharacteristicParams extends Params {
        /** An array of characteristic IDs to discover or empty array / null */
        characteristics?: string[]
    }

    interface DescriptorParams extends Params {
        /** The characteristic's ID */
        characteristic: string
    }

    interface OperationDescriptorParams  extends DescriptorParams {
        /** The descriptor's ID */
        descriptor: string
    }

    interface WriteCharacteristicParams extends DescriptorParams {
        /* Base64 encoded string */
        value: string,
        /* Set to "noResponse" to enable write without response, all other values will write normally. */
        type?: string
    }

    interface WriteDescriptorParams extends DescriptorParams {
        /** The descriptor's ID */
        descriptor: string,
        /** Base64 encoded string, number or string */
        value: string
    }

    type AdvertisingParams = AdvertisingParamsAndroid | AdvertisingParamsIOS;
    type AdvertiseMode = "balanced" | "lowLatency" | "lowPower";
    type TxPowerLevel = "high" | "low" | "ultralow" | "medium";

    interface AdvertisingParamsAndroid {
        /** Service UUID on Android */
        service: string,
        /** not documented */
        mode?: AdvertiseMode,
        /** not documented */
        connectable?: boolean,
        /** not documented */
        timeout?: number,
        /** not documented */
        txPowerLevel?: TxPowerLevel,
        /** not documented */
        manufacturerId?: number,
        /** not documented */
        manufacturerSpecificData?: any,
        /** not documented */
        includeDeviceName: boolean,
        /** not documented */
        includeTxPowerLevel: boolean
    }

    interface AdvertisingParamsIOS {
        /** Array of service UUIDs on iOS */
        services: string[],
        /** device's name */
        name?: string
    }

    interface CommonInfo {
        /** The device's display name */
        name: string,
        /** The device's address / identifier for connecting to the object */
        address: string,
    }

    interface DeviceInfo extends CommonInfo{
        /** Device's status */
        status: Status;
    }

    interface RSSI extends DeviceInfo {
        /** signal strength */
        rssi: number
    }

    interface MTU extends DeviceInfo {
        /* mtu value */
        mtu: number
    }

    interface BondedStatus extends CommonInfo {
        /** Bonded status*/
        isBonded: boolean
    }

    interface PrevConnectionStatus extends CommonInfo {
        /** Determine whether the device was connected */
        wasConnected: boolean
    }

    interface CurrConnectionStatus extends CommonInfo {
        /** Determine whether the device is connected */
        isConnected: boolean
    }

    interface DiscoverStatus extends CommonInfo {
        /** Determine whether the device's characteristics and descriptors have been discovered */
        isDiscovered: boolean
    }

    interface ScanStatus extends DeviceInfo {
        /** signal strength */
        rssi: number,
        /**
         * advertisement data in encoded string of bytes, use bluetoothle.encodedStringToBytes() (Android)
         * advertisement hash with the keys (iOS)
         * empty (Windows)
         */
        advertisement: {
            /** An array of service UUIDs */
            serviceUuids: string[],
            /** A string representing the name of the manufacturer of the device */
            manufacturerData: string,
            /** A number containing the transmit power of a peripheral */
            txPowerLevel: number,
            /** An array of one or more CBUUID objects, representing CBService UUIDs that were found in the “overflow” area of the advertisement data */
            overflowServiceUuids: string[],
            /** A boolean value that indicates whether the advertising event type is connectable */
            isConnectable: boolean,
            /** An array of one or more CBUUID objects, representing CBService UUIDs */
            solicitedServiceUuids: string[],
            /* A dictionary containing service-specific advertisement data */
            serviceData: any,
            /* A string containing the local name of a peripheral */
            localName: string
        } | string;
    }


    interface Service {
        /** Service's uuid */
        uuid: string,
        /** Array of characteristics */
        characteristics : CharacteristicList[]
    }

    interface Characteristic {
        /* Array of descriptors */
        descriptors?: any,
        /**  Characteristic's uuid */
        uuid: string,
        /**
         *  Characteristi's properties
         *  If the property is defined as a key, the characteristic has that property
         */
        properties?: {
            write?: boolean,
            broadcast?: boolean,
            extendedProps?: boolean,
            writeWithoutResponse?: boolean,
            writeNoResponse?: boolean,
            signedWrite?: boolean,
            read?: boolean,
            notify?: boolean,
            indicate?: boolean,
            authenticatedSignedWrites?: boolean,
            notifyEncryptionRequired?: boolean,
            indicateEncryptionRequired?: boolean
        },
        /**
         *  If the permission is defined as a key, the character has that permission
         */
        permissions?: {
            read?: boolean,
            readEncrypted?: boolean,
            readEncryptedMITM?: boolean
            write?: boolean,
            writeSigned?: boolean,
            writeSignedMITM?: boolean,
            writeEncryptedMITM?: boolean,
            readEncryptionRequired?: boolean,
            writeEncryptionRequired?: boolean
        }
    }

    interface CharacteristicList {
        /** Array of cahracteristic objects */
        characteristics: Characteristic[],
        /** Characteristic's UUID */
        uuid: string
    }

    interface Device extends DeviceInfo {
        /** Device's services */
        sercices: Service[]
    }

    interface Services extends DeviceInfo {
        /** Array of service UUIDS */
        services: string[],
    }

    interface Descriptors extends DeviceInfo {
        /** Characteristic's UUID */
        characteristic: string,
        /** Service's UUID */
        service: string,
        /* Array of descriptor UUIDs */
        descriptors: string[],
    }

    interface OperationResult extends DeviceInfo {
        /** Characteristic UUID */
        characteristic: string,
        /** Service's UUID */
        service: string,
        /** Base64 encoded string of bytes */
        value: string
    }

    interface UnsubscribeResult extends DeviceInfo {
        /** Characteristic UUID */
        characteristic: string,
        /** Service's UUID */
        service: string,
    }

    interface Descriptor extends OperationResult {
        descriptor: string
    }

    interface Characteristics extends DeviceInfo {
        /** Service's id */
        service: string,
        /** Array of characteristic objects*/
        characteristics: Characteristic[],
    }

    interface  InitializeResult {
        /** Device's status */
        status: Status,
        /** The address/identifier provided by the scan's return object */
        address: string,
        /** Service's UUID */
        service: string,
        /** Characteristic UUID */
        characterisitc: string,
        /** This integer value will be incremented every read/writeRequested */
        requestId: number,
        /** Offset value */
        offset: number,
        /** mtu value */
        mtu: number,
        /** Base64 encoded string of bytes */
        value: string
    }

    enum BluetoothScanMode {
        SCAN_MODE_OPPORTUNISTIC = -1,
        SCAN_MODE_LOW_POWER = 0,
        SCAN_MODE_BALANCED = 1,
        SCAN_MODE_LOW_LATENCY = 2
    }

    enum BluetoothMatchMode {
        MATCH_MODE_AGRESSIVE = 1,
        MATCH_MODE_STICKY = 2
    }

    enum BluetoothMatchNum {
        MATCH_NUM_ONE_ADVERTISEMENT = 1,
        MATCH_NUM_FEW_ADVERTISEMENT = 2,
        MATCH_NUM_MAX_ADVERTISEMENT = 3
    }

    enum BluetoothCallbackType {
        CALLBACK_TYPE_ALL_MATCHES = 1,
        CALLBACK_TYPE_FIRST_MATCH = 2,
        CALLBACK_TYPE_MATCH_LOST = 4
    }

    interface Error {
        code: number,
        message: string
    }
}

interface Window {
    bluetoothle: BluetootlePlugin.Bluetoothle
}

declare var bluetoothle: BluetootlePlugin.Bluetoothle;
