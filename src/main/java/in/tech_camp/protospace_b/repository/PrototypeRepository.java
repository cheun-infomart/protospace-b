package in.tech_camp.protospace_b.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import in.tech_camp.protospace_b.entity.PrototypeEntity;

@Mapper
public interface PrototypeRepository {
  @Select("SELECT * FROM prototypes WHERE user_id = #{userId}")
  List<PrototypeEntity> findByUserId(Integer userId);
  
  @Select("SELECT p.*, u.name AS user_name FROM prototypes p INNER JOIN users u ON p.user_id = u.id")
  @Results(value = {
    @Result(property="id", column="id"),
    @Result(property="name", column="name"),
    @Result(property="catchCopy", column="catch_copy"),
    @Result(property="concept", column="concept"),
    @Result(property="image", column="image"),
    @Result(property="user.id", column="user_id"),
    @Result(property="user.name", column="user_name")
  })
  List<PrototypeEntity> findAll();
  
  @Select("SELECT * FROM prototypes WHERE id = #{id}")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "user", column = "user_id",
      one = @One(select = "in.tech_camp.protospace_b.repository.UserRepository.findUserById")),
    @Result(property = "comments", column = "id",
      many = @Many(select = "in.tech_camp.protospace_b.repository.CommentRepository.findByPrototypeId"))
  })
  PrototypeEntity findById(Integer id);
   
  @Insert("INSERT into prototypes (name, catch_copy, concept, image, user_id) VALUES (#{name}, #{catchCopy}, #{concept}, #{image}, #{user.id})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(PrototypeEntity prototype);

  @Delete("DELETE FROM prototypes WHERE id = #{id}")
  void deleteById(Integer id);
  
  @Update("UPDATE prototypes SET name=#{name}, catch_copy=#{catchCopy}, " +
          "concept=#{concept}, image=#{image} WHERE id=#{id}")
  void update(PrototypeEntity prototype);
}
