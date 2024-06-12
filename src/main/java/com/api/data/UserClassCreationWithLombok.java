package com.api.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor // Creates No Arg Constructor
@AllArgsConstructor // Creates All Arg Constructor
@Data  // generate setters and getters
@Builder
public class UserClassCreationWithLombok {
    private String id;
    private String name;
    private String email;
    private String gender;
    private String status;


}
