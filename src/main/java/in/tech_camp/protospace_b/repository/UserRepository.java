package in.tech_camp.protospace_b.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.protospace_b.entity.UserEntity;

@Mapper
public interface UserRepository {
  @Select("SELECT * FROM users WHERE id = #{id}")
  UserEntity findById(Integer id);

  @Select("SELECT * FROM users WHERE email = #{email}")
  UserEntity findByEmail(String email);

  @Insert("INSERT INTO users (name, email, password, profile, department, position) VALUES (#{name}, #{email}, #{password}, #{profile}, #{department}, #{position})")
  void insert(UserEntity user);

  @Select("SELECT * FROM users WHERE id = #{id}")
    @Results(value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "prototypes", column = "id", 
                many = @Many(select = "in.tech_camp.protospace_b.repository.PrototypeRepository.findByUserId"))
    })
    UserEntity findByIdWithProto(Integer id);

}

