package com.pinyougou.portal.controller;
import java.util.List;

import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.content.service.ContentService;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/content")
public class ContentController {

	@Reference
	private ContentService contentService;
	
	@RequestMapping("/findCategoryById")
	public List<TbContent> findCategoryById(Long id){
		return contentService.findCategoryById(id);
	}
	
}
