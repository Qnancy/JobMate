package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.repositories.ActivityInfoRepository;
import cn.edu.zju.cs.jobmate.repositories.CompanyRepository;
import cn.edu.zju.cs.jobmate.repositories.JobInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("模糊查询功能测试")
class QueryServiceTest {

    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private ActivityInfoService activityInfoService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobInfoRepository jobInfoRepository;

    @Autowired
    private ActivityInfoRepository activityInfoRepository;

    private Company company1; // 阿里巴巴
    private Company company2; // 腾讯科技
    private Company company3; // 字节跳动

    @BeforeEach
    void setUp() {
        // 清理数据
        jobInfoRepository.deleteAll();
        activityInfoRepository.deleteAll();
        companyRepository.deleteAll();

        // 创建测试公司
        company1 = new Company("阿里巴巴集团", CompanyType.PRIVATE);
        company1 = companyRepository.save(company1);

        company2 = new Company("腾讯科技有限公司", CompanyType.PRIVATE);
        company2 = companyRepository.save(company2);

        company3 = new Company("字节跳动科技有限公司", CompanyType.PRIVATE);
        company3 = companyRepository.save(company3);

        // 创建JobInfo测试数据
        // 公司名称包含"阿里"的职位
        JobInfo job1 = new JobInfo(company1, RecruitType.INTERN, "Java开发工程师", "https://example.com/job1");
        job1.setLocation("杭州市西湖区");
        job1.setCity("杭州");
        jobInfoRepository.save(job1);

        JobInfo job2 = new JobInfo(company1, RecruitType.CAMPUS, "前端开发工程师", "https://example.com/job2");
        job2.setLocation("北京市海淀区");
        job2.setCity("北京");
        jobInfoRepository.save(job2);

        // 职位名称包含"算法"的职位
        JobInfo job3 = new JobInfo(company2, RecruitType.EXPERIENCED, "算法工程师", "https://example.com/job3");
        job3.setLocation("深圳市南山区");
        job3.setCity("深圳");
        jobInfoRepository.save(job3);

        // 位置包含"上海"的职位
        JobInfo job4 = new JobInfo(company3, RecruitType.INTERN, "产品经理", "https://example.com/job4");
        job4.setLocation("上海市浦东新区");
        job4.setCity("上海");
        jobInfoRepository.save(job4);

        JobInfo job5 = new JobInfo(company2, RecruitType.CAMPUS, "后端开发工程师", "https://example.com/job5");
        job5.setLocation("上海市黄浦区");
        job5.setCity("上海");
        jobInfoRepository.save(job5);

        // 创建ActivityInfo测试数据
        ActivityInfo activity1 = new ActivityInfo(company1, "阿里巴巴2024校园招聘宣讲会", LocalDateTime.now().plusDays(7));
        activity1.setLocation("杭州市西湖区");
        activity1.setCity("杭州");
        activityInfoRepository.save(activity1);

        ActivityInfo activity2 = new ActivityInfo(company2, "腾讯技术分享会", LocalDateTime.now().plusDays(10));
        activity2.setLocation("深圳市南山区");
        activity2.setCity("深圳");
        activityInfoRepository.save(activity2);

        ActivityInfo activity3 = new ActivityInfo(company3, "字节跳动产品发布会", LocalDateTime.now().plusDays(15));
        activity3.setLocation("北京市朝阳区");
        activity3.setCity("北京");
        activityInfoRepository.save(activity3);
    }

    @Test
    @DisplayName("测试JobInfo - 按公司名称模糊查询")
    void testJobInfoQueryByCompanyName() {
        System.out.println("\n========== 测试1: JobInfo按公司名称模糊查询 ==========");
        System.out.println("查询关键词: '阿里'");
        
        Page<JobInfo> result = jobInfoService.query("阿里", null, 0, 10);
        
        System.out.println("查询结果数量: " + result.getTotalElements());
        System.out.println("查询结果:");
        result.getContent().forEach(job -> {
            System.out.println("  - ID: " + job.getId() + 
                             ", 公司: " + job.getCompany().getName() + 
                             ", 职位: " + job.getPosition() + 
                             ", 位置: " + job.getLocation() +
                             ", 类型: " + job.getRecruitType());
        });
        
        assertEquals(2, result.getTotalElements(), "应该找到2个包含'阿里'的职位");
        assertTrue(result.getContent().stream()
                .allMatch(job -> job.getCompany().getName().contains("阿里")), 
                "所有结果的公司名称应该包含'阿里'");
    }

