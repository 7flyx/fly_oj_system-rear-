package com.fly.friend.controller.file;

import com.fly.common.core.domain.R;
import com.fly.common.file.domain.OSSResult;
import com.fly.friend.service.file.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private IFileService sysFileService;

    @PostMapping("/upload")
    public R<OSSResult> upload(@RequestBody MultipartFile file) {
        return R.success(sysFileService.upload(file));
    }

}
