package controller;

/**
 * 
 * @author marangocimihai
 *
 */
public class Entity {
	public int ID;
	public String name;
	public String status;
	public double rating;
	public int season;
	public int currentEpisode;
	public String property;
	public String value;
	public String startDate;
	public String endDate;
	public int categoryID;

	public Entity() {};

	public Entity(int ID, String name, String status, double rating, int season, int currentEpisode, String startDate, String endDate) {
		this.ID = ID;
		this.name = name;
		this.status = status;
		this.rating = rating;
		this.season = season;
		this.currentEpisode = currentEpisode;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Entity(String name, String status, double rating, int season, int currentEpisode, String startDate, String endDate, int categoryID) {
		this.name = name;
		this.status = status;
		this.rating = rating;
		this.season = season;
		this.currentEpisode = currentEpisode;
		this.startDate = startDate;
		this.endDate = endDate;
		this.categoryID = categoryID;
	}

	public Entity(String property, String value) {
		this.property = new String(property);
		this.value = new String(value);
	}

	public Entity(String name) {
		this.name = name;
	}
}
