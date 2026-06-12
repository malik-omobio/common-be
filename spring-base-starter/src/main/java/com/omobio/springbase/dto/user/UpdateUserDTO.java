package com.omobio.springbase.dto.user;

import com.omobio.springbase.common.enums.UserStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateUserDTO {
    private UUID roleId;
    private UserStatus status;
}
