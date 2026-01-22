package in.tech_camp.protospace_b.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LikeRepository {
  // いいね追加
  @Insert("INSERT into likes (user_id, prototype_id) VALUES(#{userId}, #{prototypeId})")
  void insert(@Param("userId") Integer userId, @Param("prototypeId") Integer prototypeId);

  // いいね削除
  @Delete("DELETE FROM likes WHERE user_id = #{userId} AND prototype_id = #{prototypeId}")
  void delete(@Param("userId") Integer userId, @Param("prototypeId") Integer prototypeId);

  // 投稿のいいね数を取得
  @Select("SELECT COUNT(*) FROM likes WHERE prototype_id =#{prototypeId}")
  int countByPrototypeId(@Param("prototypeId") Integer prototypeId);

  // 既にいいね済みかどうか確認
  @Select("SELECT COUNT(*) FROM likes WHERE user_id = #{userId} AND prototype_id = #{prototypeId}")
  int countByUserAndPrototype(@Param("userId") Integer userId, @Param("prototypeId") Integer prototypeId);
}
