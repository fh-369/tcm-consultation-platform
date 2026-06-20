package com.tcm.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.platform.config.SecurityConfig;
import com.tcm.platform.dto.AIAnswerResponse;
import com.tcm.platform.dto.AIContentRecommendation;
import com.tcm.platform.dto.DashboardSummary;
import com.tcm.platform.dto.LoginResponse;
import com.tcm.platform.dto.PersonnelRecord;
import com.tcm.platform.entity.Account;
import com.tcm.platform.entity.Consultation;
import com.tcm.platform.entity.KnowledgeArticle;
import com.tcm.platform.entity.PatientAccount;
import com.tcm.platform.entity.Recipe;
import com.tcm.platform.entity.User;
import com.tcm.platform.mapper.AccountMapper;
import com.tcm.platform.mapper.PatientAccountMapper;
import com.tcm.platform.mapper.ConsultationMapper;
import com.tcm.platform.mapper.KnowledgeArticleMapper;
import com.tcm.platform.mapper.RecipeMapper;
import com.tcm.platform.mapper.UploadMapper;
import com.tcm.platform.mapper.UserMapper;
import com.tcm.platform.security.JwtUtil;
import com.tcm.platform.service.AIService;
import com.tcm.platform.service.AuthService;
import com.tcm.platform.service.ConsultationExportService;
import com.tcm.platform.service.ConsultationService;
import com.tcm.platform.service.ConsultationWorkspaceService;
import com.tcm.platform.service.DashboardService;
import com.tcm.platform.service.KnowledgeArticleService;
import com.tcm.platform.service.PersonnelService;
import com.tcm.platform.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        AuthController.class,
        PatientController.class,
        AdminController.class,
        RecipeAdminController.class,
        DashboardController.class,
        PersonnelController.class,
        AIController.class
})
@Import(SecurityConfig.class)
class ApiSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuthService authService;

    @MockBean
    private ConsultationService consultationService;

    @MockBean
    private ConsultationWorkspaceService consultationWorkspaceService;

    @MockBean
    private KnowledgeArticleService knowledgeArticleService;

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private ConsultationExportService consultationExportService;

    @MockBean
    private AIService aiService;

    @MockBean
    private PersonnelService personnelService;

    @MockBean
    private AccountMapper accountMapper;

    @MockBean
    private PatientAccountMapper patientAccountMapper;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private ConsultationMapper consultationMapper;

    @MockBean
    private KnowledgeArticleMapper knowledgeArticleMapper;

    @MockBean
    private RecipeMapper recipeMapper;

    @MockBean
    private UploadMapper uploadMapper;

    @Test
    void publicContentEndpointsAllowAnonymousAccess() throws Exception {
        KnowledgeArticle article = new KnowledgeArticle();
        article.setId(1L);
        article.setTitle("春季养生");
        Recipe recipe = new Recipe();
        recipe.setId(2L);
        recipe.setName("山药粥");
        Page<KnowledgeArticle> knowledgePage = new Page<>(1, 6);
        knowledgePage.setRecords(List.of(article));
        knowledgePage.setTotal(1);
        when(knowledgeArticleService.listPublishedArticles(1, 6, null, null)).thenReturn(knowledgePage);
        when(knowledgeArticleService.listPublishedCategories()).thenReturn(List.of("四季养护"));
        when(recipeService.listPublishedRecipes()).thenReturn(List.of(recipe));

        mockMvc.perform(get("/api/patient/knowledge"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].title").value("春季养生"));

        mockMvc.perform(get("/api/patient/knowledge/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").value("四季养护"));

        mockMvc.perform(get("/api/patient/recipe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("山药粥"));
    }

    @Test
    void authenticationEndpointsAllowAnonymousAccessAndValidateInput() throws Exception {
        LoginResponse response = new LoginResponse();
        response.setToken("patient-token");
        response.setRole("patient");
        when(authService.login(any())).thenReturn(response);
        when(authService.loginPatient(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"patient1","password":"patient123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("patient-token"))
                .andExpect(jsonPath("$.data.role").value("patient"));

        mockMvc.perform(post("/api/auth/login/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"patient1","password":"patient123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("patient-token"))
                .andExpect(jsonPath("$.data.role").value("patient"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @WithMockUser(username = "patient1", roles = "PATIENT")
    void patientCanSubmitConsultationViewOwnRecordsAndAskAI() throws Exception {
        PatientAccount patient = new PatientAccount();
        patient.setId(8L);
        patient.setUsername("patient1");
        Consultation consultation = new Consultation();
        consultation.setId(10L);
        consultation.setPatientAccountId(8L);
        when(patientAccountMapper.selectOne(any())).thenReturn(patient);
        when(consultationService.createConsultation(any())).thenReturn(consultation);
        when(consultationService.listConsultations(anyLong(), anyLong(), any(), any(), eq(8L), any()))
                .thenReturn(new Page<>());
        when(aiService.answer(eq("春季如何调养？"), any(), eq(8L), eq(null)))
                .thenReturn(new AIAnswerResponse("规律作息，适量运动。", true, "不能替代医生诊断。"));

        mockMvc.perform(post("/api/patient/consultation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patientName":"李女士",
                                  "age":35,
                                  "gender":"女",
                                  "phone":"13800000000",
                                  "symptoms":"容易疲倦",
                                  "duration":"约两周",
                                  "urgency":"普通"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.patientAccountId").value(8));

        mockMvc.perform(get("/api/patient/consultation/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/api/patient/ai/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"question":"春季如何调养？"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fallback").value(true))
                .andExpect(jsonPath("$.data.disclaimer").value("不能替代医生诊断。"));
    }

    @Test
    @WithMockUser(username = "patient1", roles = "PATIENT")
    void patientCanUseStreamingAIEndpointAcrossAsyncDispatch() throws Exception {
        PatientAccount patient = new PatientAccount();
        patient.setId(8L);
        patient.setUsername("patient1");
        when(patientAccountMapper.selectOne(any())).thenReturn(patient);
        doAnswer(invocation -> {
            java.util.function.Consumer<String> consumer = invocation.getArgument(4);
            consumer.accept("建议先清淡饮食。");
            return null;
        }).when(aiService).streamAnswer(eq("结合问诊单怎么调养？"), any(), eq(8L), eq(10L), any());

        var mvcResult = mockMvc.perform(post("/api/patient/ai/question/stream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"question":"结合问诊单怎么调养？","consultationId":10}
                                """))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().string("建议先清淡饮食。"));
    }

    @Test
    @WithMockUser(username = "patient1", roles = "PATIENT")
    void patientCanLoadAIContentRecommendations() throws Exception {
        when(aiService.findRecommendations("胃口不好怎么调养？")).thenReturn(List.of(
                new AIContentRecommendation(6L, "knowledge", "一餐如何吃得更均衡", "从食物种类开始调整。"),
                new AIContentRecommendation(9L, "recipe", "山药香菇鸡肉粥", "适合作为清淡日常一餐。")
        ));

        mockMvc.perform(post("/api/patient/ai/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"question":"胃口不好怎么调养？"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].type").value("knowledge"))
                .andExpect(jsonPath("$.data[0].title").value("一餐如何吃得更均衡"))
                .andExpect(jsonPath("$.data[1].type").value("recipe"))
                .andExpect(jsonPath("$.data[1].id").value(9));
    }

    @Test
    @WithMockUser(username = "patient1", roles = "PATIENT")
    void consultationSubmissionReturnsClearValidationMessages() throws Exception {
        PatientAccount patient = new PatientAccount();
        patient.setId(8L);
        patient.setUsername("patient1");
        when(patientAccountMapper.selectOne(any())).thenReturn(patient);

        mockMvc.perform(post("/api/patient/consultation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patientName":"123",
                                  "age":0,
                                  "gender":"",
                                  "phone":"1234",
                                  "symptoms":"",
                                  "duration":"",
                                  "urgency":"普通"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.containsString("患者姓名应为"),
                        org.hamcrest.Matchers.containsString("患者年龄必须在"),
                        org.hamcrest.Matchers.containsString("请选择性别"),
                        org.hamcrest.Matchers.containsString("请输入正确的 11 位手机号"),
                        org.hamcrest.Matchers.containsString("请描述主要症状"),
                        org.hamcrest.Matchers.containsString("请输入症状持续时间")
                )));
    }

    @Test
    void protectedEndpointsRejectAnonymousAccess() throws Exception {
        mockMvc.perform(get("/api/patient/consultation/my"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    void corsPreflightForStreamingAIEndpointIsAllowed() throws Exception {
        mockMvc.perform(options("/api/patient/ai/question/stream")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    @WithMockUser(username = "patient1", roles = "PATIENT")
    void patientCannotAccessAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/admin/knowledge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"无权创建","content":"正文"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "doctor1", roles = "DOCTOR")
    void doctorCannotAccessPersonnelManagementEndpoints() throws Exception {
        mockMvc.perform(get("/api/admin/personnel/users"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/personnel/doctors"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "doctor1", roles = "DOCTOR")
    void doctorCanLoadOwnWorkspaceAndClaimButCannotAssign() throws Exception {
        User doctor = new User();
        doctor.setId(6L);
        doctor.setUsername("doctor1");
        doctor.setRole("doctor");
        when(userMapper.selectOne(any())).thenReturn(doctor);
        when(consultationWorkspaceService.listForDoctor(1, 10, null, null, null, 6L))
                .thenReturn(new Page<>());
        Consultation claimed = new Consultation();
        claimed.setId(9L);
        claimed.setDoctorId(6L);
        when(consultationWorkspaceService.claim(9L, 6L)).thenReturn(claimed);

        mockMvc.perform(get("/api/admin/consultation"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/admin/consultation/9/claim"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.doctorId").value(6));

        mockMvc.perform(put("/api/admin/consultation/9/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"doctorId":7}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanAssignButCannotClaimConsultations() throws Exception {
        Consultation assigned = new Consultation();
        assigned.setId(9L);
        assigned.setDoctorId(6L);
        when(consultationWorkspaceService.assign(9L, 6L)).thenReturn(assigned);

        mockMvc.perform(put("/api/admin/consultation/9/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"doctorId":6}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.doctorId").value(6));

        mockMvc.perform(put("/api/admin/consultation/9/claim"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanUseDashboardContentManagementAndCsvExport() throws Exception {
        User admin = new User();
        admin.setId(1L);
        admin.setUsername("admin");
        when(userMapper.selectOne(any())).thenReturn(admin);
        when(dashboardService.getSummary()).thenReturn(new DashboardSummary(
                List.of(Map.of("status", "待接诊", "count", 2)),
                List.of(Map.of("urgency", "普通", "count", 2)),
                List.of(Map.of("month", "2026-06", "count", 2))
        ));
        when(dashboardService.getTrend("week")).thenReturn(
                List.of(Map.of("period", "2026-06-16", "count", 2))
        );
        when(knowledgeArticleService.listArticles(anyLong(), anyLong(), any(), any(), any()))
                .thenReturn(new Page<>());
        when(recipeService.listRecipes(anyLong(), anyLong(), any(), any(), any(), any()))
                .thenReturn(new Page<>());
        when(consultationExportService.exportCsv())
                .thenReturn("\uFEFF问诊ID,患者姓名\n".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.statusDistribution[0].status").value("待接诊"));

        mockMvc.perform(get("/api/admin/dashboard/trend").param("period", "week"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].period").value("2026-06-16"))
                .andExpect(jsonPath("$.data[0].count").value(2));

        mockMvc.perform(get("/api/admin/knowledge"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/recipe"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/export/consultations"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"consultations.csv\""))
                .andExpect(content().contentType("text/csv;charset=UTF-8"))
                .andExpect(content().bytes("\uFEFF问诊ID,患者姓名\n".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanListPersonnelAndUpdateAccountStatus() throws Exception {
        PersonnelRecord patient = new PersonnelRecord();
        patient.setId(8L);
        patient.setUsername("patient1");
        patient.setDisplayName("小林");
        patient.setRole("patient");
        patient.setEnabled(true);
        Page<PersonnelRecord> page = new Page<>(1, 10);
        page.setRecords(List.of(patient));
        page.setTotal(1);
        Account account = new Account();
        account.setId(8L);
        account.setUsername("patient1");
        account.setEnabled(false);
        when(personnelService.listPatients(1, 10, "小林")).thenReturn(page);
        when(personnelService.updateEnabled(8L, false, "admin")).thenReturn(account);

        mockMvc.perform(get("/api/admin/personnel/users")
                        .param("current", "1")
                        .param("size", "10")
                        .param("keyword", "小林"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[0].displayName").value("小林"))
                .andExpect(jsonPath("$.data.records[0].enabled").value(true));

        mockMvc.perform(put("/api/admin/personnel/accounts/8/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"enabled":false}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(8))
                .andExpect(jsonPath("$.data.enabled").value(false));

        verify(personnelService).updateEnabled(8L, false, "admin");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanCreateUpdateAndDeleteKnowledgeAndRecipes() throws Exception {
        KnowledgeArticle article = new KnowledgeArticle();
        article.setId(11L);
        article.setTitle("春季养生");
        Recipe recipe = new Recipe();
        recipe.setId(12L);
        recipe.setName("山药粥");
        when(knowledgeArticleService.createArticle(any())).thenReturn(article);
        when(knowledgeArticleService.updateArticle(eq(11L), any())).thenReturn(article);
        when(recipeService.createRecipe(any())).thenReturn(recipe);
        when(recipeService.updateRecipe(eq(12L), any())).thenReturn(recipe);

        mockMvc.perform(post("/api/admin/knowledge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"春季养生","content":"正文","published":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(11));

        mockMvc.perform(put("/api/admin/knowledge/11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"春季养生","content":"更新正文","published":true}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/admin/knowledge/11"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/admin/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"山药粥","ingredients":"山药、粳米","steps":"熬煮","published":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(12));

        mockMvc.perform(put("/api/admin/recipe/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"山药粥","ingredients":"山药、粳米","steps":"小火熬煮","published":true}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/admin/recipe/12"))
                .andExpect(status().isOk());

        verify(knowledgeArticleService).deleteArticle(11L);
        verify(recipeService).deleteRecipe(12L);
    }
}
