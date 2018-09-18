# CoffeeScript
class JPGService
    @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    @defaultConfig = { headers: @headers }

    constructor: (@$log, @$http, @$q ) ->
        @$log.debug "constructing JPGService"
        @mp3s = []
         
    listJPGs: () ->
        @$log.debug "listJPGs()"
        deferred = @$q.defer()  
                   
        @$http.post("JPGs",{none:' '})
        .success((data, status, headers) =>
                @$log.info("Successfully listed JPGs - status #{status}")
              
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
              
                @$log.error("Failed to list JPGs - status #{status}")
                deferred.reject(data);
            )
        deferred.promise

    GetJPG: (id) ->
        @$log.debug "GetJPG()"
        @$log.debug id
        deferred = @$q.defer()  
        
                   
        @$http.get("/JPG/"+id)
        .success((data, status, headers) =>
                @$log.info("Successfully retrieved JPGs - status #{status}")
              
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
              
                @$log.error("Failed to get JPGs - status #{status}")
                deferred.reject(data);
            )
        deferred.promise
        
servicesModule.service('JPGService', JPGService)