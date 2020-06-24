package com.example.ontime;

import com.example.ontime.MainClasses.Trip;

public class Meeting {

    String uIdSender;

    String destination;
    Long timestamp;

    String meetingId;

    public Meeting(String destination, Long timestamp, String uIdSender) {
        this.destination = destination;
        this.timestamp = timestamp;
        this.uIdSender = uIdSender;
    }

    public String getMeetingId() {
        String meetingId = "M" + getTimestamp();
        return meetingId;
    }

    public String getuIdSender() {
        return uIdSender;
    }

    public String getDestination() {
        return destination;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
