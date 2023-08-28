<?php
include("../src/AccessTokenBuilder.php");

$enterpriseId = 7002485;
$token = "cf49050d2c45ea69719a6a88d5f21bd8";
$userId = "zhoujiang";
$expireTimeInSeconds = 3600;

$accessToken = AccessTokenBuilder::buildAccessToken($enterpriseId, $token, $userId, $expireTimeInSeconds);
echo 'accessToken: ' . $accessToken . PHP_EOL;

?>
