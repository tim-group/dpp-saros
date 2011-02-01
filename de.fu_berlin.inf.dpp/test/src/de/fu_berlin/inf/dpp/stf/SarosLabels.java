package de.fu_berlin.inf.dpp.stf;

import java.util.ResourceBundle;

public class SarosLabels {

    protected final static ResourceBundle sarosLabels = ResourceBundle
        .getBundle("saros_labels");

    /**********************************************
     * 
     * Basic Widgets
     * 
     **********************************************/
    // Title of Buttons
    protected final static String YES = "Yes";
    protected final static String OK = "OK";
    protected final static String NO = "No";
    protected final static String CANCEL = "Cancel";
    protected final static String FINISH = "Finish";
    protected final static String APPLY = "Apply";
    protected final static String NEXT = "Next >";
    protected final static String BROWSE = "Browse";

    protected final static String SRC = "src";
    protected final static String SUFIX_JAVA = ".java";

    /**********************************************
     * 
     * View Progress
     * 
     **********************************************/
    protected final static String SHELL_PROGRESS_INFORMATION = "Progress Information";

    /**********************************************
     * 
     * Main Menu File
     * 
     **********************************************/

    static protected final String MENU_NEW = "New";
    static protected final String MENU_PROJECT = "Project...";
    static protected final String MENU_FOLDER = "Folder";
    static protected final String MENU_FILE = "File";
    static protected final String MENU_CLASS = "Class";
    static protected final String MENU_PACKAGE = "Package";
    static protected final String MENU_JAVA_PROJECT = "Java Project";

    protected final static String SHELL_NEW_FOLDER = "New Folder";
    protected final static String SHELL_NEW_FILE = "New File";
    protected final static String SHELL_NEW_JAVA_PACKAGE = "New Java Package";
    protected final static String SHELL_NEW_JAVA_CLASS = "New Java Class";
    static protected final String SHELL_NEW_PROJECT = "New Project";
    static protected final String SHELL_NEW_JAVA_PROJECT = "New Java Project";

    /* categories and nodes of the shell "New Project" */
    static protected final String CATEGORY_GENERAL = "General";
    static protected final String NODE_PROJECT = "Project";

    static protected final String LABEL_PROJECT_NAME = "Project name:";
    static protected final String LABEL_FILE_NAME = "File name:";
    static protected final String LABEL_FOLDER_NAME = "Folder name:";

    /**********************************************
     * 
     * Main Menu Edit
     * 
     **********************************************/
    protected final static String SHELL_DELETE_RESOURCE = "Delete Resources";

    /* menu names */
    protected final static String MENU_DELETE = "Delete";
    protected final static String MENU_EDIT = "Edit";
    protected final static String MENU_COPY = "Copy";
    protected final static String MENU_PASTE = "Paste";

    /**********************************************
     * 
     * Main Menu Refactor
     * 
     **********************************************/
    /* shell titles */
    protected final static String SHELL_MOVE = "Move";
    protected final static String SHELL_RENAME_PACKAGE = "Rename Package";
    protected final static String SHELL_RENAME_RESOURCE = "Rename Resource";
    protected final static String SHELL_RENAME_COMPiIATION_UNIT = "Rename Compilation Unit";
    protected final static String LABEL_NEW_NAME = "New name:";

    /* menu names */
    protected final static String MENU_REFACTOR = "Refactor";
    protected final static String MENU_RENAME = "Rename...";
    protected final static String MENU_MOVE = "Move...";

    /**********************************************
     * 
     * Main Menu Window
     * 
     **********************************************/

    static protected final String TREE_ITEM_GENERAL_IN_PRFERENCES = "General";
    static protected final String TREE_ITEM_WORKSPACE_IN_PREFERENCES = "Workspace";
    static protected final String TREE_ITEM_GENERAL_IN_SHELL_SHOW_VIEW = "General";
    static protected final String TREE_ITEM_PROBLEM_IN_SHELL_SHOW_VIEW = "Problems";
    static protected final String TREE_ITEM_PROJECT_EXPLORER_IN_SHELL_SHOW_VIEW = "Project Explorer";

    /* name of all the main menus */
    static protected final String MENU_WINDOW = "Window";
    protected final static String MENU_OTHER = "Other...";
    protected final static String MENU_SHOW_VIEW = "Show View";

    /* IDs of all the perspectives */
    protected final static String ID_JAVA_PERSPECTIVE = "org.eclipse.jdt.ui.JavaPerspective";
    protected final static String ID_DEBUG_PERSPECTIVE = "org.eclipse.debug.ui.DebugPerspective";
    protected final static String ID_RESOURCE_PERSPECTIVE = "eclipse.ui.resourcePerspective";

