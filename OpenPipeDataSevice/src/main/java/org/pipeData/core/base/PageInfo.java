package org.pipeData.core.base;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo implements Serializable {

    private long pageSize;

    private long pageNo;

    private long total;

    private boolean countTotal;

    @Override
    public String toString() {
        return "PageInfo{" +
                "pageSize=" + pageSize +
                ", pageNo=" + pageNo +
                ", total=" + total +
                '}';
    }
}