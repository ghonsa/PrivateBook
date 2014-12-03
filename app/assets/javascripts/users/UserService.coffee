
class UserService

    @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    @defaultConfig = { headers: @headers }

    constructor: (@$log, @$http, @$q ,@toaster) ->
        @$log.debug "constructing UserService"
        @user = []
        @users = []
        @LoggedIn = []
        @session = []
        @getSession()
    
             
    listUsers: () ->
        @$log.debug "listUsers()"
        deferred = @$q.defer()  
        
           
        @$http.get("/users")
        .success((data, status, headers) =>
                @$log.info("Successfully listed Users - status #{status}")
                @isLoggedIn = true
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @isLoggedIn = false
                @$log.error("Failed to list Users - status #{status}")
                deferred.reject(data);
            )
        deferred.promise
  
         
    createUser: (user) ->
        @$log.debug "createUser #{angular.toJson(user, true)}"
        
        deferred = @$q.defer() 

        @$http.post('/user', user)
        .success((data, status, headers) =>
                @$log.info("Successfully created User - status #{status}")
                deferred.resolve(data)
                
            )
        .error((data, status, headers) =>
                @$log.error("Failed to create user - status #{status}")
                deferred.reject(data);
            )
        deferred.promise
     IsLoggedIn: () ->
       @LoggedIn 
       
     login: (username,passwd) ->
        @$log.debug "loginUser #{angular.toJson(username, true)}"
        @user = {username: username, passwd: passwd}
        deferred = @$q.defer()

        @$http.post('/login', angular.toJson(@user, true) )
        .success((data, status, headers) =>
                @$log.info("Successfully log in User - status #{status}")
                @toaster.pop('success', 'Login Successful', 'Logged in as ?' );
                deferred.resolve(data)
                @session = data.sessionData
                @user = data.user
                for key, value of @session
                   @$log.info(" key  #{key}: #{value}");
                @$log.info(" user  #{@user.userName}: logged in");  
                @LoggedIn = true
            )
        .error((data, status, headers) =>
                @$log.error("Failed to login user - status #{status}")
                deferred.reject(data);
            )
        deferred.promise
     
      logout: () ->
        @$log.debug "logoutUser #{angular.toJson(@session, true)}"
        deferred = @$q.defer()
        @$http.post('/logout', angular.toJson(@session, true) )
          .success((data, status, headers) =>
                @$log.info("Successfully log out - status #{status}")
                @toaster.pop('success', 'Logout Successful' );
                deferred.resolve(data)
                @session = []
                @user = []
                
                @LoggedIn = false
            )
        .error((data, status, headers) =>
                @$log.error("Failed to login user - status #{status}")
                deferred.reject(data);
            )
        deferred.promise
     
     getSession: () ->
        deferred = @$q.defer()
        @$log.debug "getSessionData"
        @$http.get('/getSessionData' )
        .success((data, status, headers) =>
                @$log.info("Successfull getSession - status #{status}")
                deferred.resolve(data)
                @user = data
                @LoggedIn = true
            )
        .error((data, status, headers) =>
                @$log.error("Failed to getSession- status #{status}")
                deferred.reject(data);
            )
        deferred.promise

servicesModule.service('UserService', UserService)