    /**********************************************
     * 
     * Main Menu Saros
     * 
     **********************************************/

    protected final static String MENU_SAROS = sarosLabels
        .getString("menu_saros");
    protected final static String MENU_CREATE_ACCOUNT = sarosLabels
        .getString("menu_create_account");
    protected final static String MENU_PREFERENCES = sarosLabels
        .getString("menu_preferences");

    protected final static String SHELL_PREFERNCES = sarosLabels
        .getString("shell_preferences");
    protected final static String SHELL_CREATE_NEW_XMPP_ACCOUNT = sarosLabels
        .getString("shell_create_new_xmpp_account");
    protected final static String SHELL_SAROS_CONFIGURATION = sarosLabels
        .getString("shell_saros_configuration");

    protected final static String LABEL_XMPP_JABBER_SERVER = sarosLabels
        .getString("text_label_xmpp_jabber_server");
    protected final static String LABEL_USER_NAME = sarosLabels
        .getString("text_label_user_name");
    protected final static String LABEL_PASSWORD = sarosLabels
        .getString("text_label_password");
    protected final static String LABEL_REPEAT_PASSWORD = sarosLabels
        .getString("text_label_repeat_password");

    protected final static String ERROR_MESSAGE_PASSWORDS_NOT_MATCH = sarosLabels
        .getString("error_message_passwords_not_match");
    protected final static String ERROR_MESSAGE_COULD_NOT_CONNECT = sarosLabels
        .getString("error_message_could_not_connect");
    protected final static String ERROR_MESSAGE_ACCOUNT_ALREADY_EXISTS = sarosLabels
        .getString("error_message_account_already_exists");

    static protected final String TREE_ITEM_SAROS_IN_SHELL_PREFERENCES = sarosLabels
        .getString("tree_item_label_saros_in_shell_preferences");

    /**********************************************
     * 
     * View Saros Buddies
     * 
     **********************************************/
    /* View infos */
    protected final static String VIEW_SAROS_BUDDIES = sarosLabels
        .getString("view_saros_buddies");
    protected final static String VIEW_SAROS_BUDDIES_ID = sarosLabels
        .getString("view_saros_buddies_id");

    protected final static String SHELL_REQUEST_OF_SUBSCRIPTION_RECEIVED = sarosLabels
        .getString("shell_request_of_subscription_received");
    protected final static String SHELL_BUDDY_ALREADY_ADDED = sarosLabels
        .getString("shell_buddy_already_added");
    protected final static String SHELL_NEW_BUDDY = sarosLabels
        .getString("shell_new_buddy");
    protected final static String SHELL_BUDDY_LOOKUP_FAILED = sarosLabels
        .getString("shell_buddy_look_up_failed");
    protected final static String SHELL_REMOVAL_OF_SUBSCRIPTION = sarosLabels
        .getString("shell_removal_of_subscription");

    protected final static String TB_DISCONNECT = sarosLabels
        .getString("tb_disconnect");
    protected final static String TB_ADD_A_NEW_CONTACT = sarosLabels
        .getString("tb_add_a_new_buddy");
    protected final static String TB_CONNECT = sarosLabels
        .getString("tb_connect");

    protected final static String CM_DELETE = sarosLabels
        .getString("cm_delete");
    protected final static String CM_RENAME = sarosLabels
        .getString("cm_rename");
    protected final static String CM_SKYPE_THIS_BUDDY = sarosLabels
        .getString("cm_skype_this_buddy");
    protected final static String CM_INVITE_BUDDY = sarosLabels
        .getString("cm_invite_buddy");
    protected final static String CM_TEST_DATA_TRANSFER = sarosLabels
        .getString("cm_test_data_transfer_connection");

    protected final static String TREE_ITEM_BUDDIES = sarosLabels
        .getString("tree_item_label_buddies");

    protected final static String LABEL_XMPP_JABBER_JID = sarosLabels
        .getString("text_label_xmpp_jabber_jid");

    /**********************************************
     * 
     * View Saros Session
     * 
     **********************************************/
    /*
     * View infos
     */
    protected final static String VIEW_SAROS_SESSION = sarosLabels
        .getString("view_saros_session");
    protected final static String VIEW_SAROS_SESSION_ID = sarosLabels
        .getString("view_saros_session_id");

    // Permission: Write Access
    static protected final String OWN_PARTICIPANT_NAME = sarosLabels
        .getString("own_participant_name");
    protected final static String PERMISSION_NAME = sarosLabels
        .getString("permission_name");

