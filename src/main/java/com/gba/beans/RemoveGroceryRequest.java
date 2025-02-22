package com.gba.beans;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveGroceryRequest {

    @NotNull(message = "id is required!")
    private Integer id;

}
