package com.omobio.springbase.dto.role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilterRoleDTO {
    private String name;
    private int page;
    private int perPage;
}
