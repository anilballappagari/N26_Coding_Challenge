package com.n26.maintenance;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.n26.perform.PerformAdd;
import com.n26.perform.PerformRemove;

/**
 * Schedulers which will run {@link PerformAdd} and {@link PerformRemove}
 * workers at a fixed interval rate of 1 second.
 * 
 * @author Anil Ballappagari
 * @version 1.0
 *
 */
public class JobSchedulars {

	private final static Logger LOGGER = Logger.getLogger(JobSchedulars.class.getName());

	public void runJobs() {
		ScheduledExecutorService addService = Executors.newScheduledThreadPool(3);
		ScheduledExecutorService removeService = Executors.newScheduledThreadPool(3);
		PerformAdd add = new PerformAdd();
		PerformRemove remove = new PerformRemove();
		// start add once the server is up with a delay of 1 sec
		addService.scheduleAtFixedRate(add, 1, 1, TimeUnit.SECONDS);
		// start remove with a delay of 1 sec
		removeService.scheduleAtFixedRate(remove, 1, 1, TimeUnit.SECONDS);
		LOGGER.info("Started jobs to perform add and remove...");
	}
}
