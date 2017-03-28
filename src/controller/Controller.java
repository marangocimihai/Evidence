package controller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import view.DialogException;

/**
 * The following class proceeds with all the database queries.
 * 
 * @author marangocimihai
 *
 */
public class Controller {
	static Connection connection = null;

	/**
	 * Initializing the driver used for connecting to the database.
	 */
	public static void initializeDriver() {
		try {
			connection = DriverManager.getConnection(Constants.DB_URL);
		} catch (SQLException e) {
			new DialogException(e);
		}
	}

	/**
	 * Checks for the database.db file. If it does not exist, it will
	 * automatically create it.
	 */
	public static void createDatabaseFile() {
		File file = new File(Constants.DB_FILE_PATH);

		try {
			file.createNewFile();
		} catch (IOException e) {
			new DialogException(e);
		}
	}

	/**
	 * Checks for the database ENTITY and CATEGORY tables. If they do not exist,
	 * it will automatically create them.
	 */
	public static void createDatabaseTables() {
		initializeDriver();

		try {
			String queryEntity = "CREATE TABLE IF NOT EXISTS ENTITY(`ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " + 
																   "`NAME` TEXT NOT NULL, " + 
																   "`STATUS` TEXT, " +
																   "`RATING` TEXT, " + 
																   "`SEASON` INTEGER, " + 
																   "`CURRENTEPISODE` INTEGER, " + 
																   "`STARTDATE` TEXT NOT NULL, " + 
																   "`ENDDATE` TEXT, " + 
																   "`CATEGORYID` INTEGER NOT NULL)";

			String queryCategory = "CREATE TABLE IF NOT EXISTS CATEGORY(`ID` INTEGER PRIMARY KEY AUTOINCREMENT, " + 
																	   "`NAME` TEXT)";
			Statement stmt = connection.createStatement();
			stmt.execute(queryEntity);
			stmt.execute(queryCategory);
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}
	}

