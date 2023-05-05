package com.hackyle.blog.business.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.common.pojo.JwtPayload;
import com.hackyle.blog.business.dto.AdminSignInDto;
import com.hackyle.blog.business.dto.AdminSignUpDto;
import com.hackyle.blog.business.entity.AdministratorEntity;
import com.hackyle.blog.business.mapper.AdministratorMapper;
import com.hackyle.blog.business.service.AdministratorService;
import com.hackyle.blog.business.util.BeanCopyUtils;
import com.hackyle.blog.business.util.HashUtils;
import com.hackyle.blog.business.util.IDUtils;
import com.hackyle.blog.business.util.JwtUtils;
import com.hackyle.blog.business.vo.AdministratorVo;
import com.hackyle.blog.business.vo.KaptchaVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AdministratorServiceImpl implements AdministratorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdministratorServiceImpl.class);

    @Autowired
    private AdministratorMapper administratorMapper;
    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private ValueOperations<String, String> redisValueOperations;

    @Override
    public ApiResponse<String> singUp(AdminSignUpDto adminSignUpDto) {
        AdministratorEntity administratorEntity = BeanCopyUtils.copy(adminSignUpDto, AdministratorEntity.class);

        //查询是否有相同用户名的用户
        QueryWrapper<AdministratorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AdministratorEntity::getUsername, adminSignUpDto.getUsername())
                .eq(AdministratorEntity::getDeleted, 0);
        Integer count = administratorMapper.selectCount(queryWrapper);
        if(count != 0) {
            return ApiResponse.error(ResponseEnum.SIGN_UP_FAIL.getCode(), ResponseEnum.SIGN_UP_FAIL.getMessage());
        }

        administratorEntity.setId(IDUtils.timestampID());
        administratorEntity.setPassword(HashUtils.sha256(adminSignUpDto.getPassword()));
        int insert = administratorMapper.insert(administratorEntity);

        if(insert != 1) {
            return ApiResponse.error(ResponseEnum.SIGN_UP_FAIL.getCode(), ResponseEnum.SIGN_UP_FAIL.getMessage());
        } else {
            return ApiResponse.success(ResponseEnum.SIGN_UP_OK.getCode(), ResponseEnum.SIGN_UP_OK.getMessage());
        }
    }

    @Override
    public ApiResponse<String> update(AdminSignUpDto adminSignUpDto) {
        AdministratorEntity administratorEntity = BeanCopyUtils.copy(adminSignUpDto, AdministratorEntity.class);

        if(StringUtils.isNotBlank(adminSignUpDto.getNewPassword())) {
            administratorEntity.setPassword(HashUtils.sha256(adminSignUpDto.getNewPassword()));
        }

        UpdateWrapper<AdministratorEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(AdministratorEntity::getId, adminSignUpDto.getId());

        int update = administratorMapper.update(administratorEntity, updateWrapper);
        if(update != 1) {
            throw new RuntimeException("管理员信息更新失败");
        }

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Override
    public AdministratorVo signIn(AdminSignInDto adminSignInDto) {
        //检查验证码
        String redisVerificationCode = redisValueOperations.get(adminSignInDto.getUuid());
        if (StringUtils.isEmpty(redisVerificationCode)) {
            return null;
        }
        if (!redisVerificationCode.equalsIgnoreCase(adminSignInDto.getCode())) {
            return null;
        }
        //验证通过后，立即删除该Key对应的验证码文本
        redisValueOperations.getOperations().delete(adminSignInDto.getUuid());

        QueryWrapper<AdministratorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", adminSignInDto.getUsername())
                .eq("password", HashUtils.sha256(adminSignInDto.getPassword()))
                .eq("is_deleted", 0);
        AdministratorEntity administratorEntity = administratorMapper.selectOne(queryWrapper);

        LOGGER.info("管理员登录查库-入参adminSignInDto={}，出参administratorEntity={}", JSON.toJSONString(adminSignInDto), JSON.toJSONString(administratorEntity));
        AdministratorVo administratorVo = BeanCopyUtils.copy(administratorEntity, AdministratorVo.class);
        if(administratorVo == null) {
            return null;
        }

        //签发Token
        JwtPayload jwtPayload = new JwtPayload(String.valueOf(administratorVo.getId()), administratorVo.getUsername(), administratorVo.getUsername());
        String token = JwtUtils.createJWT(jwtPayload);
        administratorVo.setToken(token);

        return administratorVo;
    }

    @Override
    public AdministratorVo info(String token) {
        if(!JwtUtils.validateJWT(token)) {
            return null;
        }

        JwtPayload jwtPayload = JwtUtils.parseJWT(token);

        QueryWrapper<AdministratorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", jwtPayload.getId())
                .eq("username", jwtPayload.getIssuer())
                .eq("is_deleted", 0);
        AdministratorEntity administratorEntity = administratorMapper.selectOne(queryWrapper);
        AdministratorVo administratorVo = BeanCopyUtils.copy(administratorEntity, AdministratorVo.class);

        return administratorVo;
    }

    /**
     * 获取验证码
     * 1.使用Kaptcha获取到验证码的字符存于kaptchaText、图片存于BufferedImage
     * 2.图片转换成Base64的方式传递给前端
     * 3.kaptchaText放在Redis中，60s有效，使用UUID作为Redis的Key
     */
    @Override
    public KaptchaVO verificationCode() {
        String kaptchaText = defaultKaptcha.createText();
        BufferedImage image = defaultKaptcha.createImage(kaptchaText);

        String base64Code = "";
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", outputStream);
            base64Code = Base64.encodeBase64String(outputStream.toByteArray());
        } catch (Exception e) {
            LOGGER.error("verificationCode exception: ", e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    LOGGER.error("verificationCode outputStream close exception: ", e);
                }
            }
        }

        KaptchaVO kaptchaVO = new KaptchaVO();
        String uuid = UUID.randomUUID().toString();
        kaptchaVO.setUuid(uuid);
        kaptchaVO.setCode("data:image/png;base64," + base64Code);
        redisValueOperations.set(uuid, kaptchaText, 60L, TimeUnit.SECONDS);

        return kaptchaVO;
    }

}
