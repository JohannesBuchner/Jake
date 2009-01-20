package com.jakeapp.core.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.apache.log4j.Logger;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.domain.TrustState;
import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.dao.IProjectMemberDao;
import com.jakeapp.core.dao.INoteObjectDao;
import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;

import java.util.Properties;
import java.util.Collection;
import java.util.List;
import java.util.Hashtable;

public class ApplicationContextFactory {
    protected static Logger log = Logger.getLogger(ProjectApplicationContextFactory.class);
    protected Hashtable<String, ConfigurableApplicationContext> contextTable;
    private String[] configLocation;

    /**
     * Get the <code>ApplicationContext</code> for a given <code>
     * Project</code>. If an
     * <code>ApplicationContext</code> for the given <code>Project</code>
     * already exists, the existing context is returned, otherwise a new context
     * will be created. This method is <code>synchronized</code>
     *
     * @param project the project for which the application context is used
     * @return the <code>ApplicationContext</code>
     */
    public synchronized ApplicationContext getApplicationContext(Project project) {

        ConfigurableApplicationContext applicationContext = null;

        log.debug("acquiring context for project: " + project.getProjectId());
        if (this.contextTable.containsKey(project.getProjectId())) {
            log.debug("context for project: " + project.getProjectId()
                    + " already created, returning existing...");
            applicationContext = this.contextTable.get(project.getProjectId());
        } else {


            applicationContext = new ClassPathXmlApplicationContext(this.configLocation);
            Properties props = new Properties();
            props.put("db_path", project.getProjectId());
            PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
            cfg.setProperties(props);

            log.debug("configuring context with path: " + project.getProjectId());
            applicationContext.addBeanFactoryPostProcessor(cfg);
            applicationContext.refresh();

            this.contextTable.put(project.getProjectId(), applicationContext);
        }
        return applicationContext;
    }

    /**
     * Set the location of the spring config file to be used by the factory to
     * create the application contexts.
     *
     * @param configLocation the location of the config file (i.e. beans.xml etc.)
     * @see org.springframework.context.support.ClassPathXmlApplicationContext
     */
    public void setConfigLocation(String[] configLocation) {
        log.debug("Setting config Location to: ");
        for (String bla : configLocation) {
            log.debug(bla);
        }
        this.configLocation = configLocation;
    }

}
