package in.tech_camp.protospace_b.entity;

import java.util.List;

import lombok.Data;

@Data
public class UserEntity {
  private Integer id;
  private String name;
  private String email;
  private String profile;
  private String department;
  private String position;
  private String image;
  private String password;
  private List<PrototypeEntity> prototypes;
  private List<CommentEntity> comments;
}
