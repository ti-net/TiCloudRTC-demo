<?php

require_once "AccessToken.php";

class AccessTokenBuilder
{

    # enterpriseId: The enterpriseId issued to you by TiCloud.
    # token:	The token issued to you by TiCloud.
    # userId: The userId. optionalUid must be unique.
    # expires: expire seconds.
    public static function buildAccessToken($enterpriseId, $token, $userId, $expires)
    {
        $accessToken = AccessToken::init($enterpriseId, $token, $userId);
        $Privileges = AccessToken::Privileges;

        $currentTimestamp = (new DateTime("now", new DateTimeZone('UTC')))->getTimestamp();
        $expireTimestamp = $currentTimestamp + $expires;
        $accessToken->addPrivilege($Privileges["API"], $expireTimestamp);

        return $accessToken->build();
    }
}


?>
