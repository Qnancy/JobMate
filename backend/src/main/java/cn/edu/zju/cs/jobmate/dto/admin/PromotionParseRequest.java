package cn.edu.zju.cs.jobmate.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request body for admin AI promotion parsing.
 */
@Data
public class PromotionParseRequest {

    @NotNull
    private PromotionParseTarget target;

    @NotBlank
    @Size(max = 20000, message = "文本过长")
    private String text;
}
