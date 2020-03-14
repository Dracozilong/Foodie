package com.imooc.controller.center;

import com.imooc.controller.BaseController;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.center.CenterUserBO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.resource.FileUpload;
import com.imooc.service.center.CenterUserService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.DateUtil;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("userInfo")
@Api(value = "用户信息",tags = "{用户信息相关接口}")
public class CenterUserController extends BaseController {

    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FileUpload fileUpload;


    @ApiOperation(value = "修改用户头像",notes = "修改用户头像",httpMethod = "POST")
    @PostMapping("uploadFace")
    public IMOOCJSONResult uploadFace(
            @ApiParam(name = "userId",value = "用户id",required = true)
            @RequestParam String userId,
            @ApiParam(name = "file",value = "用户头像",required = true)
            @RequestParam MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response
    )
    {
          // String fileSpace = IMG_USER_FACE_LOCATION;
          String fileSpace = fileUpload.getImageUserFaceLocation();

          //获取上传图片的路径
          String imgServerUrl =fileUpload.getImageServerUrl();

          //在路径上为每个用户增加一个userId，用于区分不同用户上传
          String upLoadPathPrefix = File.separator + userId;

        //开始上传文件
        if (file!= null){

            FileOutputStream fileOutputStream =null;

            //获取文件的原始名称
            String filename = file.getOriginalFilename();

            try {

                if (StringUtils.isNotBlank(filename)){

                        //对原始路径进行分割
                        String[] split = filename.split("\\.");

                        //获取文件后缀
                        String upLoadPathSuffix = split[split.length-1];

                        if(!upLoadPathSuffix.equalsIgnoreCase("png")&&!upLoadPathSuffix.equalsIgnoreCase("JPG")&&!upLoadPathSuffix.equalsIgnoreCase("JPEG")){
                            return IMOOCJSONResult.errorMsg("图片文件格式不正确");
                        }

                        //文件名称
                        String newFileName = "face-"+userId+"."+upLoadPathSuffix;

                        //最终文件上传路径
                        String finalFacePath = fileSpace + upLoadPathPrefix+File.separator+newFileName;

                        //用于提供给web服务访问

                        upLoadPathPrefix+=("/"+newFileName);

                       // upLoadPathPrefix+=(newFileName);


                        File outfile = new File(finalFacePath);

                        if(outfile.getParentFile()!= null){

                            //创建文件夹
                            outfile.getParentFile().mkdirs();
                        }

                        //保存文件到文件夹
                         fileOutputStream = new FileOutputStream(outfile);

                        InputStream inputStream = file.getInputStream();

                        IOUtils.copy(inputStream,fileOutputStream);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(fileOutputStream!= null){
                    try {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }else {
            return IMOOCJSONResult.errorMsg("文件不能为空");
        }

        //保存用户头像,由于浏览器存在缓存，所以需要加上时间戳来保证更新后的图片可以即时刷新
        String finalUserFaceUrl = imgServerUrl+upLoadPathPrefix+"?t="+ DateUtil.getCurrentDateString(DateUtil.DATETIME_PATTERN);

        Users users = centerUserService.updateUserFace(userId, finalUserFaceUrl);

        //增加令牌token，会整合进redis，分布式会话
        UsersVO usersVO = converUsersVo(users);

        //Users setNullProperty = setNullProperty(users);
        //信息存入cookie保存
        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(usersVO),true);


        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "修改用户信息",notes = "修改用户信息",httpMethod ="POST" )
    @PostMapping("update")
    public IMOOCJSONResult update(
            @ApiParam(name = "userId",value = "用户id",required = true)
            @RequestParam String userId,
            @ApiParam(name = "用户Bo",value = "用户bo",required = true)
            @RequestBody  @Valid CenterUserBO centerUserBO,
            BindingResult bindingResult,
            HttpServletRequest request, HttpServletResponse response){

        if (bindingResult.hasErrors()){
            //验证用户信息
            Map<String, String> map = getErrors(bindingResult);

            return IMOOCJSONResult.errorMap(map);
        }

        Users users = centerUserService.updateUserInfo(userId, centerUserBO);

       // Users userproperty = setNullProperty(users);

        //增加令牌token，会整合进redis，分布式会话
        UsersVO usersVO = converUsersVo(users);

        //信息存入cookie保存
        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(usersVO),true);

        return IMOOCJSONResult.ok();
    }

    private Map<String,String> getErrors(BindingResult bindingResult){

        Map<String,String> map = new HashMap<>();

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        fieldErrors.stream().forEach(fieldError -> {

            String errorField = fieldError.getField();

            String message = fieldError.getDefaultMessage();

            map.put(errorField,message);


        });
        return map;
    }

    private Users setNullProperty(Users userResult){

        //设置密码为空
        userResult.setPassword(null);

        //设置邮箱为空
        userResult.setEmail(null);

        //设置手机号为空
        userResult.setMobile(null);

        //设置生日为空
        userResult.setBirthday(null);

        //设置更新时间
        userResult.setUpdatedTime(null);

        //设置创建时间
        userResult.setCreatedTime(null);

        return userResult;
    }
}
