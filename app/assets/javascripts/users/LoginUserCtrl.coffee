
class LoginUserCtrl

    constructor: (@$log, @$location,  @UserService) ->
        @$log.debug "constructing LoginUserController"
        @userName = ""
        @passwd = ""
      

     loginUser: () ->
        @$log.debug "loginUser( #{@userName} , #{@passwd})"
        @UserService.login(@userName,@passwd)
        .then(
            (data) =>
                @$log.debug "Promise returned session #{data} "
                @session = data
                
                @$location.path("/")
            ,
            (error) =>
                @$log.error "Unable to login User: #{error}"
            )


controllersModule.controller('LoginUserCtrl', LoginUserCtrl)