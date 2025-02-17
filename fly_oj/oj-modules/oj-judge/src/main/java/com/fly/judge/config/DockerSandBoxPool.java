package com.fly.judge.config;

import cn.hutool.core.io.FileUtil;
import com.fly.common.core.constants.JudgeConstants;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Volume;

import java.util.List;
import java.io.File;

public class DockerSandBoxPool {
    private DockerClient dockerClient;
    private String sandboxImage;
    private String volumeDir;
    private Long memoryLimit;
    private Long memorySwapLimit;
    private Long cpuLimit;
    private Long poolSize;
    private String containerNamePrefix;

    public DockerSandBoxPool(DockerClient dockerClient, String sandboxImage, String volumeDir,
                             Long memoryLimit, Long memorySwapLimit,
                             Long cpuLimit, Long poolSize, String containerNamePrefix) {
        this.dockerClient = dockerClient;
        this.sandboxImage = sandboxImage;
        this.volumeDir = volumeDir;
        this.memoryLimit = memoryLimit;
        this.memorySwapLimit = memorySwapLimit;
        this.cpuLimit = cpuLimit;
        this.poolSize = poolSize;
        this.containerNamePrefix = containerNamePrefix;
    }

    public void initDockerPoll() { // 初始化容器池
        for(int i = 0; i < poolSize; i++) {
            createContainer(containerNamePrefix + "-" + i);
        }
    }

    private void createContainer(String containerName) {
        //拉取镜像
        pullJavaEnvImage();
        //创建容器  限制资源   控制权限
        HostConfig hostConfig = getHostConfig(containerName);
        CreateContainerCmd containerCmd = dockerClient
                .createContainerCmd(JudgeConstants.JAVA_ENV_IMAGE)
                .withName(containerName);
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(true) // 伪终端
                .exec();
        //记录容器id
        String containerId = createContainerResponse.getId();
        //启动容器
        dockerClient.startContainerCmd(containerId).exec();
    }

    //拉取java执行环境镜像 需要控制只拉取一次
    private void pullJavaEnvImage() {
        ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
        List<Image> imageList = listImagesCmd.exec();
        for (Image image : imageList) {
            String[] repoTags = image.getRepoTags();
            if (repoTags != null && repoTags.length > 0 && JudgeConstants.JAVA_ENV_IMAGE.equals(repoTags[0])) {
                return;
            }
        }
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(JudgeConstants.JAVA_ENV_IMAGE);
        try {
            pullImageCmd.exec(new PullImageResultCallback()).awaitCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //限制资源   控制权限
    private HostConfig getHostConfig(String containerName) {
        HostConfig hostConfig = new HostConfig();
        //设置挂载目录，指定用户代码路径
        String userCodeDir = createContainerDir(containerName);
        hostConfig.setBinds(new Bind(userCodeDir, new Volume(JudgeConstants.DOCKER_USER_CODE_DIR)));
        //限制docker容器使用资源
        hostConfig.withMemory(memoryLimit);
        hostConfig.withMemorySwap(memorySwapLimit);
        hostConfig.withCpuCount(cpuLimit);
        hostConfig.withNetworkMode("none");  //禁用网络
        hostConfig.withReadonlyRootfs(true); //禁止在root目录写文件
        return hostConfig;
    }

    //为每个容器，创建的指定挂载文件
    private String createContainerDir(String containerName) {
        //一级目录  存放所有容器的挂载目录
        String codeDir = System.getProperty("user.dir") + File.separator + JudgeConstants.CODE_DIR_POOL;
        if (!FileUtil.exist(codeDir)) {
            FileUtil.mkdir(codeDir);
        }
        return codeDir + File.separator + containerName;
    }
}
