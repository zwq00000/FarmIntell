package com.lingya.farmintell.adapters;

/**
 * Created by zwq00000 on 2015/7/11.
 */
public interface ServiceBinderProvider<TBinder> {

  /**
   * 获取服务绑定
   */
  TBinder getBinder();

}
