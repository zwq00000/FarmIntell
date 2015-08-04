package com.lingya.farmintell.models;

import java.util.Arrays;

/**
 * @Created Apr 7, 2015 11:06:21 AM
 * @Description <p> 创建一个循环队列（环形缓冲、RingBuffer），实际元素存在一个数组中，操作数组的指针，不移动元素
 */
public class FloatCircleQueue {

  /**
   * 循环队列 （数组）默认大小
   */
  private static final int DEFAULT_SIZE = 1000;
  private final Object syncLock = new Object();
  /**
   * 队尾
   */
  public int tail = 0;
  /**
   * (循环队列)数组的容量
   */
  private int capacity;
  /**
   * 数组：保存循环队列的元素
   */
  private float[] backingArray;
  /**
   * 队头(先见先出)
   */
  private int head = 0;

  /**
   * 以循环队列 默认大小创建空循环队列
   */
  public FloatCircleQueue() {
    this(DEFAULT_SIZE);
  }

  /**
   * 以指定长度的数组来创建循环队列
   */
  public FloatCircleQueue(int capacity) {
    this.capacity = capacity;
    backingArray = new float[this.capacity];
    this.clear();
  }

  /**
   * 获取循环队列的大小(包含元素的个数)
   */
  public int size() {
    if (isEmpty()) {
      return 0;
    } else if (isFull()) {
      return capacity;
    } else {
      return tail + 1;
    }
  }

  /**
   * 插入队尾一个元素
   */
  public void add(final float element) {
    synchronized (syncLock) {
      if (isEmpty()) {
        backingArray[0] = element;
      } else if (isFull()) {
        backingArray[head] = element;
        head++;
        tail++;
        head = head == capacity ? 0 : head;
        tail = tail == capacity ? 0 : tail;
      } else {
        backingArray[tail + 1] = element;
        tail++;
      }
    }
  }

  public boolean isEmpty() {
    return (tail == head) && (tail == 0) && (backingArray[tail] == Float.NaN);
  }

  public boolean isFull() {
    return head != 0 && head - tail == 1 || head == 0 && tail == capacity - 1;
  }

  /**
   * 清除缓存并重置位置指针
   */
  public void clear() {
    synchronized (syncLock) {
      Arrays.fill(backingArray, Float.NaN);
      head = 0;
      tail = 0;
    }
  }

  /**
   * @return 取 循环队列里的值（先进的index=0）
   */
  public float[] array() {
    synchronized (syncLock) {
      if (isEmpty()) {
        return new float[0];
      } else if (isFull()) {
        final float[] queueArray = new float[capacity];
        System.arraycopy(backingArray, head, queueArray, 0, capacity - head);
        System.arraycopy(backingArray, 0, queueArray, capacity - head, head);
        return queueArray;
      } else {
        // elementDataSort = elementDataCopy;//用这行代码代替下面的循环，在队列刚满时候会出错
        return Arrays.copyOf(backingArray, tail);
      }
    }
  }
}
