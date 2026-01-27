package in.tech_camp.protospace_b.entity;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;

@Data
public class CommentEntity {
  private Integer id;
  private String text;
  private UserEntity user;
  private PrototypeEntity prototype;
  private Timestamp createdAt;

  public String getRelativeTime() {
    if (this.createdAt == null) return "たった今";
    
    LocalDateTime createdTime = this.createdAt.toLocalDateTime();
    LocalDateTime now = LocalDateTime.now();
    Duration duration = Duration.between(createdTime, now);
    long seconds = duration.getSeconds();

    // 1. 30日（1ヶ月の目安）を超えた場合
    // 30日 = 60秒 * 60分 * 24時間 * 30日 = 2,592,000秒
    if (seconds >= 2592000L) {
        return createdTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    // 2. 30日以内の相対表記
    if (seconds < 60) return "たった今";
    if (seconds < 3600) return (seconds / 60) + "分前";
    if (seconds < 86400) return (seconds / 3600) + "時間前";
    
    // 1日以上30日未満の場合
    return (seconds / 86400) + "日前";
  }
}
