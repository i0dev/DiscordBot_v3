package com.i0dev.object;

import com.i0dev.Engine;
import lombok.Getter;

@Getter
public class RoleQueueObject {

    Long userID;
    Long roleID;
    Type type;

    public RoleQueueObject(Long userID, Long roleID, Type type) {
        this.userID = userID;
        this.roleID = roleID;
        this.type = type;
    }

    public RoleQueueObject add() {
        Engine.getRoleQueueList().add(this);
        return this;
    }

    @Override
    public String toString() {
        return "RoleQueueObject{" +
                "userID=" + userID +
                ", roleID=" + roleID +
                ", type=" + type +
                '}';
    }
}