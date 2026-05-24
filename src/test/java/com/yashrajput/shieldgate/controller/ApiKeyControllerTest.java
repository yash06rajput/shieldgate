// package com.yashrajput.shieldgate.controller;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.yashrajput.shieldgate.dto.GenerateApiKeyRequest;
// import com.yashrajput.shieldgate.entity.ApiKey;
// import com.yashrajput.shieldgate.service.ApiKeyService;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.web.servlet.MockMvc;


// import static org.mockito.Mockito.when;
// import static org.springframework.http.MediaType.APPLICATION_JSON;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(ApiKeyController.class)
// class ApiKeyControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private ApiKeyService apiKeyService;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @Test
//     void shouldCreateApiKey() throws Exception {
//         GenerateApiKeyRequest request = new GenerateApiKeyRequest();
//         request.setEmail("test@example.com");
//         request.setName("Demo Key");
//         request.setDailyQuota(100);

//         ApiKey apiKey = new ApiKey();
//         apiKey.setId(1L);
//         apiKey.setName("Demo Key");
//         apiKey.setKeyValue("sk_live_demo123");
//         apiKey.setDailyQuota(100);
//         apiKey.setRequestsUsed(0);
//         apiKey.setActive(true);

//         when(apiKeyService.generateApiKey(
//                 "test@example.com",
//                 "Demo Key",
//                 100
//         )).thenReturn(apiKey);

//         mockMvc.perform(post("/api/keys/generate")
//                 .contentType(APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.name").value("Demo Key"))
//                 .andExpect(jsonPath("$.dailyQuota").value(100));
//     }
// }