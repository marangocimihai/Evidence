package controller;

/**
 * Predefined constants.
 * 
 * @author marangocimihai
 *
 */
public class Constants {
	public static final String USER_NAME = System.getProperty("user.dir");
	public static final String DB_FILE_NAME = "\\database.db";
	public static final String DB_FILE_PATH = USER_NAME + DB_FILE_NAME;
	public static final String DB_NAME = "jdbc:sqlite:";
	public static final String DB_URL = DB_NAME + DB_FILE_PATH;
	public static final String CURRENTLY_WATCHING = "Currently watching";
	public static final String CURRENT_EPISODE = "Current episode";
	public static final String ON_AIR = "On air";
	public static final String HIATUS = "Hiatus";
	public static final String ENDED = "Ended";
	public static final String SEASON = "Season";
	public static final String PLUS = "plus";
	public static final String MINUS = "minus";
	public static final String EDIT = "edit";
	public static final String NEW_CATEGORY_NAME = "Type the name of the new category: ";
	public static final String NEW_ENTITY_NAME = "Type the name of the new entity: ";
	public static final String CATEGORY_EXISTS = "A category with that name already exists. Please choose another!";
	public static final String ENTITY_EXISTS = "An entity with that name already exists. Please choose another!";
	public static final String INFO_REQUIRED = "Info required";
	public static final String FAILED_SEARCH = "The specified entity has not been found.";
	public static final String NO_RESULTS_FOUND_AND_HINTS = "No results found. Hints: ";
	public static final String PRESS_ENTER_TO_SAVE = "Press ENTER to save.";
	public static final String NO_EMPTY_STRING = "This field cannot be empty!";
}
