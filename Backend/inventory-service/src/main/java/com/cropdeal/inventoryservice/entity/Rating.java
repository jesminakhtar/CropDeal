package com.cropdeal.inventoryservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Rating {
	private String dealerId;
	private int stars;
	private String comments;
}
