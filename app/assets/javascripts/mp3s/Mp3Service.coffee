
class Mp3Service

    @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    @defaultConfig = { headers: @headers }

    constructor: (@$log, @$http, @$q ) ->
        @$log.debug "constructing Mp2Service"
        @mp3s = []
       
     
             
    listMp3s: () ->
        @$log.debug "listMp3s()"
        deferred = @$q.defer()  
        
           
        @$http.get("/mp3s")
        .success((data, status, headers) =>
                @$log.info("Successfully listed Mp3s - status #{status}")
              
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
              
                @$log.error("Failed to list Mp3s - status #{status}")
                deferred.reject(data);
            )
        deferred.promise
  
    playMp3:(mp3info)  ->
        @$log.debug "playMp3()" + mp3info.filePath 
        deferred = @$q.defer()  
        
           
        @$http.post("/mp3/"+mp3info.filePath ) 
        .success((data, status, headers) =>
                @$log.info("Successfully retrieved Mp3 - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to retrieve Mp3s - status #{status}")
                deferred.reject(data);
            )
        deferred.promise
   
servicesModule.service('Mp3Service', Mp3Service)