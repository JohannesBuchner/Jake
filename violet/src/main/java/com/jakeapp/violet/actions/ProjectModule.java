package com.jakeapp.violet.actions;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.jakeapp.violet.actions.project.connect.ConnectProjectActionsFactory;
import com.jakeapp.violet.actions.project.interact.InteractProjectActionsFactory;
import com.jakeapp.violet.actions.project.local.LocalProjectActionsFactory;
import com.jakeapp.violet.context.ContextFactory;


public class ProjectModule extends AbstractModule {

	@Override
	protected void configure() {
		/**
		 * For the actions, the factory is the interface that specifies the user
		 * view -- how to get/call the action.
		 * 
		 * The DI should inject the rest (except for the model, we are not that
		 * sophisticated). Here we use Guice assisted injects / automatic
		 * factory building.
		 */
		install(new FactoryModuleBuilder()
				.build(LocalProjectActionsFactory.class));
		install(new FactoryModuleBuilder()
				.build(ConnectProjectActionsFactory.class));
		install(new FactoryModuleBuilder()
				.build(InteractProjectActionsFactory.class));

		/**
		 * To get a context, we play a similar game except we actually implement
		 * calling the constructors.
		 * 
		 * Here, we use Guice factory detection
		 */
		install(new FactoryModuleBuilder().build(ContextFactory.class));
	}
}
