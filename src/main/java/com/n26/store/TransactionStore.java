package com.n26.store;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Lists;
import com.n26.dao.Input;

/**
 * TransactionStore acts an in-memory database which stores all the data.
 * 
 * @author Anil Ballappagari
 * @version 1.0
 *
 */
public final class TransactionStore {

	/**
	 * Prevent initialization
	 */
	private TransactionStore() {
	}

	/**
	 * List which needs to calculated for statistics.
	 */
	private static List<Input> toBePerformedList = Lists.newArrayList();

	/**
	 * List which has calculated statistics data.
	 */
	private static List<Input> performedList = Lists.newArrayList();

	/**
	 * stores the {@link Input#getAmount()} in set's natural order. This will be
	 * used to fetch max and min.
	 */
	private static ConcurrentSkipListSet<Double> sortedAmount = new ConcurrentSkipListSet<>();

	/**
	 * count the duplicates of max amount.
	 */
	private static AtomicInteger dupMax = new AtomicInteger(0);
	/**
	 * count the duplicates of min amount.
	 */
	private static AtomicInteger dupMin = new AtomicInteger(0);

	/**
	 * @return list of {@link Input} which has to be calculated.
	 */
	public static List<Input> getToBePerformed() {
		return toBePerformedList;
	}

	/**
	 * @param toBePerformedInput
	 *            Input type which needs to be calculated.
	 */
	public static void putOnToBePerfomed(Input toBePerformedInput) {
		toBePerformedList.add(toBePerformedInput);
	}

	/**
	 * @param performedInput
	 *            Input type which has been calculated.
	 */
	public static void putOnPerformed(Input performedInput) {
		performedList.add(performedInput);
	}

	/**
	 * @return list of {@link Input} which has been calculated.
	 */
	public static List<Input> getPerformed() {
		return performedList;
	}

	/**
	 * @param amount
	 *            which needs to be sorted to get Max and Min
	 */
	public static void putToSortedAmount(double amount) {
		if (sortedAmount.size() > 0) {
			if (getMaxAmount() == amount) {
				dupMax.incrementAndGet();
			}
			if (getMinAmount() == amount) {
				dupMin.incrementAndGet();
			}
		}
		sortedAmount.add(amount);
	}

	/**
	 * @param expiredAmount
	 *            remove from {@code sortedAmount} incase of expiry
	 */
	public static void removeFromSortedAmount(Double expiredAmount) {
		if (sortedAmount.size() > 0) {
			if (expiredAmount == getMaxAmount() && dupMax.get() > 1) {
				dupMax.decrementAndGet();
				return;
			}
			if (expiredAmount == getMinAmount() && dupMin.get() > 1) {
				dupMin.decrementAndGet();
				return;
			}
		}
		sortedAmount.remove(expiredAmount);
	}

	/**
	 * @return max amount
	 */
	public static double getMaxAmount() {
		return sortedAmount.size() > 0 ? sortedAmount.last() : 0.0;
	}

	/**
	 * @return min amount
	 */
	public static double getMinAmount() {
		return sortedAmount.size() > 0 ? sortedAmount.first() : 0.0;
	}
}
