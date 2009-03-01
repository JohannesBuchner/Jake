package com.jakeapp.core.domain;

import com.jakeapp.core.dao.IProjectDao;
import com.jakeapp.core.dao.INoteObjectDao;
import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.synchronization.exceptions.InvalidDeserializerCallException;
import com.jakeapp.core.domain.exceptions.InvalidTagNameException;
import com.jakeapp.core.domain.logentries.*;

import java.util.UUID;
import java.util.Date;


public class LogEntrySerializer {

    IProjectDao projectDao;
    IFileObjectDao fileObjectDao;
    INoteObjectDao noteObjectDao;

    private final String SEPERATOR = "XXAAAXXX";
    private final String SEPERATOR_REGEX = "XXAAAXXX";


    public void setFileObjectDao(IFileObjectDao fileObjectDao) {
        this.fileObjectDao = fileObjectDao;
    }

    public void setNoteObjectDao(INoteObjectDao noteObjectDao) {
        this.noteObjectDao = noteObjectDao;
    }

    public void setProjectDao(IProjectDao projectDao) {
        this.projectDao = projectDao;
    }


    // FIXME: This is temporary. Remove it once someone tells me why we need a ProjectDAO
    //        if all we want to do is serialize...
    public LogEntrySerializer() {
    }

    public String serialize(ProjectLogEntry logEntry) {

        Project project = logEntry.getProject();

        StringBuffer sb = new StringBuffer(500);

        sb.append(SEPERATOR).append(project.getProjectId());
        sb.append(SEPERATOR).append(logEntry.getTimestamp().getTime());
        sb.append(SEPERATOR).append(logEntry.getLogAction().ordinal());
        sb.append(SEPERATOR).append(logEntry.getMember().getProtocolType().ordinal());
        sb.append(SEPERATOR).append(logEntry.getMember().getUserId());
        sb.append(SEPERATOR).append(logEntry.getUuid().toString());


        sb.append(SEPERATOR).append(project.getName());


        sb.append(SEPERATOR);

        return sb.toString();
    }


    public String serialize(JakeObjectNewVersionLogEntry logEntry, Project project) {
        return this.serialize((JakeObjectLogEntry) logEntry, project);
    }

    public String serialize(JakeObjectLockLogEntry logEntry, Project project) {
        return this.serialize((JakeObjectLogEntry) logEntry, project);
    }

    public String serialize(JakeObjectUnlockLogEntry logEntry, Project project) {
        return this.serialize((JakeObjectLogEntry) logEntry, project);
    }

    public String serialize(JakeObjectDeleteLogEntry logEntry, Project project) {
        return this.serialize((JakeObjectLogEntry) logEntry, project);
    }

    private String serialize(JakeObjectLogEntry logEntry, Project project) {

        StringBuffer sb = prepareSerializedString(logEntry, project);


        sb.append(SEPERATOR);
        if (logEntry.getBelongsTo() instanceof FileObject) {
            sb.append("F");
            sb.append(SEPERATOR).append(((FileObject) logEntry.getBelongsTo()).getRelPath());
        } else if (logEntry.getBelongsTo() instanceof NoteObject) {
            sb.append("N");
            sb.append(SEPERATOR).append(((NoteObject) logEntry.getBelongsTo()).getContent());
        } else
            throw new UnsupportedOperationException();


        sb.append(SEPERATOR).append(logEntry.getBelongsTo().getUuid().toString());

        sb.append(SEPERATOR).append(logEntry.getComment());
        sb.append(SEPERATOR).append(logEntry.getChecksum());

        sb.append(SEPERATOR);
        return sb.toString();
    }

    private StringBuffer prepareSerializedString(LogEntry logEntry, Project project) {
        StringBuffer sb = new StringBuffer(500);

        sb.append(SEPERATOR).append(project.getProjectId());
        sb.append(SEPERATOR).append(logEntry.getTimestamp().getTime());
        sb.append(SEPERATOR).append(logEntry.getLogAction().ordinal());
        sb.append(SEPERATOR).append(logEntry.getMember().getProtocolType().ordinal());
        sb.append(SEPERATOR).append(logEntry.getMember().getUserId());
        sb.append(SEPERATOR).append(logEntry.getUuid().toString());
        return sb;
    }


    public String serialize(TagAddLogEntry logEntry, Project project) {
        return this.serialize((TagLogEntry) logEntry, project);
    }

    public String serialize(TagRemoveLogEntry logEntry, Project project) {
        return this.serialize((TagLogEntry) logEntry, project);
    }

