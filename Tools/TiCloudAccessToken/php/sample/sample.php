<?php
include("../src/AccessTokenBuilder.php");

$enterpriseId = 7000002;
$token = "xxx";
$userId = "xxx";
$expireTimeInSeconds = 3600;

$accessToken = AccessTokenBuilder::buildAccessToken($enterpriseId, $token, $userId, $expireTimeInSeconds);
echo 'accessToken: ' . $accessToken . PHP_EOL;

?>
