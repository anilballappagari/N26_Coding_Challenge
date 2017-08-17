package com.n26.perform;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.n26.dao.Input;
import com.n26.dao.Output;
import com.n26.store.TransactionStore;
import com.n26.util.N26Utility;

/**
 * This performs the core logic of calculating statics and storing them to our
 * In-Memory Store {@link TransactionStore}
 * 
 * @author Anil Ballappagari
 * @version 1.0
 *
 */
public class CalculateStatistics {

	/**
	 * which contains the statistics of any given minute.
	 */
	private static Output output = new Output();

	private static Lock lock = new ReentrantLock();

	/**
	 * add to statistics.
	 */
	public static void addAction() {
		lock.lock();
		if (TransactionStore.getToBePerformed().size() > 0) {
			Input toBePerformedInput = TransactionStore.getToBePerformed().remove(0);
			TransactionStore.putToSortedAmount(toBePerformedInput.getAmount());
			performAdd(toBePerformedInput);
			TransactionStore.putOnPerformed(toBePerformedInput);
		}
		lock.unlock();
	}

	/**
	 * remove from calculated statistics if they expire.
	 */
	public static void removeAction() {
		lock.lock();
		List<Input> tmpPerformed = TransactionStore.getPerformed();
		if (tmpPerformed.size() > 0) {
			List<Input> expiredTransactions = IntStream.range(0, tmpPerformed.size()).parallel()
					.filter(i -> !N26Utility.validateTransaction(tmpPerformed.get(i).getTimestamp()))
					.mapToObj(tmpPerformed::get).collect(Collectors.toList());
			TransactionStore.getPerformed().removeAll(expiredTransactions);
			Consumer<Input> sortedAmtAction = (input) -> {
				TransactionStore.removeFromSortedAmount(input.getAmount());
			};
			expiredTransactions.parallelStream().forEach(sortedAmtAction);
			Consumer<Input> removeAction = (input) -> {
				performRemove(input);
			};
			expiredTransactions.parallelStream().forEach(removeAction);
		}
		lock.unlock();
	}

	/**
	 * @param input
	 */
	private static void performAdd(Input input) {
		double amount = input.getAmount();
		output.setCount(output.getCount() + 1);
		output.setMin(TransactionStore.getMinAmount());
		output.setMax(TransactionStore.getMaxAmount());
		output.setSum((double) Math.round((output.getSum() + amount) * 100) / 100);
		output.setAvg((double) Math.round((output.getSum() / output.getCount()) * 100) / 100);
	}

	/**
	 * @param input from which statistics has to be updated.
	 */
	private static void performRemove(Input input) {
		try {
			double amount = input.getAmount();
			output.setCount(output.getCount() - 1);
			output.setMin(TransactionStore.getMinAmount());
			output.setMax(TransactionStore.getMaxAmount());
			if (output.getCount() > 0) {
				output.setSum((double) Math.round((output.getSum() - amount) * 100) / 100);
				output.setAvg((double) Math.round((output.getSum() / output.getCount()) * 100) / 100);
			} else {
				output.setAvg(0);
				output.setSum(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return output which contains the statistics.
	 */
	public static synchronized Output getOutput() {
		return output;
	}

}
