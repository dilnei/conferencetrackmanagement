package com.conferencetrackmanagement.test;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.conferencetrackmanagement.model.Text;
import com.conferencetrackmanagement.service.TrackManagementService;

@RunWith(Arquillian.class)
public class ConferenceTrackManagementTest {

	private List<Text> textreturn;

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive war = ShrinkWrap.create(WebArchive.class, "conferencetrackmanagement.war").addPackages(true, "com.conferencetrackmanagement.controller")
				.addPackages(true, "com.conferencetrackmanagement.model").addPackages(true, "com.conferencetrackmanagement.model.enums").addPackages(true, "com.conferencetrackmanagement.rest")
				.addPackages(true, "com.conferencetrackmanagement.service").addPackages(true, "com.conferencetrackmanagement.util").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		System.out.println(war.toString(true));
		return war;
	}

	@Inject
	TrackManagementService trackManagementService;

	@Inject
	Logger log;

	@Before
	public void setUp() {
		textreturn = Arrays.asList(new Text("Track 1"), new Text("09:00AM Lua for the Masses 30min"), new Text("Writing Fast Tests Against Enterprise Rails 60min"), new Text("09:30AM Woah 30min"),
				new Text("10:00AM Sit Down and Write 30min"), new Text("10:30AM Programming in the Boondocks of Seattle 30min"));
	}

	@Test
	public void testTrackManagementNotImplemented() throws Exception {
		Text text = new Text();
		text.setInput("﻿Writing Fast Tests Against Enterprise Rails 60min " 
				+ "Overdoing it in Python 45min " 
				+ "Lua for the Masses 30min " 
				+ "Ruby Errors from Mismatched Gem Versions 45min "
				+ "Common Ruby Errors 45min " 
				+ "Rails for Python Developers lightning " 
				+ "Communicating Over Distance 60min " 
				+ "Accounting-Driven Development 45min " 
				+ "Woah 30min "
				+ "Sit Down and Write 30min " 
				+ "Pair Programming vs Noise 45min " 
				+ "Rails Magic 60min " 
				+ "Ruby on Rails: Why We Should Move On 60min " 
				+ "Clojure Ate Scala (on my project) 45min "
				+ "Programming in the Boondocks of Seattle 30min " 
				+ "Ruby vs. Clojure for Back-End Development 30min " 
				+ "Ruby on Rails Legacy App Maintenance 60min "
				+ "A World Without HackerNews 30min " 
				+ "User Interface CSS in Rails Apps 30min");
		List<Text> returnText = trackManagementService.register(text);
		assertNotNull(text.getInput());
		for (Text t : returnText) {
			System.out.println(text.getInput() + "\nwas proccess by track management: " + t.getInput());
			Assert.assertEquals("Not Implemented", t.getInput());
		}
	}

	@Test
	public void testTrackManagementEquals() throws Exception {
		Text text = new Text();
		text.setInput("﻿Writing Fast Tests Against Enterprise Rails 60min \nOverdoing it in Python 45min \nLua for the Masses 30min \nRuby Errors from Mismatched Gem Versions 45min \nCommon Ruby Errors 45min \nRails for Python Developers lightning \nCommunicating Over Distance 60min \nAccounting-Driven Development 45min \nWoah 30min \nSit Down and Write 30min \nPair Programming vs Noise 45min \nRails Magic 60min Ruby on Rails: Why We Should Move On 60min \nClojure Ate Scala (on my project) 45min \nProgramming in the Boondocks of Seattle 30min \nRuby vs. Clojure for Back-End Development 30min \nRuby on Rails Legacy App Maintenance 60min \nA World Without HackerNews 30min \nUser Interface CSS in Rails Apps 30min");
		List<Text> returnText = trackManagementService.register(text);
		assertNotNull(text.getInput());
		Assert.assertEquals(textreturn.get(0).getInput(), returnText.get(0).getInput());
	}
}
