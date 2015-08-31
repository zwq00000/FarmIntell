package com.lingya.qrcodegenerator;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

  private static final String TAG = "";

  public ApplicationTest() {
    super(Application.class);
  }

  private static String callCmd(String cmd, String filter) {
    String result = "";
    String line;
    OutputStreamWriter output = null;
    BufferedReader input = null;
    Process proc = null;
    try {
      proc = Runtime.getRuntime().exec("su");
      output = new OutputStreamWriter(proc.getOutputStream());
      input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      output.write(cmd);
      output.write("\n");
      output.flush();

      //执行命令cmd，只取结果中含有filter的这一行
      while ((line = input.readLine()) != null) {
        //result += line;
        Log.i("test", "line: " + line);
        if (line.contains(filter)) {
          return line;
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (output != null) {
        try {
          output.write("exit\n");
          output.flush();
          output.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (proc != null) {
        proc.destroy();
      }

    }
    return result;
  }

  /**
   * 获取主机 IpV4 地址
   *
   * @param interfaceName 网络接口名称 如 'eth0' 'wlan0'...
   * @return 本机的ip地址 xxx.xxx.xxx.xxx
   */
  public static String getHostIpv4(String interfaceName) throws SocketException {
    String ipaddress = "";
    NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
    for (Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
         inetAddresses.hasMoreElements(); ) {
      InetAddress inetAddress = inetAddresses.nextElement();
      if (!inetAddress.isLoopbackAddress()) {
        ipaddress = inetAddress.getHostAddress().toString();
        if (!ipaddress.contains("::")) {
          return ipaddress;
        }
      }
    }
    return ipaddress;
  }

  public void testGetIpAddress() throws Exception {
    Context context = getContext();
    NetworkInterface eth0 = NetworkInterface.getByName("eth0");
    List<InterfaceAddress> addresses = eth0.getInterfaceAddresses();
    for (InterfaceAddress address : addresses
        ) {
      Log.d(TAG, "address:" + address.getAddress().getHostAddress());

    }
    String ip = getHostIpv4("eth0");
    assertEquals(ip, "192.168.0.196");
  }

  public void testNetworkInfo() throws Exception {
    Context context = getContext();
    ConnectivityManager manager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    assertNotNull(manager);
    NetworkInfo[] infos = manager.getAllNetworkInfo();
    for (NetworkInfo info : infos
        ) {
      System.out.println(info.toString());
    }
    NetworkInfo netInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
    assertNotNull(netInfo);
    assertEquals(netInfo.getTypeName(), "ETH");
    assertTrue(netInfo.isConnected());
  }

  public void testGetIpaddressUseIfconfig() throws Exception {
    String result = callCmd("ifconfig eth0", "eth0");
    assertNotNull(result);
    String ipaddress = result.substring(result.indexOf("ip") + 3, result.indexOf("mask"));
    assertEquals(ipaddress, "192.168.0.196");
  }
}