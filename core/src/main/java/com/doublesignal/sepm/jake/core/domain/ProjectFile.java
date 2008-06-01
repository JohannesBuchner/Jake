package com.doublesignal.sepm.jake.core.domain;

import com.doublesignal.sepm.jake.fss.NotADirectoryException;
import com.doublesignal.sepm.jake.fss.NotAFileException;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

/**
 * This class should handle Jake's ProjectFile, meaning the configuration file in the filesystem,
 * which stores the name of the project(s) and the corresponding ProjectFolder - Path.
 */
public class ProjectFile {
    private static Logger log = Logger.getLogger(ProjectFile.class);
    private String ConfigFilename = "JakeProjects.xml";
    private File configFile;
    private String parentRootPath = "";

    private Set<Project> projects = new HashSet<Project>();




    public ProjectFile(String rootPath) throws NotADirectoryException, IOException, NotAFileException {





        File rootPathDirectory = new File(rootPath);
        if (!rootPathDirectory.isDirectory())
            throw new NotADirectoryException("rootPath must be a directory");

        parentRootPath = new File(rootPathDirectory.getParentFile().getAbsolutePath()).getAbsolutePath();

        System.out.println("parentRootPath = " + parentRootPath);


        configFile = new File(parentRootPath + File.separator + ConfigFilename);

        if (!configFile.exists()) {
            log.debug("ProjectFile(): configFile does not exist");
            if (!configFile.createNewFile()) {
                log.debug("ProjectFile(): could not create the configFile");
                throw new IOException("could not create configFile");
            }
            log.debug("ProjectFile(): could obviously create the projectFile");
        } else {
            log.debug("configFile does exist.");
            if (configFile.isDirectory()) {
                throw new NotAFileException("ConfigFile must be a file, not a directory");
            } else {
                log.debug("configFile is not a directory");
            }
        }
        // ok, configFile is a file and writable
    }


    private void loadProjects() throws NotAFileException, IOException {
        try {
            Document xmlDocument;
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

            if(!configFile.isFile())
                throw new NotAFileException();


            log.debug("loadProjects: parsing configFile " + configFile.getAbsolutePath());
            xmlDocument = builder.parse(configFile);


            Node rootNode = xmlDocument.getDocumentElement();
            if (!rootNode.getNodeName().equals("JakeProjects")) {
                // invalid JakeConfigFile
                // simply save it
                saveProjects();
            } else {
                Node child;
                Node jakeProject;
                String jakeProjectName;
                String jakeProjectPath;


                child = rootNode.getFirstChild();

                // we only have two levels, so a non recursive method does its way.
                while (child != null) {
                    // parsing projects
                    if (child.getNodeType() != Node.ELEMENT_NODE)
                    {
                        child = child.getNextSibling();
                        continue;
                    }
                    jakeProjectName = jakeProjectPath = null;

                    if(child.getNodeName().equals("JakeProject"))
                    {
                        jakeProject = child;
                        child = jakeProject.getFirstChild();

                            while(child != null)
                            {
                                if(child.getNodeType() != Node.ELEMENT_NODE)
                                {
                                    child = child.getNextSibling();
                                    continue;
                                }


                                if(child.getNodeName().equals("projectName"))
                                    jakeProjectName = child.getTextContent();
                                if(child.getNodeName().equals("projectFolder"))
                                    jakeProjectPath = child.getTextContent();

                                child = child.getNextSibling();
                            }
                        child = jakeProject;
                    }
                    if(jakeProjectName != null && jakeProjectPath != null)
                    {
                        projects.add(new Project(new File(jakeProjectPath), jakeProjectName));
                    }

                    child = child.getNextSibling();
                }

            }

        } catch (SAXException e) {
            // obviously the configFile is not parseable... so delete it, create a new one and clear the projectslist
            //configFile.delete();
            //configFile.createNewFile();
            projects.clear();
        } catch (ParserConfigurationException e) {
            projects.clear();

        }
    }

    private void saveProjects() throws IOException {

        try {
            Document xmlDocument;
            DocumentBuilderFactory documentBuilderFactory;
            DocumentBuilder builder;
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            builder = documentBuilderFactory.newDocumentBuilder();






            xmlDocument = builder.newDocument();
            xmlDocument.setXmlStandalone(true);
            xmlDocument.setXmlVersion("1.0");

            Node rootNode;

            rootNode = xmlDocument.createElement("JakeProjects");

            if (projects.size() > 0) {

                Node jakeProject;
                Node projectName;
                Node projectFolder;

                for (Project project : projects) {
                    jakeProject = rootNode.getOwnerDocument().createElement("JakeProject");
                    projectName = rootNode.getOwnerDocument().createElement("projectName");
                    projectFolder = rootNode.getOwnerDocument().createElement("projectFolder");

                    projectName.setTextContent(project.getName());
                    projectFolder.setTextContent(project.getRootPath().getAbsolutePath());
                    jakeProject.appendChild(projectName);
                    jakeProject.appendChild(projectFolder);
                    rootNode.appendChild(jakeProject);
                }


            } else {
                // projects empty
                log.debug("there are no projects defined in that file!");
            }

            xmlDocument.appendChild(rootNode);
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();



            if(configFile.isFile())
            {
                log.debug("configFile is a File");
            }

            if(configFile.exists())
            {
                log.debug("configFile exists");
            }

            log.debug("configFile.getAbsolutePath() = " + configFile.getAbsolutePath());

            transformer.transform(
                    new DOMSource(xmlDocument),
                    new StreamResult(
                            new File(configFile.getAbsolutePath())
                    )
            );


        }

        catch (TransformerConfigurationException e) {
            log.debug("catched a TransofmerConfigurationException");

        } catch (TransformerException e) {
            log.debug("catched a TransformerException");

        } catch (ParserConfigurationException e) {
            log.debug("catched a ParserConfigurationException");

        }
    }

    public Project createProject(Project jakeProject) throws IOException {
        try {
            log.debug("createProject: loadProjects()");
            loadProjects(); // get the existing projects
        }
        catch (IOException e) {
            log.debug("createProject: cought IOException");
            this.projects.clear();
        }
        catch (NotAFileException e) {
            log.debug("createProject: cought NotAFileException");
            // ok, obviously there is no configFile, so simply ignore it and create a new one
            this.projects.clear();
        }
        log.debug("createProject: adding Project to ProjectList");
        this.projects.add(jakeProject); // add the new project
        log.debug("createProject: calling saveProjects()");
        saveProjects(); // save the projects
        return jakeProject; // return the current project
    }


}
