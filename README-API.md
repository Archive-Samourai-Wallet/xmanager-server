# XManager API

## Usage

### Using `xmanager-protocol`
For your convenience, environments, endpoints, request and response beans are defined in `xmanager-protocol`.
Add it to your project with:
```
<dependency>
    <groupId>com.github.Samourai-Wallet</groupId>
    <artifactId>xmanager-protocol</artifactId>
    <version>VERSION</version>
</dependency>
```
See:
 - `XManagerEnv`
 - `XManagerProtocol`
 - `com.samourai.xmanager.protocol.rest.*`

### Environments
- MAINNET: https://xm.samourai.io:8080
- TESTNET: https://xm.samourai.io 8081

### Error response
Error response format:
```
{
    "message":"..."
}
```

## API

### Get fee address: ```POST /rest/address```
Parameters:
* id: service ID

Response:
```
{
    "address":"bc1..."
}
```

### Get fee address with index: ```POST /rest/addressIndex```
Parameters:
* id: service ID

Response:
```
{
    "address": "bc1...",
    "index": 1234
}
```

### Verify fee address with index: ```POST /rest/verifyAddressIndex```
Parameters:
* id: service ID
* address: address to verify
* index: address index to verify

Response:
```
{
    "valid": true
}
```
