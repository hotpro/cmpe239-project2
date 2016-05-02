package CMPE239.CMPE239_GROUP_1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Example {

	@SerializedName("business_id")
	@Expose
	private String businessId;
	@SerializedName("full_address")
	@Expose
	private String fullAddress;
	@SerializedName("open")
	@Expose
	private Boolean open;
	@SerializedName("categories")
	@Expose
	private List<String> categories = new ArrayList<String>();
	@SerializedName("city")
	@Expose
	private String city;
	@SerializedName("review_count")
	@Expose
	private Integer reviewCount;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("neighborhoods")
	@Expose
	private List<Object> neighborhoods = new ArrayList<Object>();
	@SerializedName("longitude")
	@Expose
	private Double longitude;
	@SerializedName("state")
	@Expose
	private String state;
	@SerializedName("stars")
	@Expose
	private Double stars;
	@SerializedName("latitude")
	@Expose
	private Double latitude;
	@SerializedName("type")
	@Expose
	private String type;

	/**
	 * 
	 * @return The businessId
	 */
	public String getBusinessId() {
		return businessId;
	}

	/**
	 * 
	 * @param businessId
	 *            The business_id
	 */
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	/**
	 * 
	 * @return The fullAddress
	 */
	public String getFullAddress() {
		return fullAddress;
	}

	/**
	 * 
	 * @param fullAddress
	 *            The full_address
	 */
	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	/**
	 * 
	 * @return The open
	 */
	public Boolean getOpen() {
		return open;
	}

	/**
	 * 
	 * @param open
	 *            The open
	 */
	public void setOpen(Boolean open) {
		this.open = open;
	}

	/**
	 * 
	 * @return The categories
	 */
	public List<String> getCategories() {
		return categories;
	}

	/**
	 * 
	 * @param categories
	 *            The categories
	 */
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	/**
	 * 
	 * @return The city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * 
	 * @param city
	 *            The city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * 
	 * @return The reviewCount
	 */
	public Integer getReviewCount() {
		return reviewCount;
	}

	/**
	 * 
	 * @param reviewCount
	 *            The review_count
	 */
	public void setReviewCount(Integer reviewCount) {
		this.reviewCount = reviewCount;
	}

	/**
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 *            The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return The neighborhoods
	 */
	public List<Object> getNeighborhoods() {
		return neighborhoods;
	}

	/**
	 * 
	 * @param neighborhoods
	 *            The neighborhoods
	 */
	public void setNeighborhoods(List<Object> neighborhoods) {
		this.neighborhoods = neighborhoods;
	}

	/**
	 * 
	 * @return The longitude
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * 
	 * @param longitude
	 *            The longitude
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	/**
	 * 
	 * @return The state
	 */
	public String getState() {
		return state;
	}

	/**
	 * 
	 * @param state
	 *            The state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * 
	 * @return The stars
	 */
	public Double getStars() {
		return stars;
	}

	/**
	 * 
	 * @param stars
	 *            The stars
	 */
	public void setStars(Double stars) {
		this.stars = stars;
	}

	/**
	 * 
	 * @return The latitude
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * 
	 * @param latitude
	 *            The latitude
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * 
	 * @return The type
	 */
	public String getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 *            The type
	 */
	public void setType(String type) {
		this.type = type;
	}

//	public String toString() {
//		return "\"" + String.join("\",\"", this.categories).replace(" ", "_") + "\"" + ",\"" + stars + "\"";
//	}
//	
//	public String toCVSLine(int length) {
//		if (this.categories.size() < length) {
//			int padding = length - this.categories.size();
//			for (int i = 0; i < padding; i++) {
//				this.categories.add("");
//			}
//		}
//		return toString();
//	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.categories.size(); i++) {
			if (i == this.categories.size() - 1) {
				sb.append("\"" + this.categories.get(i) + "\",").append(stars);
			} else {
				sb.append("\"" + this.categories.get(i) + "\",").append(stars + "\n");
			}
			
		}
		return sb.toString();
	}
	
	public String toCVSLine(int length, Map<String, Integer> map, int begin, int end) {
		Iterator<String> it = this.categories.iterator();
		while (it.hasNext()) {
			String s = it.next();
			if (map.containsKey(s) && map.get(s) < begin) {
				it.remove();
			} else if (map.containsKey(s) && map.get(s) > end) {
				it.remove();
			} else if (s.equals("Restaurants")||s.equals("Shopping")) {
				it.remove();
			}
		}
		return toString();
	}
	
	public String toDoubleCVSLine(int length, Map<String, Integer> map, int begin, int end) {
		Iterator<String> it = this.categories.iterator();
		while (it.hasNext()) {
			String s = it.next();
			if (map.containsKey(s) && map.get(s) < begin) {
				it.remove();
			} else if (map.containsKey(s) && map.get(s) > end) {
				it.remove();
			} 
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.categories.size(); i++) {
			if (i == this.categories.size() - 1) {
				sb.append(map.get(this.categories.get(i)) + ",").append(stars);
			} else {
				sb.append(map.get(this.categories.get(i)) + ",").append(stars + "\n");
			}
			
		}
		return sb.toString();
	}
}
