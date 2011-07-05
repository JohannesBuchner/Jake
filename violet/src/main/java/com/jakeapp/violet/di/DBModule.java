package com.jakeapp.violet.di;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.jakeapp.violet.model.Log;
import com.jakeapp.violet.model.LogImpl;

public class DBModule extends AbstractModule {

	@Override
	protected void configure() {
		bindConstant().annotatedWith(Names.named("dbDriver")).to("h2");
		bindConstant().annotatedWith(Names.named("db.username")).to("sa");
		bindConstant().annotatedWith(Names.named("db.password")).to("");
		bindConstant().annotatedWith(Names.named("db.password")).to("");
		bindConstant().annotatedWith(Names.named("db.password")).to("");
		bindConstant().annotatedWith(Names.named("db.password")).to("");
		bindConstant().annotatedWith(Names.named("db.password")).to("");
		bind(ILogFactory.class).to(LogFactory.class);
	}
}