    @Test
    @DisplayName("测试JobInfo - 按职位名称模糊查询")
    void testJobInfoQueryByPosition() {
        System.out.println("\n========== 测试2: JobInfo按职位名称模糊查询 ==========");
        System.out.println("查询关键词: '算法'");
        
        Page<JobInfo> result = jobInfoService.query("算法", null, 0, 10);
        
        System.out.println("查询结果数量: " + result.getTotalElements());
        System.out.println("查询结果:");
        result.getContent().forEach(job -> {
            System.out.println("  - ID: " + job.getId() + 
                             ", 公司: " + job.getCompany().getName() + 
                             ", 职位: " + job.getPosition() + 
                             ", 位置: " + job.getLocation());
        });
        
        assertEquals(1, result.getTotalElements(), "应该找到1个包含'算法'的职位");
        assertTrue(result.getContent().get(0).getPosition().contains("算法"), 
                "职位名称应该包含'算法'");
    }

    @Test
    @DisplayName("测试JobInfo - 按位置模糊查询")
    void testJobInfoQueryByLocation() {
        System.out.println("\n========== 测试3: JobInfo按位置模糊查询 ==========");
        System.out.println("查询关键词: '上海'");
        
        Page<JobInfo> result = jobInfoService.query("上海", null, 0, 10);
        
        System.out.println("查询结果数量: " + result.getTotalElements());
        System.out.println("查询结果:");
        result.getContent().forEach(job -> {
            System.out.println("  - ID: " + job.getId() + 
                             ", 公司: " + job.getCompany().getName() + 
                             ", 职位: " + job.getPosition() + 
                             ", 位置: " + job.getLocation());
        });
        
        assertEquals(2, result.getTotalElements(), "应该找到2个位置包含'上海'的职位");
        assertTrue(result.getContent().stream()
                .allMatch(job -> job.getLocation().contains("上海")), 
                "所有结果的位置应该包含'上海'");
    }

    @Test
    @DisplayName("测试JobInfo - 组合查询：关键词+招聘类型")
    void testJobInfoQueryWithRecruitType() {
        System.out.println("\n========== 测试4: JobInfo组合查询（关键词+招聘类型） ==========");
        System.out.println("查询关键词: '开发', 招聘类型: INTERN");
        
        Page<JobInfo> result = jobInfoService.query("开发", RecruitType.INTERN, 0, 10);
        
        System.out.println("查询结果数量: " + result.getTotalElements());
        System.out.println("查询结果:");
        result.getContent().forEach(job -> {
            System.out.println("  - ID: " + job.getId() + 
                             ", 公司: " + job.getCompany().getName() + 
                             ", 职位: " + job.getPosition() + 
                             ", 类型: " + job.getRecruitType());
        });
        
        assertEquals(1, result.getTotalElements(), "应该找到1个符合条件的职位");
        assertEquals(RecruitType.INTERN, result.getContent().get(0).getRecruitType());
        assertTrue(result.getContent().get(0).getPosition().contains("开发"));
    }

    @Test
    @DisplayName("测试JobInfo - 不区分大小写查询")
    void testJobInfoQueryCaseInsensitive() {
        System.out.println("\n========== 测试5: JobInfo不区分大小写查询 ==========");
        System.out.println("查询关键词: 'JAVA' (大写)");
        
        Page<JobInfo> result = jobInfoService.query("JAVA", null, 0, 10);
        
        System.out.println("查询结果数量: " + result.getTotalElements());
        System.out.println("查询结果:");
        result.getContent().forEach(job -> {
            System.out.println("  - ID: " + job.getId() + 
                             ", 公司: " + job.getCompany().getName() + 
                             ", 职位: " + job.getPosition());
        });
        
        assertEquals(1, result.getTotalElements(), "应该找到1个包含'Java'的职位（不区分大小写）");
    }

    @Test
    @DisplayName("测试ActivityInfo - 按标题模糊查询")
    void testActivityInfoQueryByTitle() {
        System.out.println("\n========== 测试6: ActivityInfo按标题模糊查询 ==========");
        System.out.println("查询关键词: '招聘'");
        
        Page<ActivityInfo> result = activityInfoService.query("招聘", 0, 10);
        
        System.out.println("查询结果数量: " + result.getTotalElements());
        System.out.println("查询结果:");
        result.getContent().forEach(activity -> {
            System.out.println("  - ID: " + activity.getId() + 
                             ", 公司: " + activity.getCompany().getName() + 
                             ", 标题: " + activity.getTitle() + 
                             ", 时间: " + activity.getTime());
        });
        
        assertEquals(1, result.getTotalElements(), "应该找到1个标题包含'招聘'的活动");
        assertTrue(result.getContent().get(0).getTitle().contains("招聘"));
    }

