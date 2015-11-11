package com.lingya.farmintell.adapters;

import android.view.ViewGroup;

/**
 * 视图 适配器接口 Created by zwq00000 on 2014/7/20.
 */
public interface ViewAdapter<ViewModel> {

    /**
     * 设置视图数据
     */
    void setViewData(ViewModel viewData);

    /**
     * 绑定视图
     *
     * @param container 容器视图
     */
    void bindView(ViewGroup container);

    /**
     * 更新视图
     */
    void notifyDataChanged();
}
