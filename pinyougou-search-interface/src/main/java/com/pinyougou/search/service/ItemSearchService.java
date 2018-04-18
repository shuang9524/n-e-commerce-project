package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /**
     * 搜索
     * @param
     * @return
     */
    Map<String,Object> search(Map searchMap);

    void importItemList(List list);
}
