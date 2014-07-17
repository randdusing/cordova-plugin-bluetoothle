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
namespace Cordova.Extension.Commands
{
    public class BluetoothLePlugin : BaseCommand
    {
        private DispatcherTimer timer;
        DeviceInformationCollection bleDevices;
        string DeviceAddress = null;
        string DeviceName = null;
        int NotifyCharaIndex = 0;
        public string response = "";
        GattCommunicationStatus xxxx;
        bool ResponseScaning = true;
        struct  DeviceServices
        {
            public string StrUuid;
        }
        struct CharacteristicWithValue
        {
            public GattCharacteristic GattCharacteristic { get; set; }
            public string Value;
        }
        CharacteristicWithValue [] currentDeviceCharacteristic;
        CharacteristicWithValue currentDeviceCharacteristicConfig;
        BluetoothLEDevice currentDevice { get; set; }
        DeviceServices[] currentDeviceServices;
        List<string> regex_charauuid = new List<string>();
        List<string> currentDeviceCharacteristicUUid = new List<string>();
        public async void initialize(string options)
        {
            bleDevices = await Windows.Devices.Enumeration.DeviceInformation.FindAllAsync(GattDeviceService.GetDeviceSelectorFromUuid(GattServiceUuids.GenericAccess));
            if (bleDevices.Count == 0)
            {
                DispatchCommandResult(new PluginResult(PluginResult.Status.ERROR, "{\"error\":\"No BLE devices were found or bluetooth disabled\",\"message\":\"Pair the device\"}"));
                Windows.System.Launcher.LaunchUriAsync(new Uri("ms-settings-bluetooth:", UriKind.RelativeOrAbsolute));
            }
            else
            {
                DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"initialized\"}"));
            }
        }
        public async void reconnect(string options)
        {
            BluetoothLEDevice bleDevice = await BluetoothLEDevice.FromIdAsync(bleDevices[0].Id);
            currentDevice = bleDevice;
            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"connected\"}"));
        }
        public void services(string options)
        {
            DeviceAddress = currentDevice.BluetoothAddress.ToString();
            DeviceName = currentDevice.Name.ToString();
            currentDeviceServices = new DeviceServices[currentDevice.GattServices.Count];
            //List<string> serviceList = new List<string>();
            string args = null;
            bool uuidsaccess=false;
            try
            {
                args = WPCordovaClassLib.Cordova.JSON.JsonHelper.Deserialize<string[]>(options)[0];
            }
            catch (FormatException)
            {
            }
            string JsonString = null;
            char[] trimarray = { '\\', '[', ']', '"', ':' };
            string regex_servuuid = Regex.Match(args, @":\[""([0-9a-zA-Z_\-]+)""").Value.Trim(trimarray);
            //ushort servuuid = Convert.ToUInt16(regex_servuuid, 16);
            for (int i=0;i<currentDevice.GattServices.Count;i++)
            {
                //serviceList.Add(service.Uuid.ToString());
                //currentDeviceServices[i].StrUuid = currentDevice.GattServices[i].Uuid.ToString().Substring(4,4);
                currentDeviceServices[i].StrUuid = currentDevice.GattServices[i].Uuid.ToString();
                if(regex_servuuid==currentDeviceServices[i].StrUuid)
                {
                    uuidsaccess=true;
                }
                if (i == currentDevice.GattServices.Count-1)
                    JsonString = JsonString +"\""+currentDeviceServices[i].StrUuid+"\"";
                else
                    JsonString = JsonString + "\"" + currentDeviceServices[i].StrUuid + "\",";
            }
            if (uuidsaccess)
            {
                DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"discoveredServices\",\"serviceUuids\":[" + JsonString + "]}"));
            }
            else
            {
                DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"nodiscoveredServices\",\"serviceUuids\":[" + null + "]}"));
            }
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
            char[] trimarray = {'\\','[',']','"',':'};
            Regex r = new Regex(@"""([0-9a-zA-Z_\-]+)""");
            Match m = r.Match(args);
            bool scanning = true;
            string value=m.Value.Trim(trimarray);
            string regex_servuuid = "";
            while (scanning)
            {
                switch(value)
                {
                    case "serviceUuid":
                        {
                            m = m.NextMatch();
                            value = m.Value.Trim(trimarray);
                            regex_servuuid = value;
                            m = m.NextMatch();
                            value = m.Value.Trim(trimarray);
                            break;
                        }
                    case "characteristicUuids":
                        {
                            m = m.NextMatch();
                            value = m.Value.Trim(trimarray);
                            break;
                        }
                    default:
                        {
                            value = m.Value.Trim(trimarray);
                            if (value == "")
                            {
                                scanning = false;
                                break;
                            }
                            else
                            {
                                regex_charauuid.Add(value);
                                m = m.NextMatch();
                                break;
                            }
                        }
                }
            }
            for (int i = 0; i < currentDeviceServices.Length; i++)
            {
                if (currentDeviceServices[i].StrUuid == regex_servuuid)
                {
                    index = i;
                }
            }
            currentDeviceCharacteristic = new CharacteristicWithValue[regex_charauuid.Count];
            for (int i = 0; i < regex_charauuid.Count; i++)
            {
                currentDeviceCharacteristic[i].GattCharacteristic = currentDevice.GattServices[index].GetCharacteristics(new Guid(regex_charauuid[i]))[0];
                currentDeviceCharacteristicUUid.Add(currentDeviceCharacteristic[i].GattCharacteristic.Uuid.ToString());
                try
                {
                    if (currentDeviceCharacteristic[i].GattCharacteristic.CharacteristicProperties.HasFlag(GattCharacteristicProperties.Notify))
                    {
                        NotifyCharaIndex = i;
                    }
                }
                catch { }

                if (i == regex_charauuid.Count - 1)
                {
                    JsonString = JsonString + "\"" + currentDeviceCharacteristicUUid[i] + "\"";
                }
                else
                {
                    JsonString = JsonString + "\"" + currentDeviceCharacteristicUUid[i] + "\",";
                }
            }
            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"discoveredCharacteristics\",\"serviceUuids\":\"" + regex_servuuid + "\",\"characteristicUuids\":[" + JsonString + "]}"));
        }
        public async void subscribe(string options)
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
            char[] trimarray = { '\\', '[', ']', '"', ':' };
            string regex_servuuid = Regex.Match(args, @":""([0-9a-zA-Z_\-]+)""").Value.Trim(trimarray);
            Regex r = new Regex(@"""([0-9a-zA-Z_\-]+)""");
            Match m = r.Match(args);
            while (m.Value.Trim(trimarray) != "characteristicUuid")
            {
                m = m.NextMatch();
            }
            m = m.NextMatch();
            string regex_charauuid = m.Value.Trim(trimarray);
            try
            {
                if (currentDeviceCharacteristic[NotifyCharaIndex].GattCharacteristic.CharacteristicProperties.HasFlag(GattCharacteristicProperties.Notify))
                {
                    currentDeviceCharacteristic[NotifyCharaIndex].GattCharacteristic.ValueChanged += characteristics_ValueChanged;
                    xxxx = await currentDeviceCharacteristic[NotifyCharaIndex].GattCharacteristic.WriteClientCharacteristicConfigurationDescriptorAsync(GattClientCharacteristicConfigurationDescriptorValue.Notify);
                }
            }
            catch { }
            byte[] data = new byte[] { (byte)'A', (byte)'T', (byte)'I' };
            xxxx = await currentDeviceCharacteristic[1].GattCharacteristic.WriteValueAsync(data.AsBuffer(), GattWriteOption.WriteWithoutResponse);
            currentDeviceCharacteristic[NotifyCharaIndex].Value = "";
            //System.Threading.Thread.Sleep(10000);
            JsonString = JsonString + "\"" + currentDeviceCharacteristic[NotifyCharaIndex].Value + "\"";

            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"subscribedResult\",\"value\":[" + JsonString + "]}"));


        }
        void characteristics_ValueChanged(GattCharacteristic sender, GattValueChangedEventArgs args)
        {
            //Update properties
            if (sender.Uuid == currentDeviceCharacteristic[NotifyCharaIndex].GattCharacteristic.Uuid)
            {
                byte[] data = new byte[args.CharacteristicValue.Length];
                Windows.Storage.Streams.DataReader.FromBuffer(args.CharacteristicValue).ReadBytes(data);
                currentDeviceCharacteristic[NotifyCharaIndex].Value = currentDeviceCharacteristic[NotifyCharaIndex].Value + System.Text.Encoding.UTF8.GetString(data, 0, (int)args.CharacteristicValue.Length);
            }
        }
        public async void read(string option)
        {
            try
            {
                    //Read
                    if (currentDeviceCharacteristic[NotifyCharaIndex].GattCharacteristic.CharacteristicProperties.HasFlag(GattCharacteristicProperties.Read))
                    {
                        var result = await currentDeviceCharacteristic[NotifyCharaIndex].GattCharacteristic.ReadValueAsync(BluetoothCacheMode.Uncached);

                        if (result.Status == GattCommunicationStatus.Success)
                        {
                            byte[] forceData = new byte[result.Value.Length];
                            DataReader.FromBuffer(result.Value).ReadBytes(forceData);
                        }
                    }
            }
            catch (Exception ex)
            {
            }
        }
    }
}