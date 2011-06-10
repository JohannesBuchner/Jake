package com.jakeapp.violet.di;

import java.util.UUID;

public interface ProjectDependentCreator<C> {

	public abstract C create(UUID projectid);

}
