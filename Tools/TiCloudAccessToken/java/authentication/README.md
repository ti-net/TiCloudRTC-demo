## 使用AccessToken鉴权

### 示例使用方式

1. 将以下代码复制到您的java文件中，将enterpriseId, token, userId替换成你自己的。

    ```java
    public class AccessTokenBuildSample {

        //请替换为天润分配给您的企业Id
        static Integer enterpriseId = 7000002;
        //请替换为天润分配给您的企业密钥
        static String token = "xxxxxxxxxxxxx";
        //请替换为您的用户ID
        static String userId = "112412512";
        //AccessToken的有效期最大为一小时
        static int expirationTimeInSeconds = 3600;

        public String buildAccessToken(Integer enterpriseId, String userId, String token, Integer expirationTimeInSeconds) {
            AccessTokenBuilder accessTokenBuilder = new AccessTokenBuilder();
            int timestamp = (int) (System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
            String result = accessTokenBuilder.buildAccessToken(String.valueOf(enterpriseId), token, userId, timestamp);
            return result;
        }

        public static void main(String[] args) {
            AccessTokenBuildSample accessTokenBuildSample = new AccessTokenBuildSample();
            System.out.println("accessToken:" + accessTokenBuildSample.buildAccessToken(enterpriseId, userId, token, expirationTimeInSeconds));
        }
    }
    ```

2. 执行main方法。您的令牌已生成并打印在您的终端窗口中：
