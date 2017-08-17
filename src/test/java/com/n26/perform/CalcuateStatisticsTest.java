package com.n26.perform;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.n26.dao.Input;
import com.n26.dao.Output;
import com.n26.store.TransactionStore;


public class CalcuateStatisticsTest {
	
	private List<Input> toBePeformed = Lists.newArrayList();
	
	@Before
	public void setUp() throws Exception {
		Input input = new Input();
		input.setAmount(25.6);
		input.setTimestamp(1502945799474l);
		TransactionStore.putOnToBePerfomed(input);
		Input input1 = new Input();
		input1.setAmount(10);
		input1.setTimestamp(1502945799474l);
		TransactionStore.putOnToBePerfomed(input1);
		toBePeformed.add(input1);
		toBePeformed.add(input);
	}

	@Test
	public void testAddAction() {
		toBePeformed.forEach((input) -> {
			CalculateStatistics.addAction();	
		});
		
		Output output = CalculateStatistics.getOutput();
		Assert.assertEquals(2, output.getCount());
		Assert.assertEquals(35.6, output.getSum(), 0.09);
		Assert.assertEquals(25.6, output.getMax(), 0.09);
		Assert.assertEquals(10, output.getMin(), 0.09);
		Assert.assertEquals(17.8, output.getAvg(), 0.09);
	}

	@Test
	public void testRemoveAction() {
		toBePeformed.forEach((input) -> {
			CalculateStatistics.removeAction();	
		});
		
		Output output = CalculateStatistics.getOutput();
		Assert.assertEquals(0, output.getCount());
		Assert.assertEquals(0, output.getSum(), 0.09);
		Assert.assertEquals(0, output.getMax(), 0.09);
		Assert.assertEquals(0, output.getMin(), 0.09);
		Assert.assertEquals(0, output.getAvg(), 0.09);
	}

}