    @Test
    @DisplayName("测试ActivityInfo - 按公司名称模糊查询")
    void testActivityInfoQueryByCompanyName() {
        System.out.println("\n========== 测试7: ActivityInfo按公司名称模糊查询 ==========");
        System.out.println("查询关键词: '腾讯'");
        
        Page<ActivityInfo> result = activityInfoService.query("腾讯", 0, 10);
        
        System.out.println("查询结果数量: " + result.getTotalElements());
        System.out.println("查询结果:");
        result.getContent().forEach(activity -> {
            System.out.println("  - ID: " + activity.getId() + 
                             ", 公司: " + activity.getCompany().getName() + 
                             ", 标题: " + activity.getTitle());
        });
        
        assertEquals(1, result.getTotalElements(), "应该找到1个公司名称包含'腾讯'的活动");
        assertTrue(result.getContent().get(0).getCompany().getName().contains("腾讯"));
    }

    @Test
    @DisplayName("测试ActivityInfo - 组合查询（标题和公司名称）")
    void testActivityInfoQueryByTitleOrCompany() {
        System.out.println("\n========== 测试8: ActivityInfo组合查询（标题或公司名称） ==========");
        System.out.println("查询关键词: '技术'");
        
        Page<ActivityInfo> result = activityInfoService.query("技术", 0, 10);
        
        System.out.println("查询结果数量: " + result.getTotalElements());
        System.out.println("查询结果:");
        result.getContent().forEach(activity -> {
            System.out.println("  - ID: " + activity.getId() + 
                             ", 公司: " + activity.getCompany().getName() + 
                             ", 标题: " + activity.getTitle());
        });
        
        assertTrue(result.getTotalElements() >= 1, "应该找到至少1个包含'技术'的活动（可能在标题或公司名称中）");
    }

    @Test
    @DisplayName("测试分页功能")
    void testPagination() {
        System.out.println("\n========== 测试9: 分页功能测试 ==========");
        System.out.println("查询所有职位，每页2条");
        
        Page<JobInfo> page1 = jobInfoService.query(null, null, 0, 2);
        System.out.println("第1页 (page=0, size=2):");
        System.out.println("  总数: " + page1.getTotalElements());
        System.out.println("  总页数: " + page1.getTotalPages());
        System.out.println("  当前页内容数: " + page1.getContent().size());
        page1.getContent().forEach(job -> {
            System.out.println("    - " + job.getCompany().getName() + " - " + job.getPosition());
        });
        
        Page<JobInfo> page2 = jobInfoService.query(null, null, 1, 2);
        System.out.println("\n第2页 (page=1, size=2):");
        System.out.println("  当前页内容数: " + page2.getContent().size());
        page2.getContent().forEach(job -> {
            System.out.println("    - " + job.getCompany().getName() + " - " + job.getPosition());
        });
        
        assertEquals(2, page1.getContent().size(), "第1页应该有2条记录");
        assertTrue(page2.getContent().size() > 0, "第2页应该有记录");
    }

    @Test
    @DisplayName("测试空查询（无过滤条件）")
    void testEmptyQuery() {
        System.out.println("\n========== 测试10: 空查询（无过滤条件） ==========");
        
        Page<JobInfo> result = jobInfoService.query(null, null, 0, 10);
        
        System.out.println("查询结果数量: " + result.getTotalElements());
        System.out.println("应该返回所有职位");
        
        assertEquals(5, result.getTotalElements(), "应该返回所有5个职位");
    }

