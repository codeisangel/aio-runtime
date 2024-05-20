package com.guodun.aio.document;


import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.system.*;
import com.guodun.aio.document.user.EnableUserModel;
import com.guodun.security.client.http.EnableSecurityClient4Http;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;

@SpringBootApplication
//@EnableCodeGenerate
@EnableUserModel
@EnableAsync
@EnableAspectJAutoProxy
@EnableSecurityClient4Http
@EnableZuulProxy
public class GuodunDocumentApplication {
    private static String getJarDirectory(){
        try {
            CodeSource codeSource = GuodunDocumentApplication.class.getProtectionDomain().getCodeSource();
            if (ObjectUtil.isNull(codeSource)){
                return null;
            }
            URL jarUrl = codeSource.getLocation(); // 获取Jar包的URL
            Path jarPath = Paths.get(jarUrl.toURI()); // 将URL转换为路径
            return jarPath.getParent().toString(); // 获取Jar包所在目录
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }
    public static void main(String[] args) {

        SpringApplication.run(GuodunDocumentApplication.class, args);

    }

}
