package in.tech_camp.protospace_b.controller;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import in.tech_camp.protospace_b.config.CustomUserDetails;
import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.service.LikeService;

@WebMvcTest(LikeController.class)
@ActiveProfiles("test")
public class LikeControllerUnitTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private LikeService likeService;

  @Nested
    class ログイン済みの場合 {

    @Test
    public void いいねボタンを押すと200OKとJSONが返ること() throws Exception {
      Map<String, Object> mockResult = new HashMap<>();
      mockResult.put("isLiked", true);
      mockResult.put("LikeCount", 5);

      when(likeService.toggleLike(anyInt(), anyInt())).thenReturn(mockResult);

      UserEntity user = new UserEntity();
      user.setId(1);
      CustomUserDetails customUserDetails = new CustomUserDetails(user);


      mockMvc.perform(post("/api/prototypes/10/like")
              .with(SecurityMockMvcRequestPostProcessors.user(customUserDetails)) // ログイン状態を再現
              .with(SecurityMockMvcRequestPostProcessors.csrf()))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.isLiked").value(true))
              .andExpect(jsonPath("$.LikeCount").value(5));
      
      verify(likeService, times(1)).toggleLike(eq(1), eq(10));
    }
  }

  @Nested
  class 未ログインの場合 {
    @Test
    public void いいねボタンを押すとエラーが発生すること() throws Exception {
      // ログイン情報を渡さずにPOSTリクエスト
      mockMvc.perform(post("/api/prototypes/10/like")
              .with(SecurityMockMvcRequestPostProcessors.csrf()))
              .andExpect(status().isUnauthorized());
      
      // Serviceは呼ばれていないことを検証
      verify(likeService, never()).toggleLike(anyInt(), anyInt());
    }
  }
}
