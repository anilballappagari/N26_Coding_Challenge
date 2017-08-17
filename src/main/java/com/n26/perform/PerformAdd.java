package com.n26.perform;

import java.util.logging.Logger;

/**
 * Worker Thread which performs adding to statistics.
 * 
 * @author Anil Ballappagari
 * @version 1.0
 *
 */
public class PerformAdd implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(PerformAdd.class.getName());

	@Override
	public void run() {
		LOGGER.info("Performing Add....");
		CalculateStatistics.addAction();
	}

}
