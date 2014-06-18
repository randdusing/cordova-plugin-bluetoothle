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
        string DeviceAddress = null;
        string DeviceName = null;
        int NotifyCharaIndex = 0;
        int waiting_time=50;
        string callbackId_sub="";
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
            string regex_servuuid = Regex.Match(args, @":\[""([0-9a-zA-Z_\-\s]+)""").Value.Trim(trimarray);
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
        public  void characteristics(string options)
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
            Regex r = new Regex(@"""([0-9a-zA-Z_\-\s]+)""");
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
                //try
                //{
                    if (currentDeviceCharacteristic[i].GattCharacteristic.CharacteristicProperties.HasFlag(GattCharacteristicProperties.Notify))
                    {
                        NotifyCharaIndex = i;
                        //currentDeviceCharacteristic[NotifyCharaIndex].GattCharacteristic.ValueChanged += characteristics_ValueChanged;
                        //xxxx = await currentDeviceCharacteristic[NotifyCharaIndex].GattCharacteristic.WriteClientCharacteristicConfigurationDescriptorAsync(GattClientCharacteristicConfigurationDescriptorValue.Notify);
                    }
                //}
                //catch { }

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
            string[] args = null;
            try
            {
                args = WPCordovaClassLib.Cordova.JSON.JsonHelper.Deserialize<string[]>(options);
            }
            catch (FormatException)
            {
            }
            string JsonString = null;
            char[] trimarray = { '\\', '[', ']', '"', ':' };
            string regex_servuuid = Regex.Match(args[0], @":""([0-9a-zA-Z_\-\s]+)""").Value.Trim(trimarray);
            Regex r = new Regex(@"""([0-9a-zA-Z_\-\s]+)""");
            Match m = r.Match(args[0]);
            while (m.Value.Trim(trimarray) != "characteristicUuid")
            {
                m = m.NextMatch();
            }
            m = m.NextMatch();
            string regex_charauuid = m.Value.Trim(trimarray);
             Deployment.Current.Dispatcher.BeginInvoke(async() =>
            {
                await currentDeviceCharacteristic[NotifyCharaIndex].GattCharacteristic.WriteClientCharacteristicConfigurationDescriptorAsync(GattClientCharacteristicConfigurationDescriptorValue.Notify);
                Thread.Sleep(waiting_time);
                currentDeviceCharacteristic[NotifyCharaIndex].GattCharacteristic.ValueChanged += this.characteristics_ValueChanged;
            });
             callbackId_sub = args[1];
            //DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"subscribedResult\",\"value\":\"\"}"));
            PluginResult result = new PluginResult(PluginResult.Status.OK, "{\"status\":\"subscribed\",\"value\":\"\"}");
            result.KeepCallback = true;
            DispatchCommandResult(result, callbackId_sub);
        }
        public async void write(string options)
        {
            currentDeviceCharacteristic[NotifyCharaIndex].Value = "";
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
            string command = Regex.Match(args, @":""([0-9a-zA-Z_\-\s]+)""").Value.Trim(trimarray);
            Regex r = new Regex(@"""([0-9a-zA-Z_\-]+)""");
            Match m = r.Match(args);
            while (m.Value.Trim(trimarray) != "characteristicUuid")
            {
                m = m.NextMatch();
            }
            m = m.NextMatch();
            string regex_charauuid = m.Value.Trim(trimarray);
            byte[] data = new byte[command.Length];
            data = System.Text.Encoding.UTF8.GetBytes(command);
            await currentDeviceCharacteristic[1].GattCharacteristic.WriteValueAsync(data.AsBuffer(), GattWriteOption.WriteWithoutResponse);
            Thread.Sleep(waiting_time);
            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"written\",\"value\":\"\"}"));
        }
        public void characteristics_ValueChanged(GattCharacteristic sender, GattValueChangedEventArgs EventArgs)
        {
            string JsonString=null;
            byte[] data = new byte[EventArgs.CharacteristicValue.Length];
            DataReader.FromBuffer(EventArgs.CharacteristicValue).ReadBytes(data);
            Thread.Sleep(waiting_time);
            //currentDeviceCharacteristic[NotifyCharaIndex].Value = currentDeviceCharacteristic[NotifyCharaIndex].Value + System.Text.Encoding.UTF8.GetString(data, 0, (int)EventArgs.CharacteristicValue.Length);
            JsonString = "\"" + System.Text.Encoding.UTF8.GetString(data, 0, (int)EventArgs.CharacteristicValue.Length)+ "\"";
            JsonString = Regex.Replace(JsonString, "\n", "\\n");
            JsonString = Regex.Replace(JsonString, "\r", "\\r");
            PluginResult result = new PluginResult(PluginResult.Status.OK, "{\"status\":\"subscribedResult\",\"value\":" + JsonString + "}");
            result.KeepCallback = true;
            DispatchCommandResult(result, callbackId_sub);
        }
        public async void unsubscribe(string options)
        {
            string args = null;
            currentDeviceCharacteristic[NotifyCharaIndex].Value = "";
            try
            {
                args = WPCordovaClassLib.Cordova.JSON.JsonHelper.Deserialize<string[]>(options)[0];
            }
            catch (FormatException)
            {
            }
            string JsonString = "";
            char[] trimarray = { '\\', '[', ']', '"', ':' };
            string regex_servuuid = Regex.Match(args, @":""([0-9a-zA-Z_\-\s]+)""").Value.Trim(trimarray);
            Regex r = new Regex(@"""([0-9a-zA-Z_\-\s]+)""");
            Match m = r.Match(args);
            while (m.Value.Trim(trimarray) != "characteristicUuid")
            {
                m = m.NextMatch();
            }
            m = m.NextMatch();
            string regex_charauuid = m.Value.Trim(trimarray);
            DateTime startTime = DateTime.Now;
            await currentDeviceCharacteristic[NotifyCharaIndex].GattCharacteristic.WriteClientCharacteristicConfigurationDescriptorAsync(GattClientCharacteristicConfigurationDescriptorValue.Notify);
            Thread.Sleep(waiting_time);
            currentDeviceCharacteristic[NotifyCharaIndex].GattCharacteristic.ValueChanged -= this.characteristics_ValueChanged;
            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"unsubscribed\",\"value\":\"\"}"));
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