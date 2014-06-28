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
namespace Cordova.Extension.Commands
{
    public class BluetoothLePlugin : BaseCommand
    {
        DeviceInformationCollection bleDevices;
        string DeviceAddress = null;
        string DeviceName = null;
        struct  DeviceServices
        {
            public string StrUuid;
            public int index;
        }
        struct CharacteristicWithValue
        {
            public GattCharacteristic GattCharacteristic { get; set; }
            public byte[] Value { get; set; }
        }
        CharacteristicWithValue currentDeviceCharacteristic = new CharacteristicWithValue();
        BluetoothLEDevice currentDevice { get; set; }
        DeviceServices[] currentDeviceServices;
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
        public void discover(string options)
        {
            DeviceAddress = currentDevice.BluetoothAddress.ToString();
            DeviceName = currentDevice.Name.ToString();
            currentDeviceServices = new DeviceServices[currentDevice.GattServices.Count];
            //List<string> serviceList = new List<string>();
            string JsonString = null;
            for (int i=0;i<currentDevice.GattServices.Count;i++)
            {
                //serviceList.Add(service.Uuid.ToString());
                currentDeviceServices[i].StrUuid = currentDevice.GattServices[i].Uuid.ToString().Substring(4,4);
                currentDeviceServices[i].index = i;
                if (i == currentDevice.GattServices.Count-1)
                    JsonString = JsonString +"\""+currentDeviceServices[i].StrUuid+"\"";
                else
                    JsonString = JsonString + "\"" + currentDeviceServices[i].StrUuid + "\",";
            }

            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"discovered\",\"serviceUuids\":[" + JsonString + "]}"));
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
            char[] trimarray = {'\\','[',']','"',':'};
            string regex_servshortuuid = Regex.Match(args, @":""(\w+)""").Value.Trim(trimarray);
            ushort servshortuuid = Convert.ToUInt16(regex_servshortuuid, 16);
            string regex_charashortuuid = Regex.Match(args, @"\[""(\w+)""\]").Value.Trim(trimarray);
            ushort charashortuuid = Convert.ToUInt16(regex_charashortuuid, 16);
            for(int i=0;i<currentDevice.GattServices.Count;i++)
            {
                if (currentDeviceServices[i].StrUuid == regex_servshortuuid)
                {
                    currentDeviceCharacteristic.GattCharacteristic = currentDevice.GattServices[i].GetCharacteristics(GattCharacteristic.ConvertShortIdToUuid(charashortuuid))[0];
                }
            }
            string currentDeviceCharacteristicUUid = currentDeviceCharacteristic.GattCharacteristic.Uuid.ToString().Substring(4, 4);
            JsonString = JsonString + "\"" + currentDeviceCharacteristicUUid + "\"";
            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"discoveredCharacteristics\",\"serviceUuids\":\"" + regex_servshortuuid + "\",\"characteristicUuids\":[" + JsonString + "]}"));
        }
        public async Task<byte[]> GetValue()
        {
            try
            {
                    //If the characteristic supports Notify then tell it to notify us.
                    try
                    {
                        if (currentDeviceCharacteristic.GattCharacteristic.CharacteristicProperties.HasFlag(GattCharacteristicProperties.Notify))
                        {
                            currentDeviceCharacteristic.GattCharacteristic.ValueChanged += characteristics_ValueChanged;
                            await currentDeviceCharacteristic.GattCharacteristic.WriteClientCharacteristicConfigurationDescriptorAsync(GattClientCharacteristicConfigurationDescriptorValue.Notify);
                        }
                    }
                    catch { }

                    //Read
                    if (currentDeviceCharacteristic.GattCharacteristic.CharacteristicProperties.HasFlag(GattCharacteristicProperties.Read))
                    {
                        var result = await currentDeviceCharacteristic.GattCharacteristic.ReadValueAsync(BluetoothCacheMode.Uncached);

                        if (result.Status == GattCommunicationStatus.Success)
                        {
                            byte[] forceData = new byte[result.Value.Length];
                            DataReader.FromBuffer(result.Value).ReadBytes(forceData);
                            return forceData;
                        }
                        else
                        {
                            //await new MessageDialog(result.Status.ToString()).ShowAsync();
                        }
                    }
               
            }
            catch (Exception ex)
            {
                //Debug.WriteLine(ex.Message);
            }
            return null;
        }
        void characteristics_ValueChanged(GattCharacteristic sender, GattValueChangedEventArgs args)
        {
            byte[] data = new byte[args.CharacteristicValue.Length];
            Windows.Storage.Streams.DataReader.FromBuffer(args.CharacteristicValue).ReadBytes(data);

            //Update properties
            if (sender.Uuid == GattCharacteristicUuids.HeartRateMeasurement)
            {
                currentDeviceCharacteristic.Value = data;
            }
        }
        public void subscribe(string options)
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
            string regex_servshortuuid = Regex.Match(args, @":""(\w+)""").Value.Trim(trimarray);
            ushort servshortuuid = Convert.ToUInt16(regex_servshortuuid, 16);
            Regex r = new Regex( @"""(\w+)""");
            Match m = r.Match(args);
            while (m.Value.Trim(trimarray) != "characteristicUuid")
            {
                m = m.NextMatch();
            }
            m = m.NextMatch();
            string regex_charashortuuid = m.Value.Trim(trimarray);
            //string regex_charashortuuid = Regex.Match(args, @"""(\w+)""").Value.Trim(trimarray);
            ushort charashortuuid = Convert.ToUInt16(regex_charashortuuid, 16);

        }
    }
}