package de.fu_berlin.inf.dpp.activities;

import de.fu_berlin.inf.dpp.FileList;

/**
 * This class contains all the information that an invited user needs. The
 * {@link FileList} of the whole project, the projectName and the session wide
 * projectID.
 */
public class ProjectExchangeInfo {
    private final FileList fileList;
    private final String projectName;
    private final String projectID;
    private final boolean partial;

    /*
     * The description is not used yet, but there was a description field all
     * the time and I didn't want to delete it. This field could be useful
     * sometime...
     */
    private final String description;

    /**
     * 
     * @param projectID
     *            Session wide ID of the project. This ID is the same for all
     *            users.
     * @param projectName
     *            Name of the project on inviter side.
     * @param fileList
     *            Complete List of all Files in the project.
     */
    public ProjectExchangeInfo(String projectID, String description,
        String projectName, boolean partial, FileList fileList) {

        this.fileList = fileList;
        this.projectName = projectName;
        this.description = description;
        this.projectID = projectID;
        this.partial = partial;
    }

    public FileList getFileList() {
        return fileList;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getDescription() {
        return description;
    }

    public String getProjectID() {
        return projectID;
    }

    public boolean isPartial() {
        return partial;
    }
}