using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Devices.Bluetooth;
using Windows.Devices.Bluetooth.GenericAttributeProfile;
using Windows.Devices.Enumeration;
using Windows.Devices.Enumeration.Pnp;
using Windows.Storage.Streams;
using WPCordovaClassLib.Cordova;
using WPCordovaClassLib.Cordova.Commands;
using WPCordovaClassLib.Cordova.JSON;
using System.Text.RegularExpressions;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Windows.Threading;
using System.Threading;
using System.Windows;
using Windows.UI.Core;
using System.Diagnostics;
namespace Cordova.Extension.Commands
{
    public class BluetoothLePlugin : BaseCommand
    {
        DeviceInformationCollection bleDevices;
        BluetoothLEDevice bleDevice;
        int waiting_time=50;
        string callbackId_sub="";
        struct  DeviceServices
        {
            public string StrUuid;
        }
        public struct JavaScriptServiceArgs
        {
            public string service;
            public string[] characteristics;
            public string CCCD;
            public byte[] value;
            public JavaScriptServiceArgs(string service,int CharaCount)
            {
                this.service = service;
                this.characteristics = new string[CharaCount];
                this.CCCD = null;
                this.value = null;
            }
            public JavaScriptServiceArgs(string service, int CharaCount,string CCCD)
            {
                this.service = service;
                this.characteristics = new string[CharaCount];
                this.CCCD = CCCD;
                this.value = null;
            }
            public JavaScriptServiceArgs(string service, int CharaCount, byte [] value)
            {
                this.service = service;
                this.characteristics = new string[CharaCount];
                this.CCCD = null;
                this.value = new byte[value.Length];
                this.value = value;
            }
        }
        JavaScriptServiceArgs[] CurrentJSGattProfileArgs;
        struct CharacteristicWithValue
        {
            public GattCharacteristic GattCharacteristic { get; set; }
            public string Value;
        }
        CharacteristicWithValue [] currentDeviceCharacteristic;
        BluetoothLEDevice currentDevice { get; set; }
        DeviceServices[] currentDeviceServices;
        public async void initialize(string options)
        {
            bleDevices = await Windows.Devices.Enumeration.DeviceInformation.FindAllAsync(GattDeviceService.GetDeviceSelectorFromUuid(GattServiceUuids.GenericAccess));
            if (bleDevices.Count == 0)
            {
                DispatchCommandResult(new PluginResult(PluginResult.Status.ERROR, "{\"error\":\"No BLE devices were found or bluetooth disabled\",\"message\":\"Pair the device\"}"));
                await Windows.System.Launcher.LaunchUriAsync(new Uri("ms-settings-bluetooth:", UriKind.RelativeOrAbsolute));
            }
            else
            {
                DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"initialized\"}"));
            }
        }
        public async void startScan(string options)
        {
            bleDevice = await BluetoothLEDevice.FromIdAsync(bleDevices[0].Id);
            currentDevice = bleDevice;
            string CurrentDeviceAddress = currentDevice.BluetoothAddress.ToString();
            string CurrentDeviceName = currentDevice.Name.ToString();
            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"scanResult\",\"address\":\"" + CurrentDeviceAddress + "\",\"name\":\"" + CurrentDeviceName + "\"}"));
        }
        public async void connect(string options)
        {
            string[] args = null;
            try
            {
                args = WPCordovaClassLib.Cordova.JSON.JsonHelper.Deserialize<string[]>(options);
            }
            catch (FormatException)
            {
            }
            bleDevice = await BluetoothLEDevice.FromIdAsync(bleDevices[0].Id);
            currentDevice = bleDevice;
            string status;
            string CurrentDeviceName=null;
            string CurrentDeviceAddress = null;
            PluginResult result;
            if (currentDevice.ConnectionStatus.ToString() == "Disconnected")
            {
                await Windows.System.Launcher.LaunchUriAsync(new Uri("ms-settings-bluetooth:", UriKind.RelativeOrAbsolute));
                status = "connecting";
                CurrentDeviceAddress = currentDevice.BluetoothAddress.ToString();
                CurrentDeviceName = currentDevice.Name.ToString();
                callbackId_sub = args[args.Length-1];
                result = new PluginResult(PluginResult.Status.OK, "{\"status\":\"" + status + "\",\"address\":\"" + CurrentDeviceAddress + "\",\"name\":\"" + CurrentDeviceName + "\"}");
                result.KeepCallback = true;
                DispatchCommandResult(result, callbackId_sub);
            }
            while(currentDevice.ConnectionStatus.ToString() != "Connected")
            {}
            status = "connected";
            CurrentDeviceAddress = currentDevice.BluetoothAddress.ToString();
            CurrentDeviceName = currentDevice.Name.ToString();
            result = new PluginResult(PluginResult.Status.OK, "{\"status\":\"" + status + "\",\"address\":\"" + CurrentDeviceAddress + "\",\"name\":\"" + CurrentDeviceName + "\"}");
            result.KeepCallback = false;
            DispatchCommandResult(result, callbackId_sub);
        }
        public async void disconnect(string options)
        {
            string[] args = null;
            try
            {
                args = WPCordovaClassLib.Cordova.JSON.JsonHelper.Deserialize<string[]>(options);
            }
            catch (FormatException)
            {
            }
            string status;
            PluginResult result;
            await Windows.System.Launcher.LaunchUriAsync(new Uri("ms-settings-bluetooth:", UriKind.RelativeOrAbsolute));
            if (currentDevice.ConnectionStatus.ToString() == "Connected")
            {
                status = "disconnecting";
                callbackId_sub = args[args.Length - 1];
                result = new PluginResult(PluginResult.Status.OK, "{\"status\":\"" + status + "\"}");
                result.KeepCallback = true;
                DispatchCommandResult(result, callbackId_sub);
            }
            while (currentDevice.ConnectionStatus.ToString() != "Disconnected")
            { }
                currentDevice = null;
                status = "disconnected";
                result = new PluginResult(PluginResult.Status.OK, "{\"status\":\"" + status + "\"}");
                result.KeepCallback = false;
                DispatchCommandResult(result, callbackId_sub);
        }
        public async void reconnect(string options)
        {
            string[] args = null;
            try
            {
                args = WPCordovaClassLib.Cordova.JSON.JsonHelper.Deserialize<string[]>(options);
            }
            catch (FormatException)
            {
            }
            string status;
            string CurrentDeviceName = null;
            string CurrentDeviceAddress = null;
            PluginResult result;
            bleDevice = await BluetoothLEDevice.FromIdAsync(bleDevices[0].Id);
            currentDevice = bleDevice;
            if (currentDevice.ConnectionStatus.ToString() == "Disconnected")
            {
                await Windows.System.Launcher.LaunchUriAsync(new Uri("ms-settings-bluetooth:", UriKind.RelativeOrAbsolute));
                status = "connecting";
                CurrentDeviceAddress = currentDevice.BluetoothAddress.ToString();
                CurrentDeviceName = currentDevice.Name.ToString();
                callbackId_sub = args[args.Length - 1];
                result = new PluginResult(PluginResult.Status.OK, "{\"status\":\"" + status + "\",\"address\":\"" + CurrentDeviceAddress + "\",\"name\":\"" + CurrentDeviceName + "\"}");
                result.KeepCallback = true;
                DispatchCommandResult(result, callbackId_sub);
            }
            while (currentDevice.ConnectionStatus.ToString() != "Connected")
            { }
            status = "connected";
            CurrentDeviceAddress = currentDevice.BluetoothAddress.ToString();
            CurrentDeviceName = currentDevice.Name.ToString();
            result = new PluginResult(PluginResult.Status.OK, "{\"status\":\"" + status + "\",\"address\":\"" + CurrentDeviceAddress + "\",\"name\":\"" + CurrentDeviceName + "\"}");
            result.KeepCallback = false;
            DispatchCommandResult(result, callbackId_sub);
        }
        public void services(string options)
        {
            //string DeviceAddress = currentDevice.BluetoothAddress.ToString();
            //string DeviceName = currentDevice.Name.ToString();
            currentDeviceServices = new DeviceServices[currentDevice.GattServices.Count];
            string args = null;
            try
            {
                args = WPCordovaClassLib.Cordova.JSON.JsonHelper.Deserialize<string[]>(options)[0];
            }
            catch (FormatException)
            {
            }
            string JsonString = null;
            //char[] trimarray = { '\\', '[', ']', '"', ':' };
            //string regex_servuuid = Regex.Match(args, @":\[""([0-9a-zA-Z_\-\s]+)""").Value.Trim(trimarray);
            bool ShortUuidFlag=false;
            string servuuid = null;
            JavaScriptArgs(args);
            for (int servJSindex = 0; servJSindex < CurrentJSGattProfileArgs.Length; servJSindex++)
            {
                if (CurrentJSGattProfileArgs[servJSindex].service.Length == 4)
                {
                    servuuid = GattCharacteristic.ConvertShortIdToUuid(Convert.ToUInt16(CurrentJSGattProfileArgs[servJSindex].service, 16)).ToString();
                    ShortUuidFlag = true;
                }
                else
                {
                    servuuid = CurrentJSGattProfileArgs[servJSindex].service;
                }
                for (int i = 0; i < currentDevice.GattServices.Count; i++)
                {
                    currentDeviceServices[i].StrUuid = currentDevice.GattServices[i].Uuid.ToString();
                    if (servuuid == currentDeviceServices[i].StrUuid)
                    {
                        if (servJSindex == CurrentJSGattProfileArgs.Length - 1)
                        {
                            if (ShortUuidFlag)
                            {
                                JsonString = JsonString + "\"" + currentDeviceServices[i].StrUuid.Substring(4, 4) +"\"";
                            }
                            else
                            {
                                JsonString = JsonString + "\"" + currentDeviceServices[i].StrUuid + "\"";
                            }
                        }
                        else
                        {
                            if (ShortUuidFlag)
                            {
                                JsonString = JsonString + "\"" + currentDeviceServices[i].StrUuid.Substring(4, 4)+ "\",";
                            }
                            else
                            {
                                JsonString = JsonString + "\"" + currentDeviceServices[i].StrUuid + "\",";
                            }
                        }
                    }
                }
            }
            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"discoveredServices\",\"serviceUuids\":[" + JsonString + "]}"));
        }
        public void characteristics(string options)
        {
            string args = null;
            try
            {
                args = WPCordovaClassLib.Cordova.JSON.JsonHelper.Deserialize<string[]>(options)[0];
            }
            catch (FormatException)
            {
            }
            string JsonString = null;
            int index = 0;
            string servuuid = null;
            JavaScriptArgs(args);
            bool ShortUuidFlag = false;
            if (CurrentJSGattProfileArgs[0].service.Length == 4)
            {
                servuuid = GattCharacteristic.ConvertShortIdToUuid(Convert.ToUInt16(CurrentJSGattProfileArgs[0].service, 16)).ToString();
                ShortUuidFlag = true;
            }
            else
            {
                servuuid = CurrentJSGattProfileArgs[0].service;
            }
            for (int i = 0; i < currentDeviceServices.Length; i++)
            {
                if (currentDeviceServices[i].StrUuid == servuuid)
                {
                    index = i;
                }
            }
            currentDeviceCharacteristic = new CharacteristicWithValue[CurrentJSGattProfileArgs[0].characteristics.Length];
            for (int i = 0; i < CurrentJSGattProfileArgs[0].characteristics.Length; i++)
            {
                if (ShortUuidFlag)
                {
                    currentDeviceCharacteristic[i].GattCharacteristic = currentDevice.GattServices[index].
                        GetCharacteristics(GattCharacteristic.ConvertShortIdToUuid(Convert.ToUInt16(CurrentJSGattProfileArgs[0].characteristics[i], 16)))[0];
                }
                else
                {
                    currentDeviceCharacteristic[i].GattCharacteristic = currentDevice.GattServices[index].GetCharacteristics(new Guid(CurrentJSGattProfileArgs[0].characteristics[i]))[0];
                }
                if (i == CurrentJSGattProfileArgs[0].characteristics.Length - 1)
                {
                    if (ShortUuidFlag)
                    {
                        JsonString = JsonString + "\"" + currentDeviceCharacteristic[i].GattCharacteristic.Uuid.ToString().Substring(4, 4) + "\"";
                    }
                    else
                    {
                        JsonString = JsonString + "\"" + currentDeviceCharacteristic[i].GattCharacteristic.Uuid.ToString() + "\"";
                    }
                }
                else
                {
                    if (ShortUuidFlag)
                    {
                        JsonString = JsonString + "\"" + currentDeviceCharacteristic[i].GattCharacteristic.Uuid.ToString().Substring(4, 4) + "\",";
                    }
                    else
                    {
                        JsonString = JsonString + "\"" + currentDeviceCharacteristic[i].GattCharacteristic.Uuid.ToString() + "\",";
                    }
                }
            }
            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"discoveredCharacteristics\",\"serviceUuids\":\"" + CurrentJSGattProfileArgs[0].service + "\",\"characteristicUuids\":[" + JsonString + "]}"));
        }
        public void descriptors(string options)
        {
            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"discoveredDescriptors\"}"));
        }
        public void subscribe(string options)
        {
            string[] args = null;
            try
            {
                args = WPCordovaClassLib.Cordova.JSON.JsonHelper.Deserialize<string[]>(options);
            }
            catch (FormatException)
            {
            }
            JavaScriptArgs(args[0]);
            bool ShortUuidFlag = false;
            string servuuid = null;
            int index = 0;
            if (CurrentJSGattProfileArgs[0].service.Length == 4)
            {
                servuuid = GattCharacteristic.ConvertShortIdToUuid(Convert.ToUInt16(CurrentJSGattProfileArgs[0].service, 16)).ToString();
                ShortUuidFlag = true;
            }
            else
            {
                servuuid = CurrentJSGattProfileArgs[0].service;
            }
            for (int i = 0; i < currentDeviceServices.Length; i++)
            {
                if (currentDeviceServices[i].StrUuid == servuuid)
                {
                    index = i;
                }
            }
            currentDeviceCharacteristic = new CharacteristicWithValue[CurrentJSGattProfileArgs[0].characteristics.Length];
            for (int i = 0; i < CurrentJSGattProfileArgs[0].characteristics.Length; i++)
            {
                if (ShortUuidFlag)
                {
                    currentDeviceCharacteristic[i].GattCharacteristic = currentDevice.GattServices[index].
                        GetCharacteristics(GattCharacteristic.ConvertShortIdToUuid(Convert.ToUInt16(CurrentJSGattProfileArgs[0].characteristics[i], 16)))[0];
                }
                else
                {
                    currentDeviceCharacteristic[i].GattCharacteristic = currentDevice.GattServices[index].GetCharacteristics(new Guid(CurrentJSGattProfileArgs[0].characteristics[i]))[0];
                }
                if (currentDeviceCharacteristic[i].GattCharacteristic.CharacteristicProperties.HasFlag(GattCharacteristicProperties.Notify))
                {
                    Deployment.Current.Dispatcher.BeginInvoke(async () =>
                    {
                        await currentDeviceCharacteristic[i].GattCharacteristic.WriteClientCharacteristicConfigurationDescriptorAsync(GattClientCharacteristicConfigurationDescriptorValue.Notify);
                        Thread.Sleep(waiting_time);
                        currentDeviceCharacteristic[i].GattCharacteristic.ValueChanged += this.characteristics_ValueChanged;
                    });
                    callbackId_sub = args[args.Length - 1];
                    PluginResult result = new PluginResult(PluginResult.Status.OK, "{\"status\":\"subscribed\",\"value\":\"\"}");
                    result.KeepCallback = true;
                    DispatchCommandResult(result, callbackId_sub);
                    break;
                }
            }
        }
        public async void write(string options)
        {
            string[] args = null;
            try
            {
                args = WPCordovaClassLib.Cordova.JSON.JsonHelper.Deserialize<string[]>(options);
            }
            catch (FormatException)
            {
            }
            JavaScriptArgs(args[0]);
            bool ShortUuidFlag = false;
            string servuuid = null;
            int index = 0;
            if (CurrentJSGattProfileArgs[0].service.Length == 4)
            {
                servuuid = GattCharacteristic.ConvertShortIdToUuid(Convert.ToUInt16(CurrentJSGattProfileArgs[0].service, 16)).ToString();
                ShortUuidFlag = true;
            }
            else
            {
                servuuid = CurrentJSGattProfileArgs[0].service;
            }
            for (int i = 0; i < currentDeviceServices.Length; i++)
            {
                if (currentDeviceServices[i].StrUuid == servuuid)
                {
                    index = i;
                }
            }
            currentDeviceCharacteristic = new CharacteristicWithValue[CurrentJSGattProfileArgs[0].characteristics.Length];
            for (int i = 0; i < CurrentJSGattProfileArgs[0].characteristics.Length; i++)
            {
                if (ShortUuidFlag)
                {
                    currentDeviceCharacteristic[i].GattCharacteristic = currentDevice.GattServices[index].
                        GetCharacteristics(GattCharacteristic.ConvertShortIdToUuid(Convert.ToUInt16(CurrentJSGattProfileArgs[0].characteristics[i], 16)))[0];
                }
                else
                {
                    currentDeviceCharacteristic[i].GattCharacteristic = currentDevice.GattServices[index].GetCharacteristics(new Guid(CurrentJSGattProfileArgs[0].characteristics[i]))[0];
                }
                if (currentDeviceCharacteristic[i].GattCharacteristic.CharacteristicProperties.HasFlag(GattCharacteristicProperties.WriteWithoutResponse))
                {
                    await currentDeviceCharacteristic[i].GattCharacteristic.WriteValueAsync(CurrentJSGattProfileArgs[0].value.AsBuffer(), GattWriteOption.WriteWithoutResponse);
                    Thread.Sleep(waiting_time);
                    DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"written\",\"value\":\"\"}"));
                }
            }
        }
        public void characteristics_ValueChanged(GattCharacteristic sender, GattValueChangedEventArgs EventArgs)
        {
            string base64String = null;
            string JsonString=null;
            byte[] forceData = new byte[EventArgs.CharacteristicValue.Length];
            DataReader.FromBuffer(EventArgs.CharacteristicValue).ReadBytes(forceData);
            Thread.Sleep(waiting_time);
            base64String = System.Convert.ToBase64String(forceData, 0, forceData.Length);
            //currentDeviceCharacteristic[NotifyCharaIndex].Value = currentDeviceCharacteristic[NotifyCharaIndex].Value + System.Text.Encoding.UTF8.GetString(data, 0, (int)EventArgs.CharacteristicValue.Length);
            //JsonString = "\"" + System.Text.Encoding.UTF8.GetString(data, 0, (int)EventArgs.CharacteristicValue.Length)+ "\"";
            JsonString = "\"" + base64String + "\"";
            //JsonString = Regex.Replace(JsonString, "\n", "\\n");
            //JsonString = Regex.Replace(JsonString, "\r", "\\r");
            PluginResult result = new PluginResult(PluginResult.Status.OK, "{\"status\":\"subscribedResult\",\"value\":" + JsonString + "}");
            result.KeepCallback = true;
            DispatchCommandResult(result, callbackId_sub);
        }
        public async void unsubscribe(string options)
        {
            string[] args = null;
            try
            {
                args = WPCordovaClassLib.Cordova.JSON.JsonHelper.Deserialize<string[]>(options);
            }
            catch (FormatException)
            {
            }
            JavaScriptArgs(args[0]);
            bool ShortUuidFlag = false;
            string servuuid = null;
            int index = 0;
            if (CurrentJSGattProfileArgs[0].service.Length == 4)
            {
                servuuid = GattCharacteristic.ConvertShortIdToUuid(Convert.ToUInt16(CurrentJSGattProfileArgs[0].service, 16)).ToString();
                ShortUuidFlag = true;
            }
            else
            {
                servuuid = CurrentJSGattProfileArgs[0].service;
            }
            for (int i = 0; i < currentDeviceServices.Length; i++)
            {
                if (currentDeviceServices[i].StrUuid == servuuid)
                {
                    index = i;
                }
            }
            currentDeviceCharacteristic = new CharacteristicWithValue[CurrentJSGattProfileArgs[0].characteristics.Length];
            for (int i = 0; i < CurrentJSGattProfileArgs[0].characteristics.Length; i++)
            {
                if (ShortUuidFlag)
                {
                    currentDeviceCharacteristic[i].GattCharacteristic = currentDevice.GattServices[index].
                        GetCharacteristics(GattCharacteristic.ConvertShortIdToUuid(Convert.ToUInt16(CurrentJSGattProfileArgs[0].characteristics[i], 16)))[0];
                }
                else
                {
                    currentDeviceCharacteristic[i].GattCharacteristic = currentDevice.GattServices[index].GetCharacteristics(new Guid(CurrentJSGattProfileArgs[0].characteristics[i]))[0];
                }
                if (currentDeviceCharacteristic[i].GattCharacteristic.CharacteristicProperties.HasFlag(GattCharacteristicProperties.Notify))
                {

                    await currentDeviceCharacteristic[i].GattCharacteristic.WriteClientCharacteristicConfigurationDescriptorAsync(GattClientCharacteristicConfigurationDescriptorValue.None);
                    Thread.Sleep(waiting_time);
                    currentDeviceCharacteristic[i].GattCharacteristic.ValueChanged -= this.characteristics_ValueChanged;
                    DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"unsubscribed\",\"value\":\"\"}"));
                    break;
                }
            }
        }
        public async void read(string options)
        {
            string args = null;
            try
            {
                args = WPCordovaClassLib.Cordova.JSON.JsonHelper.Deserialize<string[]>(options)[0];
            }
            catch (FormatException)
            {
            }
            JavaScriptArgs(args);
            bool ShortUuidFlag = false;
            string servuuid = null;
            int index = 0;
            if (CurrentJSGattProfileArgs[0].service.Length == 4)
            {
                servuuid = GattCharacteristic.ConvertShortIdToUuid(Convert.ToUInt16(CurrentJSGattProfileArgs[0].service, 16)).ToString();
                ShortUuidFlag = true;
            }
            else
            {
                servuuid = CurrentJSGattProfileArgs[0].service;
            }
            for (int i = 0; i < currentDeviceServices.Length; i++)
            {
                if (currentDeviceServices[i].StrUuid == servuuid)
                {
                    index = i;
                }
            }
            currentDeviceCharacteristic = new CharacteristicWithValue[CurrentJSGattProfileArgs[0].characteristics.Length];
            for (int i = 0; i < CurrentJSGattProfileArgs[0].characteristics.Length; i++)
            {
                if (ShortUuidFlag)
                {
                    currentDeviceCharacteristic[i].GattCharacteristic = currentDevice.GattServices[index].
                        GetCharacteristics(GattCharacteristic.ConvertShortIdToUuid(Convert.ToUInt16(CurrentJSGattProfileArgs[0].characteristics[i], 16)))[0];
                }
                else
                {
                    currentDeviceCharacteristic[i].GattCharacteristic = currentDevice.GattServices[index].GetCharacteristics(new Guid(CurrentJSGattProfileArgs[0].characteristics[i]))[0];
                }
                try
                {
                    //Read
                    string base64String = null;
                    if (currentDeviceCharacteristic[i].GattCharacteristic.CharacteristicProperties.HasFlag(GattCharacteristicProperties.Read))
                    {
                        var result = await currentDeviceCharacteristic[i].GattCharacteristic.ReadValueAsync(BluetoothCacheMode.Uncached);
                        if (result.Status == GattCommunicationStatus.Success)
                        {
                            byte[] forceData = new byte[result.Value.Length];
                            DataReader.FromBuffer(result.Value).ReadBytes(forceData);
                            try
                            {
                                base64String = System.Convert.ToBase64String(forceData, 0, forceData.Length);
                            }
                            catch (System.ArgumentNullException)
                            {
                            }
                        }
                    }
                    DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"read\",\"value\":\"" + base64String + "\"}"));
                    break;
                }
                catch (Exception ex)
                {
                }
            }
        }
        public async void readDescriptor(string options)
        {
            string[] args = null;
            try
            {
                args = WPCordovaClassLib.Cordova.JSON.JsonHelper.Deserialize<string[]>(options);
            }
            catch (FormatException)
            {
            }
            JavaScriptArgs(args[0]);
            bool ShortUuidFlag = false;
            string base64String = null;
            string servuuid = null;
            int index = 0;
            if (CurrentJSGattProfileArgs[0].service.Length == 4)
            {
                servuuid = GattCharacteristic.ConvertShortIdToUuid(Convert.ToUInt16(CurrentJSGattProfileArgs[0].service, 16)).ToString();
                ShortUuidFlag = true;
            }
            else
            {
                servuuid = CurrentJSGattProfileArgs[0].service;
            }
            for (int i = 0; i < currentDeviceServices.Length; i++)
            {
                if (currentDeviceServices[i].StrUuid == servuuid)
                {
                    index = i;
                }
            }
            currentDeviceCharacteristic = new CharacteristicWithValue[CurrentJSGattProfileArgs[0].characteristics.Length];
            for (int i = 0; i < CurrentJSGattProfileArgs[0].characteristics.Length; i++)
            {
                if (ShortUuidFlag)
                {
                    currentDeviceCharacteristic[i].GattCharacteristic = currentDevice.GattServices[index].
                        GetCharacteristics(GattCharacteristic.ConvertShortIdToUuid(Convert.ToUInt16(CurrentJSGattProfileArgs[0].characteristics[i], 16)))[0];
                }
                else
                {
                    currentDeviceCharacteristic[i].GattCharacteristic = currentDevice.GattServices[index].GetCharacteristics(new Guid(CurrentJSGattProfileArgs[0].characteristics[i]))[0];
                }
                var CCCD = await currentDeviceCharacteristic[i].GattCharacteristic.ReadClientCharacteristicConfigurationDescriptorAsync();
                switch (CCCD.ClientCharacteristicConfigurationDescriptor.ToString())
                {
                    case "None":
                        {
                            base64String = System.Convert.ToBase64String(new byte[2] { 0x00,0x00 }, 0, 2);
                            break;
                        }
                    case "Notify":
                        {
                            base64String = System.Convert.ToBase64String(new byte[2] {0x01,0x00}, 0, 2);
                            break;
                        }
                    case "Indicate":
                        {
                            base64String = System.Convert.ToBase64String(new byte[2] { 0x02,0x00 }, 0, 2);
                            break;
                        }
                }
                DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"readDescriptor\",\"value\":\"" + base64String + "\"}"));
                break;
            }
        }
        public void close(string options)
        {
            bleDevices = null;
            currentDevice = null;
            currentDeviceServices = null;
            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"closed\"}"));
        }
        public void JavaScriptArgs (string args)
        {
            List<string> services = new List<string>();
            List<string> characteristics = new List<string>();
            List<string> CCCD = new List<string>();
            string charavalue = null;
            bool servicflag = false;
            bool charcterflag = false;
            bool CCCDflag= false;
            bool valueflag = false;
            char[] trimarray = { '\\', '[', ']', '"', ':' };
            Regex r = new Regex(@"""([0-9a-zA-Z_\-\s]+)""");
            Match m = r.Match(args);
            bool scanning = true;
            string value = m.Value.Trim(trimarray);
            while (scanning)
            {
                switch (value)
                {
                    case "serviceUuids":
                        {
                            servicflag = true;
                            charcterflag = false;
                            CCCDflag = false;
                            valueflag = false;
                            m = m.NextMatch();
                            value = m.Value.Trim(trimarray);
                            break;
                        }
                    case "serviceUuid":
                        {
                            servicflag = true;
                            charcterflag = false;
                            CCCDflag = false;
                            valueflag = false;
                            m = m.NextMatch();
                            value = m.Value.Trim(trimarray);
                            break;
                        }
                    case "characteristicUuids":
                        {
                            servicflag = false;
                            charcterflag = true;
                            CCCDflag = false;
                            valueflag = false;
                            m = m.NextMatch();
                            value = m.Value.Trim(trimarray);
                            break;
                        }
                    case "characteristicUuid":
                        {
                            servicflag = false;
                            charcterflag = true;
                            CCCDflag = false;
                            valueflag = false;
                            m = m.NextMatch();
                            value = m.Value.Trim(trimarray);
                            break;
                        }
                    case "descriptorUuid":
                        {
                            servicflag = false;
                            charcterflag = false;
                            CCCDflag = true;
                            valueflag = false;
                            m = m.NextMatch();
                            value = m.Value.Trim(trimarray);
                            break;
                        }
                    case "value":
                        {
                            servicflag = false;
                            charcterflag = false;
                            CCCDflag = false;
                            valueflag = true;
                            m = m.NextMatch();
                            value = m.Value.Trim(trimarray);
                            break;
                        }

                    default:
                        {
                            if (value == "")
                            {
                                scanning = false;
                                break;
                            }
                            else
                            {
                                if (servicflag)
                                    services.Add(value);
                                else if (charcterflag)
                                    characteristics.Add(value);
                                else if (CCCDflag)
                                    CCCD.Add(value);
                                else if (valueflag)
                                    charavalue = value;
                                m = m.NextMatch();
                                value = m.Value.Trim(trimarray);
                                break;
                            }
                        }
                }
            }
            CurrentJSGattProfileArgs = new JavaScriptServiceArgs[services.Count];
            for (int i = 0; i < services.Count; i++)
            {
                if (CCCD.Count == 0 && charavalue==null)
                    CurrentJSGattProfileArgs[i] = new JavaScriptServiceArgs(services[i],characteristics.Count);
                else if(CCCD.Count!=0)
                    CurrentJSGattProfileArgs[i] = new JavaScriptServiceArgs(services[i],characteristics.Count,CCCD[0]);
                else if (charavalue!=null)
                    CurrentJSGattProfileArgs[i] = new JavaScriptServiceArgs(services[i], characteristics.Count, System.Text.Encoding.UTF8.GetBytes(charavalue));
                for (int j = 0; j < characteristics.Count; j++)
                {
                    CurrentJSGattProfileArgs[i].characteristics[j] = characteristics[j];
                }
            }
        }
    }
}