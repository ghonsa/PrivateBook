
class UserCtrl

    constructor: (@$log, @UserService) ->
        @$log.debug "constructing UserController"
        @users = []
        @getAllUsers()
        @isDebug = false
        @isLoggedIn = @UserService.isLoggedIn
       
        
    logIn: () ->
        @$log.debug "logIn()"
        "Login"
      
    getSession: () ->
      @UserService.getSession()
      
            
    getAllUsers: () ->
        @$log.debug "getAllUsers()"

        @UserService.listUsers()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Users"
                @$log.debug "#{data} "
                @users = data
            ,
            (error) =>
                @$log.error "Unable to get Users: #{error}"
            )


controllersModule.controller('UserCtrl', UserCtrl)