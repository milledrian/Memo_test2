package com.example.memo_test;

public class ListItem {
    private long id = 0;
    private String body = null;
    private String uuid = null;

    long getId() { return id; }
    String getBody() {return body; }
    String getUuid() { return uuid; }

    public void setId(long id) { this.id = id; }
    public void setBody(String body) { this.body = body; }
    public void setUuid(String uuid) { this.uuid = uuid; }
}
