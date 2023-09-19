package com.ljy.platformuser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljy.platformuser.domain.entity.Users;
import com.ljy.platformuser.service.UsersService;
import com.ljy.platformuser.dao.UsersMapper;
import org.springframework.stereotype.Service;

/**
* @author LJY
* @description 针对表【users】的数据库操作Service实现
* @createDate 2023-09-19 15:57:31
*/
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService {

}




