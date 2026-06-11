package com.tcm.platform.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class DashScopeClientTest {

    @Test
    void askSendsAuthorizedRequestAndParsesAnswer() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        String url = "https://example.test/dashscope";
        server.expect(once(), requestTo(url))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer test-key"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.model").value("qwen-plus"))
                .andExpect(jsonPath("$.input.prompt").value(containsString("你是一名中医养生助手")))
                .andExpect(jsonPath("$.input.prompt").value(containsString("春季如何调养？")))
                .andExpect(jsonPath("$.parameters.result_format").value("text"))
                .andRespond(withSuccess("""
                        {
                          "output": {
                            "text": "建议规律作息。"
                          }
                        }
                        """, MediaType.APPLICATION_JSON));
        DashScopeClient client = new DashScopeClient(restTemplate, url, "qwen-plus");

        String answer = client.ask("test-key", "春季如何调养？");

        assertThat(answer).isEqualTo("建议规律作息。");
        server.verify();
    }
}
