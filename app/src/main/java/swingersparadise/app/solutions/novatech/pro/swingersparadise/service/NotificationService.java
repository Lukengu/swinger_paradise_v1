package swingersparadise.app.solutions.novatech.pro.swingersparadise.service;


import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class NotificationService extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
