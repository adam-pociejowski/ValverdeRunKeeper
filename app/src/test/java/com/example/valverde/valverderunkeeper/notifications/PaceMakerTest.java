package com.example.valverde.valverderunkeeper.notifications;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PacemakerTest {
    private PaceMaker pacemaker;

    @Before
    public void setUp() {
        pacemaker = new PaceMaker(12.0);
    }

    @Test
    public void checkPaceMakerCorrection() {
        long expectedTime = pacemaker.getExpectedTimeInPoint(2.4);
        assertEquals(720000, expectedTime);
    }

    @Test
    public void checkDifferenceTimeAdvantage() {
        String differenceString = pacemaker.getDifferenceInSeconds(10000, 12000);
        assertEquals(differenceString, "2 seconds advantage");
    }

    @Test
    public void checkDifferenceTimeAdvantageRoundingUp() {
        String differenceString = pacemaker.getDifferenceInSeconds(10200, 12000);
        assertEquals(differenceString, "2 seconds advantage");
    }

    @Test
    public void checkDifferenceTimeAdvantageRoundingDown() {
        String differenceString = pacemaker.getDifferenceInSeconds(9600, 12000);
        assertEquals(differenceString, "2 seconds advantage");
    }

    @Test
    public void checkSameTimes() {
        String differenceString = pacemaker.getDifferenceInSeconds(9600, 9600);
        assertEquals(differenceString, "pacemaker time");
    }

    @Test
    public void checkDifferenceTimeWaste() {
        String differenceString = pacemaker.getDifferenceInSeconds(20000, 16000);
        assertEquals(differenceString, "4 seconds waste");
    }

    @Test
    public void checkDifferenceTimeWasteRoundingUp() {
        String differenceString = pacemaker.getDifferenceInSeconds(20000, 16400);
        assertEquals(differenceString, "4 seconds waste");
    }

    @Test
    public void checkDifferenceTimeWasteRoundingDown() {
        String differenceString = pacemaker.getDifferenceInSeconds(20000, 15800);
        assertEquals(differenceString, "4 seconds waste");
    }
}
