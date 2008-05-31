package com.doublesignal.sepm.jake.core.dao;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchFileException;
import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.domain.Tag;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.List;
import java.util.Set;

/**
 * Test for the JDBC JakeObject DAO
 */
public class JdbcJakeObjectDaoTest extends DBTest {
	private static JdbcJakeObjectDao dao;
	@BeforeClass
	public static void setDAO() {
		dao = new JdbcJakeObjectDao();
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName("org.hsqldb.jdbcDriver");
		ds.setUrl("jdbc:hsqldb:mem:db");
		ds.setUsername("sa");
		dao.setDataSource(ds);
	}

	@Test
	public void testGetExistingFileObject() throws NoSuchFileException {
		FileObject fo = dao.getFileObjectByName("test.docx");
		Set<Tag> tags = fo.getTags();

		boolean hasTagWord = false;
		boolean hasTagMicrosoft = false;
		boolean hasTagTest = false;
		boolean hasTagFoobar = false;

		for(Tag t: tags) {
			if("word".equals(t.getName())) {
				hasTagWord = true;
			}
			if("microsoft".equals(t.getName())) {
				hasTagMicrosoft = true;
			}
			if("test".equals(t.getName())) {
				hasTagTest = true;
			}
			if("foobar".equals(t.getName())) {
				hasTagFoobar = true;
			}
		}

		assertEquals("Should be the right FileObject", "test.docx", fo.getName());
		assertTrue("Should have tag 'word'", hasTagWord);
		assertTrue("Should have tag 'microsoft'", hasTagMicrosoft);
		assertTrue("Should have tag 'test'", hasTagTest);
		assertTrue("Should have tag 'foobar'", hasTagFoobar);
	}

	@Test
	public void testGetExistingNoteObject() throws NoSuchFileException {
		NoteObject no = dao.getNoteObjectByName("note:chris@jabber.doublesignal.com:20080531201500");
		assertEquals("Should be the right NoteObject", "I am a machine.", no.getContent());
	}

	@Test(expected= NoSuchFileException.class)
	public void testGetNonexistingFileObject() throws NoSuchFileException {
		dao.getFileObjectByName("/I_DO_NOT_EXIST");
	}

	@Test(expected= NoSuchFileException.class)
	public void testGetNonexistingNoteObject() throws NoSuchFileException {
		dao.getNoteObjectByName("note:I_DO_NOT_EXIST:37463284324");
	}

	@Test
	public void testGetAllFileObjects() {
		List<FileObject> fos = dao.getAllFileObjects();

		boolean firstExists = "subfolder/sepm.txt".equals(fos.get(0).getName()) ||
				                "subfolder/sepm.txt".equals(fos.get(1).getName()) ||
				                "subfolder/sepm.txt".equals(fos.get(2).getName());
		boolean secondExists = "test.docx".equals(fos.get(0).getName()) ||
				                 "test.docx".equals(fos.get(1).getName()) ||
				                 "test.docx".equals(fos.get(2).getName());
		boolean thirdExists = "pr0n.jpg".equals(fos.get(0).getName()) ||
				                "pr0n.jpg".equals(fos.get(1).getName()) ||
				                "pr0n.jpg".equals(fos.get(2).getName());

		assertEquals("Should have three FileObjects", 3, fos.size());
		assertTrue("First FileObject should be in resulting list", firstExists);
		assertTrue("Second FileObject should be in resulting list", secondExists);
		assertTrue("Third FileObject should be in resulting list", thirdExists);
	}

	@Test
	public void testGetAllNoteObjects() {
		List<NoteObject> nos = dao.getAllNoteObjects();

		boolean firstExists = "I am a machine.".equals(nos.get(0).getContent()) ||
				                "I am a machine.".equals(nos.get(1).getContent()) ||
				                "I am a machine.".equals(nos.get(2).getContent());
		boolean secondExists = "Lorem ipsum dolor sit amet.".equals(nos.get(0).getContent()) ||
				                 "Lorem ipsum dolor sit amet.".equals(nos.get(1).getContent()) ||
				                 "Lorem ipsum dolor sit amet.".equals(nos.get(2).getContent());
		boolean thirdExists = "99 bottles of beer on the wall!".equals(nos.get(0).getContent()) ||
				                "99 bottles of beer on the wall!".equals(nos.get(1).getContent()) ||
				                "99 bottles of beer on the wall!".equals(nos.get(2).getContent());

		assertEquals("Should have three NoteObjects", 3, nos.size());
		assertTrue("First NoteObject should be in resulting list", firstExists);
		assertTrue("Second NoteObject should be in resulting list", secondExists);
		assertTrue("Third NoteObject should be in resulting list", thirdExists);
	}

	@Test
	public void testSaveExistingFileObject() {

	}

	@Test
	public void testSaveNonexistingFileObject() {

	}

	@Test
	public void testSaveExistingNoteObject() {

	}

	@Test
	public void testSaveNonexistingNoteObject() {

	}

	@Test
	public void testDeleteFileObject() {
		
	}

	@Test
	public void testDeleteNoteObject() {

	}
}
