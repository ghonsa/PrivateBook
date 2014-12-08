
class Mp3Service

    @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    @defaultConfig = { headers: @headers }

    constructor: (@$log, @$http, @$q ) ->
        @$log.debug "constructing Mp2Service"
        @mp3s = []
       
     
    listArtists: () ->
        @$log.debug "listArtists()"
        deferred = @$q.defer()  
             
        @$http.get("/artists")
        .success((data, status, headers) =>
                @$log.info("Successfully listed Artists - status #{status}")
              
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
              
                @$log.error("Failed to list Artists - status #{status}")
                deferred.reject(data);
            )
        deferred.promise
        
    listAlbums: () ->
        @$log.debug "listAlbums()"
        deferred = @$q.defer()  
             
        @$http.get("/albums")
        .success((data, status, headers) =>
                @$log.info("Successfully listed Albums - status #{status}")
              
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
              
                @$log.error("Failed to list Albums - status #{status}")
                deferred.reject(data);
            )
        deferred.promise        
        
    listGenres: () ->
        @$log.debug "listGenres()"
        deferred = @$q.defer()  
             
        @$http.get("/genres")
        .success((data, status, headers) =>
                @$log.info("Successfully listed Genres - status #{status}")
              
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
              
                @$log.error("Failed to list Albums - Genres #{status}")
                deferred.reject(data);
            )
        deferred.promise                
             
    listMp3s: () ->
        @$log.debug "listMp3s()"
        deferred = @$q.defer()  
                   
        @$http.post("/mp3s",{none:' '})
        .success((data, status, headers) =>
                @$log.info("Successfully listed Mp3s - status #{status}")
              
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
              
                @$log.error("Failed to list Mp3s - status #{status}")
                deferred.reject(data);
            )
        deferred.promise
        
    listMp3sByArtist: (artist) ->
        @$log.debug "listMp3sByArtist()"
        deferred = @$q.defer()  
                   
        @$http.post("/mp3s",{artist: artist})
        .success((data, status, headers) =>
                @$log.info("Successfully listed Mp3s - status #{status}")
              
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to list Mp3s - status #{status}")
                deferred.reject(data);
            )
        deferred.promise
        
        
    listMp3sByAlbum: (album) ->
        @$log.debug "listMp3sByAlbum()"
        deferred = @$q.defer()  
                   
        @$http.post("/mp3s",{album: album})
        .success((data, status, headers) =>
                @$log.info("Successfully listed Mp3s - status #{status}")
              
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to list Mp3s - status #{status}")
                deferred.reject(data);
            )
        deferred.promise       

    listMp3sByGenre: (genre) ->
        @$log.debug "listMp3sByGenre()"
        deferred = @$q.defer()  
                   
        @$http.post("/mp3s",{genre: genre})
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
        
           
        @$http.post("/mp3/"+mp3info._id ) 
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