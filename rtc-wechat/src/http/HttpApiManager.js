import {HttpService} from "./HttpService.js"


let __insance = (()=>{
    let instance
    return (newInstance)=>{
        if(newInstance){
            instance = newInstance
        }
        return instance
    }
})()

/** http 接口管理 */
class HttpApiManager{
    constructor(){
        if(__insance()){
            return __insance()
        }
        __insance(this)
    }

    static initService(baseUrl){
        new HttpApiManager()
        /** 使用 accessToken 鉴权的 http 服务 */
        this.httpService = new HttpService(baseUrl)
    }
}

export {
    HttpApiManager
}

