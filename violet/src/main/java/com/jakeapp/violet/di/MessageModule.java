package com.jakeapp.violet.di;

import com.google.inject.AbstractModule;
import com.jakeapp.violet.protocol.files.IRequestMarshaller;
import com.jakeapp.violet.protocol.files.RequestMarshaller;
import com.jakeapp.violet.protocol.invites.IInvitationHandler;
import com.jakeapp.violet.protocol.invites.ProjectInvitationHandler;
import com.jakeapp.violet.protocol.msg.ILogEntryMarshaller;
import com.jakeapp.violet.protocol.msg.IMessageMarshaller;
import com.jakeapp.violet.protocol.msg.impl.LogEntryMarshaller;
import com.jakeapp.violet.protocol.msg.impl.MessageMarshaller;


public class MessageModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IMessageMarshaller.class).to(MessageMarshaller.class);
		bind(ILogEntryMarshaller.class).to(LogEntryMarshaller.class);
		bind(IInvitationHandler.class).to(ProjectInvitationHandler.class);
		bind(IRequestMarshaller.class).to(RequestMarshaller.class);
	}

}
