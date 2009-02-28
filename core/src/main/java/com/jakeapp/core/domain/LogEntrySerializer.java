package com.jakeapp.core.domain;

import com.jakeapp.core.dao.IProjectDao;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.synchronization.exceptions.InvalidDeserializerCallException;

import java.util.UUID;
import java.util.Date;


public class LogEntrySerializer {

    IProjectDao projectDao;


    private final String SEPERATOR = "XXAAAXXX"; //  SEPERATOR LOOKS LIKE |\/|
    private final String SEPERATOR_REGEX = "XXAAAXXX";

    public LogEntrySerializer(IProjectDao projectDao) {
        this.projectDao = projectDao;
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


    public String serialize(JakeObjectLogEntry logEntry, Project project) {


        StringBuffer sb = new StringBuffer(500);

        sb.append(SEPERATOR).append(project.getProjectId());
        sb.append(SEPERATOR).append(logEntry.getTimestamp().getTime());
        sb.append(SEPERATOR).append(logEntry.getLogAction().ordinal());
        sb.append(SEPERATOR).append(logEntry.getMember().getProtocolType().ordinal());
        sb.append(SEPERATOR).append(logEntry.getMember().getUserId());
        sb.append(SEPERATOR).append(logEntry.getUuid().toString());

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

//        sb.append(SEPERATOR).append(project.getName());


        sb.append(SEPERATOR);
        return sb.toString();
    }


    public String serialize(LogEntry<ILogable> logEntry, Project project) {


        throw new UnsupportedOperationException();
//        return "";
    }


    public LogEntry<? extends ILogable> deserialize(String input) throws NoSuchProjectException {
        String[] parts = input.split(SEPERATOR_REGEX);

        if (parts.length < 4)
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

            case FOLLOW_TRUSTING_PROJECTMEMBER:
                break;
            case JAKE_OBJECT_DELETE:
                break;
            case JAKE_OBJECT_LOCK:
                break;
            case JAKE_OBJECT_NEW_VERSION: {
                Project p = null;
                JakeObjectLogEntry result;
                
                p = projectDao.read(projectUUID); // throws NoSuchProjectException

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



                result = new JakeObjectLogEntry(logAction, jakeObject, remoteUser, comment, checksum, false);


                /// always do this
                result.setTimestamp(date);
                result.setUuid(logEntryUUID);
                return result;

            }

            case JAKE_OBJECT_UNLOCK:
                break;
            case NOOP:
                break;
            case PROJECT_CREATED:
            {
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
            case START_TRUSTING_PROJECTMEMBER:
                break;
            case STOP_TRUSTING_PROJECTMEMBER:
                break;
            case TAG_ADD:
                break;
            case TAG_REMOVE:
                break;
        }


        throw new UnsupportedOperationException();
//        return null;
    }

}
