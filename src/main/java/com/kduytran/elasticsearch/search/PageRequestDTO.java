package com.kduytran.elasticsearch.search;

import lombok.Data;

@Data
public class PageRequestDTO {
    private static final int DEFAULT_SIZE = 100;
    private int page;
    private int size;

    public int getSize() {
        return size != 0 ? size : DEFAULT_SIZE;
    }

}
