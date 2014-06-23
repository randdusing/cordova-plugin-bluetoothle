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
        public void find_characteristics()
            {
                //Obtain the characteristic we want to interact with  
                //var characteristic = service.GetCharacteristics(GattCharacteristic.ConvertShortIdToUuid(0x2A00))[0];
                //Read the value  
                // GattReadResult deviceNameBytes = await characteristic.ReadValueAsync();
                //Convert to string  
                // if (deviceNameBytes.Status == GattCommunicationStatus.Success)
                //   {
                //  byte[] sensorData = new byte[deviceNameBytes.Value.Length];
                //   DataReader.FromBuffer(deviceNameBytes.Value).ReadBytes(sensorData);
                //     DeviceName = Encoding.UTF8.GetString(sensorData, 0, sensorData.Length);
                // }
            }
        GattDeviceService service = null;
        public  void initialize(string options)
        {
            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"initialized\"}"));
        }
        public async void startScan(string option)
        {
            //DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"scanStarted\"}"));
            string devname = "";
            string args = null;
            string address = "00:00:00:00:00:00";
            try
            {
                args = WPCordovaClassLib.Cordova.JSON.JsonHelper.Deserialize<string[]>(option)[0];
            }
            catch (FormatException)
            {
            }
            char[] trimarray = {'\\','[',']','"'};
            string regex = Regex.Match(args, @"\[""(\w+)""\]").Value.Trim(trimarray);
            ushort shortuuid = Convert.ToUInt16(regex, 16);
            var devices = await Windows.Devices.Enumeration.DeviceInformation.FindAllAsync(GattDeviceService.GetDeviceSelectorFromShortId(shortuuid));
            foreach (var d in devices)
            {
                //devlist.Items.Add(d.Name);
                if (d.Name == "Nordic_HRM")
                {
                    service = await GattDeviceService.FromIdAsync(d.Id);
                    devname = d.Name;
                }
            }
            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "{\"status\":\"scanResult\",\"address\":\""+address+"\",\"name\":\""+devname+"\"}"));
        }
        public  async void discover(string options)
        {

            //List<String> Uuids = new List<String>();
            string[] Uuids=null;
            //string CharacteristicValue = "";
            if (service != null)
            {
                var characteristic = service.GetCharacteristics(GattCharacteristic.ConvertShortIdToUuid(0x2a37));
                int i = 0;
                Uuids = new string[characteristic.Count];
                foreach (var chara in characteristic)
                {
                    Uuids[i]=chara.Uuid.ToString();
                    //await chara.WriteClientCharacteristicConfigurationDescriptorAsync(GattClientCharacteristicConfigurationDescriptorValue.Notify);
                    //GattReadResult deviceNameBytes = await chara.ReadValueAsync();
                    //Convert to string  
                    //if (deviceNameBytes.Status == GattCommunicationStatus.Success)
                    //{
                        //byte[] sensorData = new byte[deviceNameBytes.Value.Length];
                        //DataReader.FromBuffer(deviceNameBytes.Value).ReadBytes(sensorData);
                        //CharacteristicValue = Encoding.UTF8.GetString(sensorData, 0, sensorData.Length);
                        //var chara = service.GetCharacteristics(new Guid("1e0e03d7-a996-42c8-8728-379dc90a7238"));
                        //chara[0].ValueChanged += buttonPressed;
                        //await chara[0].WriteClientCharacteristicConfigurationDescriptorAsync(GattClientCharacteristicConfigurationDescriptorValue.Notify);
                        
                    //}
                    i++; 
                }
            }
            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, Uuids[0]));
        }
    }
}
