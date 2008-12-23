package com.jakeapp.core.util;

import java.util.UUID;


public class ProjectContextHolder {
	   private static final ThreadLocal<UUID> contextHolder =
           new ThreadLocal<UUID>();
      
  public static void setProjectId(UUID projectid) {
     contextHolder.set(projectid);
  }

  public static UUID getProjectId() {
     return (UUID) contextHolder.get();
  }

  public static void clearProjectContext() {
     contextHolder.remove();
  }
}

