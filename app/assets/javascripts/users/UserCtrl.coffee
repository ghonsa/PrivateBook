
class UserCtrl

    constructor: (@$log, @UserService) ->
        @$log.debug "constructing UserController"
        @users = []
        @getAllUsers()
        @isDebug = false
        @viewMP3s = true
        @viewImages = false
        
    logIn: () ->
        @$log.debug "logIn()"
        "Login"
    logOut:() ->
      @UserService.logout() 
    isLoggedIn: () ->
       @UserService.IsLoggedIn() 
    getSession: () ->
      @UserService.getSession()
     
    MP3sVisible:() ->
      @UserService.IsLoggedIn() &  @viewMP3s
      
    imagesVisible:() ->
     @UserService.IsLoggedIn() &  @viewImages
      
    showImages: () ->
      @viewMP3s = false  
      @viewImages = true
     
    showMP3s: () ->
      @viewMP3s = true  
      @viewImages = false
              
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