    /*
     * title of shells which are pop up by performing the actions on the view.
     */
    protected final static String SHELL_CONFIRM_CLOSING_SESSION = sarosLabels
        .getString("shell_confirm_closing_session");
    protected final static String SHELL_INCOMING_SCREENSHARING_SESSION = sarosLabels
        .getString("shell_incoming_screensharing_session");
    protected final static String SHELL_SCREENSHARING_ERROR_OCCURED = sarosLabels
        .getString("shell_screensharing_an_error_occured");
    protected final static String SHELL_INVITATION = sarosLabels
        .getString("shell_invitation");
    protected final static String SHELL_ERROR_IN_SAROS_PLUGIN = sarosLabels
        .getString("shell_error_in_saros_plugin");
    protected final static String SHELL_CLOSING_THE_SESSION = sarosLabels
        .getString("close_the_session");
    protected final static String SHELL_CONFIRM_LEAVING_SESSION = sarosLabels
        .getString("comfirm_leaving_session");

    /*
     * Tool tip text of all the toolbar buttons on the view
     */
    protected final static String TB_SHARE_SCREEN_WITH_BUDDY = sarosLabels
        .getString("tb_share_screen_with_buddy");
    protected final static String TB_STOP_SESSION_WITH_USER = sarosLabels
        .getString("tb_stop_session_with_user");
    protected final static String TB_SEND_A_FILE_TO_SELECTED_BUDDY = sarosLabels
        .getString("tb_send_a_file_to_selected_buddy");
    protected final static String TB_START_VOIP_SESSION = sarosLabels
        .getString("tb_start_a_voip_session");
    protected final static String TB_INCONSISTENCY_DETECTED = sarosLabels
        .getString("tb_inconsistency_detected_in");
    protected final static String TB_OPEN_INVITATION_INTERFACE = sarosLabels
        .getString("tb_open_invitation_interface");
    protected final static String TB_RESTRICT_INVITEES_TO_READ_ONLY_ACCESS = sarosLabels
        .getString("tb_restrict_invitees_to_read_only_access");
    protected final static String TB_ENABLE_DISABLE_FOLLOW_MODE = sarosLabels
        .getString("tb_enable_disable_follow_mode");
    protected final static String TB_LEAVE_THE_SESSION = sarosLabels
        .getString("tb_leave_the_session");

    // Context menu's name of the table on the view
    protected final static String CM_GRANT_WRITE_ACCESS = sarosLabels
        .getString("cm_grant_write_access");
    protected final static String CM_RESTRICT_TO_READ_ONLY_ACCESS = sarosLabels
        .getString("cm_restrict_to_read_only_access");
    protected final static String CM_FOLLOW_THIS_BUDDY = sarosLabels
        .getString("cm_follow_this_buddy");
    protected final static String CM_STOP_FOLLOWING_THIS_BUDDY = sarosLabels
        .getString("cm_stop_following_this_buddy");
    protected final static String CM_JUMP_TO_POSITION_SELECTED_BUDDY = sarosLabels
        .getString("cm_jump_to_position_of_selected_buddy");
    protected final static String CM_CHANGE_COLOR = sarosLabels
        .getString("cm_change_color");

    /**********************************************
     * 
     * View Saros Chat
     * 
     **********************************************/
    protected final static String VIEW_SAROS_CHAT = sarosLabels
        .getString("view_saros_chat");
    protected final static String VIEW_SAROS_CHAT_ID = sarosLabels
        .getString("view_saros_chat_id");

    /**********************************************
     * 
     * View Remote Screen
     * 
     **********************************************/
    // View infos
    protected final static String VIEW_REMOTE_SCREEN = sarosLabels
        .getString("view_remote_screen");
    protected final static String VIEW_REMOTE_SCREEN_ID = sarosLabels
        .getString("view_remote_screen_id");

    protected final static String TB_CHANGE_MODE_IMAGE_SOURCE = sarosLabels
        .getString("tb_change_mode_of_image_source");
    protected final static String TB_STOP_RUNNING_SESSION = sarosLabels
        .getString("tb_stop_running_session");
    protected final static String TB_RESUME = sarosLabels
        .getString("tb_resume");
    protected final static String TB_PAUSE = sarosLabels.getString("tb_pause");

    /**********************************************
     * 
     * Context Menu Saros
     * 
     **********************************************/

