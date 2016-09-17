package com.conferencetrackmanagement.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.conferencetrackmanagement.model.Text;
import com.conferencetrackmanagement.service.TrackManagementService;

/**
 * The @Model stereotype is a convenience mechanism to make this a request-scoped bean that has an EL name.
 * 
 * @author Dilnei Cunha
 */
@Model
public class TrackManagementController {

	@Inject
	private FacesContext facesContext;

	@Inject
	private TrackManagementService trackManagementRegistration;

	@Produces
	@Named
	private Text text;

	@Named
	private List<Text> textOutInput;

	@PostConstruct
	public void initText() {
		text = new Text();
	}

	public void readInformation() throws Exception {
		try {
			textOutInput = trackManagementRegistration.register(text);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
			facesContext.addMessage(null, m);
			initText();
		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registration unsuccessful");
			facesContext.addMessage(null, m);
		}
	}

	private String getRootErrorMessage(Exception e) {
		// Default to general error message that registration failed.
		String errorMessage = "Registration failed. See server log for more information";
		if (e == null) {
			// This shouldn't happen, but return the default messages
			return errorMessage;
		}

		// Start with the exception and recurse to find the root cause
		Throwable t = e;
		while (t != null) {
			// Get the message from the Throwable class instance
			errorMessage = t.getLocalizedMessage();
			t = t.getCause();
		}
		// This is the root cause message
		return errorMessage;
	}

	public void setTextOutInput(List<Text> textOutInput) {
		this.textOutInput = textOutInput;
	}

	public List<Text> getTextOutInput() {
		return textOutInput;
	}
}
