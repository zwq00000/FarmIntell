蔬菜大棚智能监控 App 项目
==========================

## 一、 模块说明

### farmintell.modbus
 modbus 数据读取库
 1.使用示例
** 绑定服务 SensorService.START_SERVICE,并通过 服务连接器 SensorBinder 进行数据查询。
```java
 Intent intent = new Intent(SensorService.START_SERVICE, Uri.EMPTY, this, SensorService.class);
    this.bindService(intent, new ServiceConnection() {

      @Override
      public void onServiceConnected(ComponentName name, IBinder service) {
        sensorServiceBinder = (SensorService.SensorBinder) service;
      }

      @Override
      public void onServiceDisconnected(ComponentName name) {
        sensorServiceBinder = null;
      }
    }, BIND_AUTO_CREATE);
```

2.modbus 通讯参数设定

 - 485端口 /dev/ttySAC3
 - 波特率  9600
 - 数据位  8
 - 停止位  1
 - 奇偶校验 无


