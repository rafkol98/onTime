package com.example.ontime.MeetingsClasses;

import com.example.ontime.MainClasses.Trip;

public class Meeting {

    String uIdSender,emailSender , destination;

    Long timestamp;

    String meetingId;

    boolean meetingFlag;

    public Meeting(String destination, Long timestamp, String uIdSender, boolean meetingFlag) {
        this.destination = destination;
        this.timestamp = timestamp;
        this.uIdSender = uIdSender;
        this.meetingFlag = meetingFlag;
    }


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

    public boolean isMeetingFlag() {
        return meetingFlag;
    }

    public String getEmailSender() {
        return emailSender;
    }
}
