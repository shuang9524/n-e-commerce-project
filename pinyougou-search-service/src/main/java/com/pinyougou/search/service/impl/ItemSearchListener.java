package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class ItemSearchListener implements MessageListener{

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {

        TextMessage textMessage = (TextMessage) message;

        try {
            String messageText = textMessage.getText();
            List<TbItem> itemList = JSON.parseArray(messageText, TbItem.class);
            System.out.println("queue接收!!!");
            //导入到索引库
            itemSearchService.importItemList(itemList);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
