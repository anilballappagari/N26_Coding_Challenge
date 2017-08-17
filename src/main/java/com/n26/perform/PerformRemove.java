package com.n26.perform;

import java.util.logging.Logger;

/**
 * Worker Thread which performs removing from statistics.
 * 
 * @author Anil Ballappagari
 * @version 1.0
 *
 */
public class PerformRemove implements Runnable {
	
	private final static Logger LOGGER = Logger.getLogger(PerformRemove.class.getName());

	@Override
	public void run() {
		LOGGER.info("Performing Remove....");
		CalculateStatistics.removeAction();
	}

}
