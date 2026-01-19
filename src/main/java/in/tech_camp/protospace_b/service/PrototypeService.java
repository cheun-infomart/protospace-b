package in.tech_camp.protospace_b.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.protospace_b.ImageUrl;
import in.tech_camp.protospace_b.entity.PrototypeEntity;
import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.form.PrototypeForm;
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

	if (!Files.exists(imagePath)) {
        Files.createDirectories(imagePath);
    }
	
	// 基本は同じファイル名にならないが、念のため同じファイル名でも保存できるように設定(テスト対策の意味も含めて)
	Files.copy(imageFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
	return "/uploads/" + fileName;
  }

  // 新規投稿メソッド(画像処理とユーザーのセット後DBに保存する)
  public void createPrototype(PrototypeEntity prototype, MultipartFile imageFile, Authentication authentication) throws IOException {
		
		// 画像をセット
		if (imageFile != null && !imageFile.isEmpty()) {
			String imagePath = saveImage(imageFile);
			prototype.setImage(imagePath);
		}

		// ログインユーザーのuser_idをセット
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new RuntimeException("未ログインです");
		}
		
    // ログインユーザーのemailを取得し、そこからuserを取得してセットする
		String email = authentication.getName();
		UserEntity user = userRepository.findByEmail(email);
		if (user == null) {
			throw new RuntimeException("ユーザーが見つかりません");
		}
		prototype.setUser(user);

		// DBへ保存
		prototypeRepository.insert(prototype);
	}

	public PrototypeForm getPrototypeForm(Integer id) {
		PrototypeEntity prototype = prototypeRepository.findById(id);
		PrototypeForm form = new PrototypeForm();
		form.setName(prototype.getName());
		form.setCatchCopy(prototype.getCatchCopy());
		form.setConcept(prototype.getConcept());
		
		return form;
	}

	public void updatePrototype(Integer id, PrototypeForm form) throws IOException {
		PrototypeEntity prototype = prototypeRepository.findById(id);


		prototype.setName(form.getName());
		prototype.setCatchCopy(form.getCatchCopy());
		prototype.setConcept(form.getConcept());

		MultipartFile newFile = form.getImage();
		if (newFile != null && !newFile.isEmpty()) {
			
			String savedFileName = saveImage(newFile); 
			prototype.setImage(savedFileName);
		}

		prototypeRepository.update(prototype);

	}

	public PrototypeEntity findPrototypeById(Integer id) {
		PrototypeEntity prototype = prototypeRepository.findById(id);

		if (prototype == null) {
        throw new RuntimeException("Prototype ID：" + id + "が存在しません。");
    }
		return prototype;
	}

}
