package com.yakovenko.lab3.clients.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.util.List;

@Generated("jsonschema2pojo")
public class AggregateResponseDto {
    @SerializedName("count")
    @Expose
    private Long count;

    @SerializedName("eventUrls")
    @Expose
    private List<String> eventUrls = null;


    public List<String> getEventUrls() {
        return eventUrls;
    }

    public void setEventUrls(List<String> eventUrls) {
        this.eventUrls = eventUrls;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getCount() {
        return count;
    }

}
