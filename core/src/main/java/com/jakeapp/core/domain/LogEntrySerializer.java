package com.jakeapp.core.domain;

import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.core.synchronization.UserTranslator;
import com.jakeapp.core.synchronization.exceptions.InvalidDeserializerCallException;
import com.jakeapp.core.synchronization.exceptions.InvalidSerializerCallException;

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

		private String content;

		public SerializedObject(String first) {
			content = sepescape(first);
		}

		public SerializedObject add(String next) {
			content += SEP + sepescape(next);
			return this;
		}

		@Override
		public String toString() {
			return content;
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

	public static String serialize(LogEntry<ILogable> le, UserTranslator ut) {
		le.getChecksum();
		le.getLogAction();
		ProjectMember who = le.getMember();
		Project p = le.getProject();
		Long when = le.getTimestamp().getTime();
		SerializedObject result = new SerializedObject(le.getLogAction().toString());
		result.add(when.toString());
		result.add(ut.getUserIdFromProjectMember(p, who).getUserId());

		ILogable data = le.getBelongsTo();
		switch (le.getLogAction()) {
			/** LogAction **/
			case PROJECT_CREATED:
				result.add(p.getProjectId());
				result.add(p.getName());
				return result.toString();
			case START_TRUSTING_PROJECTMEMBER:
				ProjectMember other = (ProjectMember) data;
				return result.add(ut.getUserIdFromProjectMember(p, other).getUserId())
						.toString();
			case STOP_TRUSTING_PROJECTMEMBER:
				ProjectMember otherU = (ProjectMember) data;
				return result.add(ut.getUserIdFromProjectMember(p, otherU).getUserId())
						.toString();
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

	public static LogEntry<ILogable> deserialize(String entry) {
		DeserializedObject input = new DeserializedObject(entry);
		Date when = new Date(Long.decode(input.next()));
		ProjectMember who = new ProjectMember(null, input.next(), null);
		LogAction action = LogAction.valueOf(input.next());
		LogEntry<ILogable> le = new LogEntry<ILogable>(null, action, when, null, null,
				who, null, null, false);
		switch (action) {
			/** LogAction **/
			case PROJECT_CREATED: {
				String projectId = input.next();
				String name = input.next();
				Project p = new Project(name, UUID.fromString(projectId), null, null);
				le.setProject(p);
				return le;
			}
			case START_TRUSTING_PROJECTMEMBER:
			case STOP_TRUSTING_PROJECTMEMBER: {
				ProjectMember other = new ProjectMember(null, input.next(), null);
				le.setBelongsTo(other);
				return le;
			}
			case JAKE_OBJECT_NEW_VERSION:
				detachJakeObjectReference(le, input);
				le.setChecksum(input.next());
				le.setComment(input.next());
				return le;
			case JAKE_OBJECT_DELETE:
			case JAKE_OBJECT_LOCK:
			case JAKE_OBJECT_UNLOCK:
				detachJakeObjectReference(le, input);
				le.setComment(input.next());
				return le;
			case TAG_ADD:
			case TAG_REMOVE:
				detachJakeObjectReference(le, input);
				return le;
			case NOOP:
				return le;
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
