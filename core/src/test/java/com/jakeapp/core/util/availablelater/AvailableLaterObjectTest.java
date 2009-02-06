package com.jakeapp.core.util.availablelater;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import local.test.Tracer;


public class AvailableLaterObjectTest {

	public static class AvailablesProvider {

		public static AvailableLaterObject<Boolean> provideNow() {
			return new AvailableNowObject<Boolean>(true);
		}

		public static AvailableLaterObject<Boolean> provideLater() {
			return new AvailableLaterObject<Boolean>() {

				@Override
				public Boolean calculate() throws Exception {
					Thread.sleep(1);
					return true;
				}
			}.start();
		}

		public static AvailableLaterObject<Boolean> provideError() {
			return new AvailableErrorObject<Boolean>(new Exception("myerror"));
		}

		public static AvailableLaterObject<BigInteger> provideFib(final int max) {
			return new AvailableLaterObject<BigInteger>() {

				// this should be quite inefficient ...
				@Override
				public BigInteger calculate() throws Exception {
					BigInteger[] v = new BigInteger[max];
					v[0] = BigInteger.ONE;
					v[1] = BigInteger.ONE;
					int i = 2;
					int nsteps = 10;
					for (int j = 1; j <= nsteps; j++) {
						while (i < j * max / nsteps) {
							v[i] = v[i - 1].add(v[i - 2]);
							i++;
						}
						this.getListener().statusUpdate(j * 1.0 / nsteps, "looks good: " + v[i-1]);
					}
					while (i < max) {
						v[i] = v[i - 1].add(v[i - 2]);
						i++;
					}
					return v[i - 1];
				}
			}.start();
		}
	}

	class TracingListener implements AvailabilityListener<Boolean> {

		private Tracer tracer;

		public TracingListener(Tracer tracer) {
			super();
			this.tracer = tracer;
		}

		@Override
		public void error(Exception t) {
			tracer.step("error: " + t.getMessage());
		}

		@Override
		public void finished(Boolean o) {
			if (o)
				tracer.step("finished correctly");
			else
				tracer.step("finished incorrectly");
		}

		@Override
		public void statusUpdate(double progress, String status) {
			// tracer.step("status update");
			// always nice, but never required
		}
	}

	@Test
	public void testAvailableNow() {
		final Tracer tracer = new Tracer();

		AvailableLaterObject<Boolean> avl = AvailablesProvider.provideNow();
		avl.setListener(new TracingListener(tracer));
		Assert.assertTrue(tracer.await("finished correctly", 10, TimeUnit.MILLISECONDS));
		Assert.assertTrue(tracer.isDone());
	}

	@Test
	public void testAvailableLater() {
		final Tracer tracer = new Tracer();

		AvailableLaterObject<Boolean> avl = AvailablesProvider.provideLater();
		avl.setListener(new TracingListener(tracer));
		Assert.assertTrue(tracer.await("finished correctly", 10, TimeUnit.MILLISECONDS));
		Assert.assertTrue(tracer.isDone());
	}

	@Test
	public void testAvailableError() {
		final Tracer tracer = new Tracer();

		AvailableLaterObject<Boolean> avl = AvailablesProvider.provideError();
		avl.setListener(new TracingListener(tracer));
		Assert.assertTrue(tracer.await("error: myerror", 10, TimeUnit.MILLISECONDS));
		Assert.assertTrue(tracer.isDone());
	}

	private static final int FIB_MAX = 10000;

	@Test
	public void testAvailableFib() {
		final Tracer tracer = new Tracer();
		AvailableLaterObject<BigInteger> avl = AvailablesProvider.provideFib(FIB_MAX);
		avl.setListener(new AvailabilityListener<BigInteger>() {

			@Override
			public void error(Exception t) {
				tracer.step("failure!!");
				t.printStackTrace();
			}

			@Override
			public void finished(BigInteger o) {
				tracer.step("done");
				tracer.step(o.toString());
			}

			@Override
			public void statusUpdate(double progress, String status) {
				if (progress == 0.5)
					tracer.step("half done");
				tracer.step(progress + " - " + status);
			}

		});
		Assert.assertTrue(tracer.await("half done", 2000, TimeUnit.MILLISECONDS));
		Assert.assertTrue(tracer.await("done", 2000, TimeUnit.MILLISECONDS));
		//Assert.assertTrue(tracer.await("1556111435", 1000, TimeUnit.MILLISECONDS));
	}
	
}
