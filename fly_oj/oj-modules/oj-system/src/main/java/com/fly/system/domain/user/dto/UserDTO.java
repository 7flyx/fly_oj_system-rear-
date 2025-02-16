package com.fly.system.domain.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class UserDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private Integer status;
}