	/**
	 * Closes the database.
	 * 
	 * @param connection
	 */
	public static void closeDatabase(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			new DialogException(e);
		}
	}

	/**
	 * Returns the entity ID by using its name.
	 * 
	 * @param entityName
	 * @return ID
	 */
	public static int getEntityID(String entityName) {
		initializeDriver();
		int ID = 0;

		try {
			String query = "SELECT ID FROM ENTITY WHERE NAME LIKE ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, entityName);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				ID = rs.getInt("ID");
			}
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}

		return ID;
	}

	/**
	 * Returns the category ID by using its name.
	 * 
	 * @param categoryName
	 * @return ID
	 */
	public static int getCategoryID(String categoryName) {
		initializeDriver();
		int ID = 0;

		try {
			String query = "SELECT ID FROM CATEGORY WHERE NAME LIKE ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, categoryName);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				ID = rs.getInt("ID");
			}
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}

		return ID;
	}

	/**
	 * Gets the name of the category and returns its content. The query is done
	 * using the ID of the category.
	 * 
	 * @param category
	 * @return categoryContent
	 */
	public static List<String> getCategoryContent(String category) {
		initializeDriver();

		List<String> categoryContent = new ArrayList<String>();

		try {
			String query = "SELECT * FROM ENTITY WHERE CATEGORYID = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, getCategoryID(category));
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				categoryContent.add(rs.getString("NAME"));
			}
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}

		return categoryContent;
	}

	/**
	 * Gets the name of the entity and returns all the information about it.
	 * 
	 * @param entityName
	 * @return entityInfo
	 */
	public static Entity getDetails(String entityName) {
		initializeDriver();

		Entity entityInfo = null;

		try {
			String query = "SELECT * FROM ENTITY WHERE ID = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, getEntityID(entityName));
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				entityInfo = new Entity(rs.getInt("ID"), rs.getString("NAME"), rs.getString("STATUS"), rs.getDouble("RATING"), rs.getInt("SEASON"),
										rs.getInt("CURRENTEPISODE"), rs.getString("STARTDATE"), rs.getString("ENDDATE"));
			}

		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}

		return entityInfo;
	}

	/**
	 * Returns the current date in milliseconds.
	 * @return date
	 */
	public static String getCurrentDate() {
		Date date = new Date();
		long millisecond = date.getTime();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millisecond);

		return new Long(millisecond).toString();
	}

	/**
	 * Gets a milisecond value and returns the date specified by it.
	 * 
	 * @param miliseconds
	 * @return the date
	 */
	public static String getCurrentDate(String miliseconds) {
		long miliseconds1 = Long.parseLong(miliseconds);
		Calendar calendar = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy (HH:mm)");
		calendar.setTimeInMillis(miliseconds1);

		return formatter.format(calendar.getTime()).toString();
	}

	/**
	 * Queries the database for all the categories inside it.
	 * 
	 * @return categories
	 */
	public static List<String> getCategories() {
		initializeDriver();
		List<String> categories = new ArrayList<String>();

		try {
			String query = "SELECT NAME FROM CATEGORY";
			Statement stmt = connection.createStatement();

			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				categories.add(rs.getString("NAME"));
			}
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}

		return categories;
	}

	/**
	 * Adds the entity in database using its info. Returns true or false
	 * according to if the operation successfully proceed or not.
	 * 
	 * @param entity
	 * @param category
	 * @return true or false
	 */
	public static boolean addEntity(Entity entity, String category) {
		initializeDriver();
		int affectedRows = 0;

		try {
			String query = "INSERT INTO ENTITY(NAME, STATUS, RATING, SEASON, CURRENTEPISODE, STARTDATE, ENDDATE, CATEGORYID) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, entity.name);
			ps.setString(2, entity.status);
			ps.setDouble(3, entity.rating);
			ps.setInt(4, entity.season);
			ps.setInt(5, entity.currentEpisode);
			ps.setString(6, entity.startDate);
			ps.setString(7, entity.endDate);
			ps.setInt(8, entity.categoryID);

			affectedRows = ps.executeUpdate();
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}

		return affectedRows > 0 ? true : false;
	}

	/**
	 * Adds the specified rating for the specified entity.
	 * 
	 * @param entity
	 * @param rating
	 * @param category
	 * @return true or false
	 */
	public static boolean addRating(Entity entity, double rating, String category) {
		int ID = getEntityID(entity.name);
		int affectedRows = 0;
		initializeDriver();

		try {
			String query = "UPDATE ENTITY SET RATING = ? WHERE ID = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setDouble(1, rating);
			ps.setInt(2, ID);

			affectedRows = ps.executeUpdate();

		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}

		return affectedRows > 0 ? true : false;
	}

	/**
	 * Adds a new category.
	 * 
	 * @param category
	 */
	public static void addCategory(String category) {
		initializeDriver();

		try {
			String query = "INSERT INTO CATEGORY(NAME) VALUES(?)";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, category);

			ps.executeUpdate();
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}
	}

	/**
	 * Removes an entity.
	 * 
	 * @param entityName
	 * @param category
	 * @return true or false
	 */
	public static boolean removeEntity(String entityName, String category) {
		initializeDriver();
		int affectedRows = 0;

		try {
			String query = "DELETE FROM ENTITY WHERE ID = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, getEntityID(entityName));
			affectedRows = ps.executeUpdate();
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}
		return affectedRows > 0 ? true : false;
	}

	/**
	 * Removes a category.
	 * 
	 * @param category
	 * @return true or false
	 */
	public static boolean removeCategory(String category) {
		initializeDriver();
		int affectedRows = 0;

		try {
			String query = "DELETE FROM CATEGORY WHERE ID = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, getCategoryID(category));
			affectedRows = ps.executeUpdate();
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}
		return affectedRows > 0 ? true : false;
	}

	/**
	 * Delete everything linked with the specified category.
	 * 
	 * @param category
	 * @return true or false
	 */

	public static boolean removeCategoryContent(String category) {
		initializeDriver();
		int affectedRows = 0;

		try {
			String query = "DELETE FROM ENTITY WHERE CATEGORYID = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, getCategoryID(category));
			affectedRows = ps.executeUpdate();
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}
		return affectedRows > 0 ? true : false;
	}

	public static boolean changeName(Entity entity, String name) {
		int ID = getEntityID(entity.name);
		initializeDriver();
		int affectedRows = 0;

		try {
			String query = "UPDATE ENTITY SET NAME = ? WHERE ID = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, name);
			ps.setInt(2, ID);
			affectedRows = ps.executeUpdate();
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}

		return affectedRows > 0 ? true : false;
	}
	
	/**
	 * Changes the current status value.
	 * 
	 * @param entity
	 * @param option
	 * @param status
	 * @return true or false
	 */
	public static boolean changeStatus(Entity entity, String status) {
		initializeDriver();
		int affectedRows = 0;

		try {
			String query = "UPDATE ENTITY SET STATUS = ? WHERE ID = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, status);
			ps.setInt(2, getEntityID(entity.name));
			affectedRows = ps.executeUpdate();
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}

		return affectedRows > 0 ? true : false;
	}
	
	/**
	 * Changes the end date.
	 * 
	 * @param entity
	 * @return true or false
	 */
	public static boolean changeEndDate(Entity entity) {
		initializeDriver();
		int affectedRows = 0;

		try {
			String query = "UPDATE ENTITY SET ENDDATE = 'Currently watching' WHERE ID = ?";			
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, getEntityID(entity.name));
			affectedRows = ps.executeUpdate();
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}

		return affectedRows > 0 ? true : false;
	}
	
	/**
	 * Changes the current season value.
	 * 
	 * @param entity
	 * @param option
	 * @return true or false
	 */
	public static boolean changeSeason(Entity entity, String option) {
		initializeDriver();
		int affectedRows = 0;

		try {
			String query = option.equals(Constants.MINUS)
					? "UPDATE ENTITY SET SEASON = SEASON - 1 WHERE ID = ?"
					: "UPDATE ENTITY SET SEASON = SEASON + 1 WHERE ID = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, getEntityID(entity.name));
			affectedRows = ps.executeUpdate();
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}

		return affectedRows > 0 ? true : false;
	}

	/**
	 * Changes the current episode value.
	 * 
	 * @param entity
	 * @param option
	 * @return true or false
	 */
	public static boolean changeCurrentEpisode(Entity entity, String option) {
		initializeDriver();
		int affectedRows = 0;

		try {
			String query = option.equals(Constants.MINUS)
					? "UPDATE ENTITY SET CURRENTEPISODE = CURRENTEPISODE - 1 WHERE ID = ?"
					: "UPDATE ENTITY SET CURRENTEPISODE = CURRENTEPISODE + 1 WHERE ID = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, getEntityID(entity.name));
			affectedRows = ps.executeUpdate();
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}

		return affectedRows > 0 ? true : false;
	}

	/**
	 * Changes the season value.
	 * 
	 * @param entity
	 * @param season
	 * @return true or false
	 */
	public static boolean changeSeason(Entity entity, int season) {
		initializeDriver();
		int affectedRows = 0;

		try {
			String query = "UPDATE ENTITY SET SEASON = ? WHERE ID = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, season);
			ps.setInt(2, getEntityID(entity.name));
			affectedRows = ps.executeUpdate();
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}

		return affectedRows > 0 ? true : false;
	}
		
	/**
	 * Changes the current episode value.
	 * 
	 * @param entity
	 * @param episode
	 * @return true or false
	 */
	public static boolean changeCurrentEpisode(Entity entity, int episode) {
		initializeDriver();
		int affectedRows = 0;

		try {
			String query = "UPDATE ENTITY SET CURRENTEPISODE = ? WHERE ID = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, episode);
			ps.setInt(2, getEntityID(entity.name));
			affectedRows = ps.executeUpdate();
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}

		return affectedRows > 0 ? true : false;
	}

	/**
	 * Checks if an entity already exists.
	 * 
	 * @param entity
	 * @return true or false
	 */
	public static boolean entityExists(Entity entity) {
		initializeDriver();
		boolean exists = false;

		try {
			String query = "SELECT * FROM ENTITY WHERE NAME LIKE ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, entity.name);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				exists = true;
			}
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}

		return exists;
	}

	/**
	 * Checks if a category already exists.
	 * 
	 * @param category
	 * @return true or false
	 */
	public static boolean categoryExists(String category) {
		initializeDriver();
		boolean exists = false;

		try {
			String query = "SELECT * FROM CATEGORY WHERE NAME LIKE ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, category);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				exists = true;
			}
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}

		return exists;
	}

	/**
	 * Marks an entity as finished.
	 * 
	 * @param entity
	 * @param category
	 * @return true or false
	 */
	public static boolean markAsFinished(controller.Entity entity, String category) {
		initializeDriver();
		int affectedRows = 0;

		try {
			String query = "UPDATE ENTITY SET ENDDATE = ? WHERE ID = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, String.valueOf(getCurrentDate()));
			ps.setInt(2, getEntityID(entity.name));

			affectedRows = ps.executeUpdate();
		} catch (SQLException e) {
			new DialogException(e);
		} finally {
			closeDatabase(connection);
		}

		return affectedRows > 0 ? true : false;
	}
}
