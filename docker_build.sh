# 确定当前工作目录
# shellcheck disable=SC2046
workdir=$(cd $(dirname "$0") || exit; pwd)
echo "工作目录是:$workdir"
# 更新仓库文件
git pull
# 打包jar文件
mvn clean
mvn package
echo "jar文件打包完成"

#容器的名称
container_name=shb_back
#镜像的名称
name=shb_back
#判断是否容器存在
docker ps -a | grep $container_name &> /dev/null
#如果存在，关闭并删除该容器
if [ $? -eq 0 ]
then
    echo $container_name" is up,we will stop and remove it!!!"
    docker stop $container_name
    docker rm $container_name
else
    echo $container_name" is not up!!!"
fi

#判断是否镜像存在
docker images | grep $name &> /dev/null
#如果存在，删除该镜像
if [ $? -eq 0 ]
then
    echo $name" image is existed,we will remove it!!!"
    docker rmi $(docker images | grep $name | awk "{print $3}")
else
    echo $name" image is not existed!!!"
fi

docker-compose up