package reputation_bot;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

public class MonthlyTimer {

    private final int dayOfMonth;
    private final Runnable onRun;
    private Timer timer;
    private TimerTask task;

    public MonthlyTimer(int dayOfMonth, Runnable onRun) {
        this.onRun = onRun;
        this.dayOfMonth = dayOfMonth;
    }
    private long delayToNextRun(){
        Calendar c = new GregorianCalendar();
        c.setTime(new Date(Instant.now().toEpochMilli()));

        OffsetDateTime odt = OffsetDateTime.now();

        OffsetDateTime wd = odt.withDayOfMonth(dayOfMonth);
        //day of month is in the past so first run is next month
        if(!wd.isAfter(odt)){
            odt = odt.plusMonths(1).withDayOfMonth(dayOfMonth);
        }else{
            odt = wd;
        }

        return odt.toInstant().toEpochMilli()-System.currentTimeMillis();
    }

    public void start(){
        if(task!=null || timer!=null) throw new IllegalStateException("Already started.");
        schedule();
    }

    private void schedule(){
        if(task!=null) task.cancel();
        if(timer!=null) timer.cancel();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                schedule();
            }
        };
        onRun.run();
        timer.schedule(task, delayToNextRun());
    }

}
