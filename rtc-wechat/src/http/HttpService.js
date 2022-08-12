import { request } from "../utils/RequestUtil.js"


/**
 * http 接口
 *
 * @author Jarvis Semou
 */
class HttpService {
    constructor(baseUrl) {
        this.baseUrl = baseUrl
        this.LOGIN = "/interface/demo/login"
    }

    executePost(
        urlpath,
        params
    ) {
        return new Promise((resolve, reject) => {
            request.execute({
                baseURL: this.baseUrl,
                urlpath: urlpath,
                header: {},
                method: "POST",
                param: params
            }).then(res => resolve(res))
            .catch(err => reject(err))
        })
    }

    login(
        enterpriseId,
        username,
        password
    ) {
        return this.executePost(
            this.LOGIN,
            {
                enterpriseId: enterpriseId,
                username: username,
                password: password
            })
    }

}

export {
    HttpService
}