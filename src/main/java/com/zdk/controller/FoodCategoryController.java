package com.zdk.controller;

import com.alibaba.fastjson.JSON;
import com.zdk.dto.EnterpriseMeta;
import com.zdk.dto.Meta;
import com.zdk.interceptor.RightInfo;
import com.zdk.pojo.FoodCategory;
import com.zdk.service.foodCategory.FoodCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * @author zdk
 * @date 2021/5/23 14:37
 */
@RestController
public class FoodCategoryController {

    @Autowired
    @Qualifier("FoodCategoryServiceImpl")
    FoodCategoryServiceImpl foodCategoryService;

    @RightInfo("getFoodCategory")
    @GetMapping("/getFoodCategory")
    @CrossOrigin
    public Object getFoodCategory(Integer id){
        HashMap data = new HashMap<>();
        HashMap msg = new HashMap<>();
        msg.put("msg", "获取成功");
        msg.put("status", "200");
        List<FoodCategory> foodCategory;
        if(id==null){
            foodCategory = foodCategoryService.getFoodCategory(null);
        }else {
            foodCategory = foodCategoryService.getFoodCategory(id);
        }
        data.put("foodCategory", JSON.toJSON(foodCategory.toArray()));
        return JSON.toJSONString(new Meta(msg,data));
    }
}
