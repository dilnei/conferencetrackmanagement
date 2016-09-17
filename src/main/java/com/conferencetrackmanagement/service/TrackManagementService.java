package com.conferencetrackmanagement.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.conferencetrackmanagement.model.Text;
import com.conferencetrackmanagement.model.enums.Time;

/**
 * EJB Service for track management.
 * 
 * @author Dilnei Cunha
 */
@Stateless
public class TrackManagementService {

	@Inject
	private Logger log;

	private List<String> events;
	private List<Text> outpuEvents;
	private List<Integer> times;
	private final int TIME_BOUNDARY = 420;
	private int initial = -1;
	private int end = -1;
	private int time;

	public List<Text> register(Text text) throws Exception {
		log.info("Inputs" + text.getInput());
		events = Arrays.asList(text.getInput().split("[\\r\\n]+"));
		gettingTimeFromEvent();
		normalizeTracks();
		return outpuEvents;
	}

	private void gettingTimeFromEvent() {
		AtomicInteger idx = new AtomicInteger(0);
		times = new ArrayList<>();
		events.forEach(item -> {
			item = item.trim();

			// time of event.
			String currentTime = item.substring(item.lastIndexOf(' ') + 1).toLowerCase();

			// verify lightning/min.
			if (currentTime.endsWith(Time.LIGHTNING.toString())) {
				gettingTime(idx.getAndIncrement(), currentTime, Time.LIGHTNING);
			} else if (currentTime.endsWith(Time.MIN.toString())) {
				gettingTime(idx.getAndIncrement(), currentTime, Time.MIN);
			}
		});
	}

	/**
	 * Compute tracks.
	 *
	 * The conference has multiple tracks each of which has a morning and
	 * afternoon session.
	 */
	private void normalizeTracks() {
		int requiredTracks = times.stream().mapToInt(i -> i.intValue()).sum() / TIME_BOUNDARY + 1;
		outpuEvents = Stream.generate(Text::new).limit(0).collect(Collectors.toList());

		sortEventByTime();

		for (int i = 1; i <= requiredTracks; i++) {

			// morning time
			boolean found = exactSubArraySum(180);

			if (found) {
				// starts at 9 am [9 * 60 is in minutes]
				time = 9 * 60;

				outpuEvents.add(new Text("Track " + i));
				for (int j = initial; j <= end; j++) {
					outpuEvents.add(new Text(antePostMeridiem(time, "AM") + " " + events.get(j)));
					time += times.get(j);
				}

				removeElements();
				outpuEvents.add(new Text("12:00PM Lunch"));

				boolean relativeFound = relativeSubArraySum(times, 180, 240);

				if (relativeFound) {
					// starts at 1 pm [1 * 60 is in minutes]
					time = 60;
					outpuEvents.add(new Text(""));
					for (int j = initial; j <= end; j++) {
						outpuEvents.add(new Text(antePostMeridiem(time, "PM") + " " + events.get(j)));
						time += times.get(j);
					}

					removeElements();
					outpuEvents.add(new Text(antePostMeridiem(time, "PM") + " Networking Event"));
					outpuEvents.add(new Text(""));
				}
			} else {
				outpuEvents.add(new Text("Not Implemented"));
			}
		}
	}

	/**
	*
	*/
	private void removeElements() {
		String[] tempEvents = new String[events.size() - (end - initial) - 1];
		Integer[] tempTimes = new Integer[tempEvents.length];

		int index = 0;
		for (int j = 0; j < initial; j++) {
			tempEvents[index] = events.get(j);
			tempTimes[index] = times.get(j);
			index++;
		}

		for (int j = end + 1; j < events.size(); j++) {
			tempEvents[index] = events.get(j);
			tempTimes[index] = times.get(j);
			index++;
		}

		times = Arrays.asList(tempTimes);
		events = Arrays.asList(tempEvents);
	}

	/**
	 *
	 * @param idx
	 * @param currentTime
	 * @param time
	 * @param scale
	 */
	private void gettingTime(int idx, String currentTime, Time time) {
		String timeStr = currentTime.substring(0, currentTime.indexOf(time.toString()));
		if (timeStr.isEmpty()) {
			times.add(5);
		} else {
			try {
				times.add(Integer.parseInt(timeStr));
			} catch (Exception e) {
				log.log(Level.SEVERE, "Invalid Time Entry in line " + (idx + 1) + "\n", e);
			}
		}
	}

	/**
	*
	*/
	private void sortEventByTime() {
		for (int i = 1; i < times.size(); i++) {
			for (int j = 0; j < times.size() - i; j++) {
				if (times.get(j) > times.get(j + 1)) {
					int tempTime = times.get(j);
					times.set(j, times.get(j + 1));
					times.set(j + 1, tempTime);

					String tempEvent = events.get(j);
					events.set(j, events.get(j + 1));
					events.set(j + 1, tempEvent);
				}
			}
		}
	}

	/**
	 *
	 *
	 * @param array
	 * @param morningTime
	 * @return boolean
	 */
	private boolean exactSubArraySum(int morningTime) {
		if (!(times.size() >= 1)) {
			return false;
		}
		int currentSum = times.get(0), start = 0;

		for (int i = 1; i <= times.size(); i++) {
			while (currentSum > morningTime && start < i - 1) {
				currentSum -= times.get(start);
				start++;
			}

			if (currentSum == morningTime) {
				initial = start;
				end = i - 1;
				return true;
			}

			if (i < times.size()) {
				currentSum += times.get(i);
			}
		}
		// No sub array found.
		return false;
	}

	/**
	 *
	 *
	 * @param array
	 * @param minimumSum
	 * @param maximumSum
	 * @return boolean
	 */
	private boolean relativeSubArraySum(List<Integer> arrays, int minimumSum, int maximumSum) {
		if (!(arrays.size() >= 1)) {
			return false;
		}
		int currentSum = arrays.get(0), start = 0;

		for (int i = 1; i <= arrays.size(); i++) {
			while (currentSum > maximumSum && start < i - 1) {
				currentSum -= arrays.get(start);
				start++;
			}

			if (currentSum >= minimumSum && currentSum <= maximumSum) {
				initial = start;
				end = i - 1;
				return true;
			}

			if (i < arrays.size()) {
				currentSum += arrays.get(i);
			}
		}
		// No sub array found.
		return false;
	}

	/**
	 *
	 *
	 * @param time
	 * @param mode
	 * @return String
	 */
	private String antePostMeridiem(int time, String mode) {
		String hour = "";
		String minute = "";
		int hours = time / 60;
		int minutes = time % 60;

		if (hours < 10) {
			hour = "0";
		}
		if (minutes < 10) {
			minute = "0";
		}
		return hour + hours + ":" + minute + minutes + mode;
	}
}
