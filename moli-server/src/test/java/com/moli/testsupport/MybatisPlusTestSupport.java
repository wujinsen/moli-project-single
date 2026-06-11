package com.moli.testsupport;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.moli.common.domain.entity.OperationComponentDeployInfo;
import com.moli.common.domain.entity.OperationPlatformInfo;
import com.moli.common.domain.entity.OperationProjectDeployInfo;
import com.moli.common.domain.entity.OperationServerComponent;
import com.moli.common.domain.entity.OperationServerInfo;
import com.moli.common.domain.entity.OperationServerProject;
import com.moli.common.domain.entity.SysDept;
import com.moli.common.domain.entity.SysDictData;
import com.moli.common.domain.entity.SysDictType;
import com.moli.common.domain.entity.SysLoginLog;
import com.moli.common.domain.entity.SysMenu;
import com.moli.common.domain.entity.SysOperationLog;
import com.moli.common.domain.entity.SysPost;
import com.moli.common.domain.entity.SysRole;
import com.moli.common.domain.entity.SysRoleMenu;
import com.moli.common.domain.entity.SysSystem;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.entity.SysUserPost;
import com.moli.common.domain.entity.SysUserRole;
import com.moli.common.domain.entity.SysUserSystem;
import org.apache.ibatis.builder.MapperBuilderAssistant;

public final class MybatisPlusTestSupport {

    private static volatile boolean initialized;

    private MybatisPlusTestSupport() {
    }

    public static void initAll() {
        if (initialized) {
            return;
        }
        synchronized (MybatisPlusTestSupport.class) {
            if (initialized) {
                return;
            }
            MybatisConfiguration configuration = new MybatisConfiguration();
            MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
            Class<?>[] entities = new Class<?>[]{
                    SysUser.class,
                    SysSystem.class,
                    SysPost.class,
                    SysDept.class,
                    SysRole.class,
                    SysMenu.class,
                    SysDictType.class,
                    SysDictData.class,
                    SysLoginLog.class,
                    SysOperationLog.class,
                    SysUserRole.class,
                    SysUserPost.class,
                    SysUserSystem.class,
                    SysRoleMenu.class,
                    OperationPlatformInfo.class,
                    OperationServerInfo.class,
                    OperationProjectDeployInfo.class,
                    OperationComponentDeployInfo.class,
                    OperationServerProject.class,
                    OperationServerComponent.class
            };
            for (Class<?> entity : entities) {
                TableInfoHelper.initTableInfo(assistant, entity);
            }
            initialized = true;
        }
    }
}
