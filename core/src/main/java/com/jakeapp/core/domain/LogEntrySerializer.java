package com.jakeapp.core.domain;

import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;

//import com.jakeapp.core.synchronization.exceptions.InvalidDeserializerCallException;
//import com.jakeapp.core.synchronization.exceptions.InvalidSerializerCallException;


/***
 * Protocol specification:
 * <p>
 * see {@link LogAction} for what is stored each time
 * </p>
 * 
 * @author johannes
 * 
 */
public class LogEntrySerializer {

	public static final String SEP = "|";

	public static final String SEPREPLACE = "-";

	private static Logger log = Logger.getLogger(LogEntrySerializer.class);

	public static class SerializedObject {

		private StringBuffer content;

		public SerializedObject(String first) {
			content = new StringBuffer(1000).append(sepescape(first));
		}


        /**
         * A
         * @param next
         * @return
         */
        public SerializedObject add(String next) {
			content.append(SEP).append(sepescape(next));
			return this;
		}

		@Override
		public String toString() {
			return content.toString();
		}

		private String sepescape(String name) {
			return name.replace(SEP, SEPREPLACE);
		}
	}

	public static class DeserializedObject {

		private String[] parts;

		private int counter = 0;

		public DeserializedObject(String content) {
			parts = content.split("\\" + SEP);
		}

		public String next() {
			return parts[counter++];
		}
	}

	public static String serialize(LogEntry<ILogable> le, Project project) {
		le.getChecksum();
		le.getLogAction();
		UserId who = le.getMember();
		Project p = project;
		Long when = le.getTimestamp().getTime();
		SerializedObject result = new SerializedObject(le.getLogAction().toString());
		result.add(when.toString());
        // TODO
//		result.add(ut.getUserIdFromProjectMember(p, who).getUserId());

		ILogable data = le.getBelongsTo();
		switch (le.getLogAction()) {
			/** LogAction **/
			case PROJECT_CREATED:
				result.add(p.getProjectId());
				result.add(p.getName());
				return result.toString();
			case START_TRUSTING_PROJECTMEMBER:
				UserId other = (UserId) data;
				throw new RuntimeException("Method not yet implemented! This is a bug, fix it!");
//                return null; // TODO
//                return result.add(ut.getUserIdFromProjectMember(p, other).getUserId())
//						.toString();
			case STOP_TRUSTING_PROJECTMEMBER:
				UserId otherU = (UserId) data;
                throw new RuntimeException("Method not yet implemented! This is a bug, fix it!");
//                return null; // TODO
//				return result.add(ut.getUserIdFromProjectMember(p, otherU).getUserId())
//						.toString();
			case JAKE_OBJECT_NEW_VERSION:
				appendJakeObjectReference(result, (JakeObject) data);
				result.add(le.getChecksum());
				result.add(le.getComment());
				return result.toString();
			case JAKE_OBJECT_DELETE:
			case JAKE_OBJECT_LOCK:
			case JAKE_OBJECT_UNLOCK:
				appendJakeObjectReference(result, (JakeObject) data);
				result.add(le.getComment());
				return result.toString();
			case TAG_ADD:
			case TAG_REMOVE:
				appendJakeObjectReference(result, (JakeObject) data);
				return result.toString();
			case NOOP:
				return result.toString();
		}

		throw new InvalidSerializerCallException();
	}

	private static void appendJakeObjectReference(SerializedObject result, JakeObject jo) {
		if (jo instanceof FileObject) {
			result.add("F");
			result.add(((FileObject) jo).getRelPath());
		} else if (jo instanceof NoteObject) {
			result.add("N");
			NoteObject note = (NoteObject) jo;
			result.add(note.getUuid().toString());
			result.add(note.getContent());
		} else {
			throw new InvalidSerializerCallException();
		}
	}

    /**
     * This method is used to deseralize Strings to corresponding LogEntrys 
     * @param entry
     * @return an appropriate LogEntry
     */
	public static LogEntry<ILogable> deserialize(String entry) {
		DeserializedObject input = new DeserializedObject(entry);
		Date when = new Date(Long.decode(input.next()));
//		ProjectMember who = new ProjectMember(null, input.next(), null); // TODO
		LogAction action = LogAction.valueOf(input.next());
//		LogEntry<ILogable> le = new LogEntry<ILogable>(null, action, when, null, null,
//				who, null, null, false); // TODO
		switch (action) {
			/** LogAction **/
			case PROJECT_CREATED: {
				String projectId = input.next();
				String name = input.next();
				Project p = new Project(name, UUID.fromString(projectId), null, null);
//				le.setProject(p);  // TODO
//				return le; // TODO
			}
			case START_TRUSTING_PROJECTMEMBER:
			case STOP_TRUSTING_PROJECTMEMBER: {
//              TODO
//				ProjectMember other = new ProjectMember(null, input.next(), null);
//				le.setBelongsTo(other);
//				return le;
			}
			case JAKE_OBJECT_NEW_VERSION:
//              TODO
//				detachJakeObjectReference(le, input);
//				le.setChecksum(input.next());
//				le.setComment(input.next());
//				return le;
			case JAKE_OBJECT_DELETE:
			case JAKE_OBJECT_LOCK:
			case JAKE_OBJECT_UNLOCK:
//				TODO
//                detachJakeObjectReference(le, input);
//				le.setComment(input.next());
//				return le;
			case TAG_ADD:
			case TAG_REMOVE:
//				  TODO
//                detachJakeObjectReference(le, input);
//				return le;
			case NOOP:
//                TODO
//				return le;
		}

		throw new InvalidSerializerCallException();
	}

	private static void detachJakeObjectReference(LogEntry<ILogable> le,
			DeserializedObject input) {
		String type = input.next();
		if ("F".equals(type)) {
			le.setBelongsTo(new FileObject(null, input.next()));
		} else if ("N".equals(type)) {
			UUID uuid = UUID.fromString(input.next());
			String content = input.next();
			le.setBelongsTo(new NoteObject(uuid, null, content));
		} else {
			throw new InvalidDeserializerCallException();
		}
	}
}
