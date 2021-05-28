package com.zdk.controller;

import com.alibaba.fastjson.JSON;
import com.zdk.dto.AddEnterpriseMeta;
import com.zdk.dto.EditMeta;
import com.zdk.dto.EnterpriseMeta;
import com.zdk.dto.Meta;
import com.zdk.interceptor.RightInfo;
import com.zdk.pojo.EnterpriseUser;
import com.zdk.service.enterprise.EnterpriseServiceImpl;
import com.zdk.utils.DateConversion;
import com.zdk.utils.CommonMessage;
import com.zdk.utils.UUIDUtil;
import com.zdk.utils.UserConvert;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2021/4/20 16:04
 * BeanUtils.copyProperties(, );
 */
@RestController
public class EnterpriseUserController {
    @Autowired
    @Qualifier("EnterpriseServiceImpl")
    private EnterpriseServiceImpl enterpriseService;

    @PostMapping("/enterpriseLogin")
    @CrossOrigin
    public Object login(String id, String password,String email){
        EnterpriseUser result= enterpriseService.enterpriseLogin(id, password,email);
        enterpriseService.updateLoginInfo(id, DateConversion.getNowDate());
        Meta meta = CommonMessage.returnMsg(result);
        return JSON.toJSONString(meta);
    }

    @RightInfo("enterpriseList")
    @PostMapping("/enterpriseUsers")
    @CrossOrigin
    public Object enterpriseList(@Nullable String query, @Param("pagenum") Integer pagenum, @Param("pagesize") Integer pagesize){
        HashMap data = new HashMap<>();
        HashMap msg = new HashMap<>();
        int enterpriseTotalPage=enterpriseService.enterpriseTotalPage(query);
        data.put("total",enterpriseTotalPage);
        data.put("pagenum",pagenum);
        List<EnterpriseMeta> result;
        if(query==null){
            result = enterpriseService.getEnterpriseList((pagenum - 1) * pagesize, pagesize);
        }else {
            result = enterpriseService.fuzzyQueryEnterpriseList(query, (pagenum - 1) * pagesize, pagesize);
        }
        data.put("users",JSON.toJSON(result.toArray()));
        msg.put("msg", "获取成功");
        msg.put("status", "200");
        return JSON.toJSONString(new Meta(msg,data));
    }

    @RightInfo("removeEnterprise")
    @DeleteMapping("/enterpriseUsers/{id}")
    @CrossOrigin
    public Object removeEnterprise(@PathVariable String id) {
        int count = enterpriseService.removeEnterprise(id);
        return JSON.toJSONString(CommonMessage.returnStatus(count>0));
    }

    @RightInfo("addEnterprise")
    @PostMapping("/addEnterpriseUsers")
    @CrossOrigin
    public Object addEnterprise(AddEnterpriseMeta enterpriseUser) {
        enterpriseUser.setId(UUIDUtil.getUUID(6));
        int count = enterpriseService.addEnterprise(UserConvert.getAddUser(enterpriseUser, "企业用户"));
        return JSON.toJSONString(CommonMessage.returnStatus(count>0));
    }

    @RightInfo("showEnterpriseUsers")
    @GetMapping("/showEditEnterpriseUsers/{id}")
    @CrossOrigin
    public Object showEnterpriseUsers(@PathVariable String id){
        EditMeta editMeta = enterpriseService.showEnterprise(id);
        HashMap msg = new HashMap<>();
        HashMap data = new HashMap<>();
        if(editMeta!=null){
            msg.put("status", "200");
            data.put("id", id);
            data.put("username", editMeta.getUsername());
            data.put("tel", editMeta.getTel());
            data.put("email", editMeta.getEmail());
            data.put("enterpriseName", editMeta.getEnterpriseName());

        }else{
            msg.put("status", "201");
        }
        return JSON.toJSONString(new Meta(msg, data));
    }

    @RightInfo("editEnterpriseUsers")
    @PostMapping("/editEnterpriseUsers/{id}")
    @CrossOrigin
    public Object editEnterpriseUsers(EditMeta user){
        int count = enterpriseService.modifyEnterpriseUser(user);
        return JSON.toJSONString(CommonMessage.returnStatus(count>0));
    }

    @RightInfo("")
    @PostMapping("/enterpriseRegister")
    @CrossOrigin
    public Object enterpriseRegister(AddEnterpriseMeta enterpriseUser){
        enterpriseUser.setId(UUIDUtil.getUUID(6));
        int count = enterpriseService.addEnterprise(UserConvert.getAddUser(enterpriseUser, "企业用户"));
        return JSON.toJSONString(CommonMessage.returnStatus(count>0));
    }

    @RightInfo("")
    @PostMapping("/enterprisePwdChange")
    @CrossOrigin
    public Object enterprisePwdChange(AddEnterpriseMeta enterpriseUser){
        if(enterpriseService.enterpriseLogin(enterpriseUser.getId(), null,enterpriseUser.getEmail())!=null){
            int count = enterpriseService.modifyEnterprisePwd(enterpriseUser);
            return CommonMessage.returnStatus(count>0);
        }
        return null;
    }
}