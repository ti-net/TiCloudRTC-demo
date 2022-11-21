module sample

go 1.18

require (
	github.com/ti-net/Tools/TiCloudDynamicKey/go/src v0.0.0
)

replace (
    github.com/ti-net/Tools/TiCloudDynamicKey/go/src => ../src
)