package main

import (
	"fmt"
	accesstokenbuilder "github.com/ti-net/Tools/TiCloudDynamicKey/go/src/AccessTokenBuilder"
	"time"
)

func main() {

	enterpriseId := "7000002"
	token := "xxxxxxxxxxxxx"
	userId := "112412512"
	expireTimeInSeconds := uint32(3600)
	currentTimestamp := uint32(time.Now().UTC().Unix())
	expireTimestamp := currentTimestamp + expireTimeInSeconds

	result, err := accesstokenbuilder.BuildAccessTokenWithPrivilegeTs(enterpriseId, token, userId, expireTimestamp)
	if err != nil {
		fmt.Println(err)
	} else {
		fmt.Printf("AccessToken: %s\n", result)
	}

}
