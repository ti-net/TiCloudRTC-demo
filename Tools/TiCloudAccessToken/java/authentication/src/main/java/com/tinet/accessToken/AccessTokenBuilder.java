package com.tinet.accessToken;

/**
 * @Author: zhoujiang
 * @Date: 2022/7/25 16:30
 */
public class AccessTokenBuilder {

    /**
     * Builds an AccessToken.
     *
     * @param enterpriseId    The enterpriseId issued to you by TiCloud
     * @param token           The token issued to you by TiCloud
     * @param userId          The userId.
     * @param expireTimestamp represented by the number of seconds elapsed since 1/1/1970.
     */
    public String buildAccessToken(String enterpriseId, String token, String userId, int expireTimestamp) {

        AccessToken builder = new AccessToken(enterpriseId, token, userId);
        builder.addPrivilege(AccessToken.Privileges.API, expireTimestamp);

        try {
            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Builds an AccessToken with expires.
     *
     * @param enterpriseId The enterpriseId issued to you by TiCloud
     * @param token        The token issued to you by TiCloud
     * @param userId       The userId.
     * @param expires      expire seconds.
     */
    public String buildAccessTokenWithExpires(String enterpriseId, String token, String userId, int expires) {

        int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        int expireTimestamp = currentTimeMillis + expires;
        AccessToken builder = new AccessToken(enterpriseId, token, userId);
        builder.addPrivilege(AccessToken.Privileges.API, expireTimestamp);

        try {
            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Builds an AccessToken.
     *
     * @param enterpriseId    The enterpriseId issued to you by TiCloud
     * @param token           The token issued to you by TiCloud
     * @param userId          The userId.
     * @param expireTimestamp represented by the number of seconds elapsed since 1/1/1970.
     * @param salt            privilegeSalt
     * @param ts              privilegeTs
     */
    public String buildAccessTokenWithPrivilegeTs(String enterpriseId, String token, String userId, int expireTimestamp, int salt, int ts) {

        AccessToken builder = new AccessToken(enterpriseId, token, userId, salt, ts);
        builder.addPrivilege(AccessToken.Privileges.API, expireTimestamp);

        try {
            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
