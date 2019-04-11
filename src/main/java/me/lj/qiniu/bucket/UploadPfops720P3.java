package me.lj.qiniu.bucket;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import me.lj.qiniu.config.Config;

import java.io.IOException;

public class UploadPfops720P3 {
    //设置好账号的ACCESS_KEY和SECRET_KEY
    String ACCESS_KEY = Config.ACCESS_KEY;
    String SECRET_KEY = Config.SECRET_KEY;
    String bucketname = Config.BUCKET_NAME;
    //上传到七牛后保存的文件名
    //    String key = "mov/test.mov";
    String newKey = "mp4/720p/科目3-1号道讲解2X.mp4";
    String key = "mp4/4.mp4";
    //上传文件的路径
    String FilePath = "C:\\Users\\Administrator\\Documents\\Tencent Files\\2080856401\\FileRecv\\MobileFile\\科目3-1号道讲解2.mp4";

    //设置转码操作参数
    String fops = "avthumb/mp4/vcodec/libx264/s/1280x720";
    //设置转码的队列
    String pipeline = "12349";
    String persistentNotifyUrl = "http://practice.dandantuan.com/demo/qiniu/qiniu_sdk_notify.php";
    String callbackUrl = "http://practice.dandantuan.com/demo/qiniu/qiniu_sdk_notify.php";
    String callbackHost = "practice.dandantuan.com";
    //    String callbackBody = "key=$(fname)&fsize=$(fsize)";
    String callbackBody = "filename=$(fname)&filesize=$(fsize)&key=$(key)";
    String callbackBodyType = "application/json";

    //可以对转码后的文件进行使用saveas参数自定义命名，当然也可以不指定文件会默认命名并保存在当前空间。
    String urlbase64 = UrlSafeBase64.encodeToString(bucketname + ":" + newKey);
    String pfops = fops + "|saveas/" + urlbase64;

    //密钥配置
    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);

    ///////////////////////指定上传的Zone的信息//////////////////
    //第一种方式: 指定具体的要上传的zone
    //注：该具体指定的方式和以下自动识别的方式选择其一即可
    //要上传的空间(bucket)的存储区域为华东时
    // Zone z = Zone.zone0();
    //要上传的空间(bucket)的存储区域为华北时
    // Zone z = Zone.zone1();
    //要上传的空间(bucket)的存储区域为华南时
    // Zone z = Zone.zone2();

    //第二种方式: 自动识别要上传的空间(bucket)的存储区域是华东、华北、华南。
    Zone z = Zone.autoZone();
    Configuration c = new Configuration(z);

    //创建上传对象
    UploadManager uploadManager = new UploadManager(c);

    public static void main(String args[]) throws IOException {
        new UploadPfops720P3().upload();
    }

    //上传策略中设置persistentOps字段和persistentPipeline字段
    public String getUpToken() {
        return auth.uploadToken(bucketname, key, 3600, new StringMap()
                .put("deleteAfterDays", 1)
                .put("callbackUrl", callbackUrl)
                .put("callbackBody", callbackBody)
                .put("persistentOps", pfops)
                .put("persistentNotifyUrl", persistentNotifyUrl)
                .put("persistentPipeline", pipeline), true);
    }

    public void upload() throws IOException {
        try {
            //调用put方法上传
            Response res = uploadManager.put(FilePath, key, getUpToken());
            //打印返回的信息
            System.out.println(res.bodyString());
        } catch (QiniuException e) {
            Response r = e.response;
            // 请求失败时打印的异常的信息
            System.out.println(r.toString());
            try {
                //响应的文本信息
                System.out.println(r.bodyString());
            } catch (QiniuException e1) {
                //ignore
            }
        }
    }
}
