package com.lingya.farmintell.models;

import org.json.JSONException;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 传感器状态集合 Created by zwq00000 on 2015/6/29.
 */
public class SensorStatusCollection {

  /**
   * 空集合
   */
  public static final SensorStatusCollection Empty = new SensorStatusCollection();

  private List<SensorStatus> statuses = new ArrayList<SensorStatus>();
  private Date updateTime;

  public SensorStatusCollection() {
    this.updateTime = new Date();
  }

  public List<SensorStatus> getStatuses() {
    return this.statuses;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date time) {
    updateTime = time;
  }

  public String toJson() throws JSONException {
    JSONStringer stringer = new JSONStringer();
    stringer.object()
        .key("type").value("SensorStatus")
        //.key("time").value("/Date(" + updateTime.getTime() + ")/")
        .key("time").value(updateTime.getTime())
        .key("statuses")
        .array();
    for (SensorStatus status : this.statuses) {
      if (!Float.isNaN(status.getValue())) {
        stringer.object()
            .key("id").value(status.getId())
            .key("name").value(status.getName())
            .key("value").value(status.getFormatedValue())
            .endObject();
      }
    }
    stringer.endArray().endObject();
    return stringer.toString();
  }

  public int size() {
    return this.statuses.size();
  }
}
