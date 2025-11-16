package cn.edu.zju.cs.jobmate.dto.activity;

import cn.edu.zju.cs.jobmate.dto.company.CompanyResponse;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Activity information response DTO
 */
public class ActivityInfoResponse {
    
    private Integer id;
    private CompanyResponse company;
    private String title;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
    
    private String link;
    private String city;
    private String location;
    private String extra;

    public ActivityInfoResponse() {
    }

    public ActivityInfoResponse(Integer id, CompanyResponse company, String title, 
                               LocalDateTime time, String link, String city, 
                               String location, String extra) {
        this.id = id;
        this.company = company;
        this.title = title;
        this.time = time;
        this.link = link;
        this.city = city;
        this.location = location;
        this.extra = extra;
    }

    /**
     * Convert from ActivityInfo entity to ActivityInfoResponse
     */
    public static ActivityInfoResponse from(ActivityInfo activityInfo) {
        if (activityInfo == null) {
            return null;
        }
        return new ActivityInfoResponse(
            activityInfo.getId(),
            CompanyResponse.from(activityInfo.getCompany()),
            activityInfo.getTitle(),
            activityInfo.getTime(),
            activityInfo.getLink(),
            activityInfo.getCity(),
            activityInfo.getLocation(),
            activityInfo.getExtra()
        );
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CompanyResponse getCompany() {
        return company;
    }

    public void setCompany(CompanyResponse company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}

