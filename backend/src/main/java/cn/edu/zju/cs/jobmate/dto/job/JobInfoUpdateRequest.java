package cn.edu.zju.cs.jobmate.dto.job;

import cn.edu.zju.cs.jobmate.enums.RecruitType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Job information update request DTO
 * All fields are optional
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobInfoUpdateRequest {
    
    private Integer companyId;
    private RecruitType recruitType;
    
    @Size(max = 128, message = "Job position name length cannot exceed 128")
    private String position;
    
    private String link;
    private String city;
    private String location;
    private String extra;
}

