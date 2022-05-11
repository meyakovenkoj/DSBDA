package com.yakovenko.lab3.clients.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.util.List;

/**
 * Data class for aggregated responses
 */
@Generated("jsonschema2pojo")
public class AggregateResponseDto {
    /**
     * count of posts
     */
    @SerializedName("count")
    @Expose
    private Long count;

    /**
     * posts itself
     */
    @SerializedName("eventUrls")
    @Expose
    private List<String> eventUrls = null;


    /**
     * events url getter
     *
     * @return list of urls
     */
    public List<String> getEventUrls() {
        return eventUrls;
    }

    /**
     * setter
     *
     * @param eventUrls to set
     */
    public void setEventUrls(List<String> eventUrls) {
        this.eventUrls = eventUrls;
    }

    /**
     * setter
     *
     * @param count to set
     */
    public void setCount(Long count) {
        this.count = count;
    }

    /**
     * get count
     *
     * @return count of events
     */
    public Long getCount() {
        return count;
    }

}
