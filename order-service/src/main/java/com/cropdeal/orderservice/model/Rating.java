package com.cropdeal.orderservice.model;

public class Rating {
	private String dealerId;
	private int stars;
	private String comments;

	public Rating() {
	}

	public Rating(String dealerId, int stars, String comments) {
		this.dealerId = dealerId;
		this.stars = stars;
		this.comments = comments;
	}

	public String getDealerId() {
		return dealerId;
	}

	public void setDealerId(String dealerId) {
		this.dealerId = dealerId;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
}
