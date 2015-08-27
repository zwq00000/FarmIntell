package com.lingya.farmintell.models;

import junit.framework.TestCase;

/**
 * Created by zwq00000 on 2015/8/2.
 */
public class FloatCircleQueueTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testSize() throws Exception {
        FloatCircleQueue queue = new FloatCircleQueue(100);
        assertEquals(queue.size(), 0);
        for (int i = 0; i < 20; i++) {
            queue.add(i);
        }
        assertEquals(queue.size(), 20);
        for (int i = 0; i < 120; i++) {
            queue.add(i);
        }
        assertEquals(queue.size(), 100);
    }

    public void testAdd() throws Exception {
        FloatCircleQueue queue = new FloatCircleQueue(10);
        queue.add(1);
    }

    public void testIsEmpty() throws Exception {
        FloatCircleQueue queue = new FloatCircleQueue(10);
        assertTrue(queue.isEmpty());
        queue.add(1);
        assertFalse(queue.isEmpty());
        queue.clear();
        assertTrue(queue.isEmpty());
    }

    public void testIsFull() throws Exception {

    }

    public void testClear() throws Exception {

    }

    public void testArray() throws Exception {

    }
}