package com.jakeapp.core.dao;

import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectMemberException;

import java.util.List;

/**
 * The Interface for project member DAOs.
 * @author Chris, domdorn, christopher, Simon
 */
public interface UserIdDao {

	/**
	 * Persists a <code>ProjectMember</code>. If the
     * <code>ProjectMember</code> does
	 * not yet exist in the database, a new entry is created.
	 * Otherwise an existing entry is updated.
	 * @param project the <code>Project</code> to which
     * <code>ProjectMember</code>
	 * should be persisted.
	 * @param projectMember the <code>ProjectMember</code> to be persisted
	 * @return the <code>ProjectMember</code> that has been persisted
	 */
    ProjectMember persist(final Project project,
                                 ProjectMember projectMember);

    /**
     * Get all <code>ProjectMember</code>s that are associated with a
     * given project.
     * @param project The project that the <code>ProjectMember</code>s are
     * associated
     * with
     * @return all <code>ProjectMember</code>s that are associated with the
     * given project.
     * @throws NoSuchProjectException if the <code>Project</code> referenced
     * by <code>project</code> does not exist.
     */
    public List<ProjectMember> getAll(final Project project)
            throws NoSuchProjectException;

    /**
     * Make a given <code>ProjectMember</code> transient of a specific project
     *  by removing
     * the project member from the database.
     * @param project the <code>Project<code> from which the project member
     * should be removed.
     * @param projectMember <code>ProjectMember</code> to be made transient.
     * @throws NoSuchProjectMemberException if the given
     * <code>ProjectMember</code> is not associated with the given
     * <code>Project</code>.
     */
    public void makeTransient(final Project project,
                              final ProjectMember projectMember)
            throws NoSuchProjectMemberException;
}
