using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WPCordovaClassLib.Cordova;
using WPCordovaClassLib.Cordova.Commands;
using WPCordovaClassLib.Cordova.JSON;
using Windows.Devices.Bluetooth;
using Windows.Devices.Bluetooth.GenericAttributeProfile;
using Windows.Devices.Enumeration;
using Windows.Devices.Enumeration.Pnp;
using Windows.Storage.Streams;
namespace Cordova.Extension.Commands
{
    class BluetoothLePlugin : BaseCommand
    {
        string deviceName;
        async void Initialize()
        {
            var devices = await Windows.Devices.Enumeration.DeviceInformation.FindAllAsync(GattDeviceService.GetDeviceSelectorFromUuid(GattServiceUuids.GenericAccess));
            if (devices.Count == 0)
                return;
            //Connect to the service  
            var service = await GattDeviceService.FromIdAsync(devices[0].Id);
            if (service == null)
                return;
            //Obtain the characteristic we want to interact with  
            var characteristic = service.GetCharacteristics(GattCharacteristic.ConvertShortIdToUuid(0x2A00))[0];
            //Read the value  
            GattReadResult deviceNameBytes = await characteristic.ReadValueAsync();
            //Convert to string  
            if (deviceNameBytes.Status == GattCommunicationStatus.Success)
            {
                byte[] sensorData = new byte[deviceNameBytes.Value.Length];
                DataReader.FromBuffer(deviceNameBytes.Value).ReadBytes(sensorData);
                deviceName = Encoding.UTF8.GetString(sensorData, 0, sensorData.Length);
            }
        }
          public void services(string options)
        { 
          Initialize();
          while(deviceName==null)
          {
          }
            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, deviceName));
        }
    }
}
