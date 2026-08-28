package ru.practicum.aggregation.model.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @apiNote
 * {@link Float} lat<br/>
 * {@link Float} lon
 */
@Data
@Builder
public class Location implements Serializable {
	Float lat;
	Float lon;
}
