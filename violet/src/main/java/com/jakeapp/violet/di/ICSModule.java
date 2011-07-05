package com.jakeapp.violet.di;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * TODO: move to GUI. explicit dependency on ICS-XMPP, ICS-ICE
 * 
 */
public class ICSModule extends AbstractModule {

	@Override
	protected void configure() {
		bindConstant().annotatedWith(Names.named("ics global resource name"))
				.to("Jake");
		bindConstant().annotatedWith(Names.named("xmpp namespace")).to(
				"http://jakeapp.com/protocols/xmpp/versions/2");
		bindConstant()
				.annotatedWith(Names.named("ics project resource prefix")).to(
						"JakeProject");
		bind(IUserIdFactory.class).to(XMPPUserIdFactory.class);
		bind(ICSFactory.class).to(XMPPICSFactory.class);
	}

}
