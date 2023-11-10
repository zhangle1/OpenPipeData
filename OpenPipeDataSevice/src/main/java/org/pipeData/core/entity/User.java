package org.pipeData.core.entity;

//import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bas_user")
public class User extends BaseEntity {
    private String email;

    private String username;

    private String password;

    private Boolean active;

    private String name;

    private String description;

    private String avatar;
}