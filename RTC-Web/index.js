let options = {};
let btnType = "";

let client = null;

let inited = false;


const register = function () {
    options.enterpriseId = Number($("#enterpriseId").val());
    options.username = $("#username").val();
    options.accessToken = $("#accessToken").val();

    if (options.enterpriseId === null || options.enterpriseId === "") {
        addEventLog("请输入企业ID");
        return;
    }
    if (options.username === null || options.username === "") {
        addEventLog("请输入用户名称");
        return;
    }
    if (options.accessToken === null || options.accessToken === "") {
        addEventLog("请输入AccessToken");
        return;
    }
    TiCloudRTChandle();
}
/**  TiCloudRTC创建成功 */
const onSuccess = function (c) {
    client = c;
    addEventLog("登录成功");
    inited = true;
};
/**  TiCloudRTC创建失败 */
const onFailed = function (err) {
    addEventLog("登录失败" + JSON.stringify(err));
};

/** TiCloudRTC初始化 */
function TiCloudRTChandle() {
    TiCloudRTC.setup({
        debug: true, // 是否打开debug
    });

    TiCloudRTC.createClient(
        {
            userId: options.username,
            enterpriseId: options.enterpriseId,
            accessToken: options.accessToken,
            rtcEndpoint: "https://rtc-api-test.cticloud.cn",
            callerNumber: options.callerNumber,
        },
        onSuccess,
        onFailed
    );
    TiCloudRTC.on("Invite", (param) => {
        if (!options.hanguped) {
            addEventLog("对方收到呼叫");
            callSuccesHTML(btnType);
        }
    });
    TiCloudRTC.on("Cancel", (param) => {
        if (!options.hanguped) {
            addEventLog("主动挂断");
            callFailHTML(btnType);
        }
    });
    TiCloudRTC.on("Answer", (param) => {
        if (!options.hanguped) {
            addEventLog("对方接听");
            callSuccesHTML(btnType);
        }
    });
    TiCloudRTC.on("Failure", (param) => {
        if (!options.hanguped) {
            addEventLog("外呼失败:" + param);
            callFailHTML(btnType);
        }
    });
    TiCloudRTC.on("Hangup", (param) => {
        if (!options.hanguped) {
            addEventLog("通话结束，挂断原因:" + param);
            callFailHTML(btnType);
        }
    });
    TiCloudRTC.on("AccessTokenWillExpire", async () => {
        addEventLog("AccessToken将在10分钟后过期");
    });
    TiCloudRTC.on("NetworkStateChanged", (param) => {
        addEventLog("网络状态变更为:" + JSON.parse(param).networkState);
    });
    TiCloudRTC.on("ConnectionStateChanged", (param) => {
        let json = JSON.parse(param);
        if (json.newState === "ABORTED" && json.reason === "REMOTE_LOGIN") {
            addEventLog("您已经在其他页面登录");
            alert("您已经在其他页面登录,即将返回到登录页面");
            logout();
        } else {
            addEventLog(
                "服务连接状态变更为:" + json.newState + ",原因:" + json.reason
            );
        }
    });
}

/** 退出登录 */
const logout = async function () {
    if (client) {
        client.destroyClient();
        client = null;
    }
    inited = false;
    addEventLog("退出登录成功");
};
/**
 * @desc 呼叫/联系客服
 * @type 1是呼叫  6是联系客服
 * @btn 1是呼叫 2是按键客服  3是下拉框客服
 */
