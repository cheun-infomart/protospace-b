package in.tech_camp.protospace_b.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.protospace_b.ImageUrl;
import in.tech_camp.protospace_b.entity.PrototypeEntity;
import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.repository.PrototypeRepository;
import in.tech_camp.protospace_b.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PrototypeService {
  private final PrototypeRepository prototypeRepository;
  private final UserRepository userRepository;
  private final ImageUrl imageUrl;

  // 画像ファイルの保存処理
  public String saveImage(MultipartFile imageFile) throws IOException {
    if (imageFile == null || imageFile.isEmpty()) {
        return null;
    }

    String uploadDir = imageUrl.getImageUrl();
    String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_" + imageFile.getOriginalFilename();
    Path imagePath = Paths.get(uploadDir, fileName);
    
    Files.copy(imageFile.getInputStream(), imagePath);
    
    return "/uploads/" + fileName;
  }

  // 新規投稿メソッド(画像処理とユーザーのセット後保存する)
  public void createPrototype(PrototypeEntity prototype, MultipartFile imageFile, Authentication authentication) throws IOException {
        
        // 画像保存処理
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            prototype.setImage(imagePath);
        }

        // ログインユーザーのuser_idをセット
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("未ログインです");
        }
        
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("ユーザーが見つかりません");
        }
        prototype.setUserId(user.getId());

        // DBへ保存
        prototypeRepository.insert(prototype);
    }
}
