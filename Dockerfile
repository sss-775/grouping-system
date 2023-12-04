#项目所依赖的jdk镜像
FROM openjdk:8
# 修改时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
ARG active
#将maven构建好的jar添加到镜像中，第二个为别名
ADD target/*.jar app.jar
ENV LANG C.UTF-8
ENV active=${active}
#暴露的端口号(和项目端口号等同)
EXPOSE 8080
#镜像所执行的命令
ENTRYPOINT ["java","-jar","/app.jar", "--spring.profiles.active=pre"]