# OAuth2.0 参数详解

## client_id
**必须项**

授权服务器颁发给已注册客户端客户标识，代表客户端提供的注册信息的唯一字符串。并且还用于构建呈现给用户的授权URI。

## client_secret
**必须项**

客户端密钥，向授权服务证明自己的身份。授权服务的注册中心颁发，获取token的时候才用到，不对用户暴露。一但client_secret发现外露，可以在授权认证服务中手动去刷新改参数值。该参数不能包含在请求URI中，用于后台服务之间请求。

## response_type
**必须项**

构建授权链接里需要的参数，response_type=code为固定写法，用于告知授权认证服务本次请求为CODE模式同时返回code的值。

## redirect_uri
**必须项**

完成与资源所有者的交互后，授权服务器引导资源所有者的用户代理返回到客户端。授权服务器重定向用户代理至客户端的重定向端点，该改端点是事先在客户端注册过程中或者当发起授权请求时与授权服务器建立的。

## scope
**可选项**

表示申请权限的范围。

## state
**推荐项**

表示客户端的当前状态，可以指定任意值，可选项.认证服务器会原封不动地返回这个值。
由客户端（第三方应用程序）生成的随机字符串,/redirect_url?code=x&state=y请求时会携带此字符串用于比较，这是为了防止绑定交换场景下CSRF攻击。

绑定交换应用场景：第三方应用登录后，在我的账号里设置绑定例如“微信”、“微博”等场景。

参考绑定交换：https://blog.csdn.net/gjb724332682/article/details/54428808

## code
**必须项**

表示授权码，获取 access_token的时候需要用到。该码的有效期应该很短，通常设为10分钟，客户端只能使用该码一次，否则会被授权服务器拒绝。该码与客户端ID和重定向URI，是一一对应关系。

## grant_type
**必须项**

通过code进行获取access_token的时候需要指定的参数，CODE模式时的固定写法（grant_type=authorization_cod）。

## access_token
**必须项**

表示访问令牌，access_token是调用授权关系接口的调用凭证。

由于access_token有效期（目前为2个小时）较短，当access_token超时后，可以使用refresh_token进行刷新，access_token刷新结果有两种： 

> 1. 若access_token已超时，那么进行refresh_token会获取一个新的access_token，新的超时时间； 
> 2. 若access_token未超时，那么进行refresh_token不会改变access_token，但超时时间[expires_in]会刷新，相当于续期access_token。  

refresh_token拥有较长的有效期（30天），当refresh_token失效的后，需要用户重新授权。

## expires_in
**推荐项**

表示access_token过期时间，单位为秒。如果省略该参数，必须其他方式设置过期时间。

## refresh_token
**可选项**

表示更新令牌，用来获取下一次的访问令牌.refresh_token拥有较长的有效期（30天），当refresh_token失效的后，需要用户重新授权。

## 参考
https://tools.ietf.org/html/rfc6749
