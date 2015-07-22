package com.lingya.farmintell.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 传感器状态集合 Created by zwq00000 on 2015/6/29.
 */
public class SensorStatusCollection implements Parcelable {

  /**
   * {@see Intent.putExtra} EXTRA_KEY
   */
  public static final String EXTRA_KEY = "SensorStatuses";
  public static final Creator<SensorStatusCollection>
      CREATOR =
      new Creator<SensorStatusCollection>() {
        @Override
        public SensorStatusCollection createFromParcel(Parcel in) {
          return new SensorStatusCollection(in);
        }

        @Override
        public SensorStatusCollection[] newArray(int size) {
          return new SensorStatusCollection[size];
        }
      };

  /**
   * 空集合
   */
  public static final SensorStatusCollection Empty = new SensorStatusCollection();

  private List<SensorStatus> statuses = new ArrayList<SensorStatus>();
  private Date updateTime;

  public SensorStatusCollection() {
    this.updateTime = new Date();
  }

  protected SensorStatusCollection(Parcel in) {
    statuses = in.createTypedArrayList(SensorStatus.CREATOR);
    this.updateTime = new Date(in.readLong());
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
        .key("time").value("/Date(" + updateTime.getTime() + ")/")
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

  /**
   * Describe the kinds of special objects contained in this Parcelable's marshalled
   * representation.
   *
   * @return a bitmask indicating the set of special object types marshalled by the Parcelable.
   */
  @Override
  public int describeContents() {
    return 0;
  }

  /**
   * Flatten this object in to a Parcel.
   *
   * @param dest  The Parcel in which the object should be written.
   * @param flags Additional flags about how the object should be written. May be 0 or {@link
   *              #PARCELABLE_WRITE_RETURN_VALUE}.
   */
  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeTypedList(this.statuses);
    dest.writeLong(this.updateTime.getTime());
  }

  public int size() {
    return this.statuses.size();
  }
}
