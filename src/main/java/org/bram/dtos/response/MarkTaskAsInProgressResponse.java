package org.bram.dtos.response;


import lombok.Data;

@Data
public class MarkTaskAsInProgressResponse {

    private String message;
    private boolean success;
}
