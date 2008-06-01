package com.doublesignal.sepm.jake.core.dao;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchFileException;
import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.domain.Tag;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidTagNameException;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

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
		Set<Tag> tags = no.getTags();

		boolean hasTagImportant = false;
		boolean hasTagUniversity = false;
		boolean hasTagFoobar = false;

		for(Tag t: tags) {
			if("important".equals(t.getName())) {
				hasTagImportant = true;
			}
			if("university".equals(t.getName())) {
				hasTagUniversity = true;
			}
			if("foobar".equals(t.getName())) {
				hasTagFoobar = true;
			}
		}

		assertEquals("Should be the right NoteObject", "I am a machine.", no.getContent());
		assertTrue("Should have tag 'important'", hasTagImportant);
		assertTrue("Should have tag 'university'", hasTagUniversity);
		assertTrue("Should have tag 'foobar'", hasTagFoobar);
	}

	@Test(expected= NoSuchFileException.class)
	public void testGetInvalidNoteObject() throws NoSuchFileException {
		dao.getNoteObjectByName("test.docx");	
	}

	@Test(expected= NoSuchFileException.class)
	public void testGetInvalidFileObject() throws NoSuchFileException {
		dao.getFileObjectByName("note:chris@jabber.doublesignal.com:20080531201500");
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
	public void testSaveExistingFileObject()
			  throws NoSuchFileException, InvalidTagNameException {
		FileObject fo = dao.getFileObjectByName("test.docx");
		fo.addTag(new Tag("noodles"));
		dao.save(fo);

		FileObject fonew = dao.getFileObjectByName("test.docx");
		boolean hasNewTag = fonew.getTags().contains(new Tag("noodles"));
		assertTrue("Object should have been saved correctly", hasNewTag);
	}

	@Test
	public void testSaveNonexistingFileObject()
			  throws InvalidTagNameException, NoSuchFileException {
		FileObject fo = new FileObject("jinglebells.sql");
		fo.addTag(new Tag("christmas"));
		dao.save(fo);

		FileObject fonew = dao.getFileObjectByName("jinglebells.sql");
		boolean hasNewTag = fonew.getTags().contains(new Tag("christmas"));
		assertTrue("Object should now exist and have tag", hasNewTag);
	}

	@Test
	public void testSaveExistingNoteObject()
			  throws NoSuchFileException, InvalidTagNameException {
		NoteObject no = dao.getNoteObjectByName("note:chris@jabber.doublesignal.com:20080531201500");
		no.setContent("I'm a winner baby...");
		no.addTag(new Tag("winner"));
		dao.save(no);

		NoteObject nonew = dao.getNoteObjectByName("note:chris@jabber.doublesignal.com:20080531201500");
		boolean hasNewTag = nonew.getTags().contains(new Tag("winner"));
		assertEquals("Object should now have new content", "I'm a winner baby...", nonew.getContent());
		assertTrue("Object should now have new tag", hasNewTag);
	}

	@Test
	public void testSaveNonexistingNoteObject()
			  throws InvalidTagNameException, NoSuchFileException {
		NoteObject no = new NoteObject("note:foobar@jabber.doublesignal.com:20080531203520", "Ich bin so schoen, ich bin so toll, ich bin der Anton aus Tirol...");
		no.addTag(new Tag("spam"));
		dao.save(no);

		NoteObject nonew = dao.getNoteObjectByName("note:foobar@jabber.doublesignal.com:20080531203520");
		boolean hasNewTag = nonew.getTags().contains(new Tag("spam"));
		assertEquals("Object should now have content", "Ich bin so schoen, ich bin so toll, ich bin der Anton aus Tirol...", nonew.getContent());
		assertTrue("Object should now exist and have tag", hasNewTag);
	}

	@Test
	public void testDeleteFileObject() throws NoSuchFileException {
		dao.delete(dao.getFileObjectByName("test.docx"));
		
		/* We need a seperate try/catch block instead of annotation-based exception
		 * checking so that we can make sure the exception is not thrown in the
		 * getFileObjectByName(...) call above.  
		 */
		try {
			dao.getFileObjectByName("test.docx");
			fail("FileObject should no longer exist");
		} catch(NoSuchFileException e) {
			/* Nothing to see here, move along */
		}
	}

	@Test
	public void testDeleteNoteObject() throws NoSuchFileException {
		dao.delete(dao.getNoteObjectByName("note:chris@jabber.doublesignal.com:20080531201500"));

		/* We need a seperate try/catch block instead of annotation-based exception
		 * checking so that we can make sure the exception is not thrown in the
		 * getNoteObjectByName(...) call above.
		 */
		try {
			dao.getNoteObjectByName("note:chris@jabber.doublesignal.com:20080531201500");
			fail("NoteObject should no longer exist");
		} catch(NoSuchFileException e) {
			/* Nothing to see here, move along */
		}
	}

	@Test
	public void testAddTagsToObject()
			  throws InvalidTagNameException, NoSuchFileException {
		dao.addTagsTo(new FileObject("boogie.mid"), new Tag("music"), new Tag("woogie"));
		FileObject fo = dao.getFileObjectByName("boogie.mid");

		Set<Tag> tags = fo.getTags();
		boolean containsTags = false;
		if(tags.contains(new Tag("music")) && tags.contains(new Tag("woogie"))) {
			containsTags = true;
		}
		assertTrue("Object should now have these tags", containsTags);
	}

	@Test
	public void testRemoveTagsFromObject()
			  throws NoSuchFileException, InvalidTagNameException {
		dao.removeTagsFrom(dao.getNoteObjectByName("note:chris@jabber.doublesignal.com:20080531201500"), new Tag("important"));
		NoteObject no = dao.getNoteObjectByName("note:chris@jabber.doublesignal.com:20080531201500");
		assertTrue("Object should no longer have tag", !no.getTags().contains(new Tag("important")));
	}
}
