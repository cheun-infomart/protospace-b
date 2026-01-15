package in.tech_camp.protospace_b.entity;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class PrototypeEntity {
  private Integer id;
  private String name;
  private String catchCopy;
  private String concept;
  private String image;
  private Timestamp createdAt;
  private UserEntity user;
}
