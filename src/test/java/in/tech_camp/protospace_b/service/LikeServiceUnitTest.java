package in.tech_camp.protospace_b.service;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import in.tech_camp.protospace_b.repository.LikeRepository;

@ExtendWith(MockitoExtension.class)
public class LikeServiceUnitTest {

  @Mock
  private LikeRepository likeRepository;

  @InjectMocks
  private LikeService likeService;

  @Nested
  class 未いいねの場合 {

    @Test
    public void いいねが保存されisLikedがtrueで返ること() {
      Integer userId = 1;
      Integer prototypeId = 100;
      when(likeRepository.countByUserAndPrototype(userId, prototypeId)).thenReturn(0);
      when(likeRepository.countByPrototypeId(prototypeId)).thenReturn(1);

      Map<String, Object> result = likeService.toggleLike(userId, prototypeId);

      verify(likeRepository, times(1)).insert(userId, prototypeId);
      assertEquals(true, result.get("isLiked"));
    }
  }

  @Nested
  class いいね済みの場合 {

    @Test
    public void いいねが削除されisLikedがfalseで返ること() {
      Integer userId = 1;
      Integer prototypeId = 100;
      when(likeRepository.countByUserAndPrototype(userId, prototypeId)).thenReturn(1);
      when(likeRepository.countByPrototypeId(prototypeId)).thenReturn(0);

      Map<String, Object> result = likeService.toggleLike(userId, prototypeId);

      verify(likeRepository, times(1)).delete(userId, prototypeId);
      assertEquals(false, result.get("isLiked"));
    }
  }
}
