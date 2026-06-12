package com.omobio.springbase.dto.user;

import com.omobio.springbase.common.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class FilterUserDTO {
    private String email;
    private UUID roleId;
    private UserStatus status;
    private int page;
    private int perPage;
}
