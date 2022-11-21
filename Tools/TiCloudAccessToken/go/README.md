## 使用AccessToken鉴权

### 目录结构

- `sample/` 包含生成token的示例代码。
- `src/` 包含生成accessToken的源码，其中AccessTokenBuilder用于生成token。

### 示例使用方式

1. 打开sample/sample.go文件，将enterpriseId, token, userId替换成你自己的。
2. 打开您的终端，进入sample目录，然后运行以下命令。之后，文件sample文夹中会出现一个可执行文件：

    ```text
    go build
    ```

3. 运行以下命令。您的令牌已生成并打印在您的终端窗口中：

    ```text
    ./sample
    ```
