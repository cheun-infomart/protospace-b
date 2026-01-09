package in.tech_camp.protospace_b.entity;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class CommentEntity {
  private Integer id;
  private String text;
  private Integer userId;
  private Integer prototypeId;
  private Timestamp createdAt;
}
