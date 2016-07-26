package com.example.valverde.valverderunkeeper.running;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TrackerUtilsTest {
    private final double FLOATING_PRECISION = 0.0001;
    private TrackerUtils utils;

    @Before
    public void setUp() {
        utils = TrackerUtils.getInstance();
    }

    @Test
    public void getDistanceInKmTest() throws Exception {
        GPSEvent newYorkLocalization = new GPSEvent(0, 40.7142700, -74.0059700, 0f);
        GPSEvent tokioLocalization = new GPSEvent(0, 35.6895000, 139.6917100, 0f);
        double distance = utils.getDistanceInKm(newYorkLocalization, tokioLocalization);
        final double expectedDistance = 10841.8518931;
        assertEquals(expectedDistance, distance, FLOATING_PRECISION);
    }

    @Test(expected = NullPointerException.class)
    public void getDistanceInKmNullTest() throws Exception {
        utils.getDistanceInKm(null, null);
    }

    @Test
    public void addEventTest() {
        int previousSize = utils.getRoute().size();
        utils.addEvent(new GPSEvent(0, 2, 3,4f));
        int actualSize = utils.getRoute().size();
        assertEquals(actualSize, previousSize + 1);
    }
}