    private String serialize(TagLogEntry logEntry, Project project) {
        StringBuffer sb = prepareSerializedString(logEntry, project);

        sb.append(SEPERATOR);
        if (logEntry.getBelongsTo().getObject() instanceof FileObject) {
            sb.append("F");
        } else if (logEntry.getBelongsTo().getObject() instanceof NoteObject) {
            sb.append("N");
        } else
            throw new UnsupportedOperationException();

        sb.append(SEPERATOR).append(logEntry.getBelongsTo().getName());

        sb.append(SEPERATOR).append(logEntry.getBelongsTo().getObject().getUuid().toString());
        sb.append(SEPERATOR);
        return sb.toString();
    }


    public String serialize(LogEntry<? extends ILogable> logEntry, Project project) {
        LogAction action = logEntry.getLogAction();
        switch (action) {

            case FOLLOW_TRUSTING_PROJECTMEMBER:
                break;
            case JAKE_OBJECT_DELETE:
                break;
            case JAKE_OBJECT_LOCK:
                break;
            case JAKE_OBJECT_NEW_VERSION:
                break;
            case JAKE_OBJECT_UNLOCK:
                break;
            case NOOP:
                break;
            case PROJECT_CREATED:
                break;
            case START_TRUSTING_PROJECTMEMBER:
                break;
            case STOP_TRUSTING_PROJECTMEMBER:
                break;
            case TAG_ADD:

                break;
            case TAG_REMOVE:
                break;
        }
        return "foo";
    }