    @Test
    @DisplayName("测试JobInfo - 空格分词查询（跨字段匹配）")
    void testJobInfoQueryWithSpaceSeparatedTokens() {
        System.out.println("\n========== 测试11: JobInfo空格分词查询（跨字段匹配） ==========");
        System.out.println("查询关键词: '腾讯 开发'");
        System.out.println("期望: 公司名称包含'腾讯'且职位包含'开发'的记录");
        
        Page<JobInfo> result = jobInfoService.query("腾讯 开发", null, 0, 10);
        
        System.out.println("查询结果数量: " + result.getTotalElements());
        System.out.println("查询结果:");
        result.getContent().forEach(job -> {
            System.out.println("  - ID: " + job.getId() + 
                             ", 公司: " + job.getCompany().getName() + 
                             ", 职位: " + job.getPosition() + 
                             ", 位置: " + job.getLocation());
            System.out.println("    -> 公司包含'腾讯': " + job.getCompany().getName().contains("腾讯"));
            System.out.println("    -> 职位包含'开发': " + job.getPosition().contains("开发"));
        });
        
        assertTrue(result.getTotalElements() > 0, "应该找到符合条件的职位");
        // 验证每个结果都包含"腾讯"和"开发"（在不同字段中）
        result.getContent().forEach(job -> {
            boolean hasTencent = job.getCompany().getName().contains("腾讯") || 
                                job.getPosition().contains("腾讯") || 
                                (job.getLocation() != null && job.getLocation().contains("腾讯"));
            boolean hasDev = job.getCompany().getName().contains("开发") || 
                            job.getPosition().contains("开发") || 
                            (job.getLocation() != null && job.getLocation().contains("开发"));
            assertTrue(hasTencent && hasDev, "每个结果都应该同时包含'腾讯'和'开发'");
        });
    }

    @Test
    @DisplayName("测试JobInfo - 空格分词查询（三个词跨字段）")
    void testJobInfoQueryWithThreeTokens() {
        System.out.println("\n========== 测试12: JobInfo空格分词查询（三个词跨字段） ==========");
        System.out.println("查询关键词: '腾讯 算法 深圳'");
        System.out.println("期望: 公司名称包含'腾讯'、职位包含'算法'、位置包含'深圳'的记录");
        
        Page<JobInfo> result = jobInfoService.query("腾讯 算法 深圳", null, 0, 10);
        
        System.out.println("查询结果数量: " + result.getTotalElements());
        System.out.println("查询结果:");
        result.getContent().forEach(job -> {
            System.out.println("  - ID: " + job.getId() + 
                             ", 公司: " + job.getCompany().getName() + 
                             ", 职位: " + job.getPosition() + 
                             ", 位置: " + job.getLocation());
        });
        
        // 应该找到腾讯的算法工程师，位置在深圳
        assertTrue(result.getTotalElements() > 0, "应该找到符合条件的职位");
    }

    @Test
    @DisplayName("测试ActivityInfo - 空格分词查询（跨字段匹配）")
    void testActivityInfoQueryWithSpaceSeparatedTokens() {
        System.out.println("\n========== 测试13: ActivityInfo空格分词查询（跨字段匹配） ==========");
        System.out.println("查询关键词: '腾讯 技术'");
        System.out.println("期望: 公司名称包含'腾讯'且标题包含'技术'的记录");
        
        Page<ActivityInfo> result = activityInfoService.query("腾讯 技术", 0, 10);
        
        System.out.println("查询结果数量: " + result.getTotalElements());
        System.out.println("查询结果:");
        result.getContent().forEach(activity -> {
            System.out.println("  - ID: " + activity.getId() + 
                             ", 公司: " + activity.getCompany().getName() + 
                             ", 标题: " + activity.getTitle());
            System.out.println("    -> 公司包含'腾讯': " + activity.getCompany().getName().contains("腾讯"));
            System.out.println("    -> 标题包含'技术': " + activity.getTitle().contains("技术"));
        });
        
        assertTrue(result.getTotalElements() > 0, "应该找到符合条件的活动");
        // 验证每个结果都包含"腾讯"和"技术"（在不同字段中）
        result.getContent().forEach(activity -> {
            boolean hasTencent = activity.getCompany().getName().contains("腾讯") || 
                                activity.getTitle().contains("腾讯");
            boolean hasTech = activity.getCompany().getName().contains("技术") || 
                             activity.getTitle().contains("技术");
            assertTrue(hasTencent && hasTech, "每个结果都应该同时包含'腾讯'和'技术'");
        });
    }

    @Test
    @DisplayName("测试空格分词 - 单个词（向后兼容）")
    void testSingleTokenQuery() {
        System.out.println("\n========== 测试14: 单个词查询（向后兼容） ==========");
        System.out.println("查询关键词: '开发'（单个词，无空格）");
        
        Page<JobInfo> result = jobInfoService.query("开发", null, 0, 10);
        
        System.out.println("查询结果数量: " + result.getTotalElements());
        System.out.println("查询结果:");
        result.getContent().forEach(job -> {
            System.out.println("  - ID: " + job.getId() + 
                             ", 公司: " + job.getCompany().getName() + 
                             ", 职位: " + job.getPosition());
        });
        
        assertTrue(result.getTotalElements() > 0, "应该找到包含'开发'的职位");
    }
}

