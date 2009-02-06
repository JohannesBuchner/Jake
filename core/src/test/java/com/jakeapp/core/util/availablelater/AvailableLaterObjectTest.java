package com.jakeapp.core.util.availablelater;

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
	}

	class TracingListener implements AvailabilityListener<Boolean> {

		private Tracer tracer;

		public TracingListener(Tracer tracer) {
			super();
			this.tracer = tracer;
		}

		@Override
		public void error(Exception t) {
			tracer.step("error");
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
}
