package AccessTokenBuilder

import (
	accesstoken "github.com/ti-net/Tools/TiCloudDynamicKey/go/src/AccessToken"
)

//AccessTokenBuilder class
type AccessTokenBuilder struct {
}

//BuildAccessTokenWithPrivilegeTs method
// enterpriseId: The enterpriseId issued to you by TiCloud.
// token:	The token issued to you by TiCloud.
// userId: The userId. optionalUid must be unique.
// expireTimestamp: represented by the number of seconds elapsed since 1/1/1970.
func BuildAccessTokenWithPrivilegeTs(enterpriseId string, token string, userId string, expireTimestamp uint32) (string, error) {
	accessToken := accesstoken.CreateAccessToken(enterpriseId, token, userId)
	accessToken.AddPrivilege(accesstoken.API, expireTimestamp)
	return accessToken.Build()
}
