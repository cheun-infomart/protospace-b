package in.tech_camp.protospace_b.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.tech_camp.protospace_b.repository.LikeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {
  private final LikeRepository likeRepository;

  @Transactional
  public Map<String, Object> toggleLike(Integer userId, Integer prototypeId) {
    int count = likeRepository.countByUserAndPrototype(userId, prototypeId);
    boolean isLiked;

    if (count > 0) {
      likeRepository.delete(userId, prototypeId);
      isLiked = false;
    } else {
      likeRepository.insert(userId, prototypeId);
      isLiked = true;
    }

    int totalLikes = likeRepository.countByPrototypeId(prototypeId);

    Map<String, Object> result = new HashMap<>();
    result.put("isLiked", isLiked);
    result.put("LikeCount", totalLikes);
    
    return result;
  }
}