    public LogEntry<? extends ILogable> deserialize(String input) throws InvalidDeserializerCallException {
        String[] parts = input.split(SEPERATOR_REGEX);

        if (parts.length < 6)
            throw new InvalidDeserializerCallException();

        UUID projectUUID = UUID.fromString(parts[1]);
        Date date = new Date(new Long(parts[2]));
        LogAction logAction = LogAction.values()[new Integer(parts[3])];
        ProtocolType protocolType = ProtocolType.values()[new Integer(parts[4])];
        String userId = parts[5];

        UserId remoteUser = new UserId(protocolType, userId);
        UUID logEntryUUID = UUID.fromString(parts[6]);


        System.out.println("projectUUID = " + projectUUID);
        System.out.println("date = " + date);
        System.out.println("logAction = " + logAction);

        for (String part : parts) {
            System.out.println("part = " + part);
        }

        switch (logAction) {
            case JAKE_OBJECT_DELETE: {
                Project p = null;
                JakeObjectDeleteLogEntry result;

                try {
                    p = projectDao.read(projectUUID); // throws NoSuchProjectException
                } catch (NoSuchProjectException e) {
                    throw new InvalidDeserializerCallException();
                }

                String type = parts[7];

                JakeObject jakeObject;
                if (type.equals("F")) {
                    String relPath = parts[8];
                    jakeObject = new FileObject(p, relPath);
                } else if (type.equals("N")) {
                    String content = parts[8];
                    jakeObject = new NoteObject(p, content);


                } else
                    throw new InvalidDeserializerCallException();

                jakeObject.setUuid(UUID.fromString(parts[9]));


                String comment = parts[10];
                String checksum = parts[11];


                result = new JakeObjectDeleteLogEntry(jakeObject, remoteUser, comment, checksum, false);


                /// always do this
                result.setTimestamp(date);
                result.setUuid(logEntryUUID);
                return result;
            }
            case JAKE_OBJECT_LOCK: {
                Project p = null;
                JakeObjectLockLogEntry result;

                try {
                    p = projectDao.read(projectUUID); // throws NoSuchProjectException
                } catch (NoSuchProjectException e) {
                    throw new InvalidDeserializerCallException();
                }

                String type = parts[7];

                JakeObject jakeObject;
                if (type.equals("F")) {
                    String relPath = parts[8];
                    jakeObject = new FileObject(p, relPath);
                } else if (type.equals("N")) {
                    String content = parts[8];
                    jakeObject = new NoteObject(p, content);


                } else
                    throw new InvalidDeserializerCallException();

                jakeObject.setUuid(UUID.fromString(parts[9]));


                String comment = parts[10];
                String checksum = parts[11];


                result = new JakeObjectLockLogEntry(jakeObject, remoteUser, comment, checksum, false);


                /// always do this
                result.setTimestamp(date);
                result.setUuid(logEntryUUID);
                return result;
            }
            case JAKE_OBJECT_NEW_VERSION: {
                Project p = null;
                JakeObjectNewVersionLogEntry result;

                try {
                    p = projectDao.read(projectUUID); // throws NoSuchProjectException
                } catch (NoSuchProjectException e) {
                    throw new InvalidDeserializerCallException();
                }

                String type = parts[7];

                JakeObject jakeObject;
                if (type.equals("F")) {
                    String relPath = parts[8];
                    jakeObject = new FileObject(p, relPath);
                } else if (type.equals("N")) {
                    String content = parts[8];
                    jakeObject = new NoteObject(p, content);


                } else
                    throw new InvalidDeserializerCallException();

                jakeObject.setUuid(UUID.fromString(parts[9]));


                String comment = parts[10];
                String checksum = parts[11];


                result = new JakeObjectNewVersionLogEntry(jakeObject, remoteUser, comment, checksum, false);


                /// always do this
                result.setTimestamp(date);
                result.setUuid(logEntryUUID);
                return result;

            }

            case JAKE_OBJECT_UNLOCK: {
                Project p = null;
                JakeObjectUnlockLogEntry result;

                try {
                    p = projectDao.read(projectUUID); // throws NoSuchProjectException
                } catch (NoSuchProjectException e) {
                    throw new InvalidDeserializerCallException();
                }

                String type = parts[7];

                JakeObject jakeObject;
                if (type.equals("F")) {
                    String relPath = parts[8];
                    jakeObject = new FileObject(p, relPath);
                } else if (type.equals("N")) {
                    String content = parts[8];
                    jakeObject = new NoteObject(p, content);


                } else
                    throw new InvalidDeserializerCallException();

                jakeObject.setUuid(UUID.fromString(parts[9]));


                String comment = parts[10];
                String checksum = parts[11];


                result = new JakeObjectUnlockLogEntry(jakeObject, remoteUser, comment, checksum, false);


                /// always do this
                result.setTimestamp(date);
                result.setUuid(logEntryUUID);
                return result;
            }
            case PROJECT_CREATED: {
                ProjectLogEntry result;
                String projectName = parts[7];
                Project p = null;
                try {
                    p = projectDao.read(projectUUID);
                } catch (NoSuchProjectException e) {
                    p = new Project(projectName, projectUUID, null, null);
                }

                result = new ProjectLogEntry(p, remoteUser);


                /// always do this
                result.setTimestamp(date);
                result.setUuid(logEntryUUID);
                return result;
            }
            case NOOP:
                break;
            case FOLLOW_TRUSTING_PROJECTMEMBER:
                break;
            case START_TRUSTING_PROJECTMEMBER:
                break;
            case STOP_TRUSTING_PROJECTMEMBER:
                break;
            case TAG_ADD: {
                Project p = null;
                TagAddLogEntry result;

                try {
                    p = projectDao.read(projectUUID); // throws NoSuchProjectException
                } catch (NoSuchProjectException e) {
                    throw new InvalidDeserializerCallException();
                }


                String type = parts[7];
                JakeObject jakeObject;
                Tag tag = null;
                try {
                    if (type.equals("F")) {
                        jakeObject = fileObjectDao.get(UUID.fromString(parts[9]));
                    } else if (type.equals("N")) {
                        jakeObject = noteObjectDao.get(UUID.fromString(parts[9]));
                    } else
                        throw new InvalidDeserializerCallException();

                    tag = new Tag(parts[8]);
                } catch (NoSuchJakeObjectException e) {
                    throw new InvalidDeserializerCallException();
                }
                catch (InvalidTagNameException e) {
                    throw new InvalidDeserializerCallException();
                }

                tag.setObject(jakeObject);

                result = new TagAddLogEntry(tag, remoteUser);


                /// always do this
                result.setTimestamp(date);
                result.setUuid(logEntryUUID);
                return result;


            }
            case TAG_REMOVE: {
                Project p = null;
                TagRemoveLogEntry result;

                try {
                    p = projectDao.read(projectUUID); // throws NoSuchProjectException
                } catch (NoSuchProjectException e) {
                    throw new InvalidDeserializerCallException();
                }


                String type = parts[7];
                JakeObject jakeObject;
                Tag tag = null;
                try {
                    if (type.equals("F")) {
                        jakeObject = fileObjectDao.get(UUID.fromString(parts[9]));
                    } else if (type.equals("N")) {
                        jakeObject = noteObjectDao.get(UUID.fromString(parts[9]));
                    } else
                        throw new InvalidDeserializerCallException();

                    tag = new Tag(parts[8]);
                } catch (NoSuchJakeObjectException e) {
                    throw new InvalidDeserializerCallException();
                }
                catch (InvalidTagNameException e) {
                    throw new InvalidDeserializerCallException();
                }

                tag.setObject(jakeObject);

                result = new TagRemoveLogEntry(tag, remoteUser);


                /// always do this
                result.setTimestamp(date);
                result.setUuid(logEntryUUID);
                return result;


            }
        }


        throw new UnsupportedOperationException();
    }

}