const call = async function (event, type, btn) {
    if (!inited) {
        addEventLog("请先初始化");
        return;
    }
    btnType = btn;
    event.preventDefault();
    event.stopPropagation();
    let tel = $("#tel").val();
    let clid = $("#clid").val();
    let callerNumber = $("#callerNumber").val();
    let requestUniqueId = "";
    let userField = '';
    if (type !== 1) {
        if (tel === null || tel === "") {
            addEventLog("请输入客户号码");
            return;
        }
    }
    //userField的赋值
    if (btn === 1) {
        userField = $("#userField").val();
    } else if (btn === 2) {
        userField = $("#callLeftUserField").val();
    } else if (btn === 3) {
        callRightUserFieldValue = $("#callRightUserField").val();
        if (callRightUserFieldValue) {
            callRightUserField = JSON.parse(callRightUserFieldValue);
            userField = JSON.stringify([
                ...callRightUserField,
                {
                    name: "ivrNode",
                    value: $("#childNode").val(),
                    type: 1,
                },
            ]);
        } else {
            userField = JSON.stringify([
                {
                    name: "ivrNode",
                    value: $("#childNode").val(),
                    type: 1,
                },
            ]);
        }
    }
    options.hanguped = false;
    console.log("call请求参数", {
        tel: tel,
        clid: clid,
        requestUniqueId: requestUniqueId,
        userField: userField,
        type: type,
        callerNumber: callerNumber,
    });
    await client
        .call({
            tel: tel,
            clid: clid,
            requestUniqueId: requestUniqueId,
            userField: userField,
            type: type,
            callerNumber: callerNumber,
        })
        .then((res) => {
            //res代表成功 即code为200
            //eg {"code":200,"message":"成功"}
            addEventLog("呼叫成功返回结果:" + res);
        })
        .catch((err) => {
            //err代表失败 即code非200
            //eg {"code":400,"message":"呼叫失败:tel不可为空"}
            addEventLog("呼叫失败返回结果:" + err);
        });
};
/** 呼叫成功 */
const callSuccesHTML = function (btn) {
    if (btn === 1) {
        $("#callButtonContent")[0].innerHTML = `
        <button id="hangupButton" type="button" class="btn btn-danger" onclick="hangup(1)">挂断</button>
    `;
    } else if (btn === 2) {
        $("#callLeftButtonConent")[0].innerHTML = `
            <button type="button" class="btn btn-outline-primary" onclick="sendDtmf(1)">发送按键</button>
            <button id="hangupLeftButton" type="button" class="btn btn-danger" onclick="hangup(2)">挂断</button>
    `;

        $("#keyNumberContent")[0].innerHTML = `
            <label for="KeyNumber">按键数字：</label>
            <input type="text" class="form-control" id="KeyNumber" placeholder="请输入按键数字">
    `;
        $('#callLeftUserField').attr("disabled", true);
    } else if (btn === 3) {
        $("#callRightButtonContent")[0].innerHTML = `
        <button type="button" id="hangupRightButton" class="btn btn-danger" onclick="hangup(3)">挂断</button>
    `;
        $('#childNode').attr("disabled", true);
        $('#callRightUserField').attr("disabled", true);
    }
};
/** 呼叫失败 */
const callFailHTML = function (btn) {
    if (btn === 1) {
        const telVal = Boolean($("#tel").val());
        $("#callButtonContent")[0].innerHTML = `
            <button id="callButton" type="submit" class="btn btn-primary" onclick="index(event,6,1)">呼叫</button>
        `;
        $("#callButton").attr("disabled", !telVal);
    } else if (btn === 2) {
        $("#callLeftButtonConent")[0].innerHTML = `
        <button id="callLeftButton" type="submit" class="btn btn-primary" onclick="index(event,1,2)">联系客服</button>
        `;
        $("#keyNumberContent")[0].innerHTML = ``;
        $('#callLeftUserField').attr("disabled", false);
    } else if (btn === 3) {
        $("#callRightButtonContent")[0].innerHTML = `
            <button type="submit" id="callRightButton" class="btn btn-primary" onclick="index(event,1,3)">联系客服</button>
        `;
        $('#childNode').attr("disabled", false);
        $('#callRightUserField').attr("disabled", false);
    }
};
/**
 * @desc 挂断
 * @type 呼叫挂断1，客服按键挂断2，客服下拉框挂断3
 */
const hangup = async function (type) {
    if (!inited) {
        addEventLog("请先初始化");
        return;
    }
    await client
        .hangup()
        .then((r) => {
            options.hanguped = true;
            callFailHTML(type);
        })
        .catch((err) => {
            addEventLog(err);
            callSuccesHTML(type);
        });
};
/**
 * @desc 按键呼叫
 * @type 1左边按键呼叫
 */
const sendDtmf = async function (type) {
    if (!inited) {
        addEventLog("请先初始化");
        return;
    }
    let dtmf = null;
    if (type === 1) {
        dtmf = $("#KeyNumber").val();
    }
    if (dtmf === null) {
        addEventLog("请输入按键");
        return;
    }

    options.hanguped = false;
    $('#sendDtmfBtn').attr("disabled", true);

    await client.dtmf(dtmf).catch((err) => {
        $('#sendDtmfBtn').attr("disabled", false);
        addEventLog(err);
    });
};

/** 清除事件日志 */
function cleanLog() {
    $("#cicle").empty();
    $("#timeright").empty();
    $("#cicleContant").css("height", "0px");
    $("#eventLog")[0].scrollTop = 0;
}

/** 添加事件日志 */
function addEventLog(msg) {
    let date = new Date();
    let time = date.toLocaleTimeString() + ":" + date.getMilliseconds();
    const cicleLength = Array.from($("#cicle").children(".cicle")).length;
    $("#cicle").append(`
    <li class="cicle" style="top: ${cicleLength * 85}px;"></li>
    `);

    $("#timeright").append(`
    <div>
        <div class="timeLineTitle">${msg}</div>
        <div class="timeLineSub">${time}</div>
    </div>
    `);

    $("#cicleContant").css("height", cicleLength * 85 + "px");
}


/** 呼叫按钮是否禁用 */
/**
 * @desc
 * @type 1请输入客户号码 2请输入按键数字
 */
function customTel(type) {
    if (type === 1) {
        const telVal = $("#tel").val();
        if (telVal) {
            $("#callButton").attr("disabled", false);
        } else {
            $("#callButton").attr("disabled", true);
        }
    } else if (type === 2) {
        $("#sendDtmfBtn").attr("disabled", false);
    }

}

