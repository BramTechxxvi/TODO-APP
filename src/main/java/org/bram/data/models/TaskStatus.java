package org.bram.data.models;


import lombok.Getter;

@Getter
public enum TaskStatus {

    COMPLETED("Completed"),
    IN_PROGRESS("In Progress..."),
    PENDING("Pending...");

    private final String status;

    TaskStatus(String status) {
        this.status = status;
    }


}
