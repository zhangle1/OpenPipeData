package org.pipeData.core.entity;


import lombok.Data;

@Data
public class UserBaseInfo {

    private String id;

    private String email;

    private String username;

    private String avatar;

    private String name;

    private String description;

    private boolean orgOwner;

    public UserBaseInfo() {
    }

    public UserBaseInfo(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.name = user.getName();
        this.description = user.getDescription();
        this.avatar = user.getAvatar();
    }
}
