package in.tech_camp.protospace_b.entity;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class LikeEntity {
  private Integer id;
  private Integer userId;
  private Integer prototypeId;
  private Timestamp createdAt;
} 
