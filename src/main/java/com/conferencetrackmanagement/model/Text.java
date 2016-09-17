package com.conferencetrackmanagement.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Text object.
 * 
 * @author Dilnei Cunha
 */
@SuppressWarnings("serial")
@XmlRootElement
public class Text implements Serializable {

	@NotNull
	@NotEmpty
	private String input;

	public Text(String input) {
		this.input = input;
	}

	public Text() {
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
}
