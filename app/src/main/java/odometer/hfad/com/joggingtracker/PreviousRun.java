package odometer.hfad.com.joggingtracker;

import java.util.Date;

public class PreviousRun {

    Date dateRan;
    int mapScreenshot;

    public PreviousRun(Date dateRan, int mapScreenshot) {
        this.dateRan = dateRan;
        this.mapScreenshot = mapScreenshot;
    }

    public Date getDateRan() {
        return dateRan;
    }

    public int getMapScreenshot() {
        return mapScreenshot;
    }
}
