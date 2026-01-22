package in.tech_camp.protospace_b.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import in.tech_camp.protospace_b.config.CustomUserDetails;
import in.tech_camp.protospace_b.service.LikeService;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class LikeController {
  private final LikeService likeService;

  @PostMapping("/api/prototypes/{id}/like")
  public Map<String, Object> toggleLike(@PathVariable("id") Integer prototypeId, @AuthenticationPrincipal CustomUserDetails userDetails) {
    if (userDetails == null) {
        throw new RuntimeException("ログインしてください");
    }

    Integer userId = userDetails.getId();
    return likeService.toggleLike(userId, prototypeId);
  }
}
