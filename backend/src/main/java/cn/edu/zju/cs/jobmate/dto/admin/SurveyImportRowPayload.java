package cn.edu.zju.cs.jobmate.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Confirmed row payload for import (built during preview).
 */
@Data
@Builder
public class SurveyImportRowPayload {

    @NotNull
    private Integer rowNumber;

    private String rowLabel;

    @NotBlank
    private String infoType;

    @NotBlank
    private String companyName;

    /** Raw CSV value, e.g. 民营企业 */
    private String companyType;

    /** 企业常用简称（当前不入库，供管理员核对） */
    private String companyShortName;

    /** 公司官网（当前不入库，供管理员核对） */
    private String companyWebsite;

    /** 公司描述（问卷字段） */
    private String companyTagline;

    /** 公司简介（问卷字段） */
    private String companyIntro;

    /** 合并后的企业简介，导入时若 tagline/intro 为空则使用此项 */
    private String companyDescription;

    private String position;
    private String recruitType;
    private String education;
    private String location;
    private String link;
    private String deadline;
    private String jobExtra;

    /**
     * Required when preview detected conflict: UPDATE | KEEP_EXISTING | SKIP.
     */
    private String conflictAction;
}