    protected final static int CREATE_NEW_PROJECT = 1;
    protected final static int USE_EXISTING_PROJECT = 2;
    protected final static int USE_EXISTING_PROJECT_WITH_CANCEL_LOCAL_CHANGE = 3;
    protected final static int USE_EXISTING_PROJECT_WITH_COPY = 4;

    /*
     * title of shells which are pop up by performing the actions on the package
     * explorer view.
     */
    protected final static String SHELL_INVITATION_CANCELLED = sarosLabels
        .getString("shell_invitation_cancelled");
    protected final static String SHELL_SESSION_INVITATION = sarosLabels
        .getString("shell_session_invitation");
    protected final static String SHELL_SHELL_ADD_PROJECT = sarosLabels
        .getString("shell_add_project");
    protected final static String SHELL_PROBLEM_OCCURRED = sarosLabels
        .getString("shell_problem_occurred");
    protected final static String SHELL_WARNING_LOCAL_CHANGES_DELETED = sarosLabels
        .getString("shell_warning_local_changes_deleted");
    protected final static String SHELL_FOLDER_SELECTION = sarosLabels
        .getString("shell_folder_selection");
    protected final static String SHELL_SAVE_ALL_FILES_NOW = sarosLabels
        .getString("shell_save_all_files_now");

    /* Context menu of a selected tree item on the package explorer view */
    protected final static String CM_SAROS = sarosLabels.getString("cm_saros");
    protected final static String CM_SHARE_PROJECT = sarosLabels
        .getString("cm_share_project");
    protected final static String CM_SHARE_PROJECT_PARTIALLY = sarosLabels
        .getString("cm_share_project_partially");
    protected final static String CM_ADD_TO_SESSION = sarosLabels
        .getString("cm_add_to_session");

    /*
     * second page of the wizard "Session invitation"
     */
    protected final static String RADIO_USING_EXISTING_PROJECT = sarosLabels
        .getString("radio_use_existing_project");
    protected final static String RADIO_CREATE_NEW_PROJECT = sarosLabels
        .getString("radio_create_new_project");

    /**********************************************
     * 
     * Editor
     * 
     **********************************************/

    protected final static String ID_JAVA_EDITOR = "org.eclipse.jdt.ui.CompilationUnitEditor";
    protected final static String ID_TEXT_EDITOR = "org.eclipse.ui.texteditor";

    /* Title of shells */
    static String SHELL_SAVE_RESOURCE = "Save Resource";

    /**********************************************
     * 
     * View Package Explorer
     * 
     **********************************************/

    protected final static String VIEW_PACKAGE_EXPLORER = "Package Explorer";
    protected final static String VIEW_PACKAGE_EXPLORER_ID = "org.eclipse.jdt.ui.PackageExplorer";

    protected final static String SHELL_EDITOR_SELECTION = "Editor Selection";

    /* Context menu of a selected file on the package explorer view */
    protected final static String CM_OPEN = "Open";
    protected final static String CM_OPEN_WITH = "Open With";
    protected final static String CM_OTHER = "Other...";

    /**********************************************
     * 
     * Context Menu Team
     * 
     **********************************************/
    protected final static String SHELL_REVERT = "Revert";
    protected final static String SHELL_SHARE_PROJECT = "Share Project";
    protected final static String SHELL_SAROS_RUNNING_VCS_OPERATION = "Saros running VCS operation";
    protected final static String SHELL_CONFIRM_DISCONNECT_FROM_SVN = "Confirm Disconnect from SVN";
    static protected final String SHELL_IMPORT = "Import";
    static protected final String SHELL_SWITCH = "Switch";
    static protected final String SHELL_SVN_SWITCH = "SVN Switch";

    protected final static String LABEL_CREATE_A_NEW_REPOSITORY_LOCATION = "Create a new repository location";
    protected final static String LABEL_URL = "Url:";
    protected final static String LABEL_TO_URL = "To URL:";
    static protected final String LABEL_SWITCH_TOHEAD_REVISION = "Switch to HEAD revision";
    static protected final String LABEL_REVISION = "Revision:";

    /* All the sub menus of the context menu "Team" */
    protected final static String CM_REVERT = "Revert...";
    protected final static String CM_DISCONNECT = "Disconnect...";
    protected final static String CM_SHARE_PROJECT_OF_TEAM = "Share Project...";
    protected final static String CM_SWITCH_TO_ANOTHER_BRANCH_TAG_REVISION = "Switch to another Branch/Tag/Revision...";
    protected final static String CM_TEAM = "Team";

    /* table iems of the shell "Share project" of the conext menu "Team" */
    protected final static String TABLE_ITEM_REPOSITORY_TYPE_SVN = "SVN";

}