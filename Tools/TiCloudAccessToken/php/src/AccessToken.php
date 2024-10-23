<?php

class Message
{
    public $salt;
    public $ts;
    public $privileges;

    public function __construct()
    {
        $this->salt = rand(0, 100000);

        $date = new DateTime("now", new DateTimeZone('UTC'));
        $this->ts = $date->getTimestamp() + 24 * 3600;

        $this->privileges = array();
    }

    public function packContent()
    {
        $buffer = unpack("C*", pack("V", $this->salt));
        $buffer = array_merge($buffer, unpack("C*", pack("V", $this->ts)));
        $buffer = array_merge($buffer, unpack("C*", pack("v", sizeof($this->privileges))));
        foreach ($this->privileges as $key => $value) {
            $buffer = array_merge($buffer, unpack("C*", pack("v", $key)));
            $buffer = array_merge($buffer, unpack("C*", pack("V", $value)));
        }
        return $buffer;
    }
}

class AccessToken
{
    const Privileges = array(
        "API" => 1,
    );

    public $enterpriseId, $token, $userId;
    public $message;

    function __construct()
    {
        $this->message = new Message();
    }

    function setEnterpriseId($enterpriseId)
    {
        $this->enterpriseId = $enterpriseId . '';
    }

    function is_nonempty_string($name, $str)
    {
        if (is_string($str) && $str !== "") {
            return true;
        }
        echo $name . " check failed, should be a non-empty string";
        return false;
    }

    static function init($enterpriseId, $token, $userId)
    {
        $accessToken = new AccessToken();

        if (!$accessToken->is_nonempty_string("token", $token) ||
            !$accessToken->is_nonempty_string("userId", $userId)) {
            return null;
        }


        $accessToken->token = $token;
        $accessToken->userId = $userId;

        $accessToken->setEnterpriseId($enterpriseId);

        $accessToken->message = new Message();
        return $accessToken;
    }

    function addPrivilege($key, $expireTimestamp)
    {
        $this->message->privileges[$key] = $expireTimestamp;
        return $this;
    }

    function build()
    {
        $msg = $this->message->packContent();
        $val = array_merge(unpack("C*", $this->enterpriseId), unpack("C*", $this->userId), $msg);

        $sig = hash_hmac('sha256', implode(array_map("chr", $val)), $this->token, true);

        $crc_uid = crc32($this->userId) & 0xffffffff;

        $content = array_merge(unpack("C*", packString($sig)), unpack("C*", pack("V", $crc_uid)), unpack("C*", pack("v", count($msg))), $msg);
        $version = "001";
        $ret = $version . $this->enterpriseId . base64_encode(implode(array_map("chr", $content)));
        return $ret;
    }
}

function packString($value)
{
    return pack("v", strlen($value)) . $value;